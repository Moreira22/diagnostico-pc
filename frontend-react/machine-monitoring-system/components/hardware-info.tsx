import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Cpu, MemoryStick, HardDrive, MonitorCog, Globe, Server } from "lucide-react"
import type { MachineRecord } from "@/lib/types"

interface HardwareInfoProps {
  machine: MachineRecord
}

export function HardwareInfo({ machine }: HardwareInfoProps) {
  const items = [
    { label: "Processador", value: machine.cpu_name, icon: Cpu },
    { label: "Nucleos de CPU", value: `${machine.cpu_cores} nucleos`, icon: Server },
    { label: "Memoria RAM", value: `${machine.ram_total_gb} GB`, icon: MemoryStick },
    { label: "Armazenamento", value: `${machine.disk_total_gb} GB`, icon: HardDrive },
    { label: "GPU", value: machine.gpu_name || "Nao detectada", icon: MonitorCog },
    { label: "Sistema Operacional", value: machine.os, icon: Globe },
  ]

  return (
    <Card className="border-border/50 bg-card">
      <CardHeader className="p-4 pb-3">
        <CardTitle className="text-sm font-medium text-foreground">Informacoes de Hardware</CardTitle>
      </CardHeader>
      <CardContent className="p-4 pt-0">
        <div className="flex flex-col gap-3">
          {items.map((item) => (
            <div key={item.label} className="flex items-start gap-3">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-md bg-secondary">
                <item.icon className="h-4 w-4 text-muted-foreground" />
              </div>
              <div className="min-w-0">
                <p className="text-xs text-muted-foreground">{item.label}</p>
                <p className="truncate text-sm font-medium text-foreground">{item.value}</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
