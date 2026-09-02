# P5R Trader Sakai audit — 16 exchange sequence

This pass independently checks the completion route's sixteen Kichijoji Trader Sakai exchanges: requested item, reward identity, and the route date chosen inside Sakai's availability window.

## Sources

Primary route source:

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** (Steam Community). The Dayloop walkthrough sequence is derived from this schedule.

Independent Royal references:

- SAMURAI GAMERS, **Persona 5 Royal - Bargain Sakai Trading Guide**: https://samurai-gamers.com/persona-5/kichijoji-merchant-trading-guide/
- Megami Tensei Wiki, **Trading / Kichijoji** and individual item pages.
- GameFAQs Royal walkthrough/reference tables, used as a conflict check rather than silently treating any single table as canonical.

## Audited sequence

| # | Route date | Requested item | Reward |
|---:|---|---|---|
| 1 | 2016-06-12 | Imported Protein | Decorative Whip |
| 2 | 2016-06-26 | Yakisoba Pan | Black Robe |
| 3 | 2016-07-12 | Soothing Soba | Koedo Sword |
| 4 | 2016-07-26 | MRE Ration | Factorization Guide |
| 5 | 2016-08-07 | Exorcism Water | Model Gun |
| 6 | 2016-08-23 | Melon Pan | Old Man's Fist |
| 7 | 2016-09-04 | Phantom Wafer | Strength Up Ofuda x2 |
| 8 | 2016-09-19 | Thief Mask | Magic Up Ofuda x2 |
| 9 | 2016-10-02 | Calling Postcard | Strawberry Daifuku x2 |
| 10 | 2016-10-16 | Gear Girimehkala | Hot-Blooded Sword |
| 11 | 2016-10-30 | Mystery Stew | Angel Badge |
| 12 | 2016-11-13 | Moon Dango | Kintaro Axe |
| 13 | 2016-11-27 | Legendary Yaki-imo | Empowering Ofuda x3 |
| 14 | 2016-12-11 | Angel Tart | Fervent Bat |
| 15 | 2017-01-13 | Special Chimaki | Strength Belt |
| 16 | 2017-01-22 | Supernova Burger | Old Man's Elixir |

The route dates are completion-route choices inside the game's exchange windows; they are not represented as the only possible trade date.

## Corrections

- August 23 previously rendered the Melon Pan reward as **Old Man's Fists**. Independent item/trade references identify the Royal weapon as **Old Man's Fist** (singular), so the route text was corrected.

## Recorded source conflicts

### Final Supernova Burger reward

There is a real naming conflict in secondary references:

- Alyookid's source route says **Old Man's Elixir**.
- GameFAQs Royal walkthrough/reference tables and the independent megaten-database also say **Old Man's Elixir**.
- SAMURAI GAMERS and current Megami Tensei Wiki item tables use **Father's Elixir** for the Supernova Burger reward.

Dayloop retains **Old Man's Elixir** because it matches the imported route source and multiple independent walkthrough/data references. The conflict is recorded here rather than silently claiming unanimous agreement. Both labels describe the full-party HP/SP recovery reward commonly equated to a Soma in walkthrough material.

### Exorcism Water reward

Some Royal references call the 8/7–8/13 reward a **Model Gun**, while at least one GameFAQs reference table calls it **Black Gun**. Dayloop retains **Model Gun**, which matches the imported route and the other structured item/trade references used by this pass.

## Regression coverage

`P5RTraderSakaiAuditTest` pins all sixteen route-selected trades by:

- route date,
- requested item,
- reward identity,
- `(n/16)` sequence position.

The test normalizes hyphen formatting when comparing names, but does not allow an item or reward to silently change.

## Verification boundary

This closes the Trader Sakai one-off item outcome cluster for issue #12. Availability windows remain game facts; the exact date Dayloop performs each exchange remains explicitly part of the `100% Completion Route`.
