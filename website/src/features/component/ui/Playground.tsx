"use client";

import { useState } from "react";
import { useTranslation } from "@/shared/i18n";
import {
  CodeBlock,
  CodeBlockHeader,
  CodeBlockTitle,
  CodeBlockLanguage,
  CodeBlockCopyButton,
} from "@/shared/reui/code-block/code-block";
import { Button } from "@/shared/ui/button";
import { Checkbox } from "@/shared/ui/checkbox";
import { Input } from "@/shared/ui/input";
import { Label } from "@/shared/ui/label";
import { Separator } from "@/shared/ui/separator";
import { Slider } from "@/shared/ui/slider";
import { SelectField, previewWidths } from "@/shared/ui/SelectField";

import {
  catalog,
  hasBackdrop,
  type Control,
  type Props,
} from "../model/catalog";
import { WasmPreview } from "@/shared/ui/WasmPreview";
import { createCode, type InitialExamples } from "../model/code";

export function Playground({
  componentId,
  initialExamples,
}: {
  componentId: string;
  initialExamples?: InitialExamples;
}) {
  const { t, language } = useTranslation();
  const component = catalog.find((item) => item.id === componentId)!;
  const defaults = () =>
    Object.fromEntries(
      component.controls.map((control) => [control.key, control.value]),
    );
  const [props, setProps] = useState<Props>(defaults);
  const [dark, setDark] = useState(false);
  const [accent, setAccent] = useState("blue");
  const [width, setWidth] = useState("390");
  const [resetKey, setResetKey] = useState(0);
  const [status, setStatus] = useState("");
  const localizedProps = Object.fromEntries(
    Object.entries(props).map(([key, value]) => {
      const control = component.controls.find((control) => control.key === key);
      return [
        key,
        control?.type === "text" &&
        value === control.value &&
        typeof value === "string"
          ? t(value)
          : value,
      ];
    }),
  );
  const widths = previewWidths.map((option) => ({
    ...option,
    label: t(option.value === "full" ? "전체 너비" : option.label),
  }));
  const code = createCode(component, localizedProps, dark, accent, language);
  const renderControl = (
    control: Control,
    current: Props[string],
    change: (value: Props[string]) => void,
  ) => {
    const id = `control-${control.key}`;
    return (
      <Label className="control" key={control.key} htmlFor={id}>
        <span>
          {t(control.label)}
          {control.type === "range" && (
            <output className="control-output" htmlFor={id}>
              {current}
            </output>
          )}
        </span>
        {control.type === "select" ? (
          <SelectField
            id={id}
            label={t(control.label)}
            value={String(current)}
            onValueChange={change}
            options={(control.options ?? []).map((value) => ({
              value,
              label: value,
            }))}
          />
        ) : control.type === "checkbox" ? (
          <Checkbox
            id={id}
            checked={Boolean(current)}
            onCheckedChange={(value) => change(value === true)}
          />
        ) : control.type === "range" ? (
          <Slider
            id={id}
            aria-label={t(control.label)}
            thumbLabels={[t(control.label)]}
            value={[Number(current)]}
            min={0}
            max={control.max}
            step={control.step}
            onValueChange={(values) => change(values[0])}
          />
        ) : (
          <Input
            id={id}
            value={String(current)}
            onChange={(event) => change(event.target.value)}
          />
        )}
      </Label>
    );
  };

  return (
    <section aria-label={`${component.name} Playground`}>
      <div className="section-heading">
        <h2>Playground</h2>
        <span className="tag">{t("실제 Compose · Wasm")}</span>
      </div>
      <div className="playground">
        <div className="preview-column">
          <div className="preview-toolbar">
            <span className="preview-label">{t("미리보기")}</span>
            <label>
              {t("너비")}
              <SelectField
                label={t("너비")}
                value={width}
                onValueChange={setWidth}
                options={widths}
              />
            </label>
            <Button
              variant="ghost"
              size="sm"
              type="button"
              onClick={() => {
                setProps(defaults());
                setResetKey((key) => key + 1);
                setStatus(t("컴포넌트 상태를 초기화했습니다."));
              }}
            >
              {t("초기화")} ↺
            </Button>
          </div>
          <div className="preview-stage">
            <WasmPreview
              component={component.id}
              options={{
                ...localizedProps,
                dark,
                accent,
                background: hasBackdrop(component) ? "image" : "plain",
              }}
              width={width}
              resetKey={resetKey}
              title={`${component.name} ${t("실시간 미리보기")}`}
            />
          </div>
          <p className="preview-caption">
            {t("미리보기 안에서 직접 클릭하거나 드래그해 보세요.")}
          </p>
        </div>
        <form
          id="controls"
          aria-label={t("컴포넌트 속성")}
          onSubmit={(event) => event.preventDefault()}
        >
          <h3>{t("속성")}</h3>
          {component.controls.map((control) =>
            renderControl(control, localizedProps[control.key], (value) =>
              setProps((previous) => ({ ...previous, [control.key]: value })),
            ),
          )}
          <Separator className="control-divider" />
          {renderControl(
            { key: "dark", label: "다크 모드", type: "checkbox", value: false },
            dark,
            (value) => setDark(Boolean(value)),
          )}
          {renderControl(
            {
              key: "accent",
              label: "강조색",
              type: "select",
              options: ["blue", "green", "orange", "pink"],
              value: "blue",
            },
            accent,
            (value) => setAccent(String(value)),
          )}
        </form>
      </div>
      <section className="code-section" aria-labelledby="code-heading">
        <div className="section-heading">
          <h2 id="code-heading">{t("사용 예제")}</h2>
        </div>
        <p>{t("현재 Playground 설정에 맞춘 Kotlin 코드입니다.")}</p>
        <CodeBlock
          code={code}
          language="kotlin"
          lines={
            initialExamples?.[language].code === code
              ? initialExamples[language].lines
              : undefined
          }
          showLineNumbers
        >
          <CodeBlockHeader>
            <CodeBlockTitle>Example.kt</CodeBlockTitle>
            <CodeBlockLanguage />
            <CodeBlockCopyButton className="ml-auto" />
          </CodeBlockHeader>
        </CodeBlock>
        <p className="code-note">
          {t(
            "예제는 CupertinoTheme 안에서 실행합니다. backdrop을 사용하는 컴포넌트는 배경 콘텐츠에 layerBackdrop을 연결하세요.",
          )}
        </p>
        {hasBackdrop(component) && (
          <p className="code-note">
            {t(
              "배경 이미지는 샘플앱의 glass_landscape 리소스입니다. 프로젝트 이미지로 바꿔 사용할 수 있습니다.",
            )}
          </p>
        )}
      </section>
      <p className="sr-only" role="status" aria-live="polite">
        {status}
      </p>
    </section>
  );
}
