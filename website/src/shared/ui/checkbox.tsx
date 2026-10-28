"use client"

import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenTheme } from "@/shared/lib/ien-theme-context"
import * as React from "react"
import { cn } from "cn"
import { Checkbox as CheckboxPrimitive } from "radix-ui"
import { IconPlaceholder } from "@/shared/lib/reui-icons"
import { IconCheck } from "@tabler/icons-react"

export type CheckboxProps = React.ComponentProps<typeof CheckboxPrimitive.Root> & IenToneProps & {
  variant?: "circle" | "dot" | "line"
  size?: number
  enabled?: boolean
  label?: string
}

function Checkbox({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  variant = "circle",
  size,
  enabled = true,
  disabled,
  label,
  style,
  onPointerDown,
  ...props
}: CheckboxProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const isDisabled = disabled || !enabled
  const visualRef = React.useRef<HTMLSpanElement | null>(null)
  const shake = React.useRef<Animation | null>(null)
  React.useEffect(() => () => shake.current?.cancel(), [])

  return (
    <IenThemeContext.Provider value={ienTheme}>
      <CheckboxPrimitive.Root
        data-slot="checkbox"
        data-ien-typography="body2"
        data-ien=""
        data-ien-tone={ienTheme.tone}
        data-ien-gradient={ienTheme.useGradient}
        data-variant={variant}
        disabled={isDisabled}
        onPointerDown={(event) => {
          onPointerDown?.(event)
          if (event.defaultPrevented || !isDisabled || window.matchMedia("(prefers-reduced-motion: reduce)").matches) return
          shake.current?.cancel()
          shake.current = visualRef.current?.animate([
            { transform: "translateX(0)", offset: 0 },
            { transform: "translateX(-6px)", offset: 0.15 },
            { transform: "translateX(6px)", offset: 0.35 },
            { transform: "translateX(-3.6px)", offset: 0.55 },
            { transform: "translateX(3.6px)", offset: 0.75 },
            { transform: "translateX(0)", offset: 1 },
          ], { duration: 200 }) ?? null
        }}
        className={cn(
          "group/checkbox peer relative inline-flex size-(--ien-checkbox-size) shrink-0 items-center gap-(--ien-spacing-xs) border-0 bg-transparent bg-none p-0 text-(--ien-text-primary) outline-none focus-visible:ring-3 focus-visible:ring-ring/50 aria-invalid:ring-3 aria-invalid:ring-destructive/40 disabled:cursor-not-allowed disabled:text-(--ien-text-disabled) after:absolute after:inset-y-1/2 after:start-1/2 after:min-h-(--ien-state-minimum-touch-target) after:min-w-(--ien-state-minimum-touch-target) after:-translate-x-1/2 after:-translate-y-1/2 after:content-[''] rtl:after:translate-x-1/2",
          label != null && "h-auto min-h-(--ien-state-minimum-touch-target) w-fit after:inset-0 after:min-w-0 after:translate-none rtl:after:translate-none",
          className,
        )}
        style={{
          "--ien-checkbox-size": `${size ?? (variant === "dot" ? 8 : 24)}px`,
          "--ien-checkbox-gradient": ienTheme.useGradient === false ? "none" : "var(--ien-gradient, linear-gradient(135deg, color-mix(in srgb, var(--ien-control-color), var(--ien-control-on) 18%), var(--ien-control-color), color-mix(in srgb, var(--ien-control-color), var(--ien-control-on) 36%)))",
          ...style,
        } as React.CSSProperties}
        {...props}
      >
        <span
          ref={visualRef}
          data-slot="checkbox-control"
          aria-hidden="true"
          className={cn(
            "relative inline-flex size-(--ien-checkbox-size) shrink-0 items-center justify-center text-(--ien-border-strong) [--ien-control-corner-shape:round] [corner-shape:round] [transition:background-color_200ms_cubic-bezier(0.4,0,0.2,1),border-color_200ms_cubic-bezier(0.4,0,0.2,1),color_200ms_cubic-bezier(0.4,0,0.2,1),scale_var(--ien-button-press-duration)_var(--ien-button-press-easing)] group-[:enabled:active]/checkbox:scale-95 group-disabled/checkbox:opacity-(--ien-state-disabled-alpha) motion-reduce:transition-none",
            label == null && "size-full",
            variant !== "line" && "rounded-full border-(length:--ien-stroke-medium) border-(--ien-border-strong) group-data-[state=checked]/checkbox:border-(--ien-control-color) group-data-[state=checked]/checkbox:bg-(--ien-control-color) group-data-[state=checked]/checkbox:[background-image:var(--ien-checkbox-gradient)] group-data-[state=indeterminate]/checkbox:border-(--ien-control-color) group-data-[state=indeterminate]/checkbox:bg-(--ien-control-color) group-data-[state=indeterminate]/checkbox:[background-image:var(--ien-checkbox-gradient)]",
            variant === "circle" && "group-data-[state=checked]/checkbox:text-white group-data-[state=indeterminate]/checkbox:text-white",
            variant === "line" && "group-data-[state=checked]/checkbox:text-(--ien-control-color) group-data-[state=indeterminate]/checkbox:text-(--ien-control-color)",
          )}
        >
          {variant !== "dot" && (
            <>
              <IconCheck className={cn("max-h-full max-w-full shrink-0 group-data-[state=indeterminate]/checkbox:hidden", variant === "circle" ? "size-[calc(var(--ien-checkbox-size)*0.65)]" : "size-(--ien-checkbox-size)")} />
              <span className="hidden h-(--ien-stroke-medium) w-1/2 rounded-full bg-current group-data-[state=indeterminate]/checkbox:block" />
            </>
          )}
        </span>
        {label != null && <span>{label}</span>}
      </CheckboxPrimitive.Root>
    </IenThemeContext.Provider>
  )
}

export { Checkbox }
