/* Banner carousel (partials/ad-track.html) -- drives both placements, the home
 * page band and the sidebar column.
 *
 * The track is already a working scroll-snap row in CSS, so this only ADDS
 * things: the two arrows and the autoplay. They are `hidden` in the markup and
 * revealed here, so a reader without JavaScript gets a swipeable band rather
 * than dead buttons -- the same posture as /calendar/'s toolbar and the
 * sortable tables on /sitemap/.
 *
 * Position is READ FROM THE SCROLL, never tracked in a variable of our own, so
 * a swipe, a trackpad scroll, an arrow and the autoplay all agree and there is
 * no state to get out of step with where the track actually is.
 */
(function () {
  var reduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  var DELAY = 7000;

  function init(root) {
    /* Guard against a double init. Only one placement renders per page today
       (the sidebar widget stands down on the home page), so the script tag
       appears once -- but that is a property of the templates, not of this file,
       and initialising twice would give the carousel two autoplay timers racing
       each other. */
    if (root.dataset.adReady) return;
    root.dataset.adReady = '1';

    var track = root.querySelector('[data-ad-track]');
    if (!track) return;
    var slides = Array.prototype.slice.call(track.children);
    var prev = root.querySelector('[data-ad-prev]');
    var next = root.querySelector('[data-ad-next]');
    if (slides.length < 2 || !prev || !next) return;

    var timer = null;

    /* Which slide is showing: whichever one's left edge is nearest the track's
       scroll position. Works mid-swipe and after a resize, where an index
       counter would drift. */
    function current() {
      var best = 0, bestGap = Infinity;
      for (var i = 0; i < slides.length; i++) {
        var gap = Math.abs(slides[i].offsetLeft - track.scrollLeft);
        if (gap < bestGap) { bestGap = gap; best = i; }
      }
      return best;
    }

    function go(i, smooth) {
      var n = slides.length;
      var target = ((i % n) + n) % n;   /* wrap both ways */
      track.scrollTo({ left: slides[target].offsetLeft, behavior: smooth === false || reduced ? 'auto' : 'smooth' });
    }

    /* Size the track to the slide actually showing, so the box is as tall as
       this banner rather than as tall as the longest one in the set -- and the
       arrows, which are centred on the container, land on the middle of the
       visible creative instead of on empty space beneath a short one.

       Read the slide's own height with `align-items: start` in force, so this is
       the card's natural height and not the stretched row height. */
    function fit() {
      var h = slides[current()].offsetHeight;
      if (h) track.style.height = h + 'px';
    }

    /* Re-fit on the frame after a scroll settles rather than on every scroll
       event: the height transition and the smooth scroll then run together, and
       a swipe does not thrash layout mid-gesture. */
    var fitQueued = false;
    function queueFit() {
      if (fitQueued) return;
      fitQueued = true;
      requestAnimationFrame(function () { fitQueued = false; fit(); });
    }
    track.addEventListener('scroll', queueFit, { passive: true });
    window.addEventListener('resize', queueFit);

    /* A banner's height changes AFTER init, twice over: the creative is
       loading="lazy" so it has no height at parse time, and the copy reflows at
       every breakpoint. Without this the track would keep whatever height the
       first slide happened to have before its image arrived. */
    if (window.ResizeObserver) {
      var ro = new ResizeObserver(queueFit);
      for (var s = 0; s < slides.length; s++) ro.observe(slides[s]);
    } else {
      /* No ResizeObserver: catch at least the image loads. */
      var imgs = track.querySelectorAll('img');
      for (var k = 0; k < imgs.length; k++) imgs[k].addEventListener('load', queueFit);
    }

    prev.addEventListener('click', function () { stop(); go(current() - 1); });
    next.addEventListener('click', function () { stop(); go(current() + 1); });

    function start() {
      /* No autoplay when the reader has asked for less motion: a banner that
         moves on its own is exactly what that setting is about. */
      if (reduced || timer) return;
      timer = setInterval(function () {
        if (document.hidden) return;          /* don't cycle a background tab */
        go(current() + 1);
      }, DELAY);
    }
    function stop() {
      if (timer) { clearInterval(timer); timer = null; }
    }

    /* Pause while the reader is actually looking at or using it -- including
       keyboard focus, so tabbing to the button doesn't have the slide move out
       from under them. */
    root.addEventListener('mouseenter', stop);
    root.addEventListener('mouseleave', start);
    root.addEventListener('focusin', stop);
    root.addEventListener('focusout', start);

    /* Revealed only now that they do something. */
    prev.hidden = false;
    next.hidden = false;
    fit();
    start();
  }

  var roots = document.querySelectorAll('[data-ad-carousel]');
  for (var i = 0; i < roots.length; i++) init(roots[i]);
})();
