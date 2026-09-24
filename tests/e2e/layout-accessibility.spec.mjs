import { test, expect, expectClean } from './fixtures.mjs';

test('sidebar links remain fully visible during sequential keyboard navigation', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto('ai/');
  // Exercise the static links separately from the rotating banner's controls.
  const links = page.locator('.sidebar .widget:not(.widget-ad) a[href]');
  const count = await links.count();
  expect(count).toBeGreaterThan(1);
  await links.first().scrollIntoViewIfNeeded();
  await links.first().focus();
  for (let i = 0; i < count; i++) {
    if (i > 0) await page.keyboard.press('Tab');
    const focus = await links.nth(i).evaluate(link => {
      const active = document.activeElement;
      const box = active.getBoundingClientRect();
      const headerBottom = document.querySelector('.site-header').getBoundingClientRect().bottom;
      return {
        expectedLink: active === link && active.matches('.sidebar a[href]'),
        fullyVisible: box.width > 0 && box.height > 0 &&
          box.top >= headerBottom && box.bottom <= innerHeight &&
          box.left >= 0 && box.right <= innerWidth,
      };
    });
    expect(focus, `sidebar link ${i + 1} of ${count}`).toEqual({ expectedLink: true, fullyVisible: true });
  }
  expectClean(page);
});
