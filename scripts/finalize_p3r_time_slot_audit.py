#!/usr/bin/env python3
"""Finalize P3R time-slot audit metadata after walkthrough normalization."""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK = ROOT / "content" / "packs" / "p3r" / "pack.json"
LEDGER = ROOT / "docs" / "audits" / "p3r-data-audit.md"


def update_pack() -> None:
    text = PACK.read_text(encoding="utf-8")
    old = '"contentVersion": 2'
    new = '"contentVersion": 3'
    if old in text:
        text = text.replace(old, new, 1)
    elif new not in text:
        raise RuntimeError("Unexpected P3R contentVersion")
    PACK.write_text(text, encoding="utf-8")


def update_ledger() -> None:
    text = LEDGER.read_text(encoding="utf-8")

    old_status = (
        "Tartarus/rescue timing, timed and route-critical Elizabeth request chains,\n"
        "automatic story ranks, and the base achievement catalog now have explicit audit\n"
        "coverage."
    )
    new_status = (
        "Tartarus/rescue timing, timed and route-critical Elizabeth request chains,\n"
        "automatic story ranks, walkthrough time-of-day slots, and the base achievement\n"
        "catalog now have explicit audit coverage."
    )
    if old_status in text:
        text = text.replace(old_status, new_status, 1)

    finding = """### P3R-AUD-026 — Walkthrough time slots were declared but unused — FIXED BASELINE

P3R already declared three presentation slots in `pack.json` — **Day**, **After
School**, and **Evening** — but the imported walkthrough left `Step.slot` unset and
encoded many boundaries only as label prefixes such as `Evening:`. As a result,
the app could not divide P3R's daily route into time-of-day sections the way it
does for P5R.

All **819 walkthrough steps across 11 P3R month files** now carry a structured
`day`, `afternoon`, or `evening` slot. Classroom/exam actions remain in Day;
school free-time uses After School; explicit night actions, Tartarus, dorm
hangouts, and nighttime story progression use Evening; free-day daytime actions
remain in Day. Redundant `Evening:` / `Daytime:` / `After School:` prefixes were
removed where the slot heading now supplies that context. The migration also
handles fixed nighttime cases such as full-moon operations and Death ranks that
did not consistently carry an imported prefix.

Regression coverage rejects any future un-slotted P3R step, unknown slot ID, or
redundant time-prefix label and pins representative school, free-day, full-moon,
exam-Saturday, automatic-rank, and March-epilogue boundaries. `contentVersion` is
bumped to **3** because the walkthrough's rendered grouping changes even though
route order and gameplay facts do not.

"""
    marker = "## Regression rules for P3R\n"
    if "P3R-AUD-026" not in text:
        if marker not in text:
            raise RuntimeError("Cannot locate regression-rules marker")
        text = text.replace(marker, finding + marker, 1)

    rule = (
        "23. Every P3R walkthrough step carries one of the declared Day / After School /\n"
        "    Evening slot IDs; labels do not duplicate the slot heading.\n"
    )
    remaining = "\n## Remaining passes\n"
    if rule not in text:
        if remaining not in text:
            raise RuntimeError("Cannot locate remaining-passes marker")
        text = text.replace(remaining, "\n" + rule + remaining, 1)

    LEDGER.write_text(text, encoding="utf-8")


def main() -> None:
    update_pack()
    update_ledger()
    print("finalized P3R time-slot audit metadata")


if __name__ == "__main__":
    main()
