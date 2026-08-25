// Where the built site is and how it is addressed. Shared by the server, the
// Playwright config and the page discovery, so all three agree by construction.
import { readFileSync, existsSync } from 'node:fs';
import { join, dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

/**
 * The repo root, from THIS FILE's location rather than from the working
 * directory. Playwright runs `webServer.command` with the cwd it chooses (the
 * config's directory by default, not the repo root), so a relative
 * "public"/"tests/e2e/server.mjs" resolves somewhere different depending on who
 * started the process -- which is how CI ended up looking for
 * tests/e2e/tests/e2e/server.mjs. Nothing here is cwd-relative for that reason.
 */
export const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), '..', '..');
export const SERVER = join(ROOT, 'tests', 'e2e', 'server.mjs');

export const PUBLIC_DIR = process.env.SITE_DIR
  ? resolve(process.env.SITE_DIR)
  : join(ROOT, 'public');
export const PORT = Number(process.env.SITE_PORT || 8099);

/**
 * The path the site is served at, read from the home page's own canonical --
 * exactly the way scripts/validate/BuiltSite.java derives it. So this is
 * "/website/" for a trial build and "/" for a production one, with nothing to
 * configure and nothing to remember to change at cutover.
 */
export function basePath() {
  const home = join(PUBLIC_DIR, 'index.html');
  if (existsSync(home)) {
    const html = readFileSync(home, 'utf8');
    const m = html.match(/<link[^>]+rel=["']?canonical["']?[^>]*>/i);
    const href = m && m[0].match(/href=["']?([^"'\s>]+)/i);
    if (href) {
      try {
        const p = new URL(href[1]).pathname;
        return p.endsWith('/') ? p : p + '/';
      } catch { /* a relative canonical: fall through */ }
    }
  }
  return '/';
}

export const BASE_URL = () => `http://127.0.0.1:${PORT}${basePath()}`;
