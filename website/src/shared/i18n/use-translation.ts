"use client";

import { useIenLanguage } from "@/shared/lib/ien-theme-context";
import { translate } from "./messages";

export function useTranslation() {
  const language = useIenLanguage("en");
  return { language, t: (text: string) => translate(text, language) };
}
