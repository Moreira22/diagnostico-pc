"use client"

import useSWR from "swr"
import Link from "next/link"
import { MetricGauge } from "@/components/metric-gauge"
import { MetricsChart } from "@/components/metrics-chart"
import { HardwareInfo } from "@/components/hardware-info"
import { StatusIndicator } from "@/components/status-indicator"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {
  ArrowLeft,
  Cpu,
  MemoryStick,
  HardDrive,
  Clock,
  Activity,
  Loader2,
  Network,
} from "lucide-react"
import type { MachineRecord } from "@/lib/types"

const fetcher = (url: string) => fetch(url).then((res) => res.json())

function formatUptime(hours: number): string {
  if (hours < 1) return `${Math.round(hours * 60)} minutos`
  if (hours < 24) return `${Math.round(hours)} horas`
  const days = Math.floor(hours / 24)
  const remainingHours = Math.round(hours % 24)
  return `${days} dias e ${remainingHours} horas`
}

function formatLastSeen(timestamp: string): string {
  const diff = Date.now() - new Date(timestamp).getTime()
  const seconds = Math.floor(diff / 1000)
  if (seconds < 60) return `${seconds}s atras`
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}min atras`
  const hours = Math.floor(minutes / 60)
  return `${hours}h atras`
}

export function MachineDetail({ machineId }: { machineId: string }) {
  const { data: machine, isLoading } = useSWR<MachineRecord>(
    `/api/machines/${machineId}`,
    fetcher,
    { refreshInterval: 10000 }
  )

  if (isLoading || !machine) {
    return (
      <div className="flex h-screen items-center justify-center bg-background">
        <div className="flex flex-col items-center gap-3">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
          <p className="text-sm text-muted-foreground">Carregando dados...</p>
        </div>
      </div>
    )
  }

  const latest = machine.metrics_history[machine.metrics_history.length - 1]

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border/50 bg-card/50 backdrop-blur-sm">
        <div className="mx-auto flex max-w-7xl items-center gap-4 px-4 py-4 lg:px-8">
          <Link
            href="/"
            className="flex h-9 w-9 items-center justify-center rounded-lg bg-secondary text-muted-foreground transition-colors hover:bg-secondary/80 hover:text-foreground"
          >
            <ArrowLeft className="h-4 w-4" />
          </Link>
          <div className="flex flex-1 items-center justify-between">
            <div>
              <div className="flex items-center gap-3">
                <h1 className="text-lg font-bold text-foreground">{machine.hostname}</h1>
                <StatusIndicator status={machine.status} />
              </div>
              <div className="flex items-center gap-3 text-xs text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Network className="h-3 w-3" />
                  {machine.ip_address}
                </span>
                <span className="flex items-center gap-1">
                  <Clock className="h-3 w-3" />
                  Visto {formatLastSeen(machine.last_seen)}
                </span>
              </div>
            </div>
            <div className="hidden items-center gap-2 rounded-full bg-secondary px-3 py-1.5 sm:flex">
              <Activity className="h-3.5 w-3.5 text-primary" />
              <span className="text-xs font-medium text-muted-foreground">
                {machine.metrics_history.length} amostras
              </span>
            </div>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 lg:px-8">
        <div className="flex flex-col gap-6">
          {/* Current Metrics */}
          {latest && (
            <Card className="border-border/50 bg-card">
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-sm font-medium text-foreground">Metricas Atuais</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <div className="flex flex-wrap items-center justify-around gap-6 py-2">
                  <MetricGauge
                    value={latest.cpu_percent}
                    label="CPU"
                    detail={`${machine.cpu_cores} nucleos`}
                    icon={<Cpu className="h-3.5 w-3.5" />}
                  />
                  <MetricGauge
                    value={latest.ram_percent}
                    label="Memoria RAM"
                    detail={`${latest.ram_used_gb.toFixed(1)} / ${machine.ram_total_gb} GB`}
                    icon={<MemoryStick className="h-3.5 w-3.5" />}
                  />
                  <MetricGauge
                    value={latest.disk_percent}
                    label="Disco"
                    detail={`${latest.disk_used_gb.toFixed(0)} / ${machine.disk_total_gb} GB`}
                    icon={<HardDrive className="h-3.5 w-3.5" />}
                  />
                  <div className="flex flex-col items-center gap-2">
                    <div className="flex h-[100px] w-[100px] items-center justify-center rounded-full bg-secondary">
                      <Clock className="h-8 w-8 text-muted-foreground" />
                    </div>
                    <div className="flex flex-col items-center gap-0.5">
                      <span className="text-sm font-medium text-muted-foreground">Uptime</span>
                      <span className="text-xs text-muted-foreground/70">
                        {formatUptime(latest.uptime_hours)}
                      </span>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Charts */}
          <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
            <MetricsChart
              data={machine.metrics_history}
              dataKey="cpu_percent"
              label="Uso de CPU"
              color="hsl(160 84% 44%)"
              icon={<Cpu className="h-3.5 w-3.5 text-success" />}
            />
            <MetricsChart
              data={machine.metrics_history}
              dataKey="ram_percent"
              label="Uso de Memoria RAM"
              color="hsl(200 80% 50%)"
              icon={<MemoryStick className="h-3.5 w-3.5 text-accent" />}
            />
            <MetricsChart
              data={machine.metrics_history}
              dataKey="disk_percent"
              label="Uso de Disco"
              color="hsl(38 92% 60%)"
              icon={<HardDrive className="h-3.5 w-3.5 text-warning" />}
            />
            <HardwareInfo machine={machine} />
          </div>
        </div>
      </main>
    </div>
  )
}
