"use client";

import { useSyncExternalStore } from "react";
import { useIenLanguage } from "@/shared/lib/ien-theme-context";
import { translate } from "./messages";

export function useTranslation() {
  const language = useIenLanguage("en");
  return { language, t: (text: string) => translate(text, language) };
}

const languageEvent = "hig:languagechange";
const languageCookie = "hig-language";

function readLanguage(): "en" | "ko" {
  const requested = new URLSearchParams(window.location.search).get("lang");
  if (requested === "en" || requested === "ko") return requested;
  const saved = document.cookie
    .split("; ")
    .find((cookie) => cookie.startsWith(`${languageCookie}=`))
    ?.split("=")[1];
  return saved === "ko" ? "ko" : "en";
}

function subscribeLanguage(listener: () => void) {
  window.addEventListener(languageEvent, listener);
  window.addEventListener("popstate", listener);
  return () => {
    window.removeEventListener(languageEvent, listener);
    window.removeEventListener("popstate", listener);
  };
}

export function useSiteLanguage() {
  const language = useSyncExternalStore(
    subscribeLanguage,
    readLanguage,
    () => "en" as const,
  );
  const setLanguage = (next: "en" | "ko") => {
    const url = new URL(window.location.href);
    url.searchParams.set("lang", next);
    window.history.replaceState(window.history.state, "", url);
    document.cookie = `${languageCookie}=${next}; Path=/; Max-Age=31536000; SameSite=Lax${window.location.protocol === "https:" ? "; Secure" : ""}`;
    window.dispatchEvent(new Event(languageEvent));
  };
  return [language, setLanguage] as const;
}
