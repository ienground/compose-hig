"use client"
import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenTheme } from "@/shared/lib/ien-theme-context"

import * as React from "react"
import { cn } from "cn"
import { menuSurfaceClassName, menuMotionClassName, menuItemClassName, menuCheckedClassName, menuLabelClassName, menuSeparatorClassName, menuIndicatorClassName } from "@/shared/lib/ien-menu"

import { Select as SelectPrimitive } from "radix-ui"

import { IconPlaceholder } from "@/shared/lib/reui-icons"
import { IconSelector, IconCheck, IconChevronUp, IconChevronDown } from "@tabler/icons-react"

function Select({
  tone: ienTone,
  useGradient: ienUseGradient,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Root> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <IenThemeContext.Provider value={ienTheme}>
      <SelectPrimitive.Root data-slot="select" {...props} />
    </IenThemeContext.Provider>
  )
}

function SelectGroup({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Group> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Group
      data-slot="select-group"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("scroll-my-1", className)}
      {...props}
    />
  )
}

function SelectValue({
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Value>) {
  return <SelectPrimitive.Value data-slot="select-value" {...props} />
}

function SelectTrigger({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  size = "default",
  children,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Trigger> & {
  size?: "sm" | "default"
} & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Trigger
      data-slot="select-trigger"
      data-ien-typography="label1"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      data-size={size}
      className={cn(
        "border-input data-placeholder:text-muted-foreground dark:bg-input/30 dark:hover:bg-input/50 focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 gap-1.5 rounded-lg border bg-transparent py-2 pr-2 pl-2.5 text-sm transition-colors select-none focus-visible:ring-3 aria-invalid:ring-3 data-[size=default]:h-8 data-[size=sm]:h-7 data-[size=sm]:rounded-[min(var(--radius-md),10px)] *:data-[slot=select-value]:flex *:data-[slot=select-value]:gap-1.5 [&_svg:not([class*='size-'])]:size-4 flex w-fit items-center justify-between whitespace-nowrap outline-none disabled:cursor-not-allowed disabled:opacity-(--ien-state-disabled-alpha) *:data-[slot=select-value]:line-clamp-1 *:data-[slot=select-value]:flex *:data-[slot=select-value]:items-center [&_svg]:pointer-events-none [&_svg]:shrink-0",
        className,
      )}
      {...props}
    >
      {children}
      <SelectPrimitive.Icon asChild>
        <IconSelector className="text-muted-foreground size-4 pointer-events-none" />
      </SelectPrimitive.Icon>
    </SelectPrimitive.Trigger>
  )
}

function SelectContent({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  children,
  position = "item-aligned",
  align = "center",
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Content> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Portal>
      <SelectPrimitive.Content
        data-slot="select-content"
        data-ien-typography="body2"
        data-ien=""
        data-ien-tone={ienTheme.tone}
        data-ien-gradient={ienTheme.useGradient}
        data-align-trigger={position === "item-aligned"}
        className={cn(
        menuSurfaceClassName, menuMotionClassName, "min-w-40 max-h-(--radix-select-content-available-height) origin-(--radix-select-content-transform-origin) overflow-hidden data-[align-trigger=true]:animate-none",
        className
        )}
        position={position}
        align={align}
        {...props}
      >
        <SelectScrollUpButton />
        <SelectPrimitive.Viewport
          data-position={position}
          className={cn(
            "p-(--ien-spacing-xs) data-[position=popper]:w-full data-[position=popper]:min-w-(--radix-select-trigger-width)",
            position === "popper" && ""
          )}
        >
          {children}
        </SelectPrimitive.Viewport>
        <SelectScrollDownButton />
      </SelectPrimitive.Content>
    </SelectPrimitive.Portal>
  )
}

function SelectLabel({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Label> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Label
      data-slot="select-label"
      data-ien-typography="caption"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        menuLabelClassName,
        className)}
      {...props}
    />
  )
}

function SelectItem({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  children,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Item> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Item
      data-slot="select-item"
      data-ien-typography="body2"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        menuItemClassName, menuCheckedClassName, "w-full *:[span]:last:flex *:[span]:last:items-center *:[span]:last:gap-(--ien-spacing-xs)",
        className,
      )}
      {...props}
    >
      <span className={menuIndicatorClassName}>
        <SelectPrimitive.ItemIndicator>
          <IconCheck className=" pointer-events-none" />
        </SelectPrimitive.ItemIndicator>
      </span>
      <SelectPrimitive.ItemText>{children}</SelectPrimitive.ItemText>
    </SelectPrimitive.Item>
  )
}

function SelectSeparator({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.Separator> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.Separator
      data-slot="select-separator"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        menuSeparatorClassName,
        className)}
      {...props}
    />
  )
}

function SelectScrollUpButton({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.ScrollUpButton> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.ScrollUpButton
      data-slot="select-scroll-up-button"
      data-ien-typography="label1"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("bg-popover z-10 flex cursor-default items-center justify-center py-1 [&_svg:not([class*='size-'])]:size-4", className)}
      {...props}
    >
      <IconChevronUp
      />
    </SelectPrimitive.ScrollUpButton>
  )
}

function SelectScrollDownButton({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof SelectPrimitive.ScrollDownButton> &
  IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <SelectPrimitive.ScrollDownButton
      data-slot="select-scroll-down-button"
      data-ien-typography="label1"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("bg-popover z-10 flex cursor-default items-center justify-center py-1 [&_svg:not([class*='size-'])]:size-4", className)}
      {...props}
    >
      <IconChevronDown
      />
    </SelectPrimitive.ScrollDownButton>
  )
}

export {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectScrollDownButton,
  SelectScrollUpButton,
  SelectSeparator,
  SelectTrigger,
  SelectValue,
}
