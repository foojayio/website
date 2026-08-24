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
  /* Images that are part of a MAP, not part of the content. /jugs/ and
     /java-champions/ render a Leaflet map inside `.prose`, and every map tile
     is an <img> -- so without this the tiles were picked up as content images:
     they took a zoom-in cursor, a click on the map opened the tile in the
     lightbox instead of panning, and the tiles joined the < / > sequence a
     real gallery steps through. Excluding the whole container rather than the
     tile class also covers Leaflet's marker-icon and marker-shadow images, in
     case a map ever uses the default pin instead of our own badge.

     A container check, not a selector, because tiles do not exist yet when
     this runs and are created and destroyed continuously as the reader pans:
     there is no moment at which a query could see them all. */
  var MAP_CONTAINER = ".leaflet-container";
  /* Avatars in a data table are UI, not pictures in an article -- same
     distinction the map container above draws. /java-champions/ renders its
     table inside `.prose`, so all 422 champion avatars were being picked up as
     content images: each took a zoom-in cursor, a click opened a 36px face
     full-screen, and the < / > sequence on that page was 422 of them. A
     selector is enough here (unlike the map, whose tiles do not exist yet when
     this runs); add to it if another table of faces or logos ever lands inside
     `.prose`. */
  var CHROME = ".champions-table img";
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
      if (img.closest(MAP_CONTAINER)) return; // a map tile, not a content image
      if (img.matches(CHROME)) return; // an avatar in a table, not a content image
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
    /* A viewer that covers the page IS a modal dialog, and saying so is what
       tells a screen reader that the page behind it is not the thing to read.
       Native <dialog> would give the semantics, the focus trap and Esc for
       free -- as /calendar/'s detail dialog gets them -- but it cannot be
       retrofitted here without moving the overlay into the top layer, where
       the existing backdrop/positioning rules no longer apply; the four lines
       below do the same job against the markup we have. */
    overlay.setAttribute("role", "dialog");
    overlay.setAttribute("aria-modal", "true");
    overlay.setAttribute("aria-label", "Image viewer");
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

    /* Where focus was when the viewer opened, so it can be put back: closing a
       dialog and dropping focus at the top of the document loses a reader's
       place in a 2000-word article (WCAG 2.4.3). */
    var opener = null;

    function show(index) {
      current = (index + items.length) % items.length; // wraps both ways
      big.src = items[current].src;
      big.alt = items[current].alt;
      /* The dialog's own name follows the picture, so stepping through a
         gallery is announced rather than silent. */
      overlay.setAttribute("aria-label",
        (items[current].alt || "Image") + " (" + (current + 1) + " of " + items.length + ")");
    }
    function open(index, from) {
      opener = from || document.activeElement;
      show(index);
      overlay.classList.add("is-open");
      overlay.setAttribute("aria-hidden", "false");
      document.body.classList.add("lightbox-open");
      /* Into the dialog, not left behind it: the close button, so the first
         thing Tab or a screen reader reaches is the way out. */
      overlay.querySelector(".lightbox__close").focus();
    }
    function close() {
      overlay.classList.remove("is-open");
      overlay.setAttribute("aria-hidden", "true");
      document.body.classList.remove("lightbox-open");
      big.removeAttribute("src");
      if (opener && document.contains(opener)) opener.focus();
      opener = null;
    }
    function isOpen() {
      return overlay.classList.contains("is-open");
    }

    /* THE OPENER HAS TO BE KEYBOARD-OPERABLE, which an <img> is not: binding
       the click to the image alone made click-to-enlarge a mouse-only feature
       (WCAG 2.1.1), and on a gallery image -- which IS wrapped in a link to the
       full-size original -- pressing Enter navigated away from the article to a
       bare image file instead of opening the viewer, because a keyboard Enter
       fires its click on the <a> and never reaches the <img> inside it.

       So the control is the wrapping link where there is one (already focusable,
       already announced, and it keeps working with JavaScript off), and the
       image itself otherwise -- given a tabindex and role="button" rather than
       being wrapped in a real <button>, because a wrapper changes the box the
       gallery grid and the float classes lay out. */
    openers.forEach(function (opener) {
      var img = opener.img;
      img.classList.add("is-zoomable");

      var link = img.closest("a");
      var control = link || img;
      if (!link) {
        img.setAttribute("tabindex", "0");
        img.setAttribute("role", "button");
        var alt = img.getAttribute("alt");
        img.setAttribute("aria-label", alt ? "Enlarge image: " + alt : "Enlarge image");
        img.addEventListener("keydown", function (e) {
          if (e.key !== "Enter" && e.key !== " " && e.key !== "Spacebar") return;
          e.preventDefault(); // Space would scroll the page
          open(opener.index, img);
        });
      }
      control.addEventListener("click", function (e) {
        e.preventDefault(); // an image wrapped in a link opens here, not there
        open(opener.index, control);
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
      /* Trap. Without it Tab walks out of the viewer and into the article
         underneath, which is still on screen behind the backdrop but cannot be
         seen or reasoned about -- the focus is simply gone (WCAG 2.4.3/2.1.2).
         The set is recomputed per keystroke because the two arrows are hidden
         when the page has a single image. */
      if (e.key === "Tab") {
        var stops = Array.prototype.filter.call(
          overlay.querySelectorAll("button"),
          function (b) { return !b.hidden; }
        );
        if (!stops.length) return;
        var first = stops[0], last = stops[stops.length - 1];
        var active = document.activeElement;
        if (e.shiftKey && (active === first || !overlay.contains(active))) {
          last.focus(); e.preventDefault();
        } else if (!e.shiftKey && (active === last || !overlay.contains(active))) {
          first.focus(); e.preventDefault();
        }
        return;
      }
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
