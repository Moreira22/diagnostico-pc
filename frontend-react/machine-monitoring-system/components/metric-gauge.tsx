"use client"

import { cn } from "@/lib/utils"

interface MetricGaugeProps {
  value: number
  label: string
  detail?: string
  icon: React.ReactNode
  size?: "sm" | "md"
}

function getGaugeColor(value: number): string {
  if (value >= 90) return "text-destructive"
  if (value >= 75) return "text-warning"
  return "text-success"
}

function getTrackColor(value: number): string {
  if (value >= 90) return "stroke-destructive"
  if (value >= 75) return "stroke-warning"
  return "stroke-success"
}

export function MetricGauge({ value, label, detail, icon, size = "md" }: MetricGaugeProps) {
  const svgSize = size === "sm" ? 80 : 100
  const strokeWidth = size === "sm" ? 6 : 7
  const radius = (svgSize - strokeWidth) / 2
  const circumference = 2 * Math.PI * radius
  const strokeDashoffset = circumference - (value / 100) * circumference

  return (
    <div className="flex flex-col items-center gap-2">
      <div className="relative">
        <svg
          width={svgSize}
          height={svgSize}
          viewBox={`0 0 ${svgSize} ${svgSize}`}
          className="-rotate-90"
        >
          <circle
            cx={svgSize / 2}
            cy={svgSize / 2}
            r={radius}
            fill="none"
            stroke="hsl(var(--secondary))"
            strokeWidth={strokeWidth}
          />
          <circle
            cx={svgSize / 2}
            cy={svgSize / 2}
            r={radius}
            fill="none"
            className={cn("transition-all duration-700 ease-out", getTrackColor(value))}
            strokeWidth={strokeWidth}
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
          />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className={cn("font-mono font-bold", getGaugeColor(value), size === "sm" ? "text-base" : "text-xl")}>
            {value.toFixed(0)}%
          </span>
        </div>
      </div>
      <div className="flex flex-col items-center gap-0.5">
        <div className="flex items-center gap-1.5 text-muted-foreground">
          {icon}
          <span className={cn("font-medium", size === "sm" ? "text-xs" : "text-sm")}>{label}</span>
        </div>
        {detail && (
          <span className="text-xs text-muted-foreground/70">{detail}</span>
        )}
      </div>
    </div>
  )
}
