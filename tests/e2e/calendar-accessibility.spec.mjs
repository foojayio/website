import { test, expect, expectClean, PAGES } from './fixtures.mjs';

test('calendar Tab order reaches its controls before the secondary rail', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto(PAGES.calendar);
  await page.getByRole('button', { name: 'Next month', exact: true }).focus();
  await page.keyboard.press('Shift+Tab');
  expect(await page.evaluate(() => !!document.activeElement.closest('.calendar__aside'))).toBe(false);
  await page.keyboard.press('Tab');
  await expect(page.getByRole('button', { name: 'Next month', exact: true })).toBeFocused();
  expectClean(page);
});

test('forced colors retains an outline on clipped calendar controls', async ({ page }) => {
  await page.emulateMedia({ forcedColors: 'active' });
  await page.goto(PAGES.calendar);
  await page.getByRole('button', { name: 'Month', exact: true }).focus();
  await page.keyboard.press('Tab');
  await expect(page.getByRole('button', { name: 'List', exact: true })).toBeFocused();
  const ring = await page.evaluate(() => {
    const s = getComputedStyle(document.activeElement);
    return { width: parseFloat(s.outlineWidth), style: s.outlineStyle, offset: parseFloat(s.outlineOffset) };
  });
  expect(ring.width).toBeGreaterThanOrEqual(2);
  expect(ring.style).toBe('solid');
  expect(ring.offset).toBeLessThanOrEqual(-2);
});

test('enlarged text keeps the calendar inside the viewport', async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 900 });
  await page.goto(PAGES.calendar);
  await page.addStyleTag({ content: 'html { font-size: 200% !important; }' });
  expect(await page.locator('main').evaluate(el => el.scrollWidth <= el.clientWidth)).toBe(true);
  expectClean(page);
});
