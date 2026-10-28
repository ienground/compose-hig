"use client"

import type { IenToneProps } from "@/shared/lib/ien-theme"
import * as React from "react"
import { cn } from "cn"
import { Button } from "@/shared/ui/button"
import { IconX, IconEye, IconEyeOff, IconSearch } from "@tabler/icons-react"
import { useIenLanguage } from "@/shared/lib/ien-theme-context"
import { TextFieldFrame, useTextField, type TextFieldOptions } from "@/shared/lib/ien-text-field"

export type InputProps = React.ComponentProps<"input"> & IenToneProps & TextFieldOptions & {
  variant?: "boxed" | "underline" | "plain" | "big" | "hero"
  clearable?: boolean
  passwordToggle?: boolean
  leading?: React.ReactNode
  trailing?: React.ReactNode
  format?: { transform: (value: string) => string; reset?: (value: string) => string }
}

function Input({
  variant = "boxed",
  tone: ienTone,
  useGradient: ienUseGradient,
  className,
  type,
  clearable = false,
  passwordToggle = type === "password",
  leading,
  trailing,
  format,
  label,
  labelOption = "appear",
  help,
  supportingText,
  hasError,
  lengthLimit,
  state,
  onValueChange,
  value,
  defaultValue,
  maxLength,
  onChange,
  onFocus,
  onBlur,
  onCompositionStart,
  onCompositionEnd,
  required,
  disabled,
  readOnly,
  id,
  ref,
  "aria-describedby": describedBy,
  "aria-invalid": ariaInvalid,
  ...props
}: InputProps) {
  const language = useIenLanguage("ko")
  const [passwordVisible, setPasswordVisible] = React.useState(false)
  const [composing, setComposing] = React.useState(false)
  const field = useTextField({ value, defaultValue, lengthLimit, state, hasError, help, supportingText, id, "aria-describedby": describedBy, "aria-invalid": ariaInvalid })
  const blocked = disabled || state?.enabled === false
  const immutable = readOnly || state?.readOnly
  const prefix = leading ?? (type === "search" ? <IconSearch className="size-4" aria-hidden="true" /> : null)
  const canClear = clearable && !blocked && !immutable && field.currentValue.length > 0
  const decorated = prefix != null || trailing != null || clearable || passwordToggle
  const changeValue = (next: string) => {
    const node = field.inputRef.current
    if (!node || !field.accept(next)) return
    const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, "value")?.set
    setter?.call(node, next)
    node.dispatchEvent(new Event("input", { bubbles: true }))
    node.focus()
  }
  const input = (
    <input
      type={type === "password" && passwordVisible ? "text" : type}
      ref={(node) => {
        field.inputRef.current = node
        if (typeof ref === "function") return ref(node)
        if (ref) ref.current = node
      }}
      id={field.inputId}
      value={format ? (composing ? field.currentValue : format.transform(field.currentValue)) : value}
      defaultValue={format ? undefined : defaultValue}
      required={required}
      disabled={disabled || state?.enabled === false}
      readOnly={readOnly || state?.readOnly}
      maxLength={lengthLimit && lengthLimit.mode !== "none" ? undefined : maxLength}
      aria-invalid={field.invalid ? true : ariaInvalid}
      aria-describedby={field.descriptionIds}
      onFocus={(event) => { field.setFocused(true); onFocus?.(event) }}
      onBlur={(event) => { field.setFocused(false); onBlur?.(event) }}
      onChange={(event) => {
        const nextValue = format?.reset && !composing ? format.reset(event.currentTarget.value) : event.currentTarget.value
        if (!field.accept(nextValue)) {
          event.currentTarget.value = field.currentValue
          return
        }
        onChange?.(event)
        onValueChange?.(nextValue)
      }}
      onCompositionStart={(event) => { setComposing(true); onCompositionStart?.(event) }}
      onCompositionEnd={(event) => {
        setComposing(false)
        if (format?.reset) {
          const next = format.reset(event.currentTarget.value)
          if (field.accept(next)) onValueChange?.(next)
        }
        onCompositionEnd?.(event)
      }}
      data-slot="input"
      data-variant={variant}
      data-ien=""
      data-ien-tone={ienTone}
      data-ien-gradient={ienUseGradient}
      className={cn(
        "dark:bg-input/30 border-input focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 disabled:bg-input/50 dark:disabled:bg-input/80 h-(--ien-button-height-default) rounded-lg border bg-transparent px-2.5 py-1 text-[length:var(--ien-font-size-label1)] leading-(--ien-line-height-label1) font-normal transition-colors file:h-6 file:text-sm file:font-medium focus-visible:ring-3 aria-invalid:ring-3 w-full min-w-0 outline-none file:inline-flex file:border-0 file:bg-transparent file:text-foreground placeholder:text-muted-foreground disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-(--ien-state-disabled-alpha)",
        variant === "underline"
          ? "rounded-none border-x-0 border-t-0 bg-transparent px-0 dark:bg-transparent"
          : variant === "plain"
            ? "border-transparent bg-transparent shadow-none focus-visible:border-transparent dark:bg-transparent"
            : "",
        !field.invalid && state?.status?.type === "success" && "border-(--ien-success) focus-visible:border-(--ien-success)",
        variant === "big" && "h-auto min-h-17 px-5 py-4 text-[length:var(--ien-font-size-title2)] leading-(--ien-line-height-title2)",
        variant === "hero" && "h-auto min-h-21 border-transparent px-0 py-5 text-[length:var(--ien-font-size-display)] leading-(--ien-line-height-display) font-bold",
        prefix != null && "ps-9",
        (clearable || passwordToggle || trailing != null) && "pe-24",
        className,
      )}
      {...props}
    />
  )
  return (
    <TextFieldFrame field={field} label={label} labelOption={labelOption} required={required} disabled={disabled} state={state} slot="input">
      {decorated ? <div className="relative w-full">
        {prefix != null && <span className="pointer-events-none absolute inset-y-0 start-2.5 z-10 flex items-center text-(--ien-text-secondary)">{prefix}</span>}
        {input}
        <span className="absolute inset-y-0 end-2 flex items-center gap-1">
          {canClear && <Button type="button" variant="ghost" tone="neutral" size="icon-sm" aria-label={language === "ko" ? "입력 지우기" : "Clear input"} onClick={() => changeValue("")}><IconX /></Button>}
          {passwordToggle && <Button type="button" variant="ghost" tone="neutral" size="icon-sm" disabled={blocked} aria-pressed={passwordVisible} aria-label={language === "ko" ? (passwordVisible ? "비밀번호 숨기기" : "비밀번호 표시") : (passwordVisible ? "Hide password" : "Show password")} onClick={() => setPasswordVisible((previous) => !previous)}>{passwordVisible ? <IconEyeOff /> : <IconEye />}</Button>}
          {trailing}
        </span>
      </div> : input}
    </TextFieldFrame>
  )
}

export { Input }
