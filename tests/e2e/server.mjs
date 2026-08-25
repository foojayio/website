/**
 * The "staging environment": the built site, served on localhost, reachable
 * only from this workflow run. GitHub Pages has no staging slot and this needs
 * none -- there is nothing to provision, nothing public, and nothing to clean
 * up afterwards.
 *
 * Written by hand rather than reaching for `npx serve` because the point is to
 * behave like GITHUB PAGES specifically: pretty URLs resolve to index.html, a
 * directory without a trailing slash 301s to one, a miss serves 404.html with a
 * 404 status, and everything lives under the base path -- so a link that
 * escapes /website/ 404s here exactly as it would in production. A generic
 * static server has its own opinions about all four.
 *
 * Usage: node tests/e2e/server.mjs [port]
 */
import { createServer } from 'node:http';
import { createReadStream, statSync, existsSync, readFileSync } from 'node:fs';
import { join, normalize, extname, sep } from 'node:path';
import { PUBLIC_DIR, PORT, basePath } from './site.mjs';

const port = Number(process.argv[2] || PORT);
const base = basePath();
const root = normalize(PUBLIC_DIR);

const TYPES = {
  '.html': 'text/html; charset=utf-8', '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8', '.mjs': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8', '.xml': 'application/xml; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8', '.svg': 'image/svg+xml', '.png': 'image/png',
  '.jpg': 'image/jpeg', '.jpeg': 'image/jpeg', '.gif': 'image/gif', '.webp': 'image/webp',
  '.avif': 'image/avif', '.ico': 'image/x-icon', '.woff2': 'font/woff2', '.woff': 'font/woff',
  '.ttf': 'font/ttf', '.mp4': 'video/mp4', '.webm': 'video/webm', '.mp3': 'audio/mpeg',
  '.pdf': 'application/pdf', '.zip': 'application/zip', '.wasm': 'application/wasm',
  // Pagefind ships its index as .pf_fragment / .pf_index / .pf_meta blobs.
  '.pf_fragment': 'application/octet-stream', '.pf_index': 'application/octet-stream',
  '.pf_meta': 'application/octet-stream',
};

/** Resolve a URL path to a file on disk, or a redirect, or nothing. */
function resolve(urlPath) {
  if (!urlPath.startsWith(base)) return { miss: true };
  let rel = decodeURIComponent(urlPath.slice(base.length));
  // Contain the path: a normalized relative path may not climb out of public/.
  const file = normalize(join(root, rel));
  if (file !== root && !file.startsWith(root + sep)) return { miss: true };

  if (existsSync(file)) {
    const st = statSync(file);
    if (st.isFile()) return { file, size: st.size };
    if (st.isDirectory()) {
      // Pages redirects a directory to its trailing-slash form before serving
      // the index, and a relative asset in the page depends on that slash.
      if (!urlPath.endsWith('/')) return { redirect: urlPath + '/' };
      const index = join(file, 'index.html');
      if (existsSync(index)) return { file: index, size: statSync(index).size };
    }
  }
  const asHtml = file.replace(/\/$/, '') + '.html';
  if (existsSync(asHtml)) return { file: asHtml, size: statSync(asHtml).size };
  return { miss: true };
}

const notFoundBody = (() => {
  const p = join(root, '404.html');
  return existsSync(p) ? readFileSync(p) : Buffer.from('404');
})();

createServer((req, res) => {
  const urlPath = new URL(req.url, 'http://127.0.0.1').pathname;
  const r = resolve(urlPath);

  if (r.redirect) {
    res.writeHead(301, { Location: r.redirect });
    return res.end();
  }
  if (r.miss) {
    res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
    return res.end(req.method === 'HEAD' ? undefined : notFoundBody);
  }

  const type = TYPES[extname(r.file).toLowerCase()] || 'application/octet-stream';
  // Range support is not decoration: it is what a <video> uses to start playing
  // before it has the whole file, so without it the media checks would be
  // testing a code path no real browser takes.
  const range = req.headers.range && /^bytes=(\d*)-(\d*)$/.exec(req.headers.range);
  if (range && r.size) {
    const start = range[1] ? Number(range[1]) : 0;
    const end = range[2] ? Math.min(Number(range[2]), r.size - 1) : r.size - 1;
    if (start > end || start >= r.size) {
      res.writeHead(416, { 'Content-Range': `bytes */${r.size}` });
      return res.end();
    }
    res.writeHead(206, {
      'Content-Type': type,
      'Content-Range': `bytes ${start}-${end}/${r.size}`,
      'Accept-Ranges': 'bytes',
      'Content-Length': end - start + 1,
    });
    if (req.method === 'HEAD') return res.end();
    return createReadStream(r.file, { start, end }).pipe(res);
  }

  res.writeHead(200, { 'Content-Type': type, 'Content-Length': r.size, 'Accept-Ranges': 'bytes' });
  if (req.method === 'HEAD') return res.end();
  createReadStream(r.file).pipe(res);
}).listen(port, '127.0.0.1', () => {
  console.log(`Serving ${root} at http://127.0.0.1:${port}${base}`);
});
