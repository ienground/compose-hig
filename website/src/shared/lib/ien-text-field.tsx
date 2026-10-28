"use client"

import * as React from "react"
import { cn } from "cn"
import { Field, FieldDescription, FieldError, FieldLabel } from "@/shared/ui/field"

export type TextFieldLengthLimit =
  | { mode: "none" }
  | { mode: "block" | "error"; maxLength: number }

export interface TextFieldState {
  enabled?: boolean
  readOnly?: boolean
  status?:
    | { type: "normal" }
    | { type: "error"; message: string }
    | { type: "success"; message?: string }
}

export interface TextFieldOptions {
  label?: string
  labelOption?: "appear" | "sustain"
  help?: string
  supportingText?: string
  hasError?: boolean
  lengthLimit?: TextFieldLengthLimit
  state?: TextFieldState
  onValueChange?: (value: string) => void
}

export function useTextField({
  value, defaultValue, lengthLimit, state, hasError, help, supportingText, id,
  "aria-describedby": describedBy, "aria-invalid": ariaInvalid,
}: TextFieldOptions & {
  value?: string | number | readonly string[]
  defaultValue?: string | number | readonly string[]
  id?: string
  "aria-describedby"?: string
  "aria-invalid"?: React.AriaAttributes["aria-invalid"]
}) {
  const generatedId = React.useId()
  const inputId = id ?? generatedId
  const inputRef = React.useRef<HTMLInputElement | HTMLTextAreaElement | null>(null)
  const [localValue, setLocalValue] = React.useState(() => String(defaultValue ?? ""))
  const [focused, setFocused] = React.useState(false)
  const currentValue = value === undefined ? localValue : String(value)
  const limit = lengthLimit && lengthLimit.mode !== "none"
    ? Math.max(0, Math.trunc(lengthLimit.maxLength)) : undefined
  const exceeded = limit !== undefined && currentValue.length > limit
  const invalid = state?.status?.type === "error" || hasError ||
    (lengthLimit?.mode === "error" && exceeded) ||
    (ariaInvalid !== undefined && ariaInvalid !== false && ariaInvalid !== "false")
  const fallback = help ?? supportingText
  const message = state?.status?.type === "error" ? state.status.message
    : invalid ? fallback
    : state?.status?.type === "success" ? state.status.message ?? fallback : fallback
  const descriptionId = message ? `${inputId}-description` : undefined
  const counterId = limit !== undefined ? `${inputId}-counter` : undefined
  const descriptionIds = [describedBy, descriptionId, counterId].filter(Boolean).join(" ") || undefined

  React.useEffect(() => {
    if (!invalid || !inputRef.current || window.matchMedia("(prefers-reduced-motion: reduce)").matches) return
    const animation = inputRef.current.animate([{ transform: "translateX(-8px)" }, { transform: "translateX(8px)" }, { transform: "translateX(-6px)" }, { transform: "translateX(6px)" }, { transform: "translateX(-3px)" }, { transform: "translateX(3px)" }, { transform: "translateX(0px)" }], { duration: 240 })
    return () => animation.cancel()
  }, [invalid])

  React.useEffect(() => {
    const input = inputRef.current
    const form = input?.form
    if (!input || !form || value !== undefined) return
    const reset = (event: Event) => {
      queueMicrotask(() => {
        if (!event.defaultPrevented) setLocalValue(String(defaultValue ?? ""))
      })
    }
    form.addEventListener("reset", reset)
    return () => form.removeEventListener("reset", reset)
  }, [value, defaultValue])

  return {
    inputId, inputRef, currentValue, focused, setFocused, limit, exceeded,
    invalid, message, descriptionId, counterId, descriptionIds,
    accept(next: string) {
      if (lengthLimit?.mode === "block" && limit !== undefined && next.length > limit) return false
      setLocalValue(next)
      return true
    },
  }
}

export function TextFieldFrame({
  field, label, labelOption = "appear", required, disabled, state, slot, children,
}: TextFieldOptions & {
  field: ReturnType<typeof useTextField>
  required?: boolean
  disabled?: boolean
  slot: "input" | "textarea"
  children: React.ReactNode
}) {
  if (!label && !field.message && field.limit === undefined) return children
  const showLabel = labelOption === "sustain" || field.focused || field.currentValue.length > 0
  return (
    <Field data-invalid={field.invalid || undefined} data-disabled={disabled || state?.enabled === false || undefined} className="gap-(--ien-spacing-xxs)">
      {label && (
        <FieldLabel htmlFor={field.inputId} data-slot={`${slot}-label`} className={cn("text-(length:--ien-font-size-label2) font-normal text-(--ien-text-secondary) transition-opacity duration-150 motion-reduce:transition-none", !showLabel && "opacity-0")}>
          {label}
          {required && <span aria-hidden="true" className="text-destructive">*</span>}
        </FieldLabel>
      )}
      {children}
      {(field.message || field.limit !== undefined) && (
        <div className="flex items-start justify-between gap-(--ien-spacing-xs) px-(--ien-spacing-xs)">
          {field.message && (field.invalid
            ? <FieldError id={field.descriptionId}>{field.message}</FieldError>
            : <FieldDescription id={field.descriptionId} className={state?.status?.type === "success" ? "text-(--ien-success)" : undefined}>{field.message}</FieldDescription>)}
          {field.limit !== undefined && (
            <span id={field.counterId} data-slot={`${slot}-counter`} className={cn("ms-auto shrink-0 text-xs tabular-nums text-muted-foreground", field.exceeded && "text-destructive")}>
              {field.currentValue.length}/{field.limit}
            </span>
          )}
        </div>
      )}
    </Field>
  )
}
