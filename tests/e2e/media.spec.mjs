import { test, expect, expectClean, PAGES } from './fixtures.mjs';

/**
 * What "does the video play" can honestly mean on this site.
 *
 * Foojay's video is 438 YouTube embeds, 19 Vimeo, and exactly one self-hosted
 * .mp4 in a post bundle. Those are two different questions and only one of them
 * belongs in a deploy gate:
 *
 *   - A third-party embed is not ours to test. Asserting that a YouTube player
 *     reaches "playing" is asserting that YouTube is up, on a check that blocks
 *     our deploy. So the embed is checked STRUCTURALLY -- it is there, it has an
 *     absolute https source, and it survived the markdown pipeline -- and the
 *     player itself is stubbed out (see fixtures.mjs).
 *   - A file we serve IS ours to test, all the way to decoding. This is the
 *     failure cleanup/images.py had to learn about the hard way: a killed
 *     encode left a 0-byte file that every "does it exist" check called fine.
 *     Existence is not playability, so the browser is made to decode it.
 */

test('every self-hosted video is served, and actually decodes', async ({ page }) => {
  const media = PAGES.media || [];
  test.skip(!media.length, 'the build has no self-hosted video');

  await page.goto('');
  for (const path of media) {
    const response = await page.request.head(path);
    expect(response.status(), `${path} should be served`).toBe(200);
    expect(response.headers()['content-type'], `${path} content type`).toMatch(/^video\//);
    expect(Number(response.headers()['content-length'] || 0),
      `${path} is empty -- a 0-byte file passes every existence check`).toBeGreaterThan(1024);

    // Range support is what a <video> uses to start before it has the file.
    const ranged = await page.request.get(path, { headers: { Range: 'bytes=0-1023' } });
    expect(ranged.status(), `${path} should answer a range request`).toBe(206);

    // The real thing: hand it to the browser and wait for enough decoded data
    // to play. A truncated file fails here and nowhere else.
    const state = await page.evaluate(async (src) => {
      const video = document.createElement('video');
      video.preload = 'auto';
      video.muted = true;
      video.src = src;
      document.body.appendChild(video);
      return await new Promise((resolve) => {
        const done = (how) => resolve({
          how, duration: video.duration,
          // 1 aborted, 2 network, 3 decode, 4 format not supported by THIS browser.
          errorCode: video.error ? video.error.code : null,
        });
        video.addEventListener('canplay', () => done('canplay'), { once: true });
        video.addEventListener('error', () => done('error'), { once: true });
        setTimeout(() => done('timeout'), 15_000);
      });
    }, path);

    // MEDIA_ERR_SRC_NOT_SUPPORTED is a fact about the TEST browser, not about
    // the site: Playwright ships the open-source Chromium build, which carries
    // no proprietary codecs, and this file is H.264 (canPlayType returns "" for
    // avc1 and "probably" for vp9 -- so asking "can it play any video" is the
    // wrong question, and answers yes). A decode or network error is ours: that
    // is a file which downloads and then will not play.
    if (state.how === 'error' && state.errorCode === 4) {
      test.info().annotations.push({
        type: 'codec not in this browser',
        description: `${path} is served and range-requestable; this Chromium build cannot decode it`,
      });
      continue;
    }

    expect(state.how, `${path} never became playable (media error ${state.errorCode})`).toBe('canplay');
    expect(state.duration, `${path} has no duration`).toBeGreaterThan(0);
  }
});

test('an embedded video survives the pipeline into the page', async ({ page }) => {
  test.skip(!PAGES.video, 'no post in this build embeds a video');
  await page.goto(PAGES.video);

  const frames = page.locator('.prose iframe[src]');
  expect(await frames.count(), 'the embed should still be in the article').toBeGreaterThan(0);

  for (const src of await frames.evaluateAll((els) => els.map((e) => e.getAttribute('src')))) {
    expect(src, 'an embed with no source renders an empty box').toBeTruthy();
    // Absolute and https: a protocol-relative or http embed is blocked outright
    // by the browser on an https page, which looks identical to a broken post.
    expect(src.startsWith('https://'), `${src} is not an absolute https URL`).toBe(true);
  }
  expectClean(page);
});

test('every image the site serves itself actually loads', async ({ page }) => {
  // Hotlinked heroes are excluded on purpose -- 74 posts point at someone
  // else's host and 11 of those are already dead. That is a content problem
  // post-thumb.html handles with an onerror fallback, not a reason to block a
  // deploy on a stranger's uptime.
  for (const kind of ['home', 'today', 'gallery', 'author', 'sponsors', 'board']) {
    const path = PAGES[kind];
    if (!path) continue;
    await page.goto(path);
    // Most images are loading="lazy", so without walking the page they never
    // load and there is nothing to find broken -- the check would pass by
    // never looking.
    await page.evaluate(async () => {
      for (let y = 0; y < document.body.scrollHeight; y += window.innerHeight) {
        window.scrollTo(0, y);
        await new Promise((r) => setTimeout(r, 60));
      }
      window.scrollTo(0, 0);
    });
    await page.waitForLoadState('networkidle');

    const broken = await page.evaluate(() => Array.from(document.images)
      .filter((img) => img.currentSrc.startsWith('http://127.0.0.1:'))
      .filter((img) => img.complete && img.naturalWidth === 0)
      .map((img) => new URL(img.currentSrc).pathname));

    expect(broken, `broken same-origin images on ${path}`).toEqual([]);
  }
});
