# 🖥️ Sistema de Diagnóstico do Computador

Projeto full stack para **diagnóstico inteligente de computador**, composto por:

* 🐍 **Agent Python** (coleta de dados do PC)
* ☕ **Backend Spring Boot** (API, regras, histórico e PDF)
* ⚛️ **Frontend React** (dashboard com gráficos)

Ideal para estudo, portfólio e evolução para produto real.

---

## 🧠 Arquitetura

```
[ Agent Python ]  -->  [ Spring Boot API ]  -->  [ React Dashboard ]
        |                    |
        |                    ├── Diagnóstico inteligente
        |                    ├── Histórico (PostgreSQL)
        |                    └── Geração de PDF
```

---

## 📁 Estrutura do Projeto

```
diagnostico-pc/
│
├── agent-python/
│   ├── agent.py
│   └── requirements.txt
│
├── backend-spring/
│   └── diagnostico-api/
│       └── src/main/java/...
│
└── frontend-react/
```

---

## ⚙️ Pré-requisitos

* **Python 3.10+**
* **Java 17**
* **Node.js 18+**
* **PostgreSQL**
* **Maven**

---

## 🐍 1️⃣ Agent Python (Coleta de Dados)

### 📦 Instalação

```bash
cd agent-python
pip install -r requirements.txt
```

### ▶️ Executar

```bash
python agent.py
```

📌 O agent coleta dados do computador e envia automaticamente para o backend a cada 1 minuto.

---

## ☕ 2️⃣ Backend Spring Boot (API)

### 🗄️ Banco de Dados

Crie o banco no PostgreSQL:

```sql
CREATE DATABASE diagnostico;
```

Configure o arquivo `application.yml` com usuário e senha do seu PostgreSQL.

---

### ▶️ Executar o Backend

```bash
cd backend-spring/diagnostico-api
mvn spring-boot:run
```

A API ficará disponível em:

```
http://localhost:8080
```

### 📡 Endpoints principais

* `POST /api/coleta` → recebe dados do agent
* `GET /api/historico` → lista diagnósticos salvos
* `GET /api/diagnostico/{id}/pdf` → gera PDF do diagnóstico

---

## ⚛️ 3️⃣ Frontend React (Dashboard)

### 📦 Instalação

```bash
cd frontend-react
npm install
```

### ▶️ Executar

```bash
npm start
```

O frontend abrirá em:

```
http://localhost:3000
```

---

## 📄 Funcionalidades

* ✅ Coleta automática de dados do PC
* ✅ Diagnóstico inteligente (CPU, RAM, Disco)
* ✅ Histórico persistido em banco
* ✅ Dashboard com gráficos
* ✅ Geração de relatório em PDF

---


## 👨‍💻 Autor: João Vitor De Paula Moreira 

Projeto desenvolvido para estudo e portfólio.

Sinta-se livre para clonar, estudar e evoluir 🚀
