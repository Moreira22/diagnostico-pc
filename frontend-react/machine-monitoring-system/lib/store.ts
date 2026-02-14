import type { MachineRecord, MachineMetrics, MetricsSnapshot } from "./types"

const MAX_HISTORY = 60

const machines: Map<string, MachineRecord> = new Map()

function seedDemoData() {
  if (machines.size > 0) return

  const now = Date.now()
  const demoMachines: {
    id: string
    hostname: string
    os: string
    cpu: string
    cores: number
    ram: number
    disk: number
    gpu: string | null
    ip: string
    baseCpu: number
    baseRam: number
    baseDisk: number
    uptime: number
    offline?: boolean
  }[] = [
    {
      id: "srv-prod-01",
      hostname: "SERVIDOR-PROD-01",
      os: "Windows Server 2022",
      cpu: "Intel Xeon E5-2680 v4",
      cores: 14,
      ram: 64,
      disk: 500,
      gpu: "NVIDIA Tesla T4",
      ip: "192.168.1.10",
      baseCpu: 62,
      baseRam: 41,
      baseDisk: 72,
      uptime: 720,
    },
    {
      id: "srv-prod-02",
      hostname: "SERVIDOR-PROD-02",
      os: "Ubuntu 22.04 LTS",
      cpu: "AMD EPYC 7543",
      cores: 32,
      ram: 128,
      disk: 2000,
      gpu: "NVIDIA A100",
      ip: "192.168.1.11",
      baseCpu: 35,
      baseRam: 58,
      baseDisk: 45,
      uptime: 2160,
    },
    {
      id: "lab-pc-01",
      hostname: "LAB-PC-01",
      os: "Windows 11 Pro",
      cpu: "Intel Core i7-13700K",
      cores: 16,
      ram: 32,
      disk: 1000,
      gpu: "NVIDIA RTX 4070",
      ip: "192.168.2.101",
      baseCpu: 15,
      baseRam: 38,
      baseDisk: 55,
      uptime: 8,
    },
    {
      id: "lab-pc-02",
      hostname: "LAB-PC-02",
      os: "Windows 11 Pro",
      cpu: "AMD Ryzen 9 7950X",
      cores: 16,
      ram: 32,
      disk: 512,
      gpu: "AMD Radeon RX 7800 XT",
      ip: "192.168.2.102",
      baseCpu: 88,
      baseRam: 82,
      baseDisk: 91,
      uptime: 48,
    },
    {
      id: "lab-pc-03",
      hostname: "LAB-PC-03",
      os: "Windows 10 Pro",
      cpu: "Intel Core i5-10400",
      cores: 6,
      ram: 16,
      disk: 256,
      gpu: null,
      ip: "192.168.2.103",
      baseCpu: 45,
      baseRam: 60,
      baseDisk: 78,
      uptime: 24,
      offline: true,
    },
    {
      id: "dev-ws-01",
      hostname: "DEV-WORKSTATION-01",
      os: "macOS Sonoma 14.3",
      cpu: "Apple M3 Pro",
      cores: 12,
      ram: 36,
      disk: 1000,
      gpu: "Apple M3 Pro GPU (18-core)",
      ip: "192.168.3.50",
      baseCpu: 22,
      baseRam: 45,
      baseDisk: 38,
      uptime: 96,
    },
  ]

  for (const dm of demoMachines) {
    const history: MetricsSnapshot[] = []

    for (let i = 29; i >= 0; i--) {
      const jitterCpu = (Math.random() - 0.5) * 20
      const jitterRam = (Math.random() - 0.5) * 10
      history.push({
        cpu_percent: Math.max(1, Math.min(100, dm.baseCpu + jitterCpu)),
        ram_used_gb: Math.max(
          0.5,
          Math.min(dm.ram, (dm.ram * (dm.baseRam + jitterRam)) / 100)
        ),
        ram_percent: Math.max(1, Math.min(100, dm.baseRam + jitterRam)),
        disk_used_gb: (dm.disk * dm.baseDisk) / 100,
        disk_percent: dm.baseDisk,
        uptime_hours: dm.uptime,
        timestamp: new Date(now - i * 30000).toISOString(),
      })
    }

    const lastMetrics = history[history.length - 1]
    let status: "online" | "warning" | "offline" = "online"
    if (dm.offline) {
      status = "offline"
    } else if (
      lastMetrics.cpu_percent > 85 ||
      lastMetrics.ram_percent > 85 ||
      lastMetrics.disk_percent > 90
    ) {
      status = "warning"
    }

    machines.set(dm.id, {
      machine_id: dm.id,
      hostname: dm.hostname,
      os: dm.os,
      cpu_name: dm.cpu,
      cpu_cores: dm.cores,
      ram_total_gb: dm.ram,
      disk_total_gb: dm.disk,
      gpu_name: dm.gpu,
      ip_address: dm.ip,
      status,
      last_seen: dm.offline
        ? new Date(now - 3600000).toISOString()
        : new Date(now).toISOString(),
      metrics_history: history,
    })
  }
}

seedDemoData()

export function getAllMachines(): MachineRecord[] {
  return Array.from(machines.values())
}

export function getMachine(machineId: string): MachineRecord | undefined {
  return machines.get(machineId)
}

export function upsertMetrics(data: MachineMetrics): MachineRecord {
  const existing = machines.get(data.machine_id)

  const snapshot: MetricsSnapshot = {
    cpu_percent: data.cpu_percent,
    ram_used_gb: data.ram_used_gb,
    ram_percent: data.ram_percent,
    disk_used_gb: data.disk_used_gb,
    disk_percent: data.disk_percent,
    uptime_hours: data.uptime_hours,
    timestamp: data.timestamp || new Date().toISOString(),
  }

  let status: "online" | "warning" | "offline" = "online"
  if (
    data.cpu_percent > 85 ||
    data.ram_percent > 85 ||
    data.disk_percent > 90
  ) {
    status = "warning"
  }

  if (existing) {
    existing.hostname = data.hostname
    existing.os = data.os
    existing.cpu_name = data.cpu_name
    existing.cpu_cores = data.cpu_cores
    existing.ram_total_gb = data.ram_total_gb
    existing.disk_total_gb = data.disk_total_gb
    existing.gpu_name = data.gpu_name
    existing.ip_address = data.ip_address
    existing.status = status
    existing.last_seen = snapshot.timestamp
    existing.metrics_history.push(snapshot)
    if (existing.metrics_history.length > MAX_HISTORY) {
      existing.metrics_history = existing.metrics_history.slice(-MAX_HISTORY)
    }
    return existing
  }

  const record: MachineRecord = {
    machine_id: data.machine_id,
    hostname: data.hostname,
    os: data.os,
    cpu_name: data.cpu_name,
    cpu_cores: data.cpu_cores,
    ram_total_gb: data.ram_total_gb,
    disk_total_gb: data.disk_total_gb,
    gpu_name: data.gpu_name,
    ip_address: data.ip_address,
    status,
    last_seen: snapshot.timestamp,
    metrics_history: [snapshot],
  }

  machines.set(data.machine_id, record)
  return record
}
