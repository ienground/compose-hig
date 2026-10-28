"use client";

import { useState } from "react";
import { Button } from "@/shared/ui/button";
import { Checkbox } from "@/shared/ui/checkbox";
import { Input } from "@/shared/ui/input";
import { Label } from "@/shared/ui/label";
import { Separator } from "@/shared/ui/separator";
import { Slider } from "@/shared/ui/slider";
import { SelectField, previewWidths } from "@/shared/ui/SelectField";

import { catalog, type Control, type Props } from "../model/catalog";
import { WasmPreview } from "@/shared/ui/WasmPreview";
import { createCode } from "../model/code";

export function Playground({ componentId }: { componentId: string }) {
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
  const [copied, setCopied] = useState("");
  const code = createCode(component, props, dark, accent);
  const renderControl = (
    control: Control,
    current: Props[string],
    change: (value: Props[string]) => void,
  ) => {
    const id = `control-${control.key}`;
    return (
      <Label className="control" key={control.key} htmlFor={id}>
        <span>
          {control.label}
          {control.type === "range" && (
            <output className="control-output" htmlFor={id}>
              {current}
            </output>
          )}
        </span>
        {control.type === "select" ? (
          <SelectField
            id={id}
            label={control.label}
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
            aria-label={control.label}
            thumbLabels={[control.label]}
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
        <span className="tag">실제 Compose · Wasm</span>
      </div>
      <div className="playground">
        <div className="preview-column">
          <div className="preview-toolbar">
            <span className="preview-label">미리보기</span>
            <label>
              너비
              <SelectField
                label="너비"
                value={width}
                onValueChange={setWidth}
                options={previewWidths}
              />
            </label>
            <Button
              variant="ghost"
              size="sm"
              type="button"
              onClick={() => {
                setProps(defaults());
                setResetKey((key) => key + 1);
                setStatus("컴포넌트 상태를 초기화했습니다.");
              }}
            >
              초기화 ↺
            </Button>
          </div>
          <div className="preview-stage">
            <WasmPreview
              component={component.id}
              options={{ ...props, dark, accent }}
              width={width}
              resetKey={resetKey}
              title={`${component.name} 실시간 미리보기`}
            />
          </div>
          <p className="preview-caption">
            미리보기 안에서 직접 클릭하거나 드래그해 보세요.
          </p>
        </div>
        <form
          id="controls"
          aria-label="컴포넌트 속성"
          onSubmit={(event) => event.preventDefault()}
        >
          <h3>속성</h3>
          {component.controls.map((control) =>
            renderControl(control, props[control.key], (value) =>
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
          <h2 id="code-heading">사용 예제</h2>
          <Button
            variant="ghost"
            size="sm"
            type="button"
            onClick={async () => {
              try {
                await navigator.clipboard.writeText(code);
                setCopied(code);
                setStatus("Kotlin 코드를 복사했습니다.");
              } catch {
                setStatus(
                  "복사하지 못했습니다. 코드 영역에서 직접 선택해 복사하세요.",
                );
              }
            }}
          >
            {copied === code ? "복사됨" : "코드 복사"}
          </Button>
        </div>
        <p>현재 Playground 설정에 맞춘 Kotlin 코드입니다.</p>
        <pre>
          <code>{code}</code>
        </pre>
        <p className="code-note">
          예제는 <code>CupertinoTheme</code> 안에서 실행합니다.{" "}
          <code>backdrop</code>을 사용하는 컴포넌트는 배경 콘텐츠에{" "}
          <code>layerBackdrop</code>을 연결하세요.
        </p>
        {(component.id.startsWith("liquid-") ||
          component.id === "floating-action-button") && (
          <p className="code-note">
            배경 이미지는 샘플앱의 <code>glass_landscape</code> 리소스입니다.
            프로젝트 이미지로 바꿔 사용할 수 있습니다.
          </p>
        )}
      </section>
      <p className="sr-only" role="status" aria-live="polite">
        {status}
      </p>
    </section>
  );
}
