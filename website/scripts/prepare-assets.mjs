import { cp, mkdir, rm, stat, writeFile } from "node:fs/promises";
import { fileURLToPath } from "node:url";

const root = new URL("../", import.meta.url);
const distribution = new URL(
  "../example/composeApp/build/dist/wasmJs/productionExecutable/",
  root,
);
const publicRoot = new URL("public/", root);
try {
  await stat(new URL("composeApp.js", distribution));
} catch {
  throw new Error(
    "Wasm 빌드가 없습니다. website에서 npm run build:wasm을 먼저 실행하세요.",
  );
}
await rm(new URL("wasm/", publicRoot), { recursive: true, force: true });
await mkdir(new URL("wasm/", publicRoot), { recursive: true });
await cp(
  fileURLToPath(distribution),
  fileURLToPath(new URL("wasm/", publicRoot)),
  { recursive: true },
);
await mkdir(new URL("media/", publicRoot), { recursive: true });
for (const name of [
  "compose-hig.svg",
  "compose-hig-duo-open.png",
  "compose-hig-duo-closed.png",
  "compose-hig-duo-sheet.png",
  "compose-hig-duo-demo.mp4",
]) {
  await cp(
    new URL(`../docs/assets/${name}`, root),
    new URL(`media/${name}`, publicRoot),
  );
}
await writeFile(new URL(".nojekyll", publicRoot), "");
console.log("Wasm 미리보기와 iPhone Duo 자료를 준비했습니다.");
