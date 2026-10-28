"use client"

import { resolveIenTone, type IenToneProps } from "@/shared/lib/ien-theme"
import { useIenTheme } from "@/shared/lib/ien-theme-context"
import { cva, type VariantProps } from "class-variance-authority"
import { Slot } from "radix-ui"

import { cn } from "cn"

const smallBadgeSize = "px-[calc(var(--ien-spacing-xxs)+var(--ien-spacing-xxxs))] py-(--ien-spacing-xxxs) text-[length:var(--ien-font-size-caption)] leading-(--ien-line-height-caption) font-normal gap-(--ien-spacing-xxs)"
const mediumBadgeSize = "px-(--ien-spacing-xs) py-[calc(var(--ien-spacing-xxxs)*1.5)] text-[length:var(--ien-font-size-label2)] leading-(--ien-line-height-label2) font-semibold gap-(--ien-spacing-xxs)"
const largeBadgeSize = "px-[calc(var(--ien-spacing-sm)-var(--ien-spacing-xxxs))] py-[calc(var(--ien-spacing-xxs)+var(--ien-spacing-xxxs)/2)] text-[length:var(--ien-font-size-label1)] leading-(--ien-line-height-label1) font-semibold gap-(--ien-spacing-xxs)"

const badgeVariants = cva(
  [
    "relative inline-flex shrink-0 items-center justify-center w-fit font-medium whitespace-nowrap outline-none transition-shadow",
    "focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 focus-visible:ring-offset-background disabled:pointer-events-none disabled:opacity-(--ien-state-disabled-alpha)",
    "[&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*=size-])]:size-3",
  ],
  {
    variants: {
      variant: {
        fill: "ien-fill",
        weak: "ien-weak",
        line: "ien-outline border",
        ghost: "ien-clear",
      },
      size: {
        small: smallBadgeSize,
        medium: mediumBadgeSize,
        large: largeBadgeSize,
        xs: "px-1 py-0.25 text-[0.6rem] leading-none h-4 min-w-4 gap-1",
        sm: smallBadgeSize,
        default: mediumBadgeSize,
        lg: largeBadgeSize,
        xl: "px-2 py-0.75 text-sm h-6 min-w-6 gap-1.5",
      },
      /** CMP와 동일한 캡슐 모서리. `full`은 기존 API 호환용입니다. */
      radius: {
        default: "rounded-full",
        full: "rounded-full",
      },
    },
    defaultVariants: {
      variant: "weak",
      size: "medium",
      radius: "default",
    },
  },
)

interface BadgeProps
  extends React.ComponentProps<"span">,
    VariantProps<typeof badgeVariants> {
  tone?: IenToneProps["tone"]
  useGradient?: boolean

  asChild?: boolean
}

function Badge({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  variant,
  size,
  radius,
  asChild = false,
  ...props
}: BadgeProps & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const Comp = asChild ? Slot.Root : "span"

  return (
    <Comp
      data-slot="badge"
      data-ien-shape="capsule"
      data-ien-typography={size === "small" || size === "sm" || size === "xs" ? "caption" : size === "large" || size === "lg" || size === "xl" ? "label1" : "label2"}
      data-ien=""
      data-ien-tone={resolveIenTone(ienTheme.tone, variant)}
      data-ien-gradient={ienTheme.useGradient}
      data-variant={variant ?? "weak"}
      className={cn(badgeVariants({ variant, size, radius, className }))}
      {...props}
    />
  )
}

export { Badge, badgeVariants, type BadgeProps }
