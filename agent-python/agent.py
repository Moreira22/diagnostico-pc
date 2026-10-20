import psutil
import platform
import requests
import schedule
import time
import socket
import uuid
import os
import subprocess
from datetime import datetime
from cpuinfo import get_cpu_info

BACKEND_URL = "http://localhost:8080/api/coleta"

# ID único da máquina (gera baseado no MAC)
MACHINE_ID = str(uuid.getnode())


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
        "timestamp": datetime.utcnow().isoformat(),
    }


def enviar():
    dados = coletar()
    try:
        requests.post(BACKEND_URL, json=dados, timeout=5)
        print("Dados enviados:", dados)
    except Exception as e:
        print("Erro ao enviar:", e)


# Coleta a cada 10 minutos
schedule.every(10).minutes.do(enviar)

print("Agent iniciado...")

# Primeira execução imediata
enviar()

while True:
    schedule.run_pending()
    time.sleep(1)