import { notFound } from "next/navigation";
import { catalog, Playground } from "@/features/component";
import { repository } from "@/shared/config/site";

export default function ComponentPage({ slug }: { slug: string }) {
  const component = catalog.find((item) => item.id === slug);
  if (!component) notFound();
  const source =
    component.file === "@sample"
      ? "example/composeApp/src/commonMain/kotlin/cupertino/CupertinoWidgetsScreen.kt"
      : `hig/src/commonMain/kotlin/zone/ien/hig/${component.file}`;
  return (
    <>
      <div className="page-heading">
        <p className="eyebrow">컴포넌트 / {component.group}</p>
        <h1>{component.name}</h1>
        <p id="description">{component.description}</p>
        <a
          id="source-link"
          href={`${repository}/blob/main/${source}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          {component.api} 소스 보기 ↗
        </a>
      </div>
      <Playground key={component.id} componentId={component.id} />
    </>
  );
}
