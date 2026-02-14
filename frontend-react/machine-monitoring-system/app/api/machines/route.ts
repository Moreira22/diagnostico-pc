import { NextResponse } from "next/server"
import { getAllMachines, upsertMetrics } from "@/lib/store"
import type { MachineMetrics } from "@/lib/types"

export async function GET() {
  const machines = getAllMachines()
  return NextResponse.json(machines)
}

export async function POST(request: Request) {
  try {
    const data: MachineMetrics = await request.json()

    if (!data.machine_id || !data.hostname) {
      return NextResponse.json(
        { error: "machine_id e hostname sao obrigatorios" },
        { status: 400 }
      )
    }

    const record = upsertMetrics(data)
    return NextResponse.json(record, { status: 201 })
  } catch {
    return NextResponse.json(
      { error: "Dados invalidos" },
      { status: 400 }
    )
  }
}
