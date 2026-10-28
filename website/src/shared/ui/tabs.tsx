"use client"

import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenLanguage, useIenTheme } from "@/shared/lib/ien-theme-context"
import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "cn"
import { Tabs as TabsPrimitive } from "radix-ui"
import { Badge } from "@/shared/reui/badge"

export type TabSize = "small" | "large"

export interface TabItem {
  text: string
  key?: string | number
  enabled?: boolean
  redBean?: boolean
  ariaLabel?: string
  icon?: React.ReactNode
  selectedIcon?: React.ReactNode
  badge?: number
}

interface TabLayout {
  size: TabSize
  fluid: boolean
  language: "ko" | "en"
  itemGap?: number
  ariaLabel?: string
  onValuesChange?: (values: string[]) => void
  orientation: "horizontal" | "vertical"
}

const TabLayoutContext = React.createContext<TabLayout>({
  size: "large", fluid: false, language: "en", orientation: "horizontal",
})

export type TabsProps = Omit<React.ComponentProps<typeof TabsPrimitive.Root>, "onChange"> & IenToneProps & {
  items?: TabItem[]
  selectedIndex?: number
  defaultSelectedIndex?: number
  onSelectedIndexChange?: (index: number) => void
  onChange?: (index: number, key?: string | number) => void
  size?: TabSize
  fluid?: boolean
  itemGap?: number
  ariaLabel?: string
}

function Tabs({
  tone: ienTone, useGradient: ienUseGradient,
  className, orientation = "horizontal", size = "large", fluid = false,
  itemGap, ariaLabel, items, selectedIndex, defaultSelectedIndex,
  onSelectedIndexChange, onChange, value, defaultValue, onValueChange, children,
  ...props
}: TabsProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const language = useIenLanguage("ko")
  const [registeredValues, setRegisteredValues] = React.useState<string[]>([])
  const [localValue, setLocalValue] = React.useState(defaultValue)
  const onValuesChange = React.useCallback((next: string[]) => {
    setRegisteredValues((previous) => previous.length === next.length && previous.every((value, index) => value === next[index]) ? previous : next)
  }, [])
  const layout = React.useMemo(() => ({ size, fluid, language, itemGap, ariaLabel, orientation, onValuesChange }), [size, fluid, language, itemGap, ariaLabel, orientation, onValuesChange])
  const values = items ? items.map((_, index) => String(index)) : registeredValues
  return (
    <IenThemeContext.Provider value={ienTheme}>
      <TabLayoutContext.Provider value={layout}>
        <TabsPrimitive.Root
          data-slot="tabs" data-ien="" data-ien-tone={ienTheme.tone} data-ien-gradient={ienTheme.useGradient}
          data-size={size} data-fluid={fluid} orientation={orientation}
          value={selectedIndex !== undefined ? values[selectedIndex] ?? "" : value ?? localValue ?? values[defaultSelectedIndex ?? 0] ?? ""}
          onValueChange={(next) => {
            setLocalValue(next)
            onValueChange?.(next)
            const index = values.indexOf(next)
            if (index >= 0) {
              onSelectedIndexChange?.(index)
              onChange?.(index, items?.[index]?.key)
            }
          }}
          className={cn("group/tabs flex min-w-0 w-full gap-2 data-[orientation=horizontal]:flex-col", className)}
          {...props}
        >
          {items && (
            <TabsList aria-label={ariaLabel ?? (language === "ko" ? "탭 목록" : "Tabs")}>
              {items.map((item, index) => (
                <TabsTrigger key={item.key ?? index} value={String(index)} disabled={item.enabled === false}
                  aria-label={item.ariaLabel} badge={item.badge} redBean={item.redBean}
                  icon={item.icon} selectedIcon={item.selectedIcon}>
                  {item.text}
                </TabsTrigger>
              ))}
            </TabsList>
          )}
          {children}
        </TabsPrimitive.Root>
      </TabLayoutContext.Provider>
    </IenThemeContext.Provider>
  )
}

const tabsListVariants = cva(
  "group/tabs-list relative flex min-w-0 w-full items-center bg-transparent p-0 text-(--ien-text-secondary) outline-none",
  {
    variants: { variant: { default: "", line: "" } },
    defaultVariants: { variant: "default" },
  }
)

function TabsList({
  tone: ienTone, useGradient: ienUseGradient, className, variant = "default",
  size, fluid, itemGap, style, children, ref, ...props
}: React.ComponentProps<typeof TabsPrimitive.List> & VariantProps<typeof tabsListVariants> & IenToneProps & {
  size?: TabSize
  fluid?: boolean
  itemGap?: number
}) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const parent = React.useContext(TabLayoutContext)
  const layout = React.useMemo(() => ({ ...parent, size: size ?? parent.size, fluid: fluid ?? parent.fluid, itemGap: itemGap ?? parent.itemGap }), [parent, size, fluid, itemGap])
  const listRef = React.useRef<HTMLDivElement | null>(null)
  const previousActive = React.useRef<HTMLElement | null>(null)
  const [indicator, setIndicator] = React.useState<{ x: number; y: number; width: number; height: number }>()

  React.useLayoutEffect(() => {
    const list = listRef.current
    if (!list) return
    previousActive.current = null
    const measure = () => {
      layout.onValuesChange?.(Array.from(list.querySelectorAll<HTMLElement>('[role="tab"]'), (tab) => tab.dataset.tabValue ?? ""))
      const active = list.querySelector<HTMLElement>('[role="tab"][data-state="active"]')
      if (!active) {
        setIndicator(undefined)
        previousActive.current = null
        return
      }
      const bounds = active.getBoundingClientRect()
      const viewport = list.getBoundingClientRect()
      const vertical = layout.orientation === "vertical"
      const width = vertical ? 2 : Math.max(20, bounds.width - 24)
      const height = vertical ? Math.max(20, bounds.height - 24) : 2
      const next = {
        x: vertical ? list.clientWidth - 2 : bounds.left - viewport.left + list.scrollLeft + (bounds.width - width) / 2,
        y: vertical ? bounds.top - viewport.top + list.scrollTop + (bounds.height - height) / 2 : list.clientHeight - 2,
        width, height,
      }
      setIndicator((previous) => previous && previous.x === next.x && previous.y === next.y && previous.width === next.width && previous.height === next.height ? previous : next)
      if (layout.fluid && previousActive.current !== active) {
        const behavior = window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "instant" : "smooth"
        list.scrollTo(vertical
          ? { top: list.scrollTop + bounds.top - viewport.top + bounds.height / 2 - list.clientHeight / 2, behavior }
          : { left: list.scrollLeft + bounds.left - viewport.left + bounds.width / 2 - list.clientWidth / 2, behavior })
      }
      previousActive.current = active
    }
    const resize = new ResizeObserver(measure)
    const observe = () => {
      resize.disconnect()
      resize.observe(list)
      list.querySelectorAll('[role="tab"]').forEach((tab) => resize.observe(tab))
      measure()
    }
    const mutations = new MutationObserver(observe)
    mutations.observe(list, { subtree: true, childList: true, attributes: true, attributeFilter: ["data-state"] })
    observe()
    return () => { resize.disconnect(); mutations.disconnect() }
  }, [layout])

  return (
    <TabLayoutContext.Provider value={layout}>
      <TabsPrimitive.List
        ref={(node) => {
          listRef.current = node
          if (typeof ref === "function") return ref(node)
          if (ref) ref.current = node
        }}
        data-slot="tabs-list" data-ien="" data-ien-tone={ienTheme.tone} data-ien-gradient={ienTheme.useGradient}
        data-variant={variant} data-size={layout.size} data-fluid={layout.fluid}
        aria-label={layout.ariaLabel ?? (layout.language === "ko" ? "탭 목록" : "Tabs")}
        className={cn(tabsListVariants({ variant }),
          layout.orientation === "vertical" ? "h-fit flex-col" : layout.size === "small" ? "h-10" : "h-12",
          layout.fluid
            ? cn("[scrollbar-width:none] [&::-webkit-scrollbar]:hidden", layout.orientation === "vertical" ? "overflow-x-hidden overflow-y-auto" : "overflow-x-auto overflow-y-hidden")
            : "overflow-hidden",
          className)}
        style={{ gap: layout.itemGap ?? (layout.fluid ? "var(--ien-spacing-xl)" : 0), ...style }}
        {...props}
      >
        {children}
        {indicator && (
          <span aria-hidden="true" data-slot="tabs-indicator"
            className="pointer-events-none absolute top-0 left-0 rounded-full bg-(--ien-text-primary) transition-[transform,width,height] duration-(--ien-motion-spring-duration) ease-(--ien-motion-spring-medium-bouncy) motion-reduce:transition-none"
            style={{ width: indicator.width, height: indicator.height, transform: `translate(${indicator.x}px, ${indicator.y}px)` }} />
        )}
      </TabsPrimitive.List>
    </TabLayoutContext.Provider>
  )
}

function TabsTrigger({
  tone: ienTone, useGradient: ienUseGradient, className, children,
  badge = 0, redBean = false, icon, selectedIcon, ...props
}: React.ComponentProps<typeof TabsPrimitive.Trigger> & IenToneProps & {
  badge?: number
  redBean?: boolean
  icon?: React.ReactNode
  selectedIcon?: React.ReactNode
}) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const { size, fluid, orientation, language } = React.useContext(TabLayoutContext)
  const count = badge !== 0 ? badge : redBean ? -1 : 0
  return (
    <TabsPrimitive.Trigger
      data-slot="tabs-trigger" data-tab-value={props.value} data-ien-typography={size === "small" ? "label2" : "label1"}
      data-ien="" data-ien-tone={ienTheme.tone} data-ien-gradient={ienTheme.useGradient}
      className={cn(
        "group/tab relative inline-flex shrink-0 items-center justify-center gap-(--ien-spacing-xs) rounded-none border-0 bg-transparent font-semibold text-(--ien-text-secondary) data-[state=active]:text-(--ien-text-primary) transition-colors focus-visible:outline-2 focus-visible:outline-offset-[-2px] focus-visible:outline-ring disabled:pointer-events-none disabled:text-(--ien-text-disabled) [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg]:size-4",
        size === "small" ? "h-10 text-[length:var(--ien-font-size-label2)] leading-(--ien-line-height-label2)" : "h-12 text-[length:var(--ien-font-size-label1)] leading-(--ien-line-height-label1)",
        fluid ? cn("flex-none px-(--ien-spacing-xs)", size === "small" ? "min-w-12" : "min-w-14") : "min-w-0 flex-1 px-0",
        orientation === "vertical" && "w-full flex-none",
        className)}
      {...props}
    >
      <span className="inline-flex min-w-0 items-center justify-center gap-(--ien-spacing-xs) transition-transform group-active/tab:scale-[0.94] motion-reduce:transform-none">
        {selectedIcon ? <><span aria-hidden="true" className="inline-flex group-data-[state=active]/tab:hidden">{icon}</span><span aria-hidden="true" className="hidden group-data-[state=active]/tab:inline-flex">{selectedIcon}</span></> : icon && <span aria-hidden="true" className="inline-flex">{icon}</span>}
        <span className="relative min-w-0">
          {children}
          {count < 0 && <span data-slot="tabs-dot" aria-label={language === "ko" ? "새 알림" : "New notification"} className="absolute -top-px -end-[7px] size-[5px] rounded-full [--ien-control-corner-shape:round] [corner-shape:round] bg-(--ien-danger)" />}
        </span>
        {count > 0 && <Badge size="small" variant="fill" tone="danger">{count > 99 ? "99+" : count}</Badge>}
      </span>
    </TabsPrimitive.Trigger>
  )
}

function TabsContent({
  tone: ienTone, useGradient: ienUseGradient, className, ...props
}: React.ComponentProps<typeof TabsPrimitive.Content> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return <TabsPrimitive.Content data-slot="tabs-content" data-ien-typography="body2"
    data-ien="" data-ien-tone={ienTheme.tone} data-ien-gradient={ienTheme.useGradient}
    className={cn("min-w-0 flex-1 text-sm outline-none", className)} {...props} />
}

export { Tabs, TabsList, TabsTrigger, TabsContent, tabsListVariants }
