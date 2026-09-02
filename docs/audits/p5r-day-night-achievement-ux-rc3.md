# P5R Day/Night and achievement UX audit — rc3

## Scope

This audit tracks the rc3 workflow adjustments requested for Today, full-day pages, Calendar, and the P5R walkthrough data. The factual schedule source is [Alyookid's Persona 5 The Royal 100% Achievements + Perfect Schedule](https://steamcommunity.com/sharedfiles/filedetails/?id=2877808380). Task labels remain independently written under the repository's source policy.

## Walkthrough data result

The source renders its route as `Date | Day | Night`. The bundled P5R route was aligned to those authored period boundaries without changing task order:

| Contract | Result |
|---|---:|
| P5R authored dates | 301 |
| Source rows with an explicit date | 280 |
| Pack-only exam, trip, or story placeholders | 21 |
| Tasks assigned to Day | 818 |
| Tasks assigned to Night | 357 |
| Tasks without a slot | 0 |

The 21 placeholders omitted from the source table are daytime-only exams, school-trip days, or story-only days, so they are assigned to Day. Three compressed tasks crossed an authored boundary and were split: cleaning/sleeping on April 9, the Airsoft visit/desk crafting on April 17, and sending the Calling Card/saving on November 18.

The pack keeps stable schema IDs (`afternoon`, `evening`) but presents them as **Day** and **Night**. `contentVersion` is 8. A regression test pins total counts, valid slots, Day-before-Night ordering, and the three cross-boundary cases.

## Achievement result

The imported guide art already anchors 49 achievements to the guide's monthly checkpoint groups. rc3 turns those anchors into one shared checklist surface:

- Calendar, Today, full-day pages, and the Achievements tab read and write the same profile-scoped achievement state.
- The checklist is included after Tasks on the final authored day of each month.
- Automatically derived completions remain checked and read-only; manual achievements can be checked and unchecked when available.
- `Easy Money` stays manual and unanchored because the source explicitly treats the lottery result as chance-based. Its July 1 task remains the route's instruction to begin trying; the catalog retains April 25 as the earliest possible game availability.

## UX acceptance matrix

| Request | Implemented contract |
|---|---|
| Done treatment | Ordinary text strikethrough; no slash painter |
| Completion readability | 4-second Day Complete hold and 5-second perfect-day hold; tap-to-dismiss retained |
| Today shortcuts | Calendar, full-day, and Achievements links removed |
| Undo styling | Skin-aware outlined action matching End day |
| Today actions | End day and Undo day pinned to the bottom |
| Terminology and periods | Visible title is Tasks; P5R groups are Day and Night |
| Month-end achievements | Shared synchronized checklist after monthly tasks |
| Check all | Right-aligned in the Tasks header; marks all tasks Done |
| Scrolled date | Moves into the banner when its page header is no longer visible |
| Banner typography | Active destination uses the same display face as date headers |
| Full-day browsing | Previous/next authored-day controls pinned to the bottom |
| Calendar | Three-letter month heading and synchronized achievement checkboxes |

## Verification gates

- `P5RTimeSlotAuditTest` pins the source-period contract.
- `TaskGroupingTest` pins group ordering and original progress indexes.
- `MonthlyAchievementChecklistTest` pins final-authored-day placement.
- `SkinFxTimingTest` pins the new readable holds while preserving short transitions.
- The release workflow runs Android assembly, JVM tests, all pack tests, and packlint before publishing the candidate APK.
