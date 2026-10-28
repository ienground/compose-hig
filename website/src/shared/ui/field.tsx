"use client"
import type { IenToneProps } from "@/shared/lib/ien-theme"
import { IenThemeContext, useIenTheme } from "@/shared/lib/ien-theme-context"

import { useMemo } from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "cn"

import { Label } from "@/shared/ui/label"
import { Separator } from "@/shared/ui/separator"

function FieldSet({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<"fieldset"> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <fieldset
      data-slot="field-set"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("gap-4 has-[>[data-slot=checkbox-group]]:gap-3 has-[>[data-slot=radio-group]]:gap-3 flex flex-col", className)}
      {...props}
    />
  )
}

function FieldLegend({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  variant = "legend",
  ...props
}: React.ComponentProps<"legend"> & { variant?: "legend" | "label" } & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <legend
      data-slot="field-legend"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      data-variant={variant}
      className={cn("mb-1.5 font-medium data-[variant=label]:text-sm data-[variant=legend]:text-base", className)}
      {...props}
    />
  )
}

function FieldGroup({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<"div"> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <div
      data-slot="field-group"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        "gap-5 data-[slot=checkbox-group]:gap-3 *:data-[slot=field-group]:gap-4 group/field-group @container/field-group flex w-full flex-col",
        className
      )}
      {...props}
    />
  )
}

const fieldVariants = cva("data-[invalid=true]:text-destructive gap-2 group/field flex w-full", {
  variants: {
    orientation: {
      vertical:
        " flex-col *:w-full [&>.sr-only]:w-auto",
      horizontal:
        " flex-row items-center has-[>[data-slot=field-content]]:items-start *:data-[slot=field-label]:flex-auto has-[>[data-slot=field-content]]:[&>[role=checkbox],[role=radio]]:mt-px",
      responsive:
        " flex-col *:w-full @md/field-group:flex-row @md/field-group:items-center @md/field-group:*:w-auto @md/field-group:has-[>[data-slot=field-content]]:items-start @md/field-group:*:data-[slot=field-label]:flex-auto [&>.sr-only]:w-auto @md/field-group:has-[>[data-slot=field-content]]:[&>[role=checkbox],[role=radio]]:mt-px",
    },
  },
  defaultVariants: {
    orientation: "vertical",
  },
})

function Field({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  orientation = "vertical",
  ...props
}: React.ComponentProps<"div"> &
  VariantProps<typeof fieldVariants> &
  IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <IenThemeContext.Provider value={ienTheme}>
      <div
        role="group"
        data-slot="field"
        data-ien=""
        data-ien-tone={ienTheme.tone}
        data-ien-gradient={ienTheme.useGradient}
        data-orientation={orientation}
        className={cn(fieldVariants({ orientation }), className)}
        {...props}
      />
    </IenThemeContext.Provider>
  )
}

function FieldContent({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<"div"> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <div
      data-slot="field-content"
      data-ien-typography="body2"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        "gap-0.5 group/field-content flex flex-1 flex-col leading-snug",
        className
      )}
      {...props}
    />
  )
}

function FieldLabel({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<typeof Label> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <Label
      data-slot="field-label"
      data-ien-typography="label1"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        "has-data-[state=checked]:bg-primary/5 has-data-[state=checked]:border-primary/30 dark:has-data-[state=checked]:border-primary/20 dark:has-data-[state=checked]:bg-primary/10 gap-2 leading-snug group-data-[disabled=true]/field:opacity-50 has-[>[data-slot=field]]:rounded-lg has-[>[data-slot=field]]:border has-[>[data-slot=field]]:not-has-[:disabled,[data-disabled]]:hover:bg-muted/50 has-[>[data-slot=field]]:has-[:focus-visible]:border-ring has-[>[data-slot=field]]:has-[:focus-visible]:ring-ring/50 has-[>[data-slot=field]]:has-[:focus-visible]:ring-3 *:data-[slot=field]:p-2.5 group/field-label peer/field-label flex w-fit",
        "has-[>[data-slot=field]]:w-full has-[>[data-slot=field]]:flex-col",
        className
      )}
      {...props}
    />
  )
}

function FieldTitle({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<"div"> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <div
      data-slot="field-label"
      data-ien-typography="label1"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("gap-2 leading-snug text-sm font-medium group-data-[disabled=true]/field:opacity-50 flex w-fit items-center", className)}
      {...props}
    />
  )
}

function FieldDescription({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  ...props
}: React.ComponentProps<"p"> & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <p
      data-slot="field-description"
      data-ien-typography="body2"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn(
        "text-muted-foreground text-left text-sm [[data-variant=legend]+&]:-mt-1.5 leading-normal font-normal group-has-data-horizontal/field:text-balance",
        "last:mt-0 nth-last-2:-mt-1",
        "[&>a]:underline [&>a]:underline-offset-4 [&>a:hover]:text-primary",
        className
      )}
      {...props}
    />
  )
}

function FieldSeparator({
  tone: ienTone,
  useGradient: ienUseGradient,
  children,
  className,
  ...props
}: React.ComponentProps<"div"> & {
  children?: React.ReactNode
} & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  return (
    <div
      data-slot="field-separator"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      data-content={!!children}
      className={cn("-my-2 h-5 text-sm group-data-[variant=outline]/field-group:-mb-2 relative", className)}
      {...props}
    >
      <Separator className="absolute inset-0 top-1/2" />
      {children && (
        <span
          className="text-muted-foreground px-2 relative mx-auto block w-fit bg-background"
          data-slot="field-separator-content"
          data-ien-typography="body2"
          data-ien=""
          data-ien-tone={ienTheme.tone}
          data-ien-gradient={ienTheme.useGradient}
        >
          {children}
        </span>
      )}
    </div>
  )
}

function FieldError({
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  children,
  errors,
  ...props
}: React.ComponentProps<"div"> & {
  errors?: Array<{ message?: string } | undefined>
} & IenToneProps) {
  const ienTheme = useIenTheme({ tone: ienTone, useGradient: ienUseGradient })
  const content = useMemo(() => {
    if (children) {
      return children
    }

    if (!errors?.length) {
      return null
    }

    const uniqueErrors = [
      ...new Map(errors.map((error) => [error?.message, error])).values(),
    ]

    if (uniqueErrors?.length == 1) {
      return uniqueErrors[0]?.message
    }

    return (
      <ul className="ml-4 flex list-disc flex-col gap-1">
        {uniqueErrors.map(
          (error, index) =>
            error?.message && <li key={index}>{error.message}</li>
        )}
      </ul>
    )
  }, [children, errors])

  if (!content) {
    return null
  }

  return (
    <div
      role="alert"
      data-slot="field-error"
      data-ien=""
      data-ien-tone={ienTheme.tone}
      data-ien-gradient={ienTheme.useGradient}
      className={cn("text-destructive text-sm font-normal", className)}
      {...props}
    >
      {content}
    </div>
  )
}

export {
  Field,
  FieldLabel,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLegend,
  FieldSeparator,
  FieldSet,
  FieldContent,
  FieldTitle,
}
