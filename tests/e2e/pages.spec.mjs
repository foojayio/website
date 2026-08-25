import { test, expect, expectClean, PAGES } from './fixtures.mjs';
import { basePath } from './site.mjs';

const BASE = basePath();

/**
 * The broad net: one representative page of every kind renders, and renders
 * cleanly. This is what catches a JavaScript error or a 404 asset that
 * BuiltSite.java cannot see, because both only exist once a browser runs the
 * page. Which pages these are comes from the build itself -- see discover.mjs.
 */
const KINDS = ['home', 'today', 'pager', 'search', 'calendar', 'jugs', 'champions',
  'sitemap', 'sponsors', 'board', 'author', 'category', 'sponsor', 'pedia',
  'code', 'gallery', 'video'];

for (const kind of KINDS) {
  test(`${kind} page renders cleanly`, async ({ page }) => {
    const path = PAGES[kind];
    test.skip(!path, `the build contains no ${kind} page`);

    const response = await page.goto(path);
    expect(response.status(), `${path} should be served`).toBe(200);

    // A page that renders its shell but no content is the branch-bundle failure
    // mode wearing a 200: the template ran, the filter matched nothing. /search/
    // is exempt because it is SUPPOSED to arrive empty -- Pagefind fills it in,
    // and search.spec.mjs is what checks that it does.
    await expect(page.locator('main')).toBeVisible();
    if (kind !== 'search') {
      expect((await page.locator('main').innerText()).trim().length,
        `${path} has an empty <main>`).toBeGreaterThan(200);
    }

    await expect(page).toHaveTitle(/\S/);
    expectClean(page);
  });
}

test('the site nav is on the page and its links work', async ({ page }) => {
  await page.goto('');
  const nav = page.locator('.primary-nav');
  await expect(nav).toBeVisible();
  const hrefs = await nav.locator('a[href]').evaluateAll((as) => as.map((a) => a.getAttribute('href')));
  expect(hrefs.length, 'the primary nav should have links').toBeGreaterThan(5);
  // Every one of them has to stay inside the deploy's base path. BuiltSite.java
  // checks this statically; here it is checked on the URL a browser resolves.
  for (const href of hrefs) {
    const resolved = new URL(href, page.url()).pathname;
    expect(resolved.startsWith(BASE), `${href} escapes ${BASE}`).toBe(true);
  }
});

test('an unknown URL serves the 404 page, with a 404 status', async ({ page }) => {
  const response = await page.goto('this-page-does-not-exist/');
  expect(response.status()).toBe(404);
  await expect(page.locator('main')).toContainText(/404|not found/i);

  // The link home has to actually go home. It did not: `{{ "/" | relURL }}`
  // renders a bare "/", which leaves the /website/ deploy entirely.
  const home = page.getByRole('link', { name: /homepage/i });
  await expect(home).toBeVisible();
  await home.click();
  await page.waitForLoadState('load');
  expect(new URL(page.url()).pathname, 'the link home must stay inside the deploy').toBe(BASE);
  await expect(page.locator('main')).toBeVisible();
});

test('the theme toggle flips the whole page and is remembered', async ({ page }) => {
  await page.goto('');
  const toggle = page.locator('[data-theme-toggle]:visible').first();
  test.skip(!(await toggle.count()), 'no theme toggle in the header');

  const before = await page.evaluate(() => document.documentElement.getAttribute('data-theme'));
  await toggle.click();
  await expect
    .poll(() => page.evaluate(() => document.documentElement.getAttribute('data-theme')))
    .not.toBe(before);

  const after = await page.evaluate(() => document.documentElement.getAttribute('data-theme'));
  await page.goto('today/');
  expect(await page.evaluate(() => document.documentElement.getAttribute('data-theme')),
    'the choice should survive a navigation').toBe(after);
});

/**
 * The typeface is the one third-party dependency that used to be on EVERY page,
 * and stubbing (fixtures.mjs) meant no test on this site had ever loaded it.
 * Now the woff2 files are ours (themes/foojay/static/vendor/fonts/), so this
 * runs for real -- and a wrong path, a corrupt file or a re-introduced
 * fonts.googleapis.com <link> all show up here as text silently rendered in
 * Segoe UI, which no screenshot-free check would otherwise notice.
 *
 * `document.fonts.check()` is NOT usable for this, measured rather than assumed:
 * it answers TRUE for a family with no @font-face at all -- nothing matches, so
 * nothing needs loading -- which is exactly the failure being tested for. The
 * FontFace set's own `status` is the honest signal.
 */
test('the webfonts are served by us, and actually load', async ({ page }) => {
  await page.goto('');
  const fonts = await page.evaluate(async () => {
    await document.fonts.ready;
    return {
      loaded: [...document.fonts].filter((f) => f.status === 'loaded').map((f) => f.family),
      registered: document.fonts.size,
      files: performance.getEntriesByType('resource')
        .filter((e) => e.name.endsWith('.woff2'))
        .map((e) => e.name),
    };
  });

  // Both families render on every page: --font-display for headings, --font-body
  // for text. One loading and the other not is a half-broken page.
  expect(new Set(fonts.loaded), 'both webfont families should have loaded')
    .toEqual(new Set(['Barlow', 'Source Sans 3']));
  expect(fonts.registered, '40 @font-face rules across two families and seven subsets').toBe(40);

  const origin = new URL(page.url()).origin;
  expect(fonts.files.length, 'the page should have fetched webfonts at all').toBeGreaterThan(0);
  expect(fonts.files.filter((u) => !u.startsWith(origin)), 'every font file must be ours').toEqual([]);
  expectClean(page);
});
