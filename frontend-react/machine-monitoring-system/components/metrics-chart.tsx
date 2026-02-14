"use client"

import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import type { MetricsSnapshot } from "@/lib/types"

interface MetricsChartProps {
  data: MetricsSnapshot[]
  dataKey: keyof MetricsSnapshot
  label: string
  color: string
  unit?: string
  maxValue?: number
  icon: React.ReactNode
}

function formatTime(timestamp: string) {
  return new Date(timestamp).toLocaleTimeString("pt-BR", {
    hour: "2-digit",
    minute: "2-digit",
  })
}

export function MetricsChart({
  data,
  dataKey,
  label,
  color,
  unit = "%",
  maxValue = 100,
  icon,
}: MetricsChartProps) {
  const chartData = data.map((d) => ({
    time: formatTime(d.timestamp),
    value: Number(d[dataKey]),
  }))

  return (
    <Card className="border-border/50 bg-card">
      <CardHeader className="flex flex-row items-center gap-2 p-4 pb-2">
        <div className="flex h-7 w-7 items-center justify-center rounded-md bg-secondary">
          {icon}
        </div>
        <CardTitle className="text-sm font-medium text-foreground">{label}</CardTitle>
      </CardHeader>
      <CardContent className="p-4 pt-2">
        <ResponsiveContainer width="100%" height={180}>
          <AreaChart data={chartData} margin={{ top: 5, right: 5, left: -15, bottom: 0 }}>
            <defs>
              <linearGradient id={`gradient-${dataKey}`} x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor={color} stopOpacity={0.3} />
                <stop offset="100%" stopColor={color} stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid
              strokeDasharray="3 3"
              stroke="hsl(220 14% 18%)"
              vertical={false}
            />
            <XAxis
              dataKey="time"
              axisLine={false}
              tickLine={false}
              tick={{ fill: "hsl(215 15% 55%)", fontSize: 10 }}
              interval="preserveStartEnd"
            />
            <YAxis
              domain={[0, maxValue]}
              axisLine={false}
              tickLine={false}
              tick={{ fill: "hsl(215 15% 55%)", fontSize: 10 }}
              tickFormatter={(v) => `${v}${unit}`}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: "hsl(220 18% 10%)",
                border: "1px solid hsl(220 14% 18%)",
                borderRadius: "8px",
                color: "hsl(210 20% 92%)",
                fontSize: "12px",
              }}
              labelStyle={{ color: "hsl(215 15% 55%)" }}
              formatter={(value: number) => [`${value.toFixed(1)}${unit}`, label]}
            />
            <Area
              type="monotone"
              dataKey="value"
              stroke={color}
              strokeWidth={2}
              fill={`url(#gradient-${dataKey})`}
              animationDuration={800}
            />
          </AreaChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
