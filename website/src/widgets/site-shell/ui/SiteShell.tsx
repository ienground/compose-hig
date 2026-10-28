"use client";

import Link from "next/link";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { IenLanguageProvider } from "@/shared/lib/ien-theme-context";
import { usePathname } from "next/navigation";
import { useEffect, useState, type ReactNode } from "react";
import { catalog } from "@/features/component";
import { repository } from "@/shared/config/site";

export function SiteShell({ children }: { children: ReactNode }) {
  const pathname = usePathname() ?? "/";
  const [query, setQuery] = useState("");
  const [dark, setDark] = useState(false);
  useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);
  const matches = catalog.filter((component) =>
    `${component.name} ${component.api} ${component.group} ${component.description}`
      .toLowerCase()
      .includes(query.trim().toLowerCase()),
  );
  const sample = pathname.startsWith("/sample");

  return (
    <IenLanguageProvider language="ko">
      <div
        className={`site${dark ? " dark" : ""}`}
        data-theme={dark ? "dark" : "light"}
      >
        <a className="skip-link" href="#main">
          본문으로 건너뛰기
        </a>
        <header className="site-header">
          <Link className="brand" href="/">
            <span className="brand-mark" aria-hidden="true">
              H
            </span>
            Compose HIG
            <span className="brand-subtitle">컴포넌트 라이브러리</span>
          </Link>
          <nav aria-label="주요 메뉴">
            <Link
              href="/components/button/"
              aria-current={!sample ? "page" : undefined}
            >
              컴포넌트
            </Link>
            <Link href="/sample/" aria-current={sample ? "page" : undefined}>
              샘플앱
            </Link>
            <a href={repository} target="_blank" rel="noopener noreferrer">
              GitHub ↗
            </a>
            <Button
              variant="ghost"
              size="icon"
              type="button"
              onClick={() => setDark(!dark)}
              aria-label={dark ? "라이트 모드로 전환" : "다크 모드로 전환"}
            >
              ◐
            </Button>
          </nav>
        </header>
        <div className="layout">
          <aside className="sidebar" aria-label="컴포넌트 탐색">
            <div className="sidebar-heading">
              <span>컴포넌트</span>
              <span>{catalog.length}</span>
            </div>
            <label className="search">
              <span aria-hidden="true">⌕</span>
              <Input
                type="search"
                placeholder="컴포넌트 검색"
                aria-label="컴포넌트 검색"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
              />
            </label>
            <nav id="component-nav" aria-label="컴포넌트 목록">
              {[...new Set(matches.map((component) => component.group))].map(
                (group) => (
                  <section key={group} className="nav-group">
                    <h2>{group}</h2>
                    {matches
                      .filter((component) => component.group === group)
                      .map((component) => (
                        <Link
                          key={component.id}
                          href={`/components/${component.id}/`}
                          aria-current={
                            pathname.replace(/\/$/, "") ===
                            `/components/${component.id}`
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
            {matches.length === 0 && <p>검색 결과가 없습니다.</p>}
            <Link className="sample-shortcut" href="/sample/">
              <span>샘플앱 & iPhone Duo</span>
              <span aria-hidden="true">↗</span>
            </Link>
            <div className="sidebar-footer">Kotlin · Compose Multiplatform</div>
          </aside>
          <main id="main" tabIndex={-1}>
            {children}
            <footer className="page-footer">
              Compose HIG
              <span>Apple HIG를 위한 Compose Multiplatform 컴포넌트</span>
            </footer>
          </main>
        </div>
      </div>
    </IenLanguageProvider>
  );
}
