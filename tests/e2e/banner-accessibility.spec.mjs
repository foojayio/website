import { test, expect, expectClean, PAGES } from './fixtures.mjs';

test('banner navigation reserves space instead of moving the next keyboard target', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto('today/page/2/');
  const track = page.locator('[data-ad-track]');
  await track.scrollIntoViewIfNeeded();
  await expect.poll(() => track.locator('img').evaluateAll(imgs => imgs.every(img => img.complete))).toBe(true);
  const height = await track.evaluate(e => e.getBoundingClientRect().height);
  const actions = track.locator('a[href]');
  for (let i = 0; i < await actions.count(); i++) {
    await actions.nth(i).focus();
    await page.waitForTimeout(350); // Allow scroll snap and any deferred resize to finish.
    expect(Math.abs(await track.evaluate(e => e.getBoundingClientRect().height) - height)).toBeLessThan(1);
  }
  expectClean(page);
});

test('keyboard use stops banner autoplay until the reader explicitly resumes it', async ({ page }) => {
  await page.goto('today/page/2/');
  const track = page.locator('[data-ad-track]');
  await track.locator('a[href]').first().focus();
  await page.locator('.site-footer a').last().focus();
  const start = await track.evaluate(e => e.scrollLeft);
  await page.waitForTimeout(7100); // Beyond the production rotation interval.
  expect(await track.evaluate(e => e.scrollLeft)).toBe(start);
  await expect(page.getByRole('button', { name: 'Resume banner rotation' })).toBeVisible();
  expectClean(page);
});

test('reduced motion cannot expose a hidden control that restarts autoplay', async ({ page }) => {
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.goto(PAGES.home);
  const pause = page.locator('[data-ad-pause]').first();
  await expect(pause).toBeHidden();
  const track = page.locator('[data-ad-track]').first();
  const start = await track.evaluate(el => el.scrollLeft);
  await page.waitForTimeout(7100); // Deliberately exceed the actual rotation interval.
  expect(await track.evaluate(el => el.scrollLeft)).toBe(start);
  await page.getByRole('button', { name: 'Next banner', exact: true }).click();
  await expect.poll(() => track.evaluate(el => el.scrollLeft)).not.toBe(start);
  expectClean(page);
});
