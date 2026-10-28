"use client";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./select";

export function SelectField({
  value,
  onValueChange,
  options,
  label,
  id,
}: {
  value: string;
  onValueChange: (value: string) => void;
  options: readonly { value: string; label: string }[];
  label: string;
  id?: string;
}) {
  return (
    <Select value={value} onValueChange={onValueChange}>
      <SelectTrigger id={id} aria-label={label} size="sm">
        <SelectValue />
      </SelectTrigger>
      <SelectContent position="popper">
        {options.map((option) => (
          <SelectItem key={option.value} value={option.value}>
            {option.label}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}

export const previewWidths = [
  { value: "390", label: "휴대폰" },
  { value: "820", label: "태블릿" },
  { value: "full", label: "전체" },
];
