-- One row per counted page, keyed on `<section>/<slug>` (see KEY in
-- src/index.js and partials/views-key.html in the theme). Two count columns,
-- deliberately:
--
--   legacy  the view count WordPress's Post Views Counter plugin had for this
--           page at the last import. Overwritten wholesale by /seed, which is
--           re-run until cutover (see scripts/transfer/LegacyViews.java). Author pages
--           have none -- the plugin's user-archive counting is off on
--           foojay.io -- so they simply start at zero.
--   live    views counted by this Worker since it went up. Only ever
--           incremented, never touched by /seed.
--
-- Keeping them apart is what makes the import repeatable: re-seeding sets a new
-- baseline without discarding anything counted here in the meantime. The site
-- only ever shows legacy + live, as one number.
CREATE TABLE IF NOT EXISTS views (
  page_key TEXT    PRIMARY KEY,
  legacy   INTEGER NOT NULL DEFAULT 0,
  live     INTEGER NOT NULL DEFAULT 0
);
