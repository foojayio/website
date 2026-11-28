# Analytics and consent

## Problems
- Ketch's Google Consent Mode plugin is listed but not configured (`"googletag": {}`), so GA4 can't be loaded directly again.
- GTM runs with no Consent Mode defaults, and gating is Ketch's own.
- Hotjar (`hjid` 2547610) and Reo.dev (`b38cec169d83063`) were dropped at cutover, with the decision left open.
- Ketch is injected by the GTM container, so `[params.analytics.ketch]` stays commented out.

## Plan
1. **Needs a decision:** Hotjar and Reo.dev, keep dropped or restore? If the answer is "dropped", record it in `AGENTS.md` and close known gap 7.
2. Ask whoever owns the Ketch property to configure the `googletag` plugin. Only a browser test can confirm it (a plugin key in the config listing is not evidence).
3. Optional: delete the Custom HTML Ketch tag in the GTM console, then uncomment `[params.analytics.ketch]`, as `AGENTS.md` describes.
4. Do not reconcile GA4 with `data/views.json`, and do not swap the view counts (documented in `AGENTS.md`).

## To verify by the SEO specialist
Reported 2026-11-21: in a private window the Ketch consent banner did not appear. Not yet reproduced or explained.
1. The original report was Firefox private mode on a network that blocks many ad-related domains. Both can stop `gtm.js` (the only thing that loads Ketch) or `ketchcdn`, so that test is inconclusive. Retry in Chrome incognito with extensions off, on a network without the blocking (for example a phone hotspot).
2. If the banner is still missing, open the Network tab and filter on `gtm.js` and `ketchcdn`. `gtm.js` loading without `ketchcdn` means the GTM container's Ketch tag isn't firing.
3. In the GTM console (`GTM-M6ZT5NW`), check the Ketch Custom HTML tag's triggers and any region or geo conditions. Confirm it fires on All Pages.
4. In Ketch (`azul` / `foojay_io`), check the experience's jurisdiction rules. The banner may only show in some regions, and a test from another location can behave differently.
5. Confirm Google Analytics hits (`/j/collect`, `tid=UA-726113-5`) are only sent after consent where the policy requires it. There are no Consent Mode defaults, so this gating is Ketch's alone.

Report back which of these explains it. Only a real browser can confirm the fix.

## Done when
- The open decisions are recorded and the known-gaps list is updated.
