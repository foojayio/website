import { test, expect, expectClean } from './fixtures.mjs';

/**
 * Search is the one part of the site that exists only in the browser: the page
 * is served with an empty results container and Pagefind fills it in. Nothing
 * static can tell whether it works -- a missing index, a base-path mistake or a
 * broken filter all render the same empty box. So it is checked here, for real.
 *
 * The query is a word this site cannot stop saying rather than a term picked
 * for a specific post, so the test does not go red the day that post is
 * retitled. What is asserted is the SHAPE of the answer -- results appear, they
 * are grouped per section with each group's own total, and Show more extends a
 * group -- not a count that changes with every article published.
 */
const QUERY = 'java';

async function search(page, query) {
  await page.goto(`search/?q=${encodeURIComponent(query)}`);
  await expect(page.locator('#search-page-input')).toHaveValue(query);
  return page.locator('#search-page-results');
}

test('a query returns results, grouped by section', async ({ page }) => {
  const results = await search(page, QUERY);

  await expect(results.locator('.search-result').first()).toBeVisible({ timeout: 20_000 });
  await expect(page.locator('#search-page-status')).toContainText(/\d/);

  const sections = results.locator('.search-page__section');
  expect(await sections.count(), 'results should be grouped, not one flat list').toBeGreaterThan(1);

  // Each heading carries that SECTION's own total, not however many rows
  // survived a global cut -- the whole reason the page is built this way.
  for (const heading of await sections.locator('h2 .search-page__count').all()) {
    expect(Number((await heading.innerText()).replace(/\D/g, '')),
      'a section heading should carry its own count').toBeGreaterThan(0);
  }

  // Every result is a real link into this deploy.
  const hrefs = await results.locator('.search-result a[href]')
    .evaluateAll((as) => as.map((a) => a.getAttribute('href')));
  expect(hrefs.length).toBeGreaterThan(0);
  for (const href of hrefs.slice(0, 20)) {
    expect(href, 'a result should link somewhere').toBeTruthy();
    expect(href.startsWith('http') && !href.includes('127.0.0.1'),
      `${href} leaves the site`).toBe(false);
  }
  expectClean(page);
});

test('the first result opens the page it promises', async ({ page }) => {
  const results = await search(page, QUERY);
  const first = results.locator('.search-result').first();
  await expect(first).toBeVisible({ timeout: 20_000 });

  const title = (await first.locator('a[href]').last().innerText()).trim();
  await first.locator('a[href]').last().click();
  await page.waitForLoadState('load');

  expect(new URL(page.url()).pathname, 'a result must not 404').not.toContain('this-page');
  await expect(page.locator('main')).toBeVisible();
  if (title) await expect(page.locator('h1').first()).toContainText(title.slice(0, 30));
});

test('Show more extends a section without replacing it', async ({ page }) => {
  const results = await search(page, QUERY);
  await expect(results.locator('.search-result').first()).toBeVisible({ timeout: 20_000 });

  // Held by INDEX, not by a locator filtered on the button: clicking it
  // re-renders the whole group (posts are re-sorted newest-first over the
  // combined set), so a locator that matched "the section containing this
  // button" stops matching anything the moment it is clicked.
  const sections = results.locator('.search-page__section');
  let index = -1;
  for (let i = 0; i < (await sections.count()); i++) {
    if (await sections.nth(i).locator('.search-page__more').count()) { index = i; break; }
  }
  test.skip(index < 0, 'no section had more results than its first page');

  const section = sections.nth(index);
  const before = await section.locator('.search-result').count();
  await section.locator('.search-page__more').click();
  await expect
    .poll(() => section.locator('.search-result').count(), { timeout: 20_000 })
    .toBeGreaterThan(before);
  expectClean(page);
});

test('a query that matches nothing says so, rather than failing silently', async ({ page }) => {
  // Getting Pagefind to return nothing is harder than it looks, and the
  // measured numbers are the reason this query is shaped the way it is:
  // "qqzzxxjjvvww" returns 1 result and the random "xqjvbzkwqpfmdlrn" returns
  // 214 -- its matching is fuzzy enough that a single nonsense token is not an
  // empty query at all. Several nonsense tokens ANDed together is.
  await search(page, 'qqq zzz xxx jjj vvv');
  await expect(page.locator('#search-page-status')).toContainText(/no|0/i, { timeout: 20_000 });
  await expect(page.locator('#search-page-results .search-result')).toHaveCount(0);
  expectClean(page);
});

test('the header search box submits into the search page', async ({ page }) => {
  await page.goto('');
  const toggle = page.locator('[data-search-toggle]:visible').first();
  test.skip(!(await toggle.count()), 'no header search control');

  // Collapsed to its magnifier: the first click opens the field, the second
  // submits. A visitor with no JavaScript gets a plain submit either way.
  await toggle.click();
  const field = page.locator('form[role="search"] input[type="search"]:visible').first();
  await expect(field).toBeFocused();
  await field.fill(QUERY);
  await field.press('Enter');
  await page.waitForLoadState('load');

  expect(new URL(page.url()).pathname).toMatch(/\/search\/$/);
  expect(new URL(page.url()).searchParams.get('q')).toBe(QUERY);
  await expect(page.locator('#search-page-results .search-result').first())
    .toBeVisible({ timeout: 20_000 });
});
