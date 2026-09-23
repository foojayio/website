import { test, expect, expectClean, PAGES } from './fixtures.mjs';

// Check immediately: waiting for an exit animation or smooth scroll to finish
// would hide the period in which a keyboard user has no visible focus.
async function focusBounds(page) {
  return page.evaluate(() => {
    const active = document.activeElement;
    const box = active.getBoundingClientRect();
    const header = document.querySelector('.site-header').getBoundingClientRect();
    return {
      visible: box.width > 0 && box.height > 0 &&
        box.top >= (active.closest('.site-header') ? 0 : header.bottom) &&
        box.bottom <= innerHeight && box.left >= 0 && box.right <= innerWidth,
      inert: !!active.closest('[inert]'),
    };
  });
}

test('Tab from a long article into its contents sidebar is immediately visible', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto('today/3-ways-to-refactor-your-code-in-intellij-idea/');
  await page.locator('#post-related .post-card-categories a').last().focus();
  await page.keyboard.press('Tab');
  await expect(page.locator('#TableOfContents a').first()).toBeFocused();
  expect(await focusBounds(page)).toEqual({ visible: true, inert: false });
  expectClean(page);
});
