import type { Metadata } from "next";
import { translate } from "@/shared/i18n/messages";
import { catalog } from "@/features/component";
import ComponentPage from "@/pages/component";

export const dynamicParams = false;
export function generateStaticParams() {
  return catalog.map((component) => ({ slug: component.id }));
}
export async function generateMetadata({
  params,
}: {
  params: Promise<{ slug: string }>;
}): Promise<Metadata> {
  const { slug } = await params;
  const component = catalog.find((item) => item.id === slug);
  return {
    title: component?.name,
    description: component ? translate(component.description) : undefined,
  };
}
export default async function Page({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  return <ComponentPage slug={slug} />;
}
