import {
  createSvgEffects,
  generatePath,
  parseBorder,
  type SvgEffectsHandle,
  type SmoothCornerOptions,
} from "@lisse/core";

/** CMP capsule 2.1.4의 G2Continuity 코너를 반경과 가로·세로 제약에 맞춰 계산합니다. */
export function createIenContinuousRoundedRectanglePath(
  width: number,
  height: number,
  radii: readonly [number, number, number, number],
): string {
  const maxRadius = Math.max(0, Math.min(width, height) / 2);
  const corners = [
    [width, 0, 1, 0, radii[1], width, height],
    [width, height, 0, 1, radii[2], height, width],
    [0, height, -1, 0, radii[3], width, height],
    [0, 0, 0, -1, radii[0], height, width],
  ];
  const commands: string[] = [];
  for (const [originX, originY, rotationX, rotationY, radius, along, across] of corners) {
    const r = Math.max(0, Math.min(radius, maxRadius));
    const point = (x: number, y: number) =>
      `${originX + x * rotationX - y * rotationY} ${originY + x * rotationY + y * rotationX}`;
    const move = commands.length ? "L" : "M";
    if (!r) {
      commands.push(`${move} ${point(0, 0)}`);
      continue;
    }
    const alongRatio = Math.min(1, Math.max(0, (along / (2 * r) - 1) / 0.5286651));
    const acrossRatio = Math.min(1, Math.max(0, (across / (2 * r) - 1) / 0.5286651));
    const ratio = Math.min(alongRatio, acrossRatio);
    const extension = 0.5286651 * (0.75 + 0.25 * ratio);
    const arcScale = 1 / (1 + 0.0732051 * ratio);
    const angle = Math.PI / 4 * (1 - 5 / 9 * ratio);
    const sin = Math.sin(angle);
    const cos = Math.cos(angle);
    const endX = Math.SQRT1_2 * (1 - arcScale) + sin * arcScale;
    const endY = 1 - Math.SQRT1_2 * (1 - arcScale) - cos * arcScale;
    const bezier = (axisRatio: number) => {
      const offset = extension * axisRatio;
      const curvature = 1 + 0.0732051 * axisRatio;
      const c2 = endY * cos - (endX + offset) * sin;
      const lambda0 = -c2 / sin - 1.5 * curvature * endY * endY / (sin * sin * sin);
      const lambda3 = endY / sin;
      return {
        offset,
        p1: -offset + Math.max(0, lambda0),
        p2x: endX - Math.max(0, lambda3 * cos),
        p2y: endY - Math.max(0, lambda3 * sin),
      };
    };
    const horizontal = bezier(alongRatio);
    const vertical = bezier(acrossRatio);
    commands.push(
      `${move} ${point(-r * (1 + horizontal.offset), 0)}`,
      `C ${point(r * (horizontal.p1 - 1), 0)} ${point(r * (horizontal.p2x - 1), r * horizontal.p2y)} ${point(r * (endX - 1), r * endY)}`,
      `A ${r * arcScale} ${r * arcScale} 0 0 1 ${point(-r * endY, r * (1 - endX))}`,
      `C ${point(-r * vertical.p2y, r * (1 - vertical.p2x))} ${point(0, r * (1 - vertical.p1))} ${point(0, r * (1 + vertical.offset))}`,
    );
  }
  return `${commands.join(" ")} Z`;
}

/** CMP capsule 2.1.4의 G2Continuity 기본 캡슐 곡선을 SVG로 옮깁니다. */
function capsulePath(width: number, height: number): string {
  const vertical = width < height;
  const w = Math.max(width, height);
  const h = Math.min(width, height);
  const r = h / 2;
  const point = (x: number, y: number) => vertical ? `${y} ${x}` : `${x} ${y}`;
  const arc = (x: number, y: number) => `A ${r} ${r} 0 0 ${vertical ? 0 : 1} ${point(x, y)}`;
  if (w === h) return `M ${point(r, 0)} ${arc(r, h)} ${arc(r, 0)} Z`;
  const extension = 0.5286651 * 0.75;
  const ratio = Math.min(1, Math.max(0, (w / h - 1) / extension));
  const offset = extension * ratio;
  const curvature = 1 + (1.0732051 - 1) * ratio;
  const sin = Math.SQRT1_2;
  const cos = Math.SQRT1_2;
  const dy = 1 - cos;
  const c2 = dy * cos - (sin + offset) * sin;
  const lambda0 = -c2 / sin - 1.5 * curvature * dy * dy / (sin * sin * sin);
  const lambda3 = dy / sin;
  const p1 = (-offset + Math.max(0, lambda0)) * r;
  const p2x = (sin - Math.max(0, lambda3 * cos)) * r;
  const p2y = (dy - Math.max(0, lambda3 * sin)) * r;
  const p3x = sin * r;
  const p3y = dy * r;
  const e = offset * r;
  return [
    `M ${point(r + e, 0)} L ${point(w - r - e, 0)}`,
    `C ${point(w - r + p1, 0)} ${point(w - r + p2x, p2y)} ${point(w - r + p3x, p3y)}`,
    arc(w - r + p3x, h - p3y),
    `C ${point(w - r + p2x, h - p2y)} ${point(w - r + p1, h)} ${point(w - r - e, h)}`,
    `L ${point(r + e, h)}`,
    `C ${point(r - p1, h)} ${point(r - p2x, h - p2y)} ${point(r - p3x, h - p3y)}`,
    arc(r - p3x, p3y),
    `C ${point(r - p2x, p2y)} ${point(r - p1, 0)} ${point(r + e, 0)} Z`,
  ].join(" ");
}

const selector =
  '[data-ien], [data-slot], [class*="rounded"], .topbar, .search-box, .web-docs-disclosure, .project-preview-panel, .project-preview-components, .project-typography fieldset';
let active: { users: number; stop: () => void } | undefined;

/** 현재 문서의 연속 곡률을 적용하고 마지막 사용자가 해제하면 관찰을 종료합니다. */
export function observeIenContinuousCorners(): () => void {
  if (
    typeof window === "undefined" ||
    !window.CSS?.supports?.(
      "clip-path",
      'path(evenodd, "M0 0H1V1H0Z")',
    )
  )
    return () => {};
  if (!active) active = { users: 0, stop: start() };
  const current = active;
  current.users++;
  let released = false;
  return () => {
    if (released) return;
    released = true;
    current.users--;
    if (!current.users) {
      current.stop();
      if (active === current) active = undefined;
    }
  };
}

function start() {
  const supportsNativeCorners = window.CSS.supports("corner-shape", "squircle");
  const elements = new Map<
    HTMLElement,
    {
      original: Map<string, { value: string; priority: string }>;
      applied: Map<string, string>;
      signature: string;
      writtenStyle: string | null;
      renderer?: { handle: SvgEffectsHandle; svg: SVGSVGElement };
    }
  >();
  const transitions = new Map<HTMLElement, Set<string>>();
  let frame = 0;
  function restore(
    element: HTMLElement,
    entry: {
      original: Map<string, { value: string; priority: string }>;
      applied: Map<string, string>;
      renderer?: { handle: SvgEffectsHandle; svg: SVGSVGElement };
    },
  ) {
    for (const [property, applied] of entry.applied) {
      if (element.style.getPropertyValue(property) !== applied) continue;
      const original = entry.original.get(property);
      if (original?.value)
        element.style.setProperty(property, original.value, original.priority);
      else element.style.removeProperty(property);
    }
    entry.applied.clear();
    entry.renderer?.handle.destroy();
    entry.renderer = undefined;
  }
  function schedule() {
    if (!frame) frame = requestAnimationFrame(draw);
  }
  const sizes = new ResizeObserver(schedule);
  function collect(root: ParentNode) {
    const candidates = [...root.querySelectorAll<HTMLElement>(selector)];
    if (root instanceof HTMLElement && root.matches(selector))
      candidates.push(root);
    for (const element of candidates) {
      if (!(element instanceof HTMLElement) || elements.has(element)) continue;
      const style = getComputedStyle(element);
      if (
        style.clipPath !== "none" ||
        style.borderImageSource !== "none" ||
        (style.getPropertyValue("border-shape") || "none") !== "none"
      )
        continue;
      const original = new Map(
        [
          "clip-path",
          "corner-shape",
          "border-radius",
          "border-image-source",
          "border-image-slice",
          "border-image-width",
          "border-image-outset",
          "border-image-repeat",
        ].map((property) => [
          property,
          {
            value: element.style.getPropertyValue(property),
            priority: element.style.getPropertyPriority(property),
          },
        ]),
      );
      elements.set(element, {
        original,
        applied: new Map(),
        signature: "",
        writtenStyle: element.getAttribute("style"),
      });
      sizes.observe(element);
    }
  }
  function draw() {
    frame = 0;
    const updates: (() => void)[] = [];
    for (const [element, entry] of elements) {
      if (!element.isConnected) {
        restore(element, entry);
        sizes.unobserve(element);
        transitions.delete(element);
        elements.delete(element);
        continue;
      }
      const style = getComputedStyle(element);
      if (
        [...entry.applied].some(
          ([property, value]) => element.style.getPropertyValue(property) !== value,
        )
      ) {
        restore(element, entry);
        sizes.unobserve(element);
        elements.delete(element);
        continue;
      }
      if (
        !entry.applied.size &&
        (style.clipPath !== "none" || style.borderImageSource !== "none")
      )
        continue;
      const borders = [
        style.borderTopWidth,
        style.borderRightWidth,
        style.borderBottomWidth,
        style.borderLeftWidth,
      ].map((value) => Number.parseFloat(value) || 0);
      const [top, right, bottom, left] = borders;
      const width =
        Number.parseFloat(style.width) +
        (style.boxSizing === "border-box"
          ? 0
          : left +
            right +
            (Number.parseFloat(style.paddingLeft) || 0) +
            (Number.parseFloat(style.paddingRight) || 0));
      const height =
        Number.parseFloat(style.height) +
        (style.boxSizing === "border-box"
          ? 0
          : top +
            bottom +
            (Number.parseFloat(style.paddingTop) || 0) +
            (Number.parseFloat(style.paddingBottom) || 0));
      const isCapsule = element.dataset.ienShape === "capsule" || (
        element.dataset.slot === "button" &&
        element.dataset.display !== "full" &&
        style.getPropertyValue("--ien-button-shape").trim() === "capsule"
      );
      const useCmpProfile = style.getPropertyValue("--ien-control-corner-profile").trim() === "cmp";
      const radii = [
        style.borderTopLeftRadius,
        style.borderTopRightRadius,
        style.borderBottomRightRadius,
        style.borderBottomLeftRadius,
      ].map((value) => {
        if (isCapsule) return Math.min(width, height) / 2;
        const [horizontal, vertical = horizontal] = value.split(" ");
        return Math.max(
          0,
          Math.min(
            horizontal.endsWith("%")
              ? (Number.parseFloat(horizontal) * width) / 100
              : Number.parseFloat(horizontal),
            vertical.endsWith("%")
              ? (Number.parseFloat(vertical) * height) / 100
              : Number.parseFloat(vertical),
          ),
        );
      });
      const group = element.parentElement;
      const groupedCapsule = isCapsule && group?.dataset.slot === "button-group" &&
        Boolean(element.previousElementSibling || element.nextElementSibling);
      if (groupedCapsule) {
        const vertical = group.dataset.orientation === "vertical";
        if (element.previousElementSibling) {
          radii[0] = 0;
          radii[vertical ? 1 : 3] = 0;
        }
        if (element.nextElementSibling) {
          radii[2] = 0;
          radii[vertical ? 3 : 1] = 0;
        }
      }
      const smoothingValue = Number.parseFloat(
        style.getPropertyValue("--ien-control-corner-smoothing"),
      );
      const smoothing = Number.isFinite(smoothingValue)
        ? Math.min(1, Math.max(0, smoothingValue))
        : 1;
      const unsupportedBorder = [
        style.borderTopStyle,
        style.borderRightStyle,
        style.borderBottomStyle,
        style.borderLeftStyle,
      ].some((value) => !["none", "hidden", "solid"].includes(value));
      const enabled =
        width > left + right &&
        height > top + bottom &&
        radii.every(Number.isFinite) &&
        radii.some((value) => value > 0) &&
        !unsupportedBorder &&
        (isCapsule || style.getPropertyValue("--ien-control-corner-shape").trim() === "squircle");
      const colors = [
        style.borderTopColor,
        style.borderRightColor,
        style.borderBottomColor,
        style.borderLeftColor,
      ];
      const signature = JSON.stringify([
        enabled,
        width,
        height,
        radii,
        borders,
        colors,
        smoothing,
        isCapsule,
        useCmpProfile,
      ]);
      if (signature === entry.signature) continue;
      entry.signature = signature;
      if (!enabled || (supportsNativeCorners && smoothing === 1 && !isCapsule && !useCmpProfile)) {
        updates.push(() => {
          restore(element, entry);
          entry.writtenStyle = element.getAttribute("style");
        });
        continue;
      }
      const [topLeft, topRight, bottomRight, bottomLeft] = radii;
      const options: SmoothCornerOptions = {
        topLeft: { radius: topLeft, smoothing, preserveSmoothing: true },
        topRight: { radius: topRight, smoothing, preserveSmoothing: true },
        bottomRight: { radius: bottomRight, smoothing, preserveSmoothing: true },
        bottomLeft: { radius: bottomLeft, smoothing, preserveSmoothing: true },
      };
      const outer = isCapsule && !groupedCapsule
        ? capsulePath(width, height)
        : useCmpProfile || isCapsule
          ? createIenContinuousRoundedRectanglePath(width, height, [topLeft, topRight, bottomRight, bottomLeft])
          : generatePath(width, height, options);
      const uniform =
        borders.every((value) => value === top) &&
        colors.every((value) => value === colors[0]);
      let borderImage: string | undefined;
      if (uniform && (isCapsule || useCmpProfile)) {
        const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}"><defs><clipPath id="capsule"><path d="${outer}"/></clipPath></defs><path d="${outer}" fill="none" stroke="${colors[0]}" stroke-width="${top * 2}" clip-path="url(#capsule)"/></svg>`;
        borderImage = `url("data:image/svg+xml,${encodeURIComponent(svg)}")`;
      } else if (uniform) {
        if (!entry.renderer) {
          const host = document.createElement("div");
          const handle = createSvgEffects(host);
          const svg = host.querySelector("svg");
          if (!svg) throw new Error("연속 코너 SVG 렌더러를 초기화하지 못했습니다.");
          entry.renderer = { handle, svg };
        }
        entry.renderer.handle.update(
          options,
          { innerBorder: parseBorder(element, style) },
          width,
          height,
        );
        const svg = new XMLSerializer().serializeToString(entry.renderer.svg);
        borderImage = `url("data:image/svg+xml,${encodeURIComponent(svg)}")`;
      }
      // 요소 바깥은 남겨 포커스 링과 넘치는 콘텐츠를 보존하고 코너만 잘라냅니다.
      const extent = Math.max(width, height) + 4096;
      const clip = isCapsule
        ? outer
        : `M ${-extent} ${-extent} H ${width + extent} V ${height + extent} H ${-extent} Z M 0 0 H ${width} V ${height} H 0 Z ${outer}`;
      const properties = new Map([
        ["clip-path", `path(evenodd, "${clip}")`],
        ["corner-shape", "round"],
      ]);
      if (isCapsule) properties.set("border-radius", "0px");
      if (borderImage) {
        properties.set("border-image-source", borderImage);
        properties.set("border-image-slice", "0 fill");
        properties.set("border-image-width", "0");
        properties.set("border-image-outset", "0");
        properties.set("border-image-repeat", "stretch");
      }
      updates.push(() => {
        for (const property of [...entry.applied.keys()]) {
          if (properties.has(property)) continue;
          const original = entry.original.get(property);
          if (original?.value)
            element.style.setProperty(property, original.value, original.priority);
          else element.style.removeProperty(property);
          entry.applied.delete(property);
        }
        for (const [property, value] of properties) {
          element.style.setProperty(property, value);
          entry.applied.set(property, element.style.getPropertyValue(property));
        }
        entry.writtenStyle = element.getAttribute("style");
      });
    }
    for (const update of updates) update();
    if (transitions.size) schedule();
  }
  const changes = new MutationObserver((records) => {
    let dirty = false;
    for (const record of records) {
      if (
        record.type === "attributes" &&
        record.attributeName === "style" &&
        record.target instanceof HTMLElement &&
        elements.get(record.target)?.writtenStyle ===
          record.target.getAttribute("style")
      )
        continue;
      dirty = true;
      if (
        record.type === "attributes" &&
        record.attributeName === "class" &&
        record.target instanceof Element
      )
        collect(record.target);
      for (const node of record.addedNodes)
        if (node instanceof Element) collect(node);
    }
    if (dirty) schedule();
  });
  collect(document);
  changes.observe(document.documentElement, {
    subtree: true,
    childList: true,
    characterData: true,
    attributes: true,
    attributeFilter: [
      "class",
      "style",
      "data-state",
      "data-ien-shape",
      "data-size",
      "data-variant",
      "data-orientation",
      "open",
      "disabled",
    ],
  });
  schedule();
  function transitionStart(event: TransitionEvent) {
    if (
      !(event.target instanceof HTMLElement) ||
      !elements.has(event.target) ||
      !/^(?:width|height|min-|max-|padding|border-.*(?:radius|width|color)|font-size|line-height|letter-spacing|gap)/.test(
        event.propertyName,
      )
    )
      return;
    const properties = transitions.get(event.target) ?? new Set<string>();
    properties.add(event.propertyName);
    transitions.set(event.target, properties);
    schedule();
  }
  function transitionEnd(event: TransitionEvent) {
    if (event.target instanceof HTMLElement) {
      const properties = transitions.get(event.target);
      properties?.delete(event.propertyName);
      if (!properties?.size) transitions.delete(event.target);
    }
    schedule();
  }
  window.addEventListener("resize", schedule, { passive: true });
  document.addEventListener("transitionrun", transitionStart, true);
  document.addEventListener("transitionend", transitionEnd, true);
  document.addEventListener("transitioncancel", transitionEnd, true);
  document.addEventListener("animationend", schedule, true);
  document.addEventListener("focusin", schedule, true);
  document.addEventListener("focusout", schedule, true);
  document.addEventListener("pointerover", schedule, true);
  document.addEventListener("pointerout", schedule, true);
  return () => {
    changes.disconnect();
    sizes.disconnect();
    cancelAnimationFrame(frame);
    window.removeEventListener("resize", schedule);
    document.removeEventListener("transitionrun", transitionStart, true);
    document.removeEventListener("transitionend", transitionEnd, true);
    document.removeEventListener("transitioncancel", transitionEnd, true);
    document.removeEventListener("animationend", schedule, true);
    document.removeEventListener("focusin", schedule, true);
    document.removeEventListener("focusout", schedule, true);
    document.removeEventListener("pointerover", schedule, true);
    document.removeEventListener("pointerout", schedule, true);
    for (const [element, entry] of elements) restore(element, entry);
    elements.clear();
    transitions.clear();
  };
}
