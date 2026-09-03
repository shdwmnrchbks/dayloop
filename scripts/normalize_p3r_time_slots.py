#!/usr/bin/env python3
"""Normalize Persona 3 Reload walkthrough steps into structured time slots.

P3R already declares three slots in pack.json:
  day       -> Day
  afternoon -> After School
  evening   -> Evening

The imported walkthrough historically encoded many evening/daytime boundaries in
step labels instead of Step.slot. This migration converts those cues into the
schema field while preserving route order and gameplay text.
"""

from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WALKTHROUGH_DIR = ROOT / "content" / "packs" / "p3r" / "walkthrough"
VALID_SLOTS = {"day", "afternoon", "evening"}

PREFIX_RE = re.compile(
    r"^(?P<prefix>Evening|Night|Daytime|After\s+School|After-school)\s*[:\-–—]\s*",
    re.IGNORECASE,
)

# These are actions that happen during the school-day / exam portion rather than
# in the player's after-school free-time slot.
DAY_PATTERNS = (
    "answer the class question",
    "stay awake in class",
    "sleep in class",
    "exam day",
    "final exam day",
    "exam results",
)


def explicit_slot(label: str) -> tuple[str | None, str]:
    match = PREFIX_RE.match(label)
    if not match:
        return None, label

    prefix = match.group("prefix").lower().replace("-", " ")
    cleaned = label[match.end() :].strip()
    if prefix in {"evening", "night"}:
        return "evening", cleaned
    if prefix == "daytime":
        return "day", cleaned
    return "afternoon", cleaned


def is_day_action(label: str) -> bool:
    lowered = label.lower()
    return any(pattern in lowered for pattern in DAY_PATTERNS)


def normalize_day(day: dict) -> None:
    day_kind = day.get("dayKind")
    # School/exam dates default to After School once mandatory classroom actions
    # are excluded. Free/story dates default to Day. An explicit evening marker
    # moves the remainder of that day's route into the Evening slot.
    default_slot = "afternoon" if day_kind in {"school", "exam"} else "day"
    current_slot = default_slot

    for step in day.get("steps", []):
        label = step.get("label", "")
        explicit, cleaned = explicit_slot(label)

        if explicit is not None:
            current_slot = explicit
            slot = explicit
            step["label"] = cleaned
        elif is_day_action(label):
            slot = "day"
        elif "death reaches rank" in label.lower():
            # Pharos/Death automatic ranks occur during the nighttime story beat.
            slot = "evening"
        else:
            slot = current_slot

        if slot not in VALID_SLOTS:
            raise ValueError(f"{day.get('date')}: invalid slot {slot!r}: {label}")

        # Keep the human-readable fields in a stable order: label, slot, then the
        # rest of the original step metadata.
        reordered = {"label": step["label"], "slot": slot}
        for key, value in list(step.items()):
            if key not in {"label", "slot"}:
                reordered[key] = value
        step.clear()
        step.update(reordered)


def normalize_file(path: Path) -> int:
    data = json.loads(path.read_text(encoding="utf-8"))
    count = 0
    for day in data.get("days", []):
        normalize_day(day)
        count += len(day.get("steps", []))

    path.write_text(
        json.dumps(data, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    return count


def main() -> None:
    paths = sorted(WALKTHROUGH_DIR.glob("*.json"))
    if not paths:
        raise SystemExit(f"No walkthrough files found in {WALKTHROUGH_DIR}")

    total = 0
    for path in paths:
        count = normalize_file(path)
        total += count
        print(f"normalized {path.name}: {count} steps")

    print(f"normalized {len(paths)} files / {total} P3R steps")


if __name__ == "__main__":
    main()
