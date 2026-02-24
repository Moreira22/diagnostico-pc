import { NextResponse } from "next/server";

const BACKEND_URL = "http://localhost:8080/api/machines";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  try {
    const { id } = await params;

    const res = await fetch(`${BACKEND_URL}/${id}`, {
      cache: "no-store",
    });

    if (!res.ok) {
      return NextResponse.json(
        { error: "Maquina nao encontrada" },
        { status: 404 },
      );
    }

    const machine = await res.json();

    return NextResponse.json(machine);
  } catch (error) {
    return NextResponse.json(
      { error: "Erro ao buscar maquina" },
      { status: 500 },
    );
  }
}
