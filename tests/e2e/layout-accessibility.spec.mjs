import { test, expect, expectClean, PAGES } from './fixtures.mjs';

test('sidebar links remain visible during sequential keyboard navigation', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto('ai/');
  const first = page.locator('.sidebar a[href]').first();
  await first.focus();
  for (let i = 0; i < 22; i++) {
    await page.keyboard.press('Tab');
    await expect.poll(() => page.evaluate(() => {
      const box = document.activeElement.getBoundingClientRect();
      const headerBottom = document.querySelector('.site-header').getBoundingClientRect().bottom;
      return box.bottom > headerBottom && box.top < innerHeight;
    })).toBe(true);
  }
});
