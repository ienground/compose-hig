"use client"

import * as React from "react"
import { Slider as SliderPrimitive } from "radix-ui"
import { cn } from "cn"
import { useIenTheme } from "@/shared/lib/ien-theme-context"
import type { IenToneProps } from "@/shared/lib/ien-theme"

export type SliderProps = React.ComponentProps<typeof SliderPrimitive.Root> & IenToneProps & {
  label?: string
  valueLabel?: React.ReactNode
  thumbLabels?: string[]
}

function Slider({ value, defaultValue, min = 0, max = 100, label, valueLabel, thumbLabels, tone, useGradient, className, ...props }: SliderProps) {
  const theme = useIenTheme({ tone, useGradient })
  const values = value ?? defaultValue ?? [min]
  return <div className="flex w-full items-center gap-3">
    {label && <span className="text-[length:var(--ien-font-size-body2)]">{label}</span>}
    <SliderPrimitive.Root {...props} value={value} defaultValue={defaultValue ?? (value === undefined ? [min] : undefined)} min={min} max={max}
      aria-label={props["aria-label"] ?? label} data-slot="slider" data-ien="" data-ien-tone={theme.tone} data-ien-gradient={theme.useGradient}
      className={cn("relative flex min-h-11 min-w-0 flex-1 touch-none select-none items-center data-[orientation=vertical]:h-48 data-[orientation=vertical]:min-h-0 data-[orientation=vertical]:min-w-11 data-[orientation=vertical]:flex-col", className)}>
      <SliderPrimitive.Track data-slot="slider-track" className="relative grow overflow-hidden rounded-2xl bg-(--ien-control-weak) data-[orientation=horizontal]:h-9 data-[orientation=horizontal]:w-full data-[orientation=vertical]:h-full data-[orientation=vertical]:w-9">
        <SliderPrimitive.Range data-slot="slider-range" className="absolute bg-(--ien-control-color) data-disabled:bg-(--ien-text-disabled) data-[orientation=horizontal]:h-full data-[orientation=vertical]:w-full" />
      </SliderPrimitive.Track>
      {values.map((_, index) => <SliderPrimitive.Thumb key={index} aria-label={thumbLabels?.[index] ?? (values.length > 1 ? `${label ?? "범위"} ${index === 0 ? "최솟값" : "최댓값"}` : label ?? props["aria-label"])} data-slot="slider-thumb"
        className="relative block h-11 w-1 shrink-0 rounded-full shadow-[0_0_0_6px_var(--ien-surface)] data-[orientation=vertical]:h-1 data-[orientation=vertical]:w-11 bg-(--ien-control-color) outline-none ring-ring/50 transition-[box-shadow,transform] hover:ring-4 focus-visible:ring-4 active:scale-x-50 data-disabled:bg-(--ien-text-disabled) motion-reduce:transition-none" />)}
    </SliderPrimitive.Root>
    {valueLabel != null && <span className="min-w-8 text-right text-[length:var(--ien-font-size-label2)] tabular-nums">{valueLabel}</span>}
  </div>
}

export type RangeSliderProps = Omit<SliderProps, "value" | "defaultValue"> & { value?: [number, number]; defaultValue?: [number, number] }
function RangeSlider({ min = 0, max = 100, defaultValue, ...props }: RangeSliderProps) {
  return <Slider {...props} min={min} max={max} defaultValue={defaultValue ?? [min, max]} />
}

export { Slider, RangeSlider }
