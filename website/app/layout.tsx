import type { Metadata } from "next";
import type { ReactNode } from "react";
import { SiteShell } from "@/widgets/site-shell";
import { basePath } from "@/shared/config/site";
import "@/app/styles/registry.css";
import "@/app/styles/index.css";

export const metadata: Metadata = {
  icons: { icon: `${basePath}/media/compose-hig.svg` },
  title: { default: "Compose HIG · Playground", template: "%s · Compose HIG" },
  description:
    "Explore Compose HIG components, Kotlin examples, and the iPhone Duo sample app.",
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>
        <SiteShell>{children}</SiteShell>
      </body>
    </html>
  );
}
