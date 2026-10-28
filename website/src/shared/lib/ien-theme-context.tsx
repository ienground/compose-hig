"use client"

import type { ReactNode } from "react"
import { createContext, useContext, useEffect, useMemo } from "react"
import { observeIenContinuousCorners } from "@/shared/lib/ien-continuous-corners"
import type { IenLanguage, IenToneProps } from "@/shared/lib/ien-theme"

export const IenThemeContext = createContext<IenToneProps>({})
const IenLanguageContext = createContext<IenLanguage | undefined>(undefined)

/** CMP 기본 문구와 접근성 라벨의 언어를 하위 컴포넌트에 제공합니다. */
export function IenLanguageProvider({
  language = "en",
  children,
}: {
  language?: IenLanguage
  children: ReactNode
}) {
  return (
    <IenLanguageContext.Provider value={language}>
      {children}
    </IenLanguageContext.Provider>
  )
}

/** 상위 언어를 읽고, 미지정 시 컴포넌트별 기본 언어를 사용합니다. */
export function useIenLanguage(
  fallback: IenLanguage = "en",
  override?: IenLanguage,
): IenLanguage {
  const inherited = useContext(IenLanguageContext)
  return override ?? inherited ?? fallback
}

export function useIenTheme({ tone, useGradient }: IenToneProps): IenToneProps {
  useEffect(() => observeIenContinuousCorners(), [])
  const parent = useContext(IenThemeContext)
  const resolvedTone = tone ?? parent.tone
  const resolvedGradient = useGradient ?? parent.useGradient
  return useMemo(
    () => ({ tone: resolvedTone, useGradient: resolvedGradient }),
    [resolvedTone, resolvedGradient],
  )
}

export function IenContinuousCorners() {
  useEffect(() => observeIenContinuousCorners(), [])
  return null
}
