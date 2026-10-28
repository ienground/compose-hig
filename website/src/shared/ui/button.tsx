"use client"

import { ProgressIndicator } from "@/shared/ui/progress-indicator"
import { IenContinuousCorners } from "@/shared/lib/ien-theme-context"
import { resolveIenTone, type IenToneProps } from "@/shared/lib/ien-theme"
import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "cn"
import { Slot } from "radix-ui"

const buttonVariants = cva(
  "focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 rounded-lg border bg-clip-border text-[length:var(--ien-font-size-label1)] leading-(--ien-line-height-label1) font-medium focus-visible:ring-3 aria-invalid:ring-3 [&_svg:not([class*='size-'])]:size-4 group/button inline-flex shrink-0 items-center justify-center whitespace-nowrap transition-all outline-none select-none disabled:pointer-events-none [&_svg]:pointer-events-none [&_svg]:shrink-0",
  {
    variants: {
      variant: {
        fill: "ien-fill",
        weak: "ien-weak",
        line: "ien-outline",
        ghost: "ien-clear",
      },
      size: {
        default: "h-(--ien-button-height-default) gap-1.5 px-2.5",
        xs: "h-(--ien-button-height-xs) gap-1 px-2 text-[length:var(--ien-font-size-caption)] [&_svg:not([class*=size-])]:size-3",
        sm: "h-(--ien-button-height-sm) gap-1 px-2.5 text-[length:var(--ien-font-size-label2)] [&_svg:not([class*=size-])]:size-3.5",
        md: "h-(--ien-button-height-md) gap-1.5 px-2.5",
        lg: "h-(--ien-button-height-lg) gap-1.5 px-3",
        icon: "size-8",
        "icon-xs": "size-6 [&_svg:not([class*=size-])]:size-3",
        "icon-sm": "size-7 [&_svg:not([class*=size-])]:size-3.5",
        "icon-md": "size-8",
        "icon-lg": "size-9",
      },
    },
    defaultVariants: {
      variant: "fill",
      size: "default",
    },
  },
)

function Button({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  variant = "fill",
  size = "default",
  asChild = false,
  display = "inline",
  shape = "rounded",
  loading = false,
  disabled,
  children,
  onClickCapture,
  onKeyDownCapture,
  ...props
}: React.ComponentProps<"button"> &
  VariantProps<typeof buttonVariants> & {
    asChild?: boolean
    display?: "inline" | "block" | "full"
    shape?: "rounded" | "capsule"
    /** 아이콘이 있으면 아이콘만, 없으면 콘텐츠를 로더로 전환하고 입력을 막습니다. */
    loading?: boolean
  } & IenToneProps) {
  const Comp = asChild ? Slot.Root : "button"
  const blocked = disabled || loading
  const spinnerRef = React.useRef<HTMLSpanElement>(null)
  const [showSpinner, setShowSpinner] = React.useState(loading)
  React.useEffect(() => {
    if (loading) {
      setShowSpinner(true)
      return
    }
    if (!showSpinner || !spinnerRef.current) return
    const duration = Number.parseFloat(getComputedStyle(spinnerRef.current).transitionDuration) * 1000
    const timeout = window.setTimeout(() => setShowSpinner(false), duration || 0)
    return () => window.clearTimeout(timeout)
  }, [loading, showSpinner])
  const transitionClassName = "transition-[opacity,transform] duration-(--ien-motion-fast) ease-(--ien-motion-standard-easing) motion-reduce:transition-none"
  const spinner = (inline = false) => (
    <span
      ref={spinnerRef}
      data-slot="button-spinner"
      aria-hidden="true"
      className={cn("pointer-events-none absolute inset-0 inline-flex items-center justify-center", transitionClassName, !loading && "scale-80 opacity-0")}
    >
      {(loading || showSpinner) && (
        <ProgressIndicator size={20} strokeWidth={2} color="currentColor" trackColor="transparent"
          style={inline ? { width: "100%", height: "100%" } : undefined} />
      )}
    </span>
  )
  const child = asChild ? React.Children.only(children) : null
  const childElement = React.isValidElement<React.HTMLProps<HTMLElement>>(child) ? child : null
  let hasIcon = false
  const replaceIcon = (value: React.ReactNode): React.ReactNode => React.Children.map(value, (node) => {
    if (!React.isValidElement<{ children?: React.ReactNode; "data-icon"?: string }>(node)) return node
    if (node.type === React.Fragment) {
      return React.cloneElement(node, {}, replaceIcon(node.props.children))
    }
    if (hasIcon || (node.type !== "svg" && !node.props["data-icon"])) return node
    hasIcon = true
    return (
      <span data-slot="button-icon" className="relative inline-flex shrink-0">
        <span className={cn("inline-flex", transitionClassName, loading && "scale-80 opacity-0")}>{node}</span>
        {spinner(true)}
      </span>
    )
  })
  const iconContent = replaceIcon(childElement ? childElement.props.children : children)
  const content = (
    <span
      data-slot="button-content"
      className={cn("inline-flex items-center justify-center gap-[inherit]", transitionClassName, loading && !hasIcon && "scale-80 opacity-0")}
    >
      {iconContent}
    </span>
  )
  const blockInteraction = (event: React.SyntheticEvent) => {
    event.preventDefault()
    event.stopPropagation()
  }
  const slottedContent = childElement
    ? React.cloneElement(childElement, blocked ? {
        href: undefined,
        tabIndex: -1,
        onClickCapture: blockInteraction,
        onAuxClickCapture: blockInteraction,
        onKeyDownCapture: (event) => {
          if (event.key === "Enter" || event.key === " ") blockInteraction(event)
          else childElement.props.onKeyDownCapture?.(event)
        },
      } : {}, content)
    : children

  return (
    <>
      <IenContinuousCorners />
      <Comp
        {...props}
        data-slot="button"
        data-ien-typography="label1"
        data-ien=""
        data-ien-tone={resolveIenTone(ienTone, variant)}
        data-ien-gradient={ienUseGradient}
        data-variant={variant}
        data-size={size}
        data-display={display}
        data-ien-shape={shape === "capsule" && display !== "full" ? "capsule" : undefined}
        data-loading={loading || undefined}
        data-ien-disabled={disabled || undefined}
        aria-busy={loading || props["aria-busy"]}
        aria-disabled={blocked || props["aria-disabled"]}
        disabled={blocked}
        className={cn(buttonVariants({ variant, size, className: cn("relative", display !== "inline" && "w-full", shape === "capsule" && "rounded-full", display === "full" && "rounded-none", className) }))}
        onClickCapture={(event) => {
          if (blocked) {
            event.preventDefault()
            event.stopPropagation()
            return
          }
          onClickCapture?.(event)
        }}
        onKeyDownCapture={(event) => {
          if (blocked && (event.key === "Enter" || event.key === " ")) {
            event.preventDefault()
            event.stopPropagation()
            return
          }
          onKeyDownCapture?.(event)
        }}
      >
        {asChild ? <Slot.Slottable>{slottedContent}</Slot.Slottable> : content}
        {!hasIcon && spinner()}
      </Comp>
    </>
  )
}

export { Button, buttonVariants }
