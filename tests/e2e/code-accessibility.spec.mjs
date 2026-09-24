import { test, expect, expectClean, PAGES } from './fixtures.mjs';

test('code actions work with the keyboard and copy the complete source', async ({ page }) => {
  test.skip(!PAGES.code, 'no code fixture');
  await page.addInitScript(() => Object.defineProperty(navigator, 'clipboard', { value: { writeText: async value => { window.copiedCode = value; } } }));
  await page.goto(PAGES.code);
  const block = page.locator('.enlighter-default').first();
  const plain = block.getByRole('button', { name: 'Plain text' });
  await plain.focus();
  await plain.press('Space');
  await expect(plain).toHaveAttribute('aria-pressed', 'true');
  await expect(block.locator('.enlighter-raw')).toBeVisible();
  await plain.press('Tab');
  await expect(block.getByRole('button', { name: 'Copy code' })).toBeFocused();
  await page.keyboard.press('Enter');
  await expect(block.getByRole('status')).toHaveText('Code copied.');
  expect(await page.evaluate(() => window.copiedCode)).toBe(await block.locator('.enlighter-raw').textContent());
  await expect(block.getByRole('link', { name: 'Open code', exact: true })).toHaveAttribute('href', /^blob:/);
  await expect(block.getByRole('region')).toHaveAttribute('tabindex', '0');
  const legacy = page.locator('.prose pre:not(.EnlighterJSRAW):not(.mermaid)').first();
  await legacy.focus();
  await expect(legacy).toBeFocused();
  await expect(legacy).toHaveAttribute('role', 'region');
  expectClean(page);
});

test('clipboard refusal leaves a keyboard-accessible manual copy path', async ({ page }) => {
  test.skip(!PAGES.code, 'no code fixture');
  await page.addInitScript(() => Object.defineProperty(navigator, 'clipboard', { value: { writeText: async () => { throw Error('denied'); } } }));
  await page.goto(PAGES.code);
  const block = page.locator('.enlighter-default').first();
  await block.getByRole('button', { name: 'Copy code' }).press('Enter');
  await expect(block.getByRole('status')).toContainText('copy it manually');
  await expect(block.getByRole('region')).toBeFocused();
  await expect(block.locator('.enlighter-raw')).toBeVisible();
  expectClean(page);
});

test('AsciiDoc table reflows independently of the page at 320 CSS px', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 900 });
  await page.goto('today/asciidoc-support-on-foojay/');
  const table = page.locator('.prose-table-scroll:has(table.tableblock)').first();
  await expect(table).toHaveAttribute('role', 'region');
  await table.focus();
  await expect(table).toBeFocused();
  expect(await page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(320);
  await table.press('ArrowRight');
  await expect.poll(() => table.evaluate(el => el.scrollLeft)).toBeGreaterThan(0);
  await expect(table.locator('table > caption')).toBeVisible();
  expectClean(page);
});
