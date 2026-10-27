import * as skiko from "./skiko.mjs";
import "./js-reexport-symbols.mjs";

globalThis["./skiko.mjs"] = skiko;
await skiko.awaitSkiko;
