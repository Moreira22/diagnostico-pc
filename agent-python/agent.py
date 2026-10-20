import psutil
import platform
import requests
import schedule
import time
import socket
import uuid
import os
import json
import subprocess
from datetime import datetime, timezone, timedelta
from cpuinfo import get_cpu_info

# ---------------------------------------------------------------------------
# CONFIGURAÇÃO
# ---------------------------------------------------------------------------
BACKEND_BASE_URL = "http://localhost:8080"
COLETA_URL = f"{BACKEND_BASE_URL}/api/coleta"
# o backend confirma/limpa o comando quando o agente busca e executa
COMANDO_URL_TEMPLATE = f"{BACKEND_BASE_URL}/api/comando/{{client_id}}"

# ID único da máquina (gera baseado no MAC, não muda entre execuções)
MACHINE_ID = str(uuid.getnode())

# CLIENT_ID: identifica o cliente/empresa dono deste agente.
# Duas formas de preencher isso na hora do download pelo frontend:
#
# OPÇÃO A (mais simples): o backend gera o .py sob demanda e substitui o
# placeholder abaixo antes de servir o arquivo para download.
#   Ex. no backend: conteudo = template.replace("__CLIENT_ID__", client_id)
CLIENT_ID = "__CLIENT_ID__"

# OPÇÃO B (mais flexível, recomendada): o agente lê de um config.json que
# fica na mesma pasta do executável/script, também gerado no momento do
# download. Isso evita ter que reescrever o .py por cliente.
# Se existir, o config.json tem prioridade sobre o placeholder acima.
CONFIG_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "config.json")
if os.path.exists(CONFIG_PATH):
    try:
        with open(CONFIG_PATH, "r", encoding="utf-8") as f:
            _cfg = json.load(f)
            CLIENT_ID = _cfg.get("client_id", CLIENT_ID)
    except Exception as e:
        print("Erro ao ler config.json:", e)


# Arquivo que guarda quando foi a última coleta completa, para sobreviver
# a reinícios/desligamentos do PC (horário local, mesmo padrão usado
# internamente pela lib "schedule").
LAST_RUN_FILE = os.path.join(
    os.path.dirname(os.path.abspath(__file__)), "ultima_execucao.json"
)


def ler_ultima_execucao():
    if os.path.exists(LAST_RUN_FILE):
        try:
            with open(LAST_RUN_FILE, "r", encoding="utf-8") as f:
                dados = json.load(f)
                return datetime.fromisoformat(dados["last_run"])
        except Exception as e:
            print("Erro ao ler última execução:", e)
    return None


def salvar_ultima_execucao():
    try:
        with open(LAST_RUN_FILE, "w", encoding="utf-8") as f:
            json.dump({"last_run": datetime.now().isoformat()}, f)
    except Exception as e:
        print("Erro ao salvar última execução:", e)


def get_ip():
    try:
        return socket.gethostbyname(socket.gethostname())
    except Exception:
        return "0.0.0.0"


def get_installed_programs():
    """Lista os programas instalados, de acordo com o sistema operacional."""
    sistema = platform.system()
    programas = []

    try:
        if sistema == "Windows":
            import winreg

            caminhos = [
                r"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall",
                r"SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall",
            ]
            for caminho in caminhos:
                try:
                    chave = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, caminho)
                    for i in range(winreg.QueryInfoKey(chave)[0]):
                        try:
                            subkey_name = winreg.EnumKey(chave, i)
                            subkey = winreg.OpenKey(chave, subkey_name)
                            nome = winreg.QueryValueEx(subkey, "DisplayName")[0]
                            programas.append(nome)
                        except (FileNotFoundError, OSError):
                            continue
                except FileNotFoundError:
                    continue

        elif sistema == "Linux":
            try:
                saida = subprocess.check_output(["dpkg", "-l"], text=True)
                for linha in saida.splitlines():
                    if linha.startswith("ii"):
                        partes = linha.split()
                        if len(partes) > 1:
                            programas.append(partes[1])
            except Exception:
                try:
                    saida = subprocess.check_output(["rpm", "-qa"], text=True)
                    programas = saida.splitlines()
                except Exception:
                    pass

        elif sistema == "Darwin":
            app_dir = "/Applications"
            if os.path.exists(app_dir):
                programas = [p for p in os.listdir(app_dir) if p.endswith(".app")]

    except Exception as e:
        print("Erro ao coletar programas instalados:", e)

    return sorted(set(programas))


def get_recent_programs(limite=15):
    """Retorna os processos em execução ordenados pelo horário de início
    (proxy para 'programas abertos recentemente')."""
    processos = []
    for proc in psutil.process_iter(["pid", "name", "create_time"]):
        try:
            info = proc.info
            processos.append(
                {
                    "nome": info["name"],
                    "pid": info["pid"],
                    "iniciado_em": datetime.fromtimestamp(
                        info["create_time"]
                    ).isoformat(),
                }
            )
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            continue

    processos.sort(key=lambda x: x["iniciado_em"], reverse=True)
    return processos[:limite]


def coletar():
    # CPU nome real
    try:
        cpu_nome = get_cpu_info()["brand_raw"]
    except Exception:
        cpu_nome = "Não identificado"

    # GPU
    gpu_nome = None
    try:
        import GPUtil

        gpus = GPUtil.getGPUs()
        if gpus:
            gpu_nome = gpus[0].name
    except Exception:
        pass

    vm = psutil.virtual_memory()
    disk = psutil.disk_usage("/")
    boot_time = psutil.boot_time()

    return {
        "id": str(uuid.uuid4()),  # id único desta coleta
        "client_id": CLIENT_ID,  # usado pelo backend para validar/vincular ao cliente
        "machine_id": MACHINE_ID,
        "hostname": platform.node(),
        "os": f"{platform.system()} {platform.release()}",
        "cpu_name": cpu_nome,
        "cpu_percent": psutil.cpu_percent(interval=None),
        "cpu_cores": psutil.cpu_count(logical=True),
        "ram_total_gb": round(vm.total / (1024**3), 2),
        "ram_used_gb": round(vm.used / (1024**3), 2),
        "ram_percent": vm.percent,
        "disk_total_gb": round(disk.total / (1024**3), 2),
        "disk_used_gb": round(disk.used / (1024**3), 2),
        "disk_percent": disk.percent,
        "gpu_name": gpu_nome,
        "uptime_hours": round((time.time() - boot_time) / 3600, 2),
        "ip_address": get_ip(),
        "installed_programs": get_installed_programs(),
        "recent_programs": get_recent_programs(),
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }


def enviar():
    dados = coletar()
    try:
        resp = requests.post(COLETA_URL, json=dados, timeout=5)
        if resp.status_code in (401, 403):
            print("Client_id inválido/não autorizado pelo backend:", resp.text)
        else:
            print("Dados enviados:", dados["id"])
    except Exception as e:
        print("Erro ao enviar:", dados)
    finally:
        salvar_ultima_execucao()


def verificar_comando():
    """Pergunta ao backend se existe um comando pendente (ex: o usuário
    clicou em 'coletar agora' no frontend). Se houver, executa a coleta
    imediatamente. O backend deve limpar a flag ao responder (ou o agente
    confirma a execução em uma segunda chamada, conforme a API)."""
    if CLIENT_ID in ("", "__CLIENT_ID__"):
        # evita rodar sem um client_id configurado
        return
    try:
        url = COMANDO_URL_TEMPLATE.format(client_id=CLIENT_ID)
        resp = requests.get(url, timeout=5)
        if resp.status_code == 200:
            comando = resp.json()
            if comando.get("coletar_agora"):
                print("Comando recebido do backend: coletar agora")
                enviar()
    except Exception:
        # silencioso para não poluir o log a cada poucos segundos
        pass


# Coleta completa a cada 24 horas, mas respeitando o tempo já passado
# mesmo que o PC tenha ficado desligado nesse meio tempo.
job_coleta = schedule.every(24).hours.do(enviar)

ultima_execucao = ler_ultima_execucao()
agora = datetime.now()

if ultima_execucao is None or (agora - ultima_execucao) >= timedelta(hours=24):
    print("Já passou 24h desde a última coleta (ou é a primeira vez). Coletando agora...")
    enviar()
    job_coleta.next_run = datetime.now() + timedelta(hours=24)
else:
    proxima = ultima_execucao + timedelta(hours=24)
    job_coleta.next_run = proxima
    print(f"Última coleta foi há menos de 24h. Próxima coleta agendada para {proxima}")

# Verifica comando remoto ("coletar agora") a cada 5 segundos
schedule.every(5).seconds.do(verificar_comando)

print(f"Agent iniciado... client_id={CLIENT_ID} machine_id={MACHINE_ID}")

while True:
    schedule.run_pending()
    time.sleep(1)