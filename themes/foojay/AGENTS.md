# `themes/foojay/` — templates, scripts and styles

Read the root `AGENTS.md` first; this file holds the detail for this folder.

### Templates, metadata and feeds

- **`$isTrial` is gone (2026-09-23), and the shape it had is what to keep.**
  While the Hugo site was a copy of still-live WordPress content, a crawlable
  preview host would have put ~2600 duplicate URLs into Google's index; four
  templates derived `baseURL != params.productionBaseURL` and suppressed
  indexing and analytics. **It was a derivation, never a config flag**, so it
  turned itself off when the URL changed with nobody doing anything. Nothing
  deploys anywhere else now, so it was four copies of an always-false
  condition and is deleted. If a preview host is ever wanted, re-derive it the
  same way rather than adding a flag.

- **A page title carries the short brand token.** `<title>` is the page title,
  then `(page N)` on a pager, then `| Foojay.io` — except on the home page,
  whose title IS `site.Title`, and on `og:title`, where `og:site_name` already
  carries the brand. Appending the full `site.Title` is what the template used
  to refuse, and that reasoning still holds: it is 33 of the ~60 characters
  Google renders, on 2147 titles that already average past that.

- **`og:image` always resolves, via a dedicated social card.** Pages without an
  `image:` fall back to `images/foojay-social-card.png` (1200x630) and the card
  is always `summary_large_image`. A second asset,
  `images/foojay-logo-square.png` (512x512, white ground), exists only as the
  schema.org `Organization` logo, because Google composites a logo onto white
  and the header wordmark is light-blue-on-transparent. **Don't** re-crop
  `foojay-logo.png` to make either.

  **A hero of `Favicon-3-2.png` is a migration artefact.** Yoast refuses to
  emit an SVG or AVIF as `og:image` and serves the favicon instead, so **48
  posts lost their real hero**, indistinguishable in the page HTML from a post
  with none. An **SVG hero is deliberately kept off `og:image`** — no preview
  scraper renders SVG, so such a post previews as the generic foojay card
  rather than its own picture. AVIF is left through, knowingly.

  **No post carries an SVG hero any more** (2026-09-23): the 17 that did were
  given 1600x900 JPEGs, rendered from the SVG with headless Chromium and
  composited with Pillow. Nothing is cropped — an opaque card is scaled to fit
  and the canvas extends its own border colour, so the letterbox is invisible;
  a transparent logo or diagram gets white, or `--surface-navy` when the mark
  is light and sparse. The four SVGs that are also in-article figures stay in
  their bundles. `baseof.html`'s skip remains the guard for the next one, and
  `template/post.md` already tells authors not to use SVG for a hero.

- **Structured data covers three page kinds and nothing else.** A post gets an
  `@graph` of `BlogPosting` + `Organization` + `BreadcrumbList`, an author gets
  `Person`, the home page gets `Organization` + `WebSite`. Everything is
  derived. **The Organization is ONE node** in its own partial — a post used to
  inline its own `publisher`, so foojay was two entities sharing a name, and
  the inline copy used the wide transparent wordmark Google cannot composite.
  **Breadcrumbs follow the post's FIRST category** and the last crumb has no
  `item`: one path from the root, matching the chips the page itself shows.
  Use `site.Language.Locale`, not the deprecated `LanguageCode`.

- **`partials/meta-description.html` is the single definition of a page's
  description**, used for `<meta name=description>`, `og:description` and the
  JSON-LD description, so the three cannot disagree. It replaced a bare
  `.Params.description | default site.Params.description`, which put one
  boilerplate string on **470 URLs** — 17% of the sitemap describing none of
  them. Derived per kind: an author's `bio:`, a term's name and page count, a
  sponsor's name and article count. An explicit `description:` always wins, and
  author bundles may now set one (`template/author.md` documents it) — worth
  doing wherever a long bio would otherwise be cut mid-list. Every source is
  **length-guarded at 200 characters** on a word boundary; the stored text is
  untouched.

- **Machine-readable copies are derived, never authored.** Posts and glossary
  entries also publish `<page>/index.md` (the MARKDOWN output, switched on by a
  cascade in the two `_index.md` files), and `/llms-full.txt` carries the
  glossary and Quick Start in full. The sitemap is our own template so a page
  whose `canonical:` names another URL is left out; glossary entries and Quick
  Start steps get `DefinedTerm` and `TechArticle` JSON-LD from `json-ld.html`.

- **`/search/` and the 404 are out of the index, and `/search/` is out of the
  sitemap** — it has no server-rendered content to rank on, so listing it and
  then telling it `noindex` was two contradictory instructions. The 404 also
  has **no** canonical: it is not a page with a URL of its own.

- **The home page's `<h1>` is visually hidden**, and is the only one on the
  site that is. The page opens straight into the lead card, so its first
  heading was an `<h2>` — the most linked-to page had no top-level heading. The
  text is `site.Title`, not a keyword line.

- **The webfonts are SELF-HOSTED and `vendor/fonts/fonts.css` is generated.**
  Barlow and Source Sans 3 came from `fonts.googleapis.com` on every page: a
  render-blocking stylesheet on someone else's server in front of the whole
  site's first paint. Both are SIL OFL 1.1. Five things are load-bearing:
  the `url()`s are **relative**, so nothing depends on the base path (the one
  class of bug `BuiltSite.java` cannot see, since it reads HTML not CSS);
  **exactly two faces are preloaded**, the ones every first paint waits on;
  **those preloads are also the test**, since a wrong path fails silently into
  Segoe UI but a preload is requested on every page; **regenerate with a modern
  User-Agent**, or Google serves TrueType at four times the bytes; and
  **`document.fonts.check()` cannot verify this** — it returns true for a
  family with no `@font-face` at all, which is exactly the failure being
  tested for.

- **The LCP image is eager and `fetchpriority="high"`; everything else is
  lazy.** The post hero and the home page's lead card are the LCP element and
  were being scheduled behind the stylesheet, the fonts and ten cards below the
  fold. `post-card.html` keeps `loading="lazy"`.

- **`partials/post-thumb.html` is the single definition of a card's thumbnail,
  and a card ALWAYS has one.** It renders the image over a derived placeholder
  tile, so three states are one box: the image loads; the post has no `image:`;
  or the image FAILS, because the `<img>` sits on top and carries
  `onerror="this.remove()"`. That third case is why it exists — **74 posts
  hotlink their hero and 11 are already dead**, and no build step can see that,
  which is why the fallback is an inline `onerror` rather than a check.

  The tile is **derived, never authored**: the label is the post's first
  category and the hue is `mod (hash.FNV32a $label) 360`, so a category reads
  as one colour site-wide. Three traps: `.post-card-image img` needs
  `position: relative; z-index: 1`, or the absolutely-positioned tile paints
  over every image on the site; it needs its own opaque background, since many
  WordPress heroes are transparent PNGs; and the search page's `hueFrom()` must
  stay byte-compatible with `hash.FNV32a` (FNV-1a with `Math.imul`).

  **`resource-url.html`'s external test is `http://`/`https://`, not a bare
  `http`** — four bundles hold a file whose *name* starts with the scheme, and
  a bare prefix check passed those through as relative paths that resolved on
  the post's own page and 404'd from every listing one directory deeper.

- **`partials/paginator.html` is the single definition of how a page
  paginates.** Not tidiness: `<head>` renders before `main`, so the canonical
  needs the pager first, and Hugo errors if `.Paginate` is called twice with
  different arguments. It must be plain `partial`, **never `partialCached`** —
  Hugo renders each pager by re-executing the same Page, so a page-keyed cache
  serves page 1's posts on every pager.

- **Author and sponsor profiles are BRANCH bundles, which is what makes them
  paginate.** `.Paginate` accepts only `home`, `section`, `taxonomy` and
  `term`; a prolific author has 290+ articles. URLs are unchanged via
  `[permalinks.section]`, and view-counter keys are unchanged because
  `.File.ContentBaseName` returns the folder name for `_index.md` too.
  **`content/authors/` is FLAT** — letter buckets became 23 sections claiming
  URLs like `/today/author/a/`.

  Four things broke silently in that conversion, all the same shape — a filter
  that still parses, still runs, and now matches nothing:
  1. **`site.RegularPages` does not contain branch bundles**, so every
     `where site.RegularPages "Section" "authors"` returned empty and the A-Z
     grid, sidebar widget, HTML sitemap and byline lookup rendered *nothing*.
     Use `partials/authors-all.html` and `partials/sponsors-ordered.html`.
  2. **`.IsPage` is false for a profile**, which silently stopped author view
     counts and `Person` JSON-LD. Both key off `type:` now.
  3. **A cascade applies to the page that declares it**, so
     `/today/author/` rendered with the *profile* layout. Both index files set
     `type:` explicitly, which always beats a cascaded value.
  4. **The RSS cascade targeted `kind: page`** and matched nothing afterwards.

- **"Edit this page on GitHub" is DERIVED from `.File.Path`**, and a page Hugo
  generates has none — **`with .File` is the whole guard**. It is a URL builder
  plus a link partial, because a podcast episode's note points at
  `transcript.md` beside the page rather than at `index.md`. Which layouts call
  it is a rule, not a list: a page gets the link when what you are reading IS
  its file, so the derived pages deliberately don't. It carries
  `data-pagefind-ignore`, or 2246 pages are a result for "edit".

- **Internal links are `.RelPermalink`; only absolute-by-contract URLs are
  `.Permalink`.** `.Permalink` is built from the CONFIGURED baseURL, which is
  not what `hugo server` serves. Keep `.Permalink` for canonical, `og:url`, the
  JSON-LD `url`/`@id`, RSS `<link>`/`<guid>` and alternate-format links.

- **A pager on a profile carries an `#articles` anchor; one on a listing does
  not** — on a profile the grid sits below a bio and stats, so paging without
  it throws the reader back to the top.

- **`partials/pagination.html` is ours, not `_internal/pagination.html`**,
  which had three faults fixable only in markup: a nested `<span>` inside each
  arrow's `<a>`, an `<a aria-current>` the theme's `.active span` rule could
  never match, and a disabled arrow rendered as an `<a>` with no href. Now
  every cell is one `.pagination__btn`, sized with `inline-flex` + fixed
  `height` rather than padding; non-links are `<span>`, so focus only lands
  where you can go. It renders **above and below** the grid, the window is
  **always five numbers** when five exist, and the ends are the numbers 1 and
  `$total`. **Use integer arithmetic** — Hugo's `math.*` return float64, and a
  comparison against an int page number is always false, which bit twice
  silently. On a phone it becomes two rows and drops `--far` cells.

- **Feeds are posts-only and capped at 30.** `[services.rss] limit = 30` —
  Hugo's default is unbounded, which made `/index.xml` 3.85 MB of 2584 items
  led by pages carrying `pubDate Mon, 01 Jan 0001`, a date some aggregators
  drop the item over. `layouts/index.rss.xml` filters `Section "posts"`,
  because "what's new on foojay" means articles.

- **`partials/feed-link.html` is the single definition of how a feed is
  OFFERED.** 484 working feeds were advertised only as a `<link rel=alternate>`
  in `<head>`, which no browser surfaces — in practice unreachable. It renders
  **nothing** when the page has no `rss` output, so no caller guards.

  **The label is NOT a parameter**, because it was one for a day and `/ai/`
  said something different from everywhere else. **One placement rule**: the
  page's own meta line, beside the read count. **It opens in a new tab** —
  deliberately not the usual same-origin rule, because the destination is not a
  page: browsers render a feed as raw XML or hand it to an app, so in the same
  tab a curious click destroys the reader's place. `--feed` is its own colour
  token in both schemes and deliberately not the canonical `#f26522`, which is
  2.77 on `--page-head-bg`.

- **`/calendar/` has a feed of the next 30 events**, which needed its own
  template because `[outputs]` takes *classes* of page and this is a `page`.
  Events come from `partials/calendar-events.html`, so the feed cannot disagree
  with the page. Two things an event feed faces that an article feed doesn't:
  **`pubDate` is the sync time, one value for every item, not the event's
  start** (a reader sorts by pubDate descending, so dating by start buries next
  week's meetup under next year's conference); and **so WHEN it is has to be in
  the item**, title and description both, **including the UTC offset**.

- **`/jugs/` and `/java-champions/` carry their WHOLE list**, the one place the
  30 limit is ignored: these lists barely change, so what a reader subscribes
  for is an ADDITION, and A-Z cut at 30 hides every JUG from H onwards for
  good. Their pubDates differ because their data does — a champion's `year` is
  a real per-item date (ordered `year` descending, dated 1 January), while a
  JUG has none, so a JUG item carries **no pubDate**, which is legal RSS 2.0
  and better than claiming all 100 were published today on every deploy.

- **A section whose children are BRANCH bundles has an EMPTY feed**, and three
  were shipping one — a `<channel>` with no `<item>`, advertised in `<head>`,
  where a reader could subscribe and receive nothing for ever. A sponsor
  profile gained its own template over `partials/sponsor-posts.html`; the two
  listing pages set `outputs: [html]`, since a feed of "new author profiles" is
  not a thing anyone wants.

- **An XML declaration is only a declaration at byte 0**, and 348 author feeds
  shipped a blank line above it — invisible, because a lenient reader takes it
  anyway. All four feed templates write it with `{{-`. Re-run a parse over
  every built feed after editing one; nothing else looks at these files.

- **`enableGitInfo` dates the pages that have no date, and sits BELOW `date:`**
  in the `[frontmatter]` chains. Leading with `:git` would stamp all 2147 posts
  with the migration date and tell Google the whole archive changed at once.
  This needs **full git history at build time**, hence `fetch-depth: 0` — on a
  shallow clone every page claims one identical lastmod.

- **`static/js/cluster-map.js` is the world map**, shared by `/java-champions/`
  and `/jugs/`. The template supplies only the points, the noun and an element
  id; grouping, badge tiers and cluster totals live in the script, or the same
  number would mean something different on each map.

  **One marker per PLACE, not per item.** Coordinates are city centres, so
  items genuinely share a point (22 champions on the USA centroid, 16 on
  London) and separate markers stack into one unclickable pin. **A cluster's
  number counts ITEMS, not markers** — markercluster's default counts markers,
  so a cluster over the Benelux would read "3" while covering 30 people. **A
  popup is built on OPEN, not at page load**: passing a node instead of a
  function built all 240 popup trees up front, and once each row carried an
  `<img>` that meant 420 requests at javachampions.org before any click.

  Smaller load-bearing points: popups are **DOM nodes, never an HTML string**,
  since every name is upstream data; the grouping key is **`toFixed(5)`**, so
  `51.5072` and `51.50720` don't split; `MarkerCluster.Default.css` is
  deliberately not loaded; the badge is **sized by content**, since "1" and
  "422" are different widths; and the `<script>` tag carries **no `defer`**,
  because the inline call runs at parse time.

  **Leaflet and markercluster are VENDORED** (v1.9.4 / v1.4.1, from `npm pack`,
  **no `.map` files**, verified byte-identical to what unpkg served). They came
  from a CDN, which cost three things: the map disappeared *silently* when
  unpkg was slow, the deploy gate could only skip its map tests, and every
  reader of those pages sent a third party a request. **The map TILES are still
  third-party and cannot be anything else** — but a failed tile leaves the
  markers working over an empty ground, where a failed library left nothing.

- **`lightbox.js` skips anything inside `.leaflet-container`**, and that is not
  a nicety: it binds `.prose img`, and **every map tile is an `<img>`**, so
  tiles took the zoom cursor, a click opened a 256px tile instead of panning,
  and they joined the ‹ › sequence. It has to be a **container check at click
  time, not a selector** — tiles are created and destroyed as the reader pans.
  **`.champions-table img` is excluded too**, and that one was live: all 422
  avatars were treated as content images, making a 422-face gallery. That one
  *is* a selector, since those images all exist when the script runs.

- **`/sitemap/` is an HTML page for readers, derived from the content tree** —
  not Hugo's `/sitemap.xml`. The footer linked it from every page and nothing
  answered the URL. Nesting comes from sorting on `.File.Path`, since a
  lexicographic path sort is already tree order.

  **Articles and Authors are sortable, filterable TABLES; pages, pedia and
  sponsors stay link lists** — the split is whether an entry has FIELDS worth
  comparing. Four rules: **the HTML is sorted and the JavaScript only adds the
  buttons** (so without JS there is no dead control); **a cell sorts on
  `data-sort-value`, never on what it shows** (a timestamp behind a date, a raw
  integer behind "68,330"); **every cell reuses the site's own partial** for
  what it shows, so this page cannot credit a post differently from a card;
  and **`table-filter.js` is generic**, with `data-filter-cols` limiting which
  columns are searched and the haystack cached on first keystroke rather than
  rendered into a per-row attribute.

  Three silent CSS traps: `.sitemap-table td` is class-plus-type, so it
  outranks a single class; a filtered-out `<tr>` needs
  `display: none !important`, because `display: table-row` beats `[hidden]`;
  and the sticky header only sticks above 60rem, since an overflow container
  becomes the positioning context.

- **`partials/calendar-events.html` is the single definition of what an event
  is.** It flattens both sources, enriches each event with what the templates
  and the JavaScript need, and is `partialCached`, so `/calendar/` and the home
  page band cannot disagree about a date, a place or a colour.

- **The home page's "next two weeks" band is a WINDOW, not a top-N.** The
  filter is an **overlap** (`end >= today` and `start <= today+14`), because a
  five-day conference that opened yesterday is still on. It renders **nothing**
  when the window is empty — a quiet fortnight is a reason to show no section.

- **`/calendar/` is two views of its events, and only one is content.** The
  **agenda is server-rendered and the month grid is not**: the agenda is real
  HTML, so it is what Pagefind indexes and what a reader without JavaScript
  gets, while the grid is built from a JSON island. The whole toolbar starts
  `hidden` and the script switches it on, and a narrow screen opens on the
  agenda. Times are formatted **in the template, in each event's own UTC
  offset** — rebuilding them in the browser would restate every event in the
  reader's timezone. Each JUG's colour is a hue hashed from its slug.

  **A multi-day event occupies every day it runs**, via a `days` array, so a
  conference is a band rather than one chip on the Monday — and that is why
  `days` exists instead of the script reading `date`/`endDate`, since a few
  upstream entries carry an end a week after their start.

  **The calendar has no past, so neither does the grid.** The back arrow stops
  at the current month, and the current month begins at the Monday of today's
  week — a **week**, not the day, because the columns are weekdays. Forward has
  no limit. **The month arrows mean different things in the two views**: in the
  grid they step a month, in the list they step to the next month that *has*
  events, since the agenda has no section for an empty one.

  **The toolbar state is in the URL** — `?month=YYYY-MM&view=grid|list`, so a
  month and view can be shared as a link. Both arrows, Today and the view
  buttons write it; a load with either parameter reads it back, clamped to the
  current month. `view=month` is accepted as a synonym for `grid`, since that
  is the button's label. **`replaceState`, not push**: the arrows are a
  scrubber, and Back should leave the page rather than walk months. A clean
  load writes nothing, so merely opening `/calendar/` never stamps a URL.

- **The palette is the LIVE SITE'S, read out of its own CSS.** The scaffold's
  "warm amber accent" was simply wrong — there is no amber token in that
  stylesheet at all. **Sample the logo, don't trust a description of it**:
  `foojay-logo.png` is 97,620px of exactly `#50c2f7`.

  Three load-bearing decisions: **a bright blue cannot be both the accent and
  accent-TEXT** (`#50c2f7` is 2.02:1 on white, so it is a fill and never text;
  `--brand-accent-strong` is the same hue at 4.75) — **realigning hues to the
  live design is the point, importing its contrast failures is not**; **the
  accent is the one brand fill that does not change between schemes**; and
  **`--on-accent`/`--on-accent-strong`/`--on-ink` exist because a fill that
  inverts cannot carry a literal label colour**. Category chips are green
  because a tag is green upstream, and `--brand-secondary` is the LINK colour.
  **Sponsor tiers were untouched**, so amber correctly survives where it means
  "gold" — a grep for warm hex will find them and they are not leftovers.

- **`.highlight-panel` is the single definition of "this block is the one to
  look at"**, used by the home page lead and the featured-author band. It is a
  class, not a selector list, because the site has ONE highlight treatment.
  **`--ink-muted` is redefined INSIDE the panel** — the token is 4.39:1 on the
  tinted ground and a dozen descendants use it, so stepping it once fixes all
  of them where naming each would be a dozen rules a new descendant misses.

- **The home page spotlights a DAY, not a post.** `index.html` takes every post
  sharing the newest post's publish DATE, because posts on one day cannot be
  ranked — a scheduled post carries no time, so a day's articles land at the
  same instant and Hugo's sort falls through to the file path. **523 of 1497
  publishing days carry more than one post.** Four details: the day comes from
  the **newest post, never `now`** (foojay does not publish daily); the
  selection is a **loop with a `break`, not a `where`**, since `where` cannot
  compare dates a calendar day at a time across UTC offsets; the grid below is
  `after (len $leads)`; and the panel grid is `auto-fit` where `.post-grid` is
  `auto-fill`. **Equal cards rather than one big card plus siblings** — keeping
  an anchor keeps the arbitrary pick, which is the bug.

- **The home page's "Getting started with Java" band is the QUICK START TRAIL,
  not posts** — a category is not an editorial decision. It was `first 4` of
  "Java Beginner", which authors reach for with anything approachable, so the
  four on show changed with whatever was published last and none of them
  answered "I don't have Java yet, where do I start?".

- **The quick start trail is described ONCE, in the pages themselves.** Three
  places render it, and each Markdown page also used to write its own list by
  hand — 38 links, all absolute `https://foojay.io/…` URLs, with titles that
  had already drifted. Now `partials/quick-start-pages.html` finds the children
  of a hub by `File.Dir`, uses **weight if every child has one, `Title`
  otherwise** (all-or-nothing, because a partly weighted folder is a mistake to
  notice), and each row's gloss is the target page's own `description:`. Two
  traps: **strip leading zeros before `int`**, since Hugo's `int` reads "08" as
  octal and FAILS THE WHOLE BUILD; and **a template cannot build an element
  NAME**, so `<{{ $tag }}>` is escaped to visible text.

- **The logo file is cropped to its artwork and sized in CSS.** A 1500x500
  export whose mark occupied 1022x352 meant the padding was a silent 30%
  shrink, and every attempt to make the logo bigger fought the file. Re-export
  a padded PNG and it shrinks again with nothing in the templates to show why.

- **The footer navigation is `[[menu.footer]]` in `hugo.toml`, not markup.** It
  was hardcoded, which made it a second definition of the site's navigation
  with nothing tying it to the first, and it had drifted exactly that way.
  Three things stay in the template because none is a link an editor picks: the
  brand blurb, `now.Year`, and the **RSS URL** (resolved from
  `site.Home.OutputFormats`, since the feed path is Hugo's to decide). Two
  traps: **Hugo lowercases config keys**, so `[[menu.footerLegal]]` resolves to
  nothing with no error — keep footer menu names all-lowercase; and `relURL`
  **does** normalise a trailing slash, so verify a claim like that against
  built HTML, not the template.

- **The header search field is collapsed to its magnifier, and the button is a
  real submit** — so the form still works with JavaScript off. Three details:
  the collapsed input is `visibility: hidden`, not just `opacity: 0`, or it
  stays a tab stop; that visibility is transitioned with a delay when closing
  but `0s` when opening, because **`focus()` on a `visibility: hidden` element
  does nothing**; and the mobile drawer copy is excluded from both the CSS and
  the JS filter.

- **Sponsors appear site-wide via the sidebar**, tier-ordered with the logo
  sized by tier. Deliberately NOT subject to the TOC-height cull that drops the
  other widgets on long posts — sponsor visibility is contractual.

- **The category index lives at `/today/category/`, next to its terms.** Hugo
  puts a taxonomy's list page at `/categories/` while the permalink puts every
  term one level down, so the index was orphaned from its own children and both
  the sidebar and the home page band pointed at `/today/` instead. Rendered
  alphabetically with a filter box, since the top categories are already shown
  elsewhere and the only reason to open a list of 123 is to find one.

- **The AI portal is a derived category landing page.** `/ai/` is what
  WordPress serves as the "Machine Learning" category with an editorial intro,
  so the page file holds the intro plus `list_category:` and the layout derives
  the rest. Don't turn it into a hand-picked `posts:` list. Its grid is **not**
  paginated (`.Paginate` doesn't work on a page kind), and its cards pass
  `maxCategories: 0`, because on a portal the chips are the navigation.

- **`/search/` gives each SECTION its own quota, never a global top-N.**
  Pagefind returns one relevance-ordered list, so a global `slice(0, 30)` is
  spent on whichever section matches most: on "pi4j", 328 pages matched and the
  30 rows rendered were 29 posts and one author — **the two authors whose bio
  says Pi4J were unreachable**. Each section now shows its first 10 with a Show
  more button and its OWN total in the heading. Four load-bearing details:
  **`pagefind.filters()` must be awaited once** before per-section counts
  exist; **the quota costs one filtered search per section that HAS a hit**;
  **a folded group is re-merged on `.score`**; and **posts are re-sorted
  newest-first over the WHOLE group on every render**, so a Show more batch
  can't interleave. Two guards for silent failures: a `runId` per query, and a
  fallback to one ungrouped list if the counts don't add up.

- **A post's comment thread is keyed on its slug, never its pathname.** giscus
  is configured `data-mapping="specific"` with the slug as the term — pathname
  mapping was the tempting default and a trap, since the trial served a
  different path and every thread would have been orphaned at cutover.
  `data-strict="1"` goes with it: non-strict is a fuzzy title search taking the
  first hit, and **30 foojay slugs are substrings of another slug**. Change any
  of mapping, term or strict and a mismatch shows up as an empty comment
  section, not an error.

  **The legacy WordPress comments are NOT in Discussions and must not be put
  there** — importing them got the posting account **banned by GitHub** a few
  posts in. They are `content/posts/**/comments.json`, rendered under the
  widget. Only a handful of real discussions exist, which is correct.
