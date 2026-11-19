# Analytics and consent

## Problems
- Ketch's Google Consent Mode plugin is listed but not configured (`"googletag": {}`), so GA4 can't be loaded directly again.
- GTM runs with no Consent Mode defaults, and gating is Ketch's own.
- Hotjar (`hjid` 2547610) and Reo.dev (`b38cec169d83063`) were dropped at cutover, with the decision left open.
- Ketch is injected by the GTM container, so `[params.analytics.ketch]` stays commented out.

## Plan
1. **Needs a decision from Frank:** Hotjar and Reo.dev, keep dropped or restore? If the answer is "dropped", record it in `AGENTS.md` and close known gap 7.
2. Ask whoever owns the Ketch property to configure the `googletag` plugin. Only a browser test can confirm it (a plugin key in the config listing is not evidence).
3. Optional: delete the Custom HTML Ketch tag in the GTM console, then uncomment `[params.analytics.ketch]`, as `AGENTS.md` describes.
4. Do not reconcile GA4 with `data/views.json`, and do not swap the view counts (documented in `AGENTS.md`).

## Done when
- The open decisions are recorded and the known-gaps list is updated.
