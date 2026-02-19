import psutil
import platform
import requests
import schedule
import time
import socket
import uuid
from datetime import datetime
from cpuinfo import get_cpu_info

BACKEND_URL = "http://localhost:8080/api/coleta"

# ID único da máquina (gera baseado no MAC)
MACHINE_ID = str(uuid.getnode())

def get_ip():
    try:
        return socket.gethostbyname(socket.gethostname())
    except:
        return "0.0.0.0"

def coletar():

    # CPU nome real
    try:
        cpu_nome = get_cpu_info()['brand_raw']
    except:
        cpu_nome = "Não identificado"

    # GPU
    gpu_nome = None
    try:
        import GPUtil
        gpus = GPUtil.getGPUs()
        if gpus:
            gpu_nome = gpus[0].name
    except:
        pass

    vm = psutil.virtual_memory()
    disk = psutil.disk_usage('/')
    boot_time = psutil.boot_time()

    return {
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
        "timestamp": datetime.utcnow().isoformat()
    }

def enviar():
    dados = coletar()
    try:
        requests.post(BACKEND_URL, json=dados, timeout=5)
        print("Dados enviados:", dados)
    except Exception as e:
        print("Erro ao enviar:", e)

# Coleta a cada 1 minuto
schedule.every(10).minutes.do(enviar)

print("Agent iniciado...")

# Primeira execução imediata
enviar()

while True:
    schedule.run_pending()
    time.sleep(1)