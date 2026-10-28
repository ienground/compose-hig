"use client";

import { useTranslation } from "@/shared/i18n";
import { notFound } from "next/navigation";
import {
  catalog,
  Playground,
  type InitialExamples,
} from "@/features/component";
import { repository } from "@/shared/config/site";

export default function ComponentPage({
  slug,
  initialExamples,
}: {
  slug: string;
  initialExamples?: InitialExamples;
}) {
  const { t } = useTranslation();
  const component = catalog.find((item) => item.id === slug);
  if (!component) notFound();
  const source =
    component.file === "@sample"
      ? "example/composeApp/src/commonMain/kotlin/cupertino/CupertinoWidgetsScreen.kt"
      : `hig/src/commonMain/kotlin/zone/ien/hig/${component.file}`;
  return (
    <>
      <div className="page-heading">
        <p className="eyebrow">
          {t("컴포넌트")} / {t(component.group)}
        </p>
        <h1>{component.name}</h1>
        <p id="description">{t(component.description)}</p>
        <a
          id="source-link"
          href={`${repository}/blob/main/${source}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          {component.api} {t("소스 보기")} ↗
        </a>
      </div>
      <Playground
        key={component.id}
        componentId={component.id}
        initialExamples={initialExamples}
      />
    </>
  );
}
