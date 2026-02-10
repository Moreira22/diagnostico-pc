import psutil
import platform
import requests
import schedule
import time
from cpuinfo import get_cpu_info

BACKEND_URL = "http://localhost:8080/api/coleta"

def coletar():

    # CPU nome real
    try:
        cpu_nome = get_cpu_info()['brand_raw']
    except:
        cpu_nome = "Não identificado"

    # GPU
    gpu_nome = "Não encontrada"
    try:
        import GPUtil
        gpus = GPUtil.getGPUs()
        if gpus:
            gpu_nome = gpus[0].name
    except:
        pass

    return {
        "cpuNome": cpu_nome,
        "cpuUso": psutil.cpu_percent(interval=None),
        "ramTotal": round(psutil.virtual_memory().total / (1024**3), 2),
        "ramUso": psutil.virtual_memory().percent,
        "discoLivre": round(psutil.disk_usage('/').free / (1024**3), 2),
        "gpu": gpu_nome
    }

def enviar():
    dados = coletar()
    try:
        requests.post(BACKEND_URL, json=dados, timeout=5)
        print("Dados enviados:", dados)
    except Exception as e:
        print("Erro ao enviar:", e)

# Coleta a cada 1 minuto
schedule.every(1).minutes.do(enviar)

print("Agent iniciado...")

# 👇 primeira execução imediata
enviar()

while True:
    schedule.run_pending()
    time.sleep(1)
