import { translate } from "@/shared/i18n/messages";
import type { CodeBlockLine } from "@/shared/reui/code-block/code-block-highlight";
import type { IenLanguage } from "@/shared/lib/ien-theme";
import { hasBackdrop, type Component, type Props } from "./catalog";

export type InitialExamples = Record<
  IenLanguage,
  { code: string; lines: CodeBlockLine[] }
>;

export function createCode(
  component: Component,
  props: Props,
  dark: boolean,
  accent: string,
  language: IenLanguage = "en",
) {
  const source = component.snippet(props);
  const body =
    language === "ko"
      ? source
      : source.replace(/"((?:\\.|[^"\\])*)"/g, (literal, text: string) => {
          if (Object.values(props).includes(text)) return literal;
          const translated = translate(text, language);
          return translated === text ? literal : JSON.stringify(translated);
        });
  let imports =
    "import androidx.compose.runtime.*\nimport androidx.compose.foundation.layout.*\nimport androidx.compose.ui.Modifier\nimport androidx.compose.ui.unit.dp\nimport zone.ien.hig.*\nimport zone.ien.hig.theme.*";
  if (/CupertinoIcons/.test(body))
    imports +=
      "\nimport zone.ien.hig.icons.CupertinoIcons\nimport zone.ien.hig.icons.filled.Heart";
  if (/SectionStyle/.test(body)) imports += "\nimport zone.ien.hig.section.*";
  if (/ToggleableState/.test(body))
    imports += "\nimport androidx.compose.ui.state.ToggleableState";
  if (/Alignment/.test(body))
    imports += "\nimport androidx.compose.ui.Alignment";
  if (/scope.launch/.test(body))
    imports += "\nimport kotlinx.coroutines.launch";
  if (component.id === "swipe-box")
    imports +=
      "\nimport androidx.compose.foundation.rememberScrollState\nimport cupertino.RecentCallsSwipeChapter";
  const usesBackdrop = hasBackdrop(component);
  const imageBackground = usesBackdrop;
  if (imageBackground) {
    imports +=
      "\nimport androidx.compose.foundation.Image\nimport androidx.compose.ui.layout.ContentScale\nimport org.jetbrains.compose.resources.painterResource\nimport zone.ien.hig.composeapp.generated.resources.Res\nimport zone.ien.hig.composeapp.generated.resources.glass_landscape";
  }
  if (usesBackdrop)
    imports +=
      "\nimport androidx.compose.foundation.background\nimport com.kyant.backdrop.backdrops.rememberLayerBackdrop\nimport com.kyant.backdrop.backdrops.layerBackdrop";
  const color =
    (
      {
        blue: "007AFF",
        green: "248A3D",
        orange: "FF9500",
        pink: "FF2D55",
      } as Record<string, string>
    )[accent] ?? "007AFF";
  imports += "\nimport androidx.compose.ui.graphics.Color";
  const laidOutBody = `Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {\n${body
    .split("\n")
    .map((line) => "    " + line)
    .join("\n")}\n}`;
  if (!/Alignment/.test(body))
    imports += "\nimport androidx.compose.ui.Alignment";
  const background = imageBackground
    ? `Image(painterResource(Res.drawable.glass_landscape), contentDescription = null, modifier = Modifier.matchParentSize().layerBackdrop(backdrop), contentScale = ContentScale.Crop)`
    : `Box(Modifier.matchParentSize().layerBackdrop(backdrop).background(CupertinoTheme.colorScheme.systemGroupedBackground))`;
  const previewContent = usesBackdrop
    ? `val backdrop = rememberLayerBackdrop()\nBox(Modifier.fillMaxSize()) {\n    ${background}\n${laidOutBody
        .split("\n")
        .map((line) => "    " + line)
        .join("\n")}\n}`
    : laidOutBody;
  const content = ["alert-dialog", "action-sheet"].includes(component.id)
    ? `CupertinoScaffold(containerColor = Color.Transparent, contentWindowInsets = WindowInsets(0)) {\n${previewContent
        .split("\n")
        .map((line) => "    " + line)
        .join("\n")}\n}`
    : previewContent;
  return `${imports}\n\n@OptIn(ExperimentalCupertinoApi::class)\n@Composable\nfun Example() {\n    CupertinoTheme(colorScheme = ${dark ? "dark" : "light"}ColorScheme(accent = Color(0xFF${color}))) {\n${content
    .split("\n")
    .map((line) => "        " + line)
    .join("\n")}\n    }\n}`;
}
