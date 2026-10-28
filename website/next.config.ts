import type { NextConfig } from "next";
import { readFileSync } from "node:fs";
import { resolve } from "node:path";

const catalog = readFileSync(
  resolve(process.cwd(), "../gradle/libs.versions.toml"),
  "utf8",
);
const libraryVersion = catalog.match(/^lib-version-name\s*=\s*"([^"]+)"/m)?.[1];
if (!libraryVersion)
  throw new Error("Gradle 카탈로그에서 라이브러리 버전을 찾을 수 없습니다.");

const nextConfig: NextConfig = {
  output: "export",
  basePath: process.env.NEXT_PUBLIC_BASE_PATH ?? "",
  env: { NEXT_PUBLIC_HIG_VERSION: libraryVersion },
  agentRules: false,
  outputFileTracingRoot: process.cwd(),
  trailingSlash: true,
  images: { unoptimized: true },
};

export default nextConfig;
