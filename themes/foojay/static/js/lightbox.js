/* Click-to-enlarge for every content image -- no per-post markup needed.
   Applies to article body images (.prose img) and the post hero. Images that
   are already links open that link (if it points at an image, it's shown in the
   lightbox; otherwise the link is left to do its thing).

   Once open, the lightbox steps through every enlargeable image on the page in
   document order -- the < / > buttons, the arrow keys, or a swipe -- so a
   gallery can be read through without closing and reopening it. Vanilla JS,
   no deps. */
(function () {
  "use strict";

  var SELECTOR = ".prose img, img.post-hero";
  var IMG_HREF = /\.(jpe?g|png|gif|webp|svg|avif)(\?|#|$)/i;
  var SWIPE_MIN = 40; // px of horizontal travel before a touch counts as a swipe

  /* The full-size source for an image: the href of the link around it when that
     points at an image, otherwise the image itself. Null means "not ours" --
     the image sits inside a link to something else, which is left alone. */
  function fullSrc(img) {
    var link = img.closest("a");
    if (!link) return img.currentSrc || img.src;
    var href = link.getAttribute("href") || "";
    return IMG_HREF.test(href) ? href : null;
  }

  function init() {
    /* The page's enlargeable images, in document order, one entry per distinct
       full-size source: a post whose featured image is also its first gallery
       image would otherwise show the same picture twice in a row. Both those
       thumbnails open the same entry. */
    var items = [];
    var indexOfSrc = Object.create(null);
    var openers = [];
    Array.prototype.forEach.call(document.querySelectorAll(SELECTOR), function (img) {
      var full = fullSrc(img);
      if (full === null) return; // linked to something that isn't an image
      var index = indexOfSrc[full];
      if (index === undefined) {
        index = items.length;
        items.push({ src: full, alt: img.getAttribute("alt") || "" });
        indexOfSrc[full] = index;
      }
      openers.push({ img: img, index: index });
    });
    if (!items.length) return;

    var overlay = document.createElement("div");
    overlay.className = "lightbox";
    overlay.setAttribute("aria-hidden", "true");
    overlay.innerHTML =
      '<button class="lightbox__close" type="button" aria-label="Close">×</button>' +
      '<button class="lightbox__nav lightbox__nav--prev" type="button" aria-label="Previous image">‹</button>' +
      '<img class="lightbox__img" alt="">' +
      '<button class="lightbox__nav lightbox__nav--next" type="button" aria-label="Next image">›</button>';
    document.body.appendChild(overlay);
    var big = overlay.querySelector(".lightbox__img");
    var prev = overlay.querySelector(".lightbox__nav--prev");
    var next = overlay.querySelector(".lightbox__nav--next");

    // A single image on the page has nothing to step to.
    if (items.length < 2) {
      prev.hidden = true;
      next.hidden = true;
    }

    var current = 0;

    function show(index) {
      current = (index + items.length) % items.length; // wraps both ways
      big.src = items[current].src;
      big.alt = items[current].alt;
    }
    function open(index) {
      show(index);
      overlay.classList.add("is-open");
      overlay.setAttribute("aria-hidden", "false");
      document.body.classList.add("lightbox-open");
    }
    function close() {
      overlay.classList.remove("is-open");
      overlay.setAttribute("aria-hidden", "true");
      document.body.classList.remove("lightbox-open");
      big.removeAttribute("src");
    }
    function isOpen() {
      return overlay.classList.contains("is-open");
    }

    openers.forEach(function (opener) {
      opener.img.classList.add("is-zoomable");
      opener.img.addEventListener("click", function (e) {
        e.preventDefault(); // an image wrapped in a link opens here, not there
        open(opener.index);
      });
    });

    prev.addEventListener("click", function () { show(current - 1); });
    next.addEventListener("click", function () { show(current + 1); });

    overlay.addEventListener("click", function (e) {
      // Only the backdrop and the × close; the arrows handle their own clicks.
      if (e.target === overlay || e.target.classList.contains("lightbox__close")) close();
    });

    document.addEventListener("keydown", function (e) {
      if (!isOpen()) return;
      if (e.key === "Escape") close();
      else if (e.key === "ArrowLeft") show(current - 1);
      else if (e.key === "ArrowRight") show(current + 1);
      else return;
      e.preventDefault();
    });

    // Swipe: the arrows are small on a phone, where dragging is the natural move.
    var touchX = null;
    overlay.addEventListener("touchstart", function (e) {
      touchX = e.changedTouches[0].clientX;
    }, { passive: true });
    overlay.addEventListener("touchend", function (e) {
      if (touchX === null || items.length < 2) return;
      var dx = e.changedTouches[0].clientX - touchX;
      touchX = null;
      if (Math.abs(dx) >= SWIPE_MIN) show(current + (dx < 0 ? 1 : -1));
    }, { passive: true });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
