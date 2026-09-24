import { test, expect, expectClean, PAGES } from './fixtures.mjs';

for (const scheme of ['light', 'dark']) {
  test(`navigation exposes one consistent disclosure state (${scheme})`, async ({ page }) => {
    await page.addInitScript(s => localStorage.setItem('foojay-theme', s), scheme);
    await page.setViewportSize({ width: 1440, height: 1000 });
    await page.goto(PAGES.home);
    const trigger = page.getByRole('button', { name: 'Java Basics', exact: true });
    const panel = page.locator('#' + await trigger.getAttribute('aria-controls'));
    await trigger.focus();
    await expect(panel).toBeHidden();
    await trigger.press('Enter');
    await expect(trigger).toHaveAttribute('aria-expanded', 'true');
    await expect(panel).toBeVisible();
    expect(await panel.getByRole('link').evaluateAll(links => links.map(a => new URL(a.href).pathname))).toEqual([
      '/pedia/', '/java-quick-start/other-tutorials/',
      '/java-quick-start/install-java/',
      ...['install-java-on-windows', 'install-java-on-macos', 'install-java-on-linux', 'check-your-java-installation', 'find-another-java-version'].map(slug => `/java-quick-start/install-java/${slug}/`),
      '/java-quick-start/quick-start-tutorial/',
      ...['choosing-an-editor', 'hello-world', 'using-the-arguments', 'working-with-numbers', 'if-then-else', 'enum-and-switch', 'using-methods', 'using-objects', 'reading-a-text-file', 'using-streams', 'whats-next'].map(slug => `/java-quick-start/quick-start-tutorial/${slug}/`),
      '/java-quick-start/',
    ]);
    await page.keyboard.press('Tab');
    await expect(panel.getByRole('link').first()).toBeFocused();
    await page.keyboard.press('Escape');
    await expect(trigger).toBeFocused();
    await expect(trigger).toHaveAttribute('aria-expanded', 'false');
    await expect(panel).toBeHidden();
    expectClean(page);
  });

  test(`mobile close control belongs to the modal tab order (${scheme})`, async ({ page }) => {
    await page.addInitScript(s => localStorage.setItem('foojay-theme', s), scheme);
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto(PAGES.home);
    const open = page.getByRole('button', { name: 'Open menu', exact: true });
    await open.press('Enter');
    const dialog = page.getByRole('dialog', { name: 'Navigation' });
    const close = dialog.getByRole('button', { name: 'Close menu', exact: true });
    await expect(close).toBeFocused();
    await expect(page.locator('main')).toHaveJSProperty('inert', true);
    await close.press('Shift+Tab');
    await expect(dialog.getByRole('link', { name: 'Write for Foojay' })).toBeFocused();
    await page.keyboard.press('Tab');
    await expect(close).toBeFocused();
    await close.press('Space');
    await expect(dialog).toHaveCount(0);
    await expect(open).toBeFocused();
    await expect(page.locator('main')).toHaveJSProperty('inert', false);
    await open.press('Enter');
    await page.setViewportSize({ width: 1440, height: 1000 });
    await expect(page.locator('main')).toHaveJSProperty('inert', false);
    await expect(page.getByRole('button', { name: 'News', exact: true })).toBeFocused();
    expectClean(page);
  });
}

test('header actions fit at the previous overflow breakpoint and 200% text', async ({ page }) => {
  await page.setViewportSize({ width: 1120, height: 1000 });
  await page.goto(PAGES.home);
  const fits = () => page.evaluate(() => document.documentElement.scrollWidth <= innerWidth);
  expect(await fits()).toBe(true);
  await page.locator('.site-header__inner > .header-actions [data-search-toggle]').click();
  await expect(page.locator('.site-header__inner > .header-actions input[name="q"]')).toBeFocused();
  expect(await fits()).toBe(true);
  await page.keyboard.press('Escape');
  await page.setViewportSize({ width: 1280, height: 900 });
  await page.addStyleTag({ content: 'html { font-size: 200%; }' });
  expect(await fits()).toBe(true);
  for (const label of ['Write for Foojay', 'Join our Slack']) {
    const box = await page.locator('.site-header__inner > .header-actions').getByRole('link', { name: label }).boundingBox();
    expect(box.x).toBeGreaterThanOrEqual(0);
    expect(box.x + box.width).toBeLessThanOrEqual(1280);
  }
});


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

for (const scheme of ['light', 'dark']) {
  test(`closing the drawer removes its controls from Tab order immediately (${scheme})`, async ({ page }) => {
    await page.addInitScript(s => localStorage.setItem('foojay-theme', s), scheme);
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto(PAGES.home);
    await page.getByRole('button', { name: 'Open menu', exact: true }).press('Enter');
    await page.getByRole('button', { name: 'Close menu', exact: true }).press('Space');
    await page.keyboard.press('Tab');
    expect(await page.evaluate(() => !!document.activeElement.closest('#nav-shell'))).toBe(false);
    expect(await focusBounds(page)).toEqual({ visible: true, inert: false });
    expectClean(page);
  });

  test(`Escape removes the collapsed search input from reverse Tab order (${scheme})`, async ({ page }) => {
    await page.addInitScript(s => localStorage.setItem('foojay-theme', s), scheme);
    await page.setViewportSize({ width: 1440, height: 1000 });
    await page.goto(PAGES.home);
    const search = page.locator('.site-header__inner > .header-actions [data-search-toggle]');
    await search.press('Enter');
    await page.keyboard.press('Escape');
    await page.keyboard.press('Shift+Tab');
    await expect(page.getByRole('button', { name: 'About', exact: true })).toBeFocused();
    expectClean(page);
  });
}

test('leaving a disclosure with the pointer does not hide its keyboard focus', async ({ page }) => {
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto(PAGES.home);
  const news = page.getByRole('button', { name: 'News', exact: true });
  await news.hover();
  await news.focus();
  await page.keyboard.press('Tab');
  const panel = page.locator('#' + await news.getAttribute('aria-controls'));
  await expect(panel.getByRole('link').first()).toBeFocused();
  await page.mouse.move(1400, 900);
  await page.waitForTimeout(350); // Exceeds the actual 140ms hover-close timer.
  await expect(panel).toBeVisible();
  expect(await focusBounds(page)).toEqual({ visible: true, inert: false });
  await page.keyboard.press('Escape');
  await expect(news).toBeFocused();
});
