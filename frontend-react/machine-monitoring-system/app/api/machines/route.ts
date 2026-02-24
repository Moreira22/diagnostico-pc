import { NextResponse } from "next/server"

const BACKEND_URL = "http://localhost:8080/api/machines"

export async function GET() {
  try {
    const res = await fetch(BACKEND_URL, {
      cache: "no-store"
    })

    if (!res.ok) {
      return NextResponse.json(
        { error: "Erro ao buscar maquinas no backend" },
        { status: res.status }
      )
    }

    const data = await res.json()
    return NextResponse.json(data)

  } catch (error) {
    return NextResponse.json(
      { error: "Backend indisponivel" },
      { status: 500 }
    )
  }
}

export async function POST(request: Request) {
  try {
    const body = await request.json()

    const res = await fetch(BACKEND_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(body)
    })

    if (!res.ok) {
      return NextResponse.json(
        { error: "Erro ao enviar dados para o backend" },
        { status: res.status }
      )
    }

    const data = await res.json()
    return NextResponse.json(data, { status: 201 })

  } catch (error) {
    return NextResponse.json(
      { error: "Erro ao processar requisicao" },
      { status: 500 }
    )
  }
}