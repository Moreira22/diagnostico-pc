"use client"

import { useState } from "react"
import { MachineCard } from "@/components/machine-card"
import { Input } from "@/components/ui/input"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Search } from "lucide-react"
import type { MachineRecord } from "@/lib/types"

interface MachineGridProps {
  machines: MachineRecord[]
}

export function MachineGrid({ machines }: MachineGridProps) {
  const [search, setSearch] = useState("")
  const [filter, setFilter] = useState("all")

  const filtered = machines.filter((m) => {
    const matchesSearch =
      m.hostname.toLowerCase().includes(search.toLowerCase()) ||
      m.ip_address.includes(search) ||
      m.machine_id.toLowerCase().includes(search.toLowerCase())

    const matchesFilter =
      filter === "all" ||
      (filter === "online" && m.status === "online") ||
      (filter === "warning" && m.status === "warning") ||
      (filter === "offline" && m.status === "offline")

    return matchesSearch && matchesFilter
  })

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Buscar maquina..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9 bg-card border-border/50 text-foreground placeholder:text-muted-foreground"
          />
        </div>
        <Tabs value={filter} onValueChange={setFilter}>
          <TabsList className="bg-secondary">
            <TabsTrigger value="all" className="text-xs">Todas</TabsTrigger>
            <TabsTrigger value="online" className="text-xs">Online</TabsTrigger>
            <TabsTrigger value="warning" className="text-xs">Alertas</TabsTrigger>
            <TabsTrigger value="offline" className="text-xs">Offline</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>
      {filtered.length > 0 ? (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((machine) => (
            <MachineCard key={machine.machine_id} machine={machine} />
          ))}
        </div>
      ) : (
        <div className="flex h-40 items-center justify-center rounded-lg border border-dashed border-border/50 text-sm text-muted-foreground">
          Nenhuma maquina encontrada
        </div>
      )}
    </div>
  )
}
