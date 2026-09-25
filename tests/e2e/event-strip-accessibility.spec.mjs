import { test, expect, expectClean, PAGES } from './fixtures.mjs';

for (const width of [390, 1440]) {
  test(`event links remain fully visible while tabbing in both directions at ${width}px`, async ({ page }) => {
    await page.setViewportSize({ width, height: 1000 });
    await page.goto(PAGES.home);
    const links = page.locator('.event-strip a[href]');
    const count = await links.count();
    expect(count).toBeGreaterThan(1);
    await links.first().focus();
    async function check(index) {
      const result = await links.nth(index).evaluate(link => {
        const box = link.getBoundingClientRect();
        const strip = link.closest('.event-strip').getBoundingClientRect();
        return {
          expectedLink: document.activeElement === link,
          fullyVisible: box.left >= strip.left - 1 && box.right <= strip.right + 1,
        };
      });
      expect(result, `event link ${index + 1}`).toEqual({ expectedLink: true, fullyVisible: true });
    }
    for (let i = 1; i < count; i++) {
      await page.keyboard.press('Tab');
      await check(i);
    }
    for (let i = count - 2; i >= 0; i--) {
      await page.keyboard.press('Shift+Tab');
      await check(i);
    }
    expectClean(page);
  });
}
