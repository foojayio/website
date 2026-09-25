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
    var pause = root.querySelector('[data-ad-pause]');
    if (slides.length < 2 || !prev || !next) return;

    var timer = null;
    // Pause and keyboard use persist until an explicit Resume action.
    var stopped = reduced;

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

    /* Driving it by hand ends the autoplay: someone stepping through the
       banners is reading them, and having the track move again on its own two
       seconds later takes the one they chose off the screen. */
    prev.addEventListener('click', function () { stopped = true; stop(); go(current() - 1); syncPause(); });
    next.addEventListener('click', function () { stopped = true; stop(); go(current() + 1); syncPause(); });
    function syncPause() {
      if (!pause || pause.hidden) return;
      pause.setAttribute('aria-label', stopped ? 'Resume banner rotation' : 'Pause banner rotation');
      pause.dataset.paused = stopped ? 'true' : 'false';
    }

    function start() {
      /* No autoplay when the reader has asked for less motion: a banner that
         moves on its own is exactly what that setting is about. */
      if (stopped || timer) return;
      timer = setInterval(function () {
        if (document.hidden) return;          /* don't cycle a background tab */
        go(current() + 1);
      }, DELAY);
    }
    function stop() {
      if (timer) { clearInterval(timer); timer = null; }
    }

    // Focus stops rotation until the reader explicitly resumes (APG carousel).
    root.addEventListener('mouseenter', stop);
    root.addEventListener('mouseleave', start);
    root.addEventListener('focusin', function (event) {
      stopped = true;
      stop();
      syncPause();
      var slide = event.target.closest('.ad-carousel__slide');
      if (slide) go(slides.indexOf(slide), false);
    });

    /* Pause/play. `stopped` is what start() honours, so this survives the
       hover and focus handlers above -- which is the whole point: a pause a
       mouse movement undoes is not a pause. */
    if (pause) {
      var setPaused = function (on) {
        stopped = on;
        pause.setAttribute('aria-label', on ? 'Resume banner rotation' : 'Pause banner rotation');
        pause.dataset.paused = on ? 'true' : 'false';
        if (on) stop(); else start();
      };
      // Pointer focus stops rotation before click; retain the action the
      // reader pressed. Keyboard activation uses the current visible label.
      var pausedBeforePointer = null;
      pause.addEventListener('pointerdown', function () { pausedBeforePointer = stopped; });
      pause.addEventListener('pointercancel', function () { pausedBeforePointer = null; });
      pause.addEventListener('click', function (event) {
        var wasStopped = event.detail && pausedBeforePointer !== null ? pausedBeforePointer : stopped;
        pausedBeforePointer = null;
        setPaused(!wasStopped);
      });
      /* Not shown when nothing is moving: with prefers-reduced-motion there is
         no autoplay to stop, so the control would be a button that does
         nothing. */
      if (!reduced) {
        setPaused(false);
        pause.hidden = false;
      }
    }

    /* Revealed only now that they do something. */
    prev.hidden = false;
    next.hidden = false;
    start();
  }

  /* IMPRESSIONS AND CLICKS, posted to the counter in worker/views/ under
   * `ad-view/<slug>` and `ad-click/<slug>`.
   *
   * TWO PSEUDO-SECTIONS RATHER THAN `ad/<slug>/view`, which reads better but
   * has three segments and would not match the Worker's KEY. That is not an
   * oversight there: KEY deliberately bounds the SHAPE of a key instead of
   * allow-listing section names, precisely so a new kind of counter needs no
   * Worker change and no redeploy. Keeping to two segments is taking that
   * offer -- banner counting went live against the Worker already running on
   * foojay.io, with nothing to deploy and no window where a beacon is silently
   * dropped. The cost is cosmetic: data/views.json groups all the impressions
   * together and all the clicks together rather than pairing them per
   * campaign.
   *
   * SEPARATE FROM init(), and called even when init() bails out. init()
   * returns early for a single-banner carousel (nothing to page through, so no
   * arrows and no autoplay) -- but a lone banner is still shown and still
   * clicked, and folding the counting into init() would report zero for
   * exactly the case foojay runs most often between campaigns.
   *
   * AN IMPRESSION IS A SLIDE THE READER ACTUALLY SAW, not a page that happened
   * to contain one. Posting for every banner on load would hand each sponsor
   * the same number -- the page's own view count -- which says nothing about
   * their campaign and is already on /ad-stats/ as the site total. So: at least
   * half the slide in the viewport, held for a second, in a tab that is
   * actually in front. The one-second hold is what keeps a fast scroll past the
   * band from counting, and the visibility check keeps a backgrounded tab from
   * counting a carousel that autoplayed to itself.
   *
   * ONCE PER SLIDE PER PAGE LOAD. The autoplay comes back around every seven
   * seconds and would otherwise turn one reader into an impression a minute.
   * Deliberately NOT the sessionStorage dedup partials/views-beacon.html uses:
   * a reader who opens ten articles saw the banner ten times, and those are ten
   * impressions -- what that guard exists to stop is one article counting twice
   * on a refresh, which is not the same thing.
   */
  function counted(root) {
    var endpoint = root.dataset.adEndpoint;
    if (!endpoint) return;                     /* unconfigured or local build */

    function post(key, event) {
      var url = endpoint + '/hit/ad-' + event + '/' + encodeURIComponent(key);
      if (navigator.sendBeacon) navigator.sendBeacon(url);
      else fetch(url, { method: 'POST', mode: 'no-cors', keepalive: true }).catch(function () {});
    }

    var slides = root.querySelectorAll('[data-ad-key]');
    for (var i = 0; i < slides.length; i++) {
      (function (slide) {
        var key = slide.dataset.adKey;
        if (!key) return;

        /* The primary CTA only. The optional second button is an INTERNAL link
           (partials/ad-slide.html), so counting it would mix "went to the
           advertiser" with "went to a foojay page" in the one number a sponsor
           reads as click-through. */
        var cta = slide.querySelector('.ad__btn:not(.ad__btn--ghost)');
        if (cta) {
          cta.addEventListener('click', function () { post(key, 'click'); });
        }

        /* No IntersectionObserver (a browser old enough that it also lacks
           sendBeacon) means no impression rather than a guessed one. An
           undercount is a number a sponsor can trust the direction of; a
           guess is not. */
        if (!window.IntersectionObserver) return;

        var seen = false, hold = null;
        var io = new IntersectionObserver(function (entries) {
          for (var e = 0; e < entries.length; e++) {
            var showing = entries[e].isIntersecting && entries[e].intersectionRatio >= 0.5;
            if (showing && !seen && !hold) {
              hold = setTimeout(function () {
                hold = null;
                if (seen || document.visibilityState !== 'visible') return;
                seen = true;
                io.disconnect();
                post(key, 'view');
              }, 1000);
            } else if (!showing && hold) {
              clearTimeout(hold);
              hold = null;
            }
          }
        }, { threshold: [0, 0.5, 1] });
        io.observe(slide);
      })(slides[i]);
    }
  }

  var roots = document.querySelectorAll('[data-ad-carousel]');
  for (var i = 0; i < roots.length; i++) {
    counted(roots[i]);
    init(roots[i]);
  }
})();
