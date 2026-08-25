import { test as base, expect } from '@playwright/test';
import { loadPages } from './discover.mjs';

export const PAGES = loadPages();

/**
 * Every page under test gets two things.
 *
 * THIRD PARTIES ARE STUBBED, NEVER FETCHED. 438 posts embed a YouTube player
 * and the fonts come from Google; letting a test suite load them would make it
 * slow, make it fail when someone else's CDN has a bad afternoon, and send a
 * request to YouTube for every build. They are answered locally with an empty
 * body of the right type instead -- so the page still lays out, the iframe is
 * still there to assert on, and nothing leaves the runner. Checking that a
 * YouTube embed actually PLAYS is not something a deploy gate can honestly do:
 * it would be testing YouTube.
 *
 * PROBLEMS ARE COLLECTED, so any test can assert the page came up clean.
 * `pageerror` is an uncaught exception -- unambiguous, and never caused by the
 * stubbing above. Same-origin 4xx/5xx are ours. Console messages are
 * deliberately NOT collected: a stubbed resource makes Chrome log errors that
 * say nothing about this site.
 */
export const test = base.extend({
  page: async ({ page }, use) => {
    const problems = { errors: [], failed: [] };
    const stubbed = [];

    await page.route('**/*', (route) => {
      const url = route.request().url();
      if (url.startsWith('http://127.0.0.1:')) return route.continue();
      stubbed.push(url);
      const type = route.request().resourceType();
      const contentType = type === 'stylesheet' ? 'text/css'
        : type === 'script' ? 'text/javascript'
        : type === 'document' ? 'text/html'
        : type === 'font' ? 'font/woff2'
        : 'text/plain';
      return route.fulfill({ status: 200, contentType, body: '' });
    });

    page.on('pageerror', (e) => problems.errors.push(String(e)));
    page.on('response', (r) => {
      if (r.url().startsWith('http://127.0.0.1:') && r.status() >= 400) {
        problems.failed.push(`${r.status()} ${new URL(r.url()).pathname}`);
      }
    });

    page.problems = problems;
    page.stubbed = stubbed;
    await use(page);
  },
});

/** Assert the page came up with no uncaught exception and no failed asset of ours. */
export function expectClean(page) {
  expect(page.problems.errors, 'uncaught JavaScript errors').toEqual([]);
  expect(page.problems.failed, 'failed same-origin requests').toEqual([]);
}

export { expect };
