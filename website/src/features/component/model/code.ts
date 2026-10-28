import type { Component, Props } from "./catalog";

export function createCode(
  component: Component,
  props: Props,
  dark: boolean,
  accent: string,
) {
  const body = component.snippet(props);
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
  const usesBackdrop = body.includes("backdrop");
  const imageBackground =
    component.id.startsWith("liquid-") ||
    component.id === "floating-action-button";
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
  const content = usesBackdrop
    ? `val backdrop = rememberLayerBackdrop()\nBox(Modifier.fillMaxSize()) {\n    ${background}\n${laidOutBody
        .split("\n")
        .map((line) => "    " + line)
        .join("\n")}\n}`
    : laidOutBody;
  return `${imports}\n\n@OptIn(ExperimentalCupertinoApi::class)\n@Composable\nfun Example() {\n    CupertinoTheme(colorScheme = ${dark ? "dark" : "light"}ColorScheme(accent = Color(0xFF${color}))) {\n${content
    .split("\n")
    .map((line) => "        " + line)
    .join("\n")}\n    }\n}`;
}
