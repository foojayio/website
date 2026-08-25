import { defineConfig, devices } from '@playwright/test';
import { writeFileSync } from 'node:fs';
import { PORT, basePath, ROOT, SERVER } from './site.mjs';
import { discover, PAGES_FILE } from './discover.mjs';

// Which pages to test is worked out once, from the build, and shared with every
// worker -- see discover.mjs for why they are not a hardcoded list.
writeFileSync(PAGES_FILE, JSON.stringify(discover(), null, 2));

export default defineConfig({
  testDir: '.',
  testMatch: '*.spec.mjs',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  // No retries, deliberately. Every third-party request is stubbed out (see
  // fixtures.mjs), so nothing here depends on the network and a failure that
  // comes and goes is a real bug in the page, not weather. A retry would hide
  // exactly the flake worth knowing about.
  retries: 0,
  workers: process.env.CI ? 4 : undefined,
  reporter: process.env.CI ? [['github'], ['list']] : [['list']],
  use: {
    baseURL: `http://127.0.0.1:${PORT}${basePath()}`,
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    // NOTHING MAY LEAVE THE RUNNER. The tests stub third parties at the route
    // layer (fixtures.mjs), but that is a Playwright-level promise and the site
    // sends a REAL view beacon: [params.views] endpoint is set to
    // foojay.io/api/views and views-beacon.html fires it on every page. A gate
    // that ran a browser over 20 pages and counted 20 reads against the live
    // counter on every deploy would quietly corrupt the numbers printed on the
    // site -- the one measurement here that is published.
    //
    // So DNS itself is switched off for everything but localhost, below the
    // level any page script can reach. Belt and braces on purpose: route
    // interception keeps the pages rendering cleanly, this makes the promise
    // true even for a request shape Playwright does not intercept.
    launchOptions: {
      args: ['--host-resolver-rules=MAP * ~NOTFOUND, EXCLUDE 127.0.0.1'],
    },
  },
  webServer: {
    // Absolute path plus an explicit cwd: `webServer.cwd` defaults to the
    // config's own directory, so a relative command resolved to
    // tests/e2e/tests/e2e/server.mjs and the server never started.
    cwd: ROOT,
    command: `node ${JSON.stringify(SERVER)} ${PORT}`,
    url: `http://127.0.0.1:${PORT}${basePath()}`,
    // ALWAYS launch it, locally too. `reuseExistingServer: !process.env.CI` is
    // the default advice and it hid a CI-only failure completely: a server left
    // running from an earlier session was silently reused on every local run,
    // so the launch path -- the thing CI actually does -- was never exercised
    // once, and the first real run of it failed on a doubled path. The cost of
    // not reusing is a fraction of a second; the cost of reusing was a red
    // deploy that looked green in every rehearsal.
    reuseExistingServer: false,
    timeout: 30_000,
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
