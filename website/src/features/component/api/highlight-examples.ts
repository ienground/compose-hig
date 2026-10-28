import { catalog } from "../model/catalog";
import { createCode } from "../model/code";
import { translate } from "@/shared/i18n/messages";
import { highlightCode } from "@/shared/reui/code-block/code-block-highlight";
import type { InitialExamples } from "../model/code";

export async function highlightExamples(
  componentId: string,
): Promise<InitialExamples | undefined> {
  const component = catalog.find((item) => item.id === componentId);
  if (!component) return undefined;
  const example = async (language: "en" | "ko") => {
    const props = Object.fromEntries(
      component.controls.map((control) => [
        control.key,
        control.type === "text" && typeof control.value === "string"
          ? translate(control.value, language)
          : control.value,
      ]),
    );
    const code = createCode(component, props, false, "blue", language);
    const lines = await highlightCode(code, {
      language: "kotlin",
      instanceKey: `${componentId}-${language}`,
    });
    if (
      !lines.some((line) =>
        line.tokens.some((token) => token.color && token.colorDark),
      )
    ) {
      throw new Error(
        `Kotlin 구문 강조를 생성하지 못했습니다: ${componentId} (${language})`,
      );
    }
    return { code, lines };
  };
  const [en, ko] = await Promise.all([example("en"), example("ko")]);
  return { en, ko };
}
