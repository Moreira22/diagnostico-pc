import psutil
import platform
import requests
import schedule
import time

BACKEND_URL = "http://localhost:8080/api/coleta"

def coletar():
    return {
        "cpuUso": psutil.cpu_percent(interval=1),
        "ramTotal": round(psutil.virtual_memory().total / (1024**3), 2),
        "ramUso": psutil.virtual_memory().percent,
        "discoLivre": round(psutil.disk_usage('/').free / (1024**3), 2)
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
while True:
    schedule.run_pending()
    time.sleep(1)
