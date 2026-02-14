import { Card, CardContent } from "@/components/ui/card"
import { Monitor, Wifi, AlertTriangle, WifiOff } from "lucide-react"
import type { MachineRecord } from "@/lib/types"

interface StatsOverviewProps {
  machines: MachineRecord[]
}

export function StatsOverview({ machines }: StatsOverviewProps) {
  const total = machines.length
  const online = machines.filter((m) => m.status === "online").length
  const warnings = machines.filter((m) => m.status === "warning").length
  const offline = machines.filter((m) => m.status === "offline").length

  const avgCpu =
    machines.length > 0
      ? machines
          .filter((m) => m.status !== "offline")
          .reduce((sum, m) => {
            const last = m.metrics_history[m.metrics_history.length - 1]
            return sum + (last?.cpu_percent ?? 0)
          }, 0) / Math.max(1, machines.filter((m) => m.status !== "offline").length)
      : 0

  const stats = [
    {
      label: "Total de Maquinas",
      value: total,
      icon: Monitor,
      color: "text-foreground",
      bgColor: "bg-secondary",
    },
    {
      label: "Online",
      value: online,
      icon: Wifi,
      color: "text-success",
      bgColor: "bg-success/10",
    },
    {
      label: "Alertas",
      value: warnings,
      icon: AlertTriangle,
      color: "text-warning",
      bgColor: "bg-warning/10",
    },
    {
      label: "Offline",
      value: offline,
      icon: WifiOff,
      color: "text-destructive",
      bgColor: "bg-destructive/10",
    },
  ]

  return (
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-5">
      {stats.map((stat) => (
        <Card key={stat.label} className="border-border/50 bg-card">
          <CardContent className="flex items-center gap-3 p-4">
            <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg ${stat.bgColor}`}>
              <stat.icon className={`h-5 w-5 ${stat.color}`} />
            </div>
            <div>
              <p className="text-2xl font-bold tabular-nums text-foreground">{stat.value}</p>
              <p className="text-xs text-muted-foreground">{stat.label}</p>
            </div>
          </CardContent>
        </Card>
      ))}
      <Card className="border-border/50 bg-card">
        <CardContent className="flex items-center gap-3 p-4">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-accent/10">
            <Monitor className="h-5 w-5 text-accent" />
          </div>
          <div>
            <p className="text-2xl font-bold tabular-nums text-foreground">{avgCpu.toFixed(0)}%</p>
            <p className="text-xs text-muted-foreground">CPU Media</p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
