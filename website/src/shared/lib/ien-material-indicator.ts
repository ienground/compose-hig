"use client"

import { useEffect, type RefObject } from "react"

/** Material의 캔버스 색상 캐시를 CMP 테마 변경에 동기화합니다. */
export function useMaterialIndicatorTheme(ref: RefObject<HTMLElement | null>) {
  useEffect(() => {
    const element = ref.current
    if (!element) return
    const sync = () => {
      const style = getComputedStyle(element)
      const theme = ["--mtrl-sys-color-primary", "--mtrl-sys-color-secondary-container"]
        .map((name) => style.getPropertyValue(name)).join(";")
      if (element.dataset.theme !== theme) element.dataset.theme = theme
    }
    const observer = new MutationObserver(sync)
    for (let parent: HTMLElement | null = element; parent; parent = parent.parentElement) {
      observer.observe(parent, { attributes: true, attributeFilter: ["class", "style", "data-ien-tone"] })
    }
    sync()
    return () => observer.disconnect()
  }, [ref])
}
