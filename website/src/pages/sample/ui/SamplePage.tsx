"use client";

import { useState } from "react";
import { useTranslation } from "@/shared/i18n";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/shared/ui/tabs";
import { SelectField, previewWidths } from "@/shared/ui/SelectField";
import Image from "next/image";
import { WasmPreview } from "@/shared/ui/WasmPreview";
import { repository, basePath } from "@/shared/config/site";

export default function SamplePage() {
  const { t } = useTranslation();
  const widths = previewWidths.map((option) => ({
    ...option,
    label: t(option.value === "full" ? "전체 너비" : option.label),
  }));
  const [width, setWidth] = useState("390");
  return (
    <>
      <div className="page-heading">
        <p className="eyebrow">{t("샘플")} / COMPOSE MULTIPLATFORM</p>
        <h1>{t("샘플앱")}</h1>
        <p id="description">
          {t(
            "전체 샘플앱을 탐색하고, iPhone Duo에서 실행한 iOS 화면을 확인하세요.",
          )}
        </p>
        <a
          id="source-link"
          href={`${repository}/tree/main/example/composeApp`}
          target="_blank"
          rel="noopener noreferrer"
        >
          {t("샘플앱 소스 보기")} ↗
        </a>
      </div>
      <section aria-label={t("샘플앱과 iPhone Duo")}>
        <Tabs defaultValue="web">
          <TabsList aria-label={t("샘플 보기")}>
            <TabsTrigger value="web">{t("웹 샘플앱")}</TabsTrigger>
            <TabsTrigger value="duo">{t("iPhone Duo · iOS 캡처")}</TabsTrigger>
          </TabsList>
          <TabsContent value="duo">
            <>
              <div className="section-heading">
                <h2>iPhone Duo</h2>
                <span className="tag">{t("iOS 시뮬레이터")}</span>
              </div>
              <p className="duo-description">
                {t(
                  "실제 iOS 샘플앱을 캡처한 화면입니다. 펼친 화면의 탐색 구조와 하단 시트 배치를 확인할 수 있습니다. 웹 샘플은 UIKit 힌지 API를 실행하지 않습니다.",
                )}
              </p>
              <div className="duo-gallery">
                {[
                  ["open", "펼친 화면", "펼친 iPhone Duo의 Compose HIG 샘플앱"],
                  [
                    "closed",
                    "접힌 화면",
                    "접힌 iPhone Duo의 Compose HIG 샘플앱",
                  ],
                  [
                    "sheet",
                    "하단 시트",
                    "펼친 iPhone Duo에서 표시한 하단 시트",
                  ],
                ].map(([name, caption, alt]) => (
                  <figure key={name}>
                    <Image
                      src={`${basePath}/media/compose-hig-duo-${name}.png`}
                      alt={t(alt)}
                      width={1000}
                      height={1000}
                    />
                    <figcaption>{t(caption)}</figcaption>
                  </figure>
                ))}
              </div>
              <figure className="duo-video">
                <video
                  controls
                  playsInline
                  preload="none"
                  poster={`${basePath}/media/compose-hig-duo-open.png`}
                  aria-label={t("iPhone Duo 샘플앱 시연 영상")}
                >
                  <source
                    src={`${basePath}/media/compose-hig-duo-demo.mp4`}
                    type="video/mp4"
                  />
                  {t("이 브라우저는 영상을 재생하지 못합니다.")}
                </video>
                <figcaption>
                  {t("iPhone Duo 시연")} ·{" "}
                  <a href={`${basePath}/media/compose-hig-duo-demo.mp4`}>
                    {t("영상 파일 열기")} ↗
                  </a>
                </figcaption>
              </figure>
            </>
          </TabsContent>
          <TabsContent value="web">
            <>
              <div className="section-heading">
                <h2>{t("샘플앱")}</h2>
                <label>
                  {t("화면 너비")}
                  <SelectField
                    label={t("화면 너비")}
                    value={width}
                    onValueChange={setWidth}
                    options={widths}
                  />
                </label>
              </div>
              <div className="sample-stage">
                <WasmPreview
                  component="sample"
                  title={t("Compose HIG 전체 샘플앱")}
                  width={width}
                />
              </div>
              <p className="preview-caption">
                {t(
                  "앱 안에서 테마, 화면 이동, 컴포넌트 동작을 확인할 수 있습니다.",
                )}
              </p>
            </>
          </TabsContent>
        </Tabs>
      </section>
    </>
  );
}
