import { cn } from "@/lib/utils"

interface StatusIndicatorProps {
  status: "online" | "warning" | "offline"
  showLabel?: boolean
  className?: string
}

const statusConfig = {
  online: {
    label: "Online",
    dotClass: "bg-success",
    textClass: "text-success",
  },
  warning: {
    label: "Alerta",
    dotClass: "bg-warning",
    textClass: "text-warning",
  },
  offline: {
    label: "Offline",
    dotClass: "bg-destructive",
    textClass: "text-destructive",
  },
}

export function StatusIndicator({
  status,
  showLabel = true,
  className,
}: StatusIndicatorProps) {
  const config = statusConfig[status]

  return (
    <div className={cn("flex items-center gap-2", className)}>
      <span className="relative flex h-2.5 w-2.5">
        {status === "online" && (
          <span
            className={cn(
              "absolute inline-flex h-full w-full animate-ping rounded-full opacity-75",
              config.dotClass
            )}
          />
        )}
        <span
          className={cn(
            "relative inline-flex h-2.5 w-2.5 rounded-full",
            config.dotClass
          )}
        />
      </span>
      {showLabel && (
        <span className={cn("text-xs font-medium", config.textClass)}>
          {config.label}
        </span>
      )}
    </div>
  )
}
