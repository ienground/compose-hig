"use client"

import { useEffect, useMemo, useRef, type ComponentProps, type CSSProperties } from "react"
import { Progress as MaterialProgress } from "material/react"
import { circularIndeterminateFrame, cubicBezier, waveOffset, waveTravelMillis } from "@kyralabs/m3e-motion"
import { CircularWavyPathCache, circularWavyVertexCount, toSvgPathData } from "@kyralabs/m3e-shapes"
import { cn } from "cn"
import type { IenTone } from "@/shared/lib/ien-theme"
import { useIenLanguage } from "@/shared/lib/ien-theme-context"
import { useMaterialIndicatorTheme } from "@/shared/lib/ien-material-indicator"

// Compose ProgressIndicator.kt의 keyframes에서 추가 회전 구간은 선형입니다.
const circularTiming = {
  durationMillis: 6000,
  rotation: 1080,
  additionalRotation: 360,
  additionalDelayMillis: 1500,
  additionalDurationMillis: 300,
  minProgress: 0.1,
  maxProgress: 0.87,
  easing: cubicBezier(0.2, 0, 0, 1),
}

function circularArc(size: number, stroke: number, start: number, sweep: number) {
  if (sweep <= 0) return ""
  const r = Math.max(0, (size - stroke) / 2)
  const center = size / 2
  const end = start + sweep * Math.PI * 2
  return `M${center + r * Math.cos(start)} ${center + r * Math.sin(start)}A${r} ${r} 0 ${sweep > 0.5 ? 1 : 0} 1 ${center + r * Math.cos(end)} ${center + r * Math.sin(end)}`
}

function CircularIndeterminateIndicator({ shape, size, strokeWidth, ariaLabel }: {
  shape: "flat" | "wavy"
  size: number
  strokeWidth: number
  ariaLabel: string
}) {
  const rotationRef = useRef<SVGGElement>(null)
  const progressRef = useRef<SVGPathElement>(null)
  const trackRef = useRef<SVGPathElement>(null)
  const sample = useMemo(() => {
    const cache = new CircularWavyPathCache()
    const vertices = circularWavyVertexCount(size, 40, strokeWidth)
    const travel = waveTravelMillis(40, 40, 1, vertices)
    return (elapsed: number) => {
      const frame = circularIndeterminateFrame(elapsed, circularTiming)
      if (shape === "wavy") {
        const paths = cache.paths({ size, wavelength: 40, start: 0, end: frame.sweep,
          amplitude: 1, waveOffset: waveOffset(elapsed, travel), motion: true,
          gap: 4, stroke: strokeWidth, trackStroke: strokeWidth, round: true }, false)
        return { rotation: frame.rotation + 90,
          progress: toSvgPathData(paths.progress), track: toSvgPathData(paths.track) }
      }
      const gap = Math.min(frame.sweep, (4 + strokeWidth) / (Math.PI * size))
      return { rotation: frame.rotation,
        progress: circularArc(size, strokeWidth, 0, frame.sweep),
        track: circularArc(size, strokeWidth, (frame.sweep + gap) * Math.PI * 2, 1 - frame.sweep - gap * 2) }
    }
  }, [shape, size, strokeWidth])
  const initial = useMemo(() => sample(0), [sample])
  useEffect(() => {
    const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)")
    let animationFrame = 0
    let startedAt = performance.now()
    const draw = (elapsed: number) => {
      const frame = sample(elapsed)
      rotationRef.current?.setAttribute("transform", `rotate(${frame.rotation} ${size / 2} ${size / 2})`)
      progressRef.current?.setAttribute("d", frame.progress)
      trackRef.current?.setAttribute("d", frame.track)
    }
    const tick = (now: number) => {
      draw(now - startedAt)
      animationFrame = requestAnimationFrame(tick)
    }
    const start = () => {
      cancelAnimationFrame(animationFrame)
      startedAt = performance.now()
      draw(0)
      if (!reducedMotion.matches) animationFrame = requestAnimationFrame(tick)
    }
    start()
    reducedMotion.addEventListener("change", start)
    return () => {
      cancelAnimationFrame(animationFrame)
      reducedMotion.removeEventListener("change", start)
    }
  }, [sample, size])
  return (
    <svg viewBox={`0 0 ${size} ${size}`} role="progressbar" aria-label={ariaLabel} className="block size-full" focusable="false">
      <g ref={rotationRef} transform={`rotate(${initial.rotation} ${size / 2} ${size / 2})`} fill="none" strokeWidth={strokeWidth} strokeLinecap="round">
        <path ref={trackRef} d={initial.track} stroke="var(--mtrl-sys-color-secondary-container)" />
        <path ref={progressRef} d={initial.progress} stroke="var(--mtrl-sys-color-primary)" />
      </g>
    </svg>
  )
}

type ProgressIndicatorProps = Omit<ComponentProps<"span">, "children" | "color"> & {
  tone?: IenTone
  /** 원형 또는 선형 진행 표시입니다. */
  variant?: "circular" | "linear"
  /** Expressive 물결 모양을 사용합니다. */
  shape?: "flat" | "wavy"
  /** 0~1 진행률입니다. 생략하면 불확정 애니메이션을 표시합니다. */
  progress?: number
  /** 원형 표시 크기(px)입니다. */
  size?: number
  /** 선 두께(px)입니다. */
  strokeWidth?: number
  color?: string
  trackColor?: string
}

function ProgressIndicator({
  variant = "circular", shape = "flat", progress, size = shape === "wavy" ? 48 : 40,
  strokeWidth = 4, color, trackColor, tone = "brand",
  className, style, "aria-label": ariaLabelProp, ...props
}: ProgressIndicatorProps) {
  const language = useIenLanguage("ko")
  const ariaLabel = ariaLabelProp ?? (language === "ko" ? "불러오는 중" : "Loading")
  const ref = useRef<HTMLSpanElement>(null)
  useMaterialIndicatorTheme(ref)
  return (
    <span
      {...props}
      ref={ref}
      data-slot="progress-indicator"
      data-ien=""
      data-ien-tone={tone}
      className={cn("inline-flex shrink-0 align-middle", variant === "linear" ? "w-full" : "size-[var(--ien-indicator-size)]", className)}
      style={{
        color,
        "--ien-indicator-size": `${size}px`,
        "--mtrl-sys-color-primary": color ?? "var(--ien-control-color)",
        "--mtrl-sys-color-secondary-container": trackColor ?? "var(--ien-control-weak)",
        "--mtrl-sys-color-primary-container": trackColor ?? "var(--ien-control-weak)",
        ...style,
      } as CSSProperties}
    >
      {variant === "circular" && progress === undefined ? (
        <CircularIndeterminateIndicator shape={shape} size={size} strokeWidth={strokeWidth} ariaLabel={ariaLabel} />
      ) : (
      <MaterialProgress
        key={`${variant}-${size}`}
        variant={variant}
        shape={shape}
        value={progress === undefined ? 0 : Math.max(0, Math.min(1, progress)) * 100}
        indeterminate={progress === undefined}
        thickness={String(strokeWidth)}
        size={size}
        aria-label={ariaLabel}
        className={cn("w-full", variant === "circular" && "h-full [&::part(progress)]:h-full [&::part(progress)]:w-full [&::part(canvas)]:h-full! [&::part(canvas)]:w-full!")}
      />
      )}
    </span>
  )
}

export { ProgressIndicator }
export type { ProgressIndicatorProps }


/** 기존 진행 표시를 크기 단계와 선택적 진행률 레이블로 제공합니다. */
function ProgressBar({ progress = 0, barSize = "normal", showLabel = false, ...props }: Omit<ProgressIndicatorProps, "variant" | "size"> & { barSize?: "small" | "normal" | "large"; showLabel?: boolean }) {
  const safe = Math.max(0, Math.min(1, progress))
  return <span className="flex w-full items-center gap-3"><ProgressIndicator {...props} variant="linear" progress={safe} style={{ height: { small: 4, normal: 8, large: 12 }[barSize], ...props.style }} />{showLabel && <span className="text-[length:var(--ien-font-size-label2)] tabular-nums">{Math.round(safe * 100)}%</span>}</span>
}
function Loader({ label, loaderSize = "medium", ...props }: ProgressIndicatorProps & { label?: string; loaderSize?: "small" | "medium" | "large" }) {
  return <span className="inline-flex flex-col items-center gap-3"><ProgressIndicator {...props} size={props.size ?? { small: 20, medium: 32, large: 48 }[loaderSize]} aria-label={label ?? props["aria-label"]} />{label && <span className="text-[length:var(--ien-font-size-body2)]">{label}</span>}</span>
}
export { ProgressBar, Loader }
