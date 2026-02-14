#!/usr/bin/env python3
"""
SysMon Agent - Agente de Monitoramento de Maquinas
===================================================
Agente leve que coleta metricas de desempenho e hardware
e envia para o servidor de monitoramento via HTTP.

Requisitos:
    pip install psutil requests

Uso:
    python agent.py --server https://seu-servidor.vercel.app --interval 30
"""

import argparse
import json
import platform
import socket
import time
import hashlib
from datetime import datetime

try:
    import psutil
except ImportError:
    print("[ERRO] Biblioteca 'psutil' nao encontrada.")
    print("       Instale com: pip install psutil")
    exit(1)

try:
    import requests
except ImportError:
    print("[ERRO] Biblioteca 'requests' nao encontrada.")
    print("       Instale com: pip install requests")
    exit(1)


def get_machine_id():
    """Gera um ID unico baseado no hostname e MAC address."""
    hostname = socket.gethostname()
    mac = hex(psutil.net_if_addrs().get(
        list(psutil.net_if_addrs().keys())[0], [None]
    )[0].address.__hash__() & 0xFFFFFFFF) if psutil.net_if_addrs() else "0"
    raw = f"{hostname}-{mac}"
    return hashlib.md5(raw.encode()).hexdigest()[:12]


def get_gpu_name():
    """Tenta detectar o nome da GPU."""
    try:
        import subprocess
        # Windows (NVIDIA)
        result = subprocess.run(
            ["nvidia-smi", "--query-gpu=name", "--format=csv,noheader,nounits"],
            capture_output=True, text=True, timeout=5
        )
        if result.returncode == 0 and result.stdout.strip():
            return result.stdout.strip().split("\n")[0]
    except (FileNotFoundError, subprocess.TimeoutExpired):
        pass

    try:
        import subprocess
        # Windows (WMI)
        if platform.system() == "Windows":
            result = subprocess.run(
                ["wmic", "path", "win32_videocontroller", "get", "name"],
                capture_output=True, text=True, timeout=5
            )
            if result.returncode == 0:
                lines = [l.strip() for l in result.stdout.strip().split("\n") if l.strip() and l.strip() != "Name"]
                if lines:
                    return lines[0]
    except (FileNotFoundError, subprocess.TimeoutExpired):
        pass

    try:
        # Linux (lspci)
        import subprocess
        result = subprocess.run(
            ["lspci"], capture_output=True, text=True, timeout=5
        )
        if result.returncode == 0:
            for line in result.stdout.split("\n"):
                if "VGA" in line or "3D" in line:
                    return line.split(": ")[-1].strip()
    except (FileNotFoundError, subprocess.TimeoutExpired):
        pass

    return None


def get_ip_address():
    """Obtem o endereco IP local da maquina."""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"


def collect_metrics():
    """Coleta todas as metricas da maquina."""
    cpu_percent = psutil.cpu_percent(interval=1)
    memory = psutil.virtual_memory()
    disk = psutil.disk_usage("/")
    boot_time = psutil.boot_time()
    uptime_seconds = time.time() - boot_time
    uptime_hours = round(uptime_seconds / 3600, 2)

    return {
        "machine_id": get_machine_id(),
        "hostname": socket.gethostname(),
        "os": f"{platform.system()} {platform.release()}",
        "cpu_name": platform.processor() or "Processador Desconhecido",
        "cpu_percent": round(cpu_percent, 1),
        "cpu_cores": psutil.cpu_count(logical=True),
        "ram_total_gb": round(memory.total / (1024 ** 3), 1),
        "ram_used_gb": round(memory.used / (1024 ** 3), 1),
        "ram_percent": round(memory.percent, 1),
        "disk_total_gb": round(disk.total / (1024 ** 3), 0),
        "disk_used_gb": round(disk.used / (1024 ** 3), 1),
        "disk_percent": round(disk.percent, 1),
        "gpu_name": get_gpu_name(),
        "uptime_hours": uptime_hours,
        "ip_address": get_ip_address(),
        "timestamp": datetime.utcnow().isoformat() + "Z",
    }


def send_metrics(server_url, metrics):
    """Envia metricas para o servidor."""
    url = f"{server_url.rstrip('/')}/api/machines"
    try:
        response = requests.post(
            url,
            json=metrics,
            headers={"Content-Type": "application/json"},
            timeout=10,
        )
        if response.status_code in (200, 201):
            print(f"[OK] Metricas enviadas com sucesso - CPU: {metrics['cpu_percent']}% | RAM: {metrics['ram_percent']}% | Disco: {metrics['disk_percent']}%")
            return True
        else:
            print(f"[ERRO] Servidor retornou status {response.status_code}: {response.text}")
            return False
    except requests.exceptions.ConnectionError:
        print(f"[ERRO] Nao foi possivel conectar ao servidor: {url}")
        return False
    except requests.exceptions.Timeout:
        print(f"[ERRO] Tempo limite de conexao excedido: {url}")
        return False
    except Exception as e:
        print(f"[ERRO] Erro inesperado: {e}")
        return False


def main():
    parser = argparse.ArgumentParser(
        description="SysMon Agent - Agente de Monitoramento de Maquinas"
    )
    parser.add_argument(
        "--server",
        type=str,
        default="http://localhost:3000",
        help="URL do servidor de monitoramento (padrao: http://localhost:3000)",
    )
    parser.add_argument(
        "--interval",
        type=int,
        default=30,
        help="Intervalo entre coletas em segundos (padrao: 30)",
    )
    args = parser.parse_args()

    print("=" * 60)
    print("  SysMon Agent - Monitoramento de Maquinas")
    print("=" * 60)
    print(f"  Servidor:  {args.server}")
    print(f"  Intervalo: {args.interval}s")
    print(f"  Hostname:  {socket.gethostname()}")
    print(f"  IP:        {get_ip_address()}")
    print(f"  OS:        {platform.system()} {platform.release()}")
    print(f"  Machine ID: {get_machine_id()}")
    print("=" * 60)
    print()

    # Coleta e envio inicial
    print("[INFO] Iniciando coleta de metricas...")
    metrics = collect_metrics()
    print(f"[INFO] Dados coletados: {json.dumps(metrics, indent=2)}")
    send_metrics(args.server, metrics)

    # Loop de coleta periodica
    print(f"\n[INFO] Entrando em modo de monitoramento continuo (a cada {args.interval}s)...\n")
    while True:
        try:
            time.sleep(args.interval)
            metrics = collect_metrics()
            send_metrics(args.server, metrics)
        except KeyboardInterrupt:
            print("\n[INFO] Agente encerrado pelo usuario.")
            break
        except Exception as e:
            print(f"[ERRO] Erro durante a coleta: {e}")
            time.sleep(5)


if __name__ == "__main__":
    main()
