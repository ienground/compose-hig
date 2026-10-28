"use client"
import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenTheme } from "@/shared/lib/ien-theme-context"

import * as React from "react"
import { cn } from "cn"
import { Separator as SeparatorPrimitive } from "radix-ui"

function Separator({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  orientation = "horizontal",
  decorative = true,
  ...props
}: React.ComponentProps<typeof SeparatorPrimitive.Root> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <IenThemeContext.Provider value={ienTheme}>
      <SeparatorPrimitive.Root
        data-slot="separator"
        data-ien=""
        data-ien-tone={ienTheme.tone}
        data-ien-gradient={ienTheme.useGradient}
        decorative={decorative}
        orientation={orientation}
        className={cn(
        "shrink-0 bg-border data-[orientation=horizontal]:h-px data-[orientation=horizontal]:w-full data-[orientation=vertical]:w-px data-[orientation=vertical]:self-stretch",
        className
      )}
        {...props}
      />
    </IenThemeContext.Provider>
  )
}

export { Separator }
