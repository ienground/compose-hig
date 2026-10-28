"use client";

import { useTranslation } from "@/shared/i18n";
import { wasmPreview } from "@/shared/config/site";

export function WasmPreview({
  component,
  options = {},
  width = "390",
  resetKey = 0,
  title,
}: {
  component: string;
  options?: Record<string, string | number | boolean>;
  width?: string;
  resetKey?: number;
  title: string;
}) {
  const { language } = useTranslation();
  const fragment = new URLSearchParams(
    Object.entries({ component, ...options, language }).map(([key, value]) => [
      key,
      String(value),
    ]),
  ).toString();
  return (
    <iframe
      key={resetKey}
      title={title}
      data-preview-width={width}
      src={`${wasmPreview}#${fragment}`}
      style={{
        width: width === "full" ? "100%" : `${width}px`,
        maxWidth: width === "820" ? "none" : "100%",
      }}
    />
  );
}
