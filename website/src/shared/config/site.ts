export const repository = "https://github.com/ienground/compose-hig";
export const basePath = process.env.NEXT_PUBLIC_BASE_PATH ?? "";
export const wasmPreview = `${basePath}/wasm/index.html`;
export const libraryVersion = process.env.NEXT_PUBLIC_HIG_VERSION ?? "";

export const author = {
  name: "IENGROUND",
  handle: "@ienground",
  website: "https://www.ien.zone",
  github: "https://github.com/ienground",
  logo: `${basePath}/logo_ienlab.png`,
};
