"use client";

import Link from "next/link";
import Image from "next/image";
import { usePathname } from "next/navigation";
import { useEffect, useState, type ReactNode } from "react";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { IenLanguageProvider } from "@/shared/lib/ien-theme-context";
import type { IenLanguage } from "@/shared/lib/ien-theme";
import { translate } from "@/shared/i18n/messages";
import { catalog } from "@/features/component";
import { repository, libraryVersion } from "@/shared/config/site";

export function SiteShell({ children }: { children: ReactNode }) {
  const pathname = usePathname() ?? "/";
  const [query, setQuery] = useState("");
  const [dark, setDark] = useState(false);
  const [language, setLanguage] = useState<IenLanguage>("en");
  const t = (text: string) => translate(text, language);
  useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);
  useEffect(() => {
    document.documentElement.lang = language;
  }, [language]);
  const matches = catalog.filter((component) =>
    `${component.name} ${component.api} ${component.group} ${t(component.group)} ${component.description} ${t(component.description)}`
      .toLowerCase()
      .includes(query.trim().toLowerCase()),
  );
  const sample = pathname.startsWith("/sample");

  return (
    <IenLanguageProvider language={language}>
      <div
        className={`site${dark ? " dark" : ""}`}
        data-theme={dark ? "dark" : "light"}
      >
        <a className="skip-link" href="#main">
          {t("본문으로 건너뛰기")}
        </a>
        <header className="site-header">
          <Link className="brand" href="/">
            <Image
              className="brand-logo"
              src="/media/compose-hig.svg"
              width={36}
              height={36}
              alt=""
              priority
            />
            <span>Compose HIG</span>
            <span className="brand-version">v{libraryVersion}</span>
          </Link>
          <nav aria-label={t("주요 메뉴")}>
            <Link
              href="/components/button/"
              aria-current={!sample ? "page" : undefined}
            >
              {t("컴포넌트")}
            </Link>
            <Link href="/sample/" aria-current={sample ? "page" : undefined}>
              {t("샘플앱")}
            </Link>
            <a href={repository} target="_blank" rel="noopener noreferrer">
              GitHub ↗
            </a>
            <Button
              variant="ghost"
              size="sm"
              type="button"
              onClick={() => setLanguage(language === "en" ? "ko" : "en")}
              aria-label={
                language === "en" ? "한국어로 전환" : "Switch to English"
              }
            >
              {language === "en" ? "한국어" : "English"}
            </Button>
            <Button
              variant="ghost"
              size="icon"
              type="button"
              onClick={() => setDark(!dark)}
              aria-label={t(dark ? "라이트 모드로 전환" : "다크 모드로 전환")}
            >
              ◐
            </Button>
          </nav>
        </header>
        <div className="layout">
          <aside className="sidebar" aria-label={t("컴포넌트 탐색")}>
            <div className="sidebar-heading">
              <span>{t("컴포넌트")}</span>
              <span>{catalog.length}</span>
            </div>
            <div className="search">
              <Input
                type="search"
                placeholder={t("컴포넌트 검색")}
                aria-label={t("컴포넌트 검색")}
                value={query}
                onChange={(event) => setQuery(event.target.value)}
              />
            </div>
            <nav id="component-nav" aria-label={t("컴포넌트 목록")}>
              {[...new Set(matches.map((component) => component.group))].map(
                (group) => (
                  <section key={group} className="nav-group">
                    <h2>{t(group)}</h2>
                    {matches
                      .filter((component) => component.group === group)
                      .map((component) => (
                        <Link
                          key={component.id}
                          href={`/components/${component.id}/`}
                          aria-current={
                            pathname.replace(/\/$/, "") ===
                              `/components/${component.id}` ||
                            (pathname === "/" && component.id === "button")
                              ? "page"
                              : undefined
                          }
                        >
                          {component.name}
                        </Link>
                      ))}
                  </section>
                ),
              )}
            </nav>
            {matches.length === 0 && <p>{t("검색 결과가 없습니다.")}</p>}
            <Link className="sample-shortcut" href="/sample/">
              <span>{t("샘플앱 & iPhone Duo")}</span>
              <span aria-hidden="true">↗</span>
            </Link>
            <div className="sidebar-footer">Kotlin · Compose Multiplatform</div>
          </aside>
          <main id="main" tabIndex={-1}>
            {children}
            <footer className="page-footer">
              Compose HIG
              <span>
                {t("Apple HIG를 위한 Compose Multiplatform 컴포넌트")}
              </span>
            </footer>
          </main>
        </div>
      </div>
    </IenLanguageProvider>
  );
}
