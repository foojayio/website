/**
 * Picks the pages the browser checks run against, by looking at what the build
 * actually contains rather than by naming URLs in a list. A hardcoded "the post
 * with the mermaid diagram is /today/foo/" rots the moment that post is renamed
 * or a newer one lands -- and it rots SILENTLY, into a test that still passes
 * because the page still loads and simply has no diagram on it.
 *
 * First match in sorted order, so the choice is deterministic across runs and
 * machines: two runs of the same build test the same pages.
 *
 * Run by globalSetup, written to .pages.json, read by the specs.
 */
import { readdirSync, readFileSync, statSync, writeFileSync } from 'node:fs';
import { join, sep } from 'node:path';
import { PUBLIC_DIR } from './site.mjs';

/** What each feature looks like in the built (and minified) HTML. */
const FEATURES = {
  // A ```mermaid fence -- render-codeblock.html emits this, mermaid.js swaps in an <svg>.
  mermaid: /<pre class=["']?mermaid["' ]/i,
  // A ```java fence, rendered back into the element EnlighterJS looks for.
  code: /EnlighterJSRAW/,
  // The {{< gallery >}} shortcode: several images the lightbox steps through.
  gallery: /<figure class=["']?gallery["' ]/i,
  // An embedded video. 438 posts carry one; none of them is self-hosted.
  video: /<iframe[^>]+src=["']?https:\/\/www\.youtube\.com\/embed\//i,
};

/** Fixed pages -- these have a URL because of what they ARE, not what is on them. */
const FIXED = {
  home: './',
  today: 'today/',
  search: 'search/',
  calendar: 'calendar/',
  jugs: 'jugs/',
  champions: 'java-champions/',
  sitemap: 'sitemap/',
  sponsors: 'our-sponsors/',
  board: 'board/',
  notFound: '404.html',
};

/** The first page under a prefix, so "an author profile" needs no author named. */
function firstUnder(prefix) {
  const dir = join(PUBLIC_DIR, prefix);
  let names;
  try { names = readdirSync(dir).sort(); } catch { return null; }
  for (const name of names) {
    const p = join(dir, name);
    try {
      if (statSync(p).isDirectory() && statSync(join(p, 'index.html')).isFile()) {
        return `${prefix}${name}/`;
      }
    } catch { /* not a page directory */ }
  }
  return null;
}

/** Every self-hosted video in the build, as URLs below the base path. */
function mediaFiles() {
  const out = [];
  const walk = (dir) => {
    let entries;
    try { entries = readdirSync(dir, { withFileTypes: true }); } catch { return; }
    for (const e of entries.sort((a, b) => (a.name < b.name ? -1 : 1))) {
      const p = join(dir, e.name);
      if (e.isDirectory()) walk(p);
      else if (/\.(mp4|webm|ogv)$/i.test(e.name)) out.push(p.slice(PUBLIC_DIR.length + 1).split(sep).join('/'));
    }
  };
  walk(PUBLIC_DIR);
  return out;
}

function* htmlUnder(dir) {
  let entries;
  try { entries = readdirSync(dir, { withFileTypes: true }); } catch { return; }
  for (const e of entries.sort((a, b) => (a.name < b.name ? -1 : 1))) {
    const p = join(dir, e.name);
    if (e.isDirectory()) yield* htmlUnder(p);
    else if (e.name.endsWith('.html')) yield p;
  }
}

export function discover() {
  const pages = { ...FIXED };
  const wanted = new Set(Object.keys(FEATURES));

  // Only articles are searched: every feature above is something an author puts
  // in a post body, and scanning the other 2000 pages to prove that costs time
  // for no extra coverage.
  for (const file of htmlUnder(join(PUBLIC_DIR, 'today'))) {
    if (!wanted.size) break;
    const html = readFileSync(file, 'utf8');
    for (const key of [...wanted]) {
      if (FEATURES[key].test(html)) {
        pages[key] = file.slice(PUBLIC_DIR.length + 1).split(sep).slice(0, -1).join('/') + '/';
        wanted.delete(key);
      }
    }
  }
  for (const key of wanted) pages[key] = null;   // the build genuinely has none

  pages.author = firstUnder('today/author/');
  pages.category = firstUnder('today/category/');
  pages.sponsor = firstUnder('sponsor/');
  pages.pedia = firstUnder('pedia/');
  pages.pager = 'today/page/2/';
  // Self-hosted media is found on DISK, not by looking for a link to it: the
  // one .mp4 in the build is a bundle resource whose post still links to the
  // WordPress /wp-content/ path it was imported from, so there is no working
  // link to discover. What matters is that the file itself is served and
  // decodable -- a 0-byte or truncated media file is exactly the failure
  // cleanup/images.py had to learn to guard against.
  pages.media = mediaFiles();
  return pages;
}

export const PAGES_FILE = new URL('.pages.json', import.meta.url).pathname;

export function loadPages() {
  return JSON.parse(readFileSync(PAGES_FILE, 'utf8'));
}

if (import.meta.url === `file://${process.argv[1]}`) {
  const pages = discover();
  writeFileSync(PAGES_FILE, JSON.stringify(pages, null, 2));
  console.log(pages);
}
