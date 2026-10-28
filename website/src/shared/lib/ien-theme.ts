export type IenTone =
  | "neutral"
  | "brand"
  | "success"
  | "warning"
  | "danger"
  | "info"

export type IenVariant = "fill" | "weak" | "line" | "ghost"

/** 내장 문구와 접근성 라벨에 사용하는 UI 언어입니다. */
export type IenLanguage = "ko" | "en"

export interface IenToneProps {
  /** CMP의 의미적 색상입니다. 생략하면 상위 컴포넌트의 tone을 따릅니다. */
  tone?: IenTone
  /** 채움 면에 CMP 그라데이션을 적용합니다. 기본값은 true입니다. */
  useGradient?: boolean
}

export function resolveIenTone(
  tone: IenTone | undefined,
  variant?: string | null,
): IenTone | undefined {
  if (tone) return tone
  const name = variant?.split("-")[0]
  if (name === "destructive") return "danger"
  if (name === "secondary" || name === "invert") return "neutral"
  if (name === "primary" || name === "focus") return "brand"
  if (name === "success" || name === "warning" || name === "info") return name
  return undefined
}
