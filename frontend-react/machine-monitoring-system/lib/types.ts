export interface MachineMetrics {
  machine_id: string
  hostname: string
  os: string
  cpu_name: string
  cpu_percent: number
  cpu_cores: number
  ram_total_gb: number
  ram_used_gb: number
  ram_percent: number
  disk_total_gb: number
  disk_used_gb: number
  disk_percent: number
  gpu_name: string | null
  uptime_hours: number
  ip_address: string
  timestamp: string
}

export interface MachineRecord {
  machine_id: string
  hostname: string
  os: string
  cpu_name: string
  cpu_cores: number
  ram_total_gb: number
  disk_total_gb: number
  gpu_name: string | null
  ip_address: string
  status: "online" | "warning" | "offline"
  last_seen: string
  metrics_history: MetricsSnapshot[]
}

export interface MetricsSnapshot {
  cpu_percent: number
  ram_used_gb: number
  ram_percent: number
  disk_used_gb: number
  disk_percent: number
  uptime_hours: number
  timestamp: string
}
