import { NextResponse } from "next/server"
import { getMachine } from "@/lib/store"

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id } = await params
  const machine = getMachine(id)

  if (!machine) {
    return NextResponse.json(
      { error: "Maquina nao encontrada" },
      { status: 404 }
    )
  }

  return NextResponse.json(machine)
}
