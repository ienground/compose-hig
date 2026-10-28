"use client"
import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenTheme } from "@/shared/lib/ien-theme-context"

import * as React from "react"
import { cn } from "cn"
import { Label as LabelPrimitive } from "radix-ui"

function Label({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof LabelPrimitive.Root> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <IenThemeContext.Provider value={ienTheme}>
      <LabelPrimitive.Root
        data-slot="label"
        data-ien-typography="label1"
        data-ien=""
        data-ien-tone={ienTheme.tone}
        data-ien-gradient={ienTheme.useGradient}
        className={cn(
          "gap-2 text-sm leading-none font-medium group-data-[disabled=true]:opacity-50 peer-disabled:opacity-(--ien-state-disabled-alpha) flex items-center select-none group-data-[disabled=true]:pointer-events-none peer-disabled:cursor-not-allowed",
          className,
        )}
        {...props}
      />
    </IenThemeContext.Provider>
  )
}

export { Label }
