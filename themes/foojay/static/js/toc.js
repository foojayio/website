/* foojay.io — table of contents scroll-spy
   Highlights whichever heading is currently under the sticky header as the
   reader scrolls, for the "On this page" panel built by
   themes/foojay/layouts/partials/toc.html from Hugo's .TableOfContents.
   No-ops on any page without a .toc (most pages -- only posts/pages/pedia
   entries with h2/h3 headings get one). */
(function () {
  var toc = document.querySelector('.toc');
  if (!toc) return;

  var entries = Array.prototype.slice.call(toc.querySelectorAll('a[href^="#"]'))
    .map(function (link) {
      var heading = document.getElementById(decodeURIComponent(link.getAttribute('href').slice(1)));
      return heading ? { link: link, heading: heading } : null;
    })
    .filter(Boolean);
  if (!entries.length) return;

  var active = null;
  function setActive(link) {
    if (active === link) return;
    if (active) active.classList.remove('is-active');
    if (link) link.classList.add('is-active');
    active = link;
  }

  function headerHeight() {
    var header = document.querySelector('.site-header');
    return header ? header.offsetHeight : 64;
  }

  // A heading counts as "current" once it crosses a line just under the
  // sticky header; the -70% bottom margin keeps only headings near the top
  // of the viewport eligible, so scrolling through a long section doesn't
  // flicker between it and the next one.
  var observer = new IntersectionObserver(function (observed) {
    var visible = observed.filter(function (entry) { return entry.isIntersecting; });
    if (!visible.length) return;
    visible.sort(function (a, b) { return a.boundingClientRect.top - b.boundingClientRect.top; });
    var match = entries.find(function (e) { return e.heading === visible[0].target; });
    if (match) setActive(match.link);
  }, {
    rootMargin: '-' + (headerHeight() + 8) + 'px 0px -70% 0px',
    threshold: 0
  });

  entries.forEach(function (e) { observer.observe(e.heading); });
})();
