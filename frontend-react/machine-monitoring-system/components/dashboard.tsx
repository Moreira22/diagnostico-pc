"use client"

import useSWR from "swr"
import { DashboardHeader } from "@/components/dashboard-header"
import { StatsOverview } from "@/components/stats-overview"
import { MachineGrid } from "@/components/machine-grid"
import type { MachineRecord } from "@/lib/types"
import { Loader2 } from "lucide-react"

const fetcher = (url: string) => fetch(url).then((res) => res.json())

export function Dashboard() {
  const { data: machines, isLoading } = useSWR<MachineRecord[]>(
    "/api/machines",
    fetcher,
    { refreshInterval: 10000 }
  )

  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="mx-auto max-w-7xl px-4 py-6 lg:px-8">
        {isLoading || !machines ? (
          <div className="flex h-[60vh] items-center justify-center">
            <div className="flex flex-col items-center gap-3">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
              <p className="text-sm text-muted-foreground">Carregando maquinas...</p>
            </div>
          </div>
        ) : (
          <div className="flex flex-col gap-6">
            <StatsOverview machines={machines} />
            <section>
              <h2 className="mb-4 text-base font-semibold text-foreground">Maquinas Monitoradas</h2>
              <MachineGrid machines={machines} />
            </section>
          </div>
        )}
      </main>
    </div>
  )
}
