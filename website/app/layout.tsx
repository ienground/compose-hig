import type { Metadata } from "next";
import type { ReactNode } from "react";
import { SiteShell } from "@/widgets/site-shell";
import "@/app/styles/registry.css";
import "@/app/styles/index.css";

export const metadata: Metadata = {
  title: { default: "Compose HIG · Playground", template: "%s · Compose HIG" },
  description:
    "Compose HIG 컴포넌트를 직접 조작하고 Kotlin 코드와 iPhone Duo 샘플을 확인하세요.",
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="ko">
      <body>
        <SiteShell>{children}</SiteShell>
      </body>
    </html>
  );
}
