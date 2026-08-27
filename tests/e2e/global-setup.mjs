/**
 * Which pages the browser checks run against is worked out ONCE, here, from the
 * build -- see discover.mjs for why they are a discovery and not a list.
 *
 * IT HAS TO BE globalSetup AND NOT THE CONFIG'S MODULE BODY. Playwright loads
 * playwright.config.mjs in the main process AND again in every worker, so a
 * top-level writeFileSync ran five times over on CI's four workers -- and one
 * worker truncating .pages.json while another read it in fixtures.mjs is
 * exactly what "SyntaxError: Unexpected end of JSON input at discover.mjs:122"
 * was: a red deploy caused by nothing on the site, on a suite whose whole point
 * is that a failure means a real bug. It also walked the 4200-page build five
 * times to compute the same answer. globalSetup runs once, in the main process,
 * before any worker exists.
 *
 * The write is atomic anyway -- staged to a temp file and renamed, the same
 * rule cleanup/images.py had to learn -- so no reader can ever observe a
 * half-written file, whatever comes to run alongside this later.
 */
import { renameSync, writeFileSync } from 'node:fs';
import { discover, PAGES_FILE } from './discover.mjs';

export default function globalSetup() {
  const staging = `${PAGES_FILE}.tmp`;
  writeFileSync(staging, JSON.stringify(discover(), null, 2));
  renameSync(staging, PAGES_FILE);
}
