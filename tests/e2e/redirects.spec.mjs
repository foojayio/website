import { test, expect, PAGES } from './fixtures.mjs';
import { basePath } from './site.mjs';

const BASE = basePath();

/**
 * A Hugo alias is a meta-refresh stub aimed at the ABSOLUTE canonical URL, and
 * the browser here is blocked from every host but localhost -- so the refresh
 * cannot be followed. What can be checked is where each stub points, and that
 * the target is served. BuiltSite.java checks the stub exists, not this.
 */
async function refreshTarget(page, path) {
  const response = await page.request.get(path);
  expect(response.status(), `${path} should be served`).toBe(200);
  const target = (await response.text()).match(/http-equiv="refresh" content="0; url=([^"]+)"/)?.[1];
  expect(target, `${path} should be a redirect stub`).toBeTruthy();
  return new URL(target).pathname;
}

const MOVED = [
  ['ai/', 'today/category/ai/'],
  ['pedia/', 'java-terms-explained/'],
  ['jvm-weekly/', 'month-overview/'],
];

for (const [from, to] of MOVED) {
  test(`/${from} redirects to /${to}, which is served`, async ({ page }) => {
    expect(await refreshTarget(page, from)).toBe(`${BASE}${to}`);
    expect((await page.request.get(to)).status()).toBe(200);
  });
}

test('an old glossary entry URL redirects to the entry', async ({ page }) => {
  test.skip(!PAGES.pedia, 'the build contains no pedia page');
  const entry = PAGES.pedia.replace(/^java-terms-explained\//, '');
  expect(await refreshTarget(page, `pedia/${entry}`)).toBe(`${BASE}${PAGES.pedia}`);
});
