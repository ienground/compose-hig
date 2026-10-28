import { highlightExamples } from "@/features/component";
import ComponentPage from "./ui/ComponentPage";

export default async function Page({ slug }: { slug: string }) {
  const initialExamples = await highlightExamples(slug);
  return <ComponentPage slug={slug} initialExamples={initialExamples} />;
}
