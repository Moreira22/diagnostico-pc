"use client"

import Link from "next/link"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { StatusIndicator } from "@/components/status-indicator"
import { MetricGauge } from "@/components/metric-gauge"
import { Cpu, MemoryStick, HardDrive, Clock, MonitorCog } from "lucide-react"
import type { MachineRecord } from "@/lib/types"

interface MachineCardProps {
  machine: MachineRecord
}

function formatUptime(hours: number): string {
  if (hours < 1) return `${Math.round(hours * 60)}min`
  if (hours < 24) return `${Math.round(hours)}h`
  const days = Math.floor(hours / 24)
  const remainingHours = Math.round(hours % 24)
  return `${days}d ${remainingHours}h`
}

export function MachineCard({ machine }: MachineCardProps) {
  const latest = machine.metrics_history[machine.metrics_history.length - 1]

  return (
    <Link href={`/machine/${machine.machine_id}`}>
      <Card className="group border-border/50 bg-card transition-all hover:border-primary/30 hover:bg-card/80">
        <CardHeader className="flex flex-row items-start justify-between p-4 pb-2">
          <div className="flex items-center gap-3 min-w-0">
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-secondary">
              <MonitorCog className="h-4.5 w-4.5 text-muted-foreground" />
            </div>
            <div className="min-w-0">
              <h3 className="truncate text-sm font-semibold text-foreground group-hover:text-primary transition-colors">
                {machine.hostname}
              </h3>
              <p className="truncate text-xs text-muted-foreground font-mono">
                {machine.ip_address}
              </p>
            </div>
          </div>
          <StatusIndicator status={machine.status} showLabel={false} />
        </CardHeader>
        <CardContent className="p-4 pt-1">
          <div className="mb-3 flex flex-wrap gap-1.5">
            <Badge variant="secondary" className="text-[10px] font-normal bg-secondary text-muted-foreground border-0">
              {machine.os}
            </Badge>
            {machine.gpu_name && (
              <Badge variant="secondary" className="text-[10px] font-normal bg-secondary text-muted-foreground border-0">
                GPU
              </Badge>
            )}
          </div>

          {latest ? (
            <>
              <div className="flex items-center justify-around py-2">
                <MetricGauge
                  value={latest.cpu_percent}
                  label="CPU"
                  icon={<Cpu className="h-3 w-3" />}
                  size="sm"
                />
                <MetricGauge
                  value={latest.ram_percent}
                  label="RAM"
                  detail={`${latest.ram_used_gb.toFixed(1)}/${machine.ram_total_gb}GB`}
                  icon={<MemoryStick className="h-3 w-3" />}
                  size="sm"
                />
                <MetricGauge
                  value={latest.disk_percent}
                  label="Disco"
                  icon={<HardDrive className="h-3 w-3" />}
                  size="sm"
                />
              </div>
              <div className="mt-2 flex items-center justify-center gap-1.5 text-xs text-muted-foreground/70">
                <Clock className="h-3 w-3" />
                <span>Uptime: {formatUptime(latest.uptime_hours)}</span>
              </div>
            </>
          ) : (
            <div className="flex h-24 items-center justify-center text-sm text-muted-foreground">
              Sem dados
            </div>
          )}
        </CardContent>
      </Card>
    </Link>
  )
}
