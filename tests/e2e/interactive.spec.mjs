import { test, expect, expectClean, PAGES } from './fixtures.mjs';

/**
 * The parts of the site that only exist after JavaScript runs. Every one of
 * these fails SILENTLY when it breaks -- the page still returns 200, still has
 * its content, and simply stops doing the thing. That is precisely the class of
 * bug neither BuiltSite.java nor a human skimming a diff will catch.
 */

test('code blocks are highlighted', async ({ page }) => {
  test.skip(!PAGES.code, 'no post in this build has a code block');
  await page.goto(PAGES.code);

  // The stored form is a Markdown fence; render-codeblock.html turns it back
  // into the element the vendored EnlighterJS initialiser looks for, and
  // baseof.html loads the highlighter only when the page has one.
  await expect(page.locator('.prose pre.EnlighterJSRAW').first()).toBeAttached();
  await expect(page.locator('.enlighter, [class*="enlighter"]').first())
    .toBeVisible({ timeout: 10_000 });
  expectClean(page);
});

test('the gallery lightbox opens, steps and closes', async ({ page }) => {
  test.skip(!PAGES.gallery, 'no post in this build has a gallery');
  await page.goto(PAGES.gallery);

  const images = page.locator('.prose .gallery img');
  expect(await images.count(), 'the gallery shortcode should render images').toBeGreaterThan(0);

  // Gallery images are loading="lazy" and below the fold, so until one is
  // scrolled to it has no box at all -- and an element with no box never
  // becomes clickable. Scroll, then wait for the decode, then click: which is
  // also the order a reader does it in.
  const first = images.first();
  await first.evaluate((img) => img.scrollIntoView({ block: 'center' }));
  await expect.poll(() => first.evaluate((img) => img.complete && img.naturalWidth > 0),
    { timeout: 15_000 }).toBe(true);

  await first.click();
  const overlay = page.locator('.lightbox.is-open, .lightbox__img');
  await expect(overlay.first()).toBeVisible({ timeout: 10_000 });

  await page.keyboard.press('Escape');
  await expect(page.locator('.lightbox.is-open')).toHaveCount(0);
  expectClean(page);
});

/**
 * The maps are the one feature here that CANNOT be fully checked offline:
 * /jugs/ and /java-champions/ load Leaflet and markercluster from unpkg.com at
 * read time, and nothing here may reach the network (see fixtures.mjs). So this
 * asserts everything that is ours -- the host element, our own script, and the
 * points it was handed -- and says out loud why it stops there.
 *
 * Note what the skip is evidence OF: cluster-map.js guards on `!window.L` and
 * returns, so when unpkg is unreachable the map silently disappears and the
 * page still renders clean. Vendoring Leaflet the way mermaid is vendored would
 * remove that dependency AND make the marker assertion below run for real.
 */
for (const kind of ['jugs', 'champions']) {
  test(`the ${kind} map is wired to real points`, async ({ page }) => {
    test.skip(!PAGES[kind], `no ${kind} page`);
    await page.goto(PAGES[kind]);

    const wiring = await page.evaluate(() => ({
      builder: typeof window.foojayClusterMap,
      host: !!document.querySelector('.cluster-map, [id$="-map"], #map'),
      leaflet: typeof window.L,
    }));
    expect(wiring.builder, 'static/js/cluster-map.js should have loaded').toBe('function');
    expect(wiring.host, 'the map needs an element to render into').toBe(true);

    test.skip(wiring.leaflet === 'undefined',
      'Leaflet comes from unpkg.com and the tests make no network calls');

    // One marker per PLACE, with the item count baked into the badge. Zero
    // markers is what a bad coordinate field or a failed grouping looks like.
    await expect(page.locator('.leaflet-container')).toBeVisible({ timeout: 15_000 });
    await expect
      .poll(() => page.locator('.leaflet-marker-icon').count(), { timeout: 15_000 })
      .toBeGreaterThan(5);
    expectClean(page);
  });
}

test('the champions table is not treated as a gallery', async ({ page }) => {
  test.skip(!PAGES.champions, 'no champions page');
  await page.goto(PAGES.champions);
  await expect(page.locator('.champions-table').first()).toBeVisible();

  // The table sits inside .prose, so all 422 avatars were once picked up as
  // content images -- a 36px face opening full-screen, and a next/prev sequence
  // 422 faces long. With nothing enlargeable on the page the lightbox must not
  // build itself at all.
  await expect(page.locator('.lightbox')).toHaveCount(0);
});

test('map tiles are not treated as content images', async ({ page }) => {
  test.skip(!PAGES.jugs, 'no jugs page');
  await page.goto(PAGES.jugs);
  test.skip(await page.evaluate(() => typeof window.L === 'undefined'),
    'Leaflet comes from unpkg.com and the tests make no network calls');
  await expect(page.locator('.leaflet-container')).toBeVisible({ timeout: 15_000 });

  const hijacked = await page.evaluate(() => Array.from(document.querySelectorAll('.leaflet-container img'))
    .filter((img) => getComputedStyle(img).cursor === 'zoom-in').length);
  expect(hijacked, 'a map tile should pan the map, not open a lightbox').toBe(0);
});

test('the sitemap tables sort and filter', async ({ page }) => {
  test.skip(!PAGES.sitemap, 'no sitemap page');
  await page.goto(PAGES.sitemap);

  const table = page.locator('table[data-sortable]').first();
  await expect(table).toBeVisible();

  // The HTML arrives sorted and the script only adds the controls, so without
  // JavaScript there is no dead button -- which means their PRESENCE is the
  // evidence the script ran.
  const sortButton = table.locator('thead button').first();
  await expect(sortButton).toBeVisible({ timeout: 10_000 });

  const firstCell = () => table.locator('tbody tr:visible td').first().innerText();
  const before = await firstCell();
  await sortButton.click();
  await expect.poll(firstCell, { timeout: 10_000 }).not.toBe(before);

  // The filter is bound to a specific table by id, so pair it with the table
  // under test rather than grabbing whichever box happens to be first.
  const tableId = await table.getAttribute('id');
  const filter = page.locator(`input[data-filter-for="${tableId}"]`);
  test.skip(!(await filter.count()), 'this table has no filter box');
  await expect(filter).toBeVisible({ timeout: 10_000 });
  const rows = () => table.locator('tbody tr:visible').count();
  const all = await rows();
  await filter.fill('zzqqxwv');
  await expect.poll(rows, { timeout: 10_000 }).toBe(0);
  await filter.fill('');
  await expect.poll(rows, { timeout: 10_000 }).toBe(all);
  expectClean(page);
});

test('mermaid diagrams render to SVG, and follow the theme', async ({ page }) => {
  // Skips until a published post uses a ```mermaid fence -- the support is
  // built and vendored, but nothing in content/ exercises it yet. This turns
  // itself on the day that post lands, with nothing to remember to enable.
  test.skip(!PAGES.mermaid, 'no published page has a mermaid diagram');
  await page.goto(PAGES.mermaid);

  const diagram = page.locator('pre.mermaid').first();
  await expect(diagram.locator('svg')).toBeVisible({ timeout: 15_000 });
  await expect(diagram).toHaveAttribute('data-processed', /.+/);
  // The source is stashed before rendering precisely so a theme flip can
  // re-render; without it a reader switching to dark keeps a white diagram.
  await expect(diagram).toHaveAttribute('data-mermaid-source', /.+/);
  expectClean(page);
});

test('the read counter is wired, and counts nothing from here', async ({ page }) => {
  await page.goto(PAGES.code || 'today/');
  await page.waitForLoadState('networkidle');

  // views-beacon.html renders nothing unless [params.views] endpoint is set, so
  // a build without it legitimately sends none.
  const beacons = page.stubbed.filter((url) => /\/api\/views/.test(url));
  test.skip(!beacons.length, 'this build has no view counter configured');

  // Every one of them was answered locally. That it appears in `stubbed` at all
  // is the proof it never reached the network -- and DNS is switched off for
  // anything but localhost besides (see playwright.config.mjs), so a deploy
  // cannot add reads to the numbers printed on the site.
  for (const url of beacons) {
    expect(url.startsWith('http://127.0.0.1:'), `${url} left the runner`).toBe(false);
  }
});
