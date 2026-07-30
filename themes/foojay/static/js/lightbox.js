/* Click-to-enlarge for every content image -- no per-post markup needed.
   Applies to article body images (.prose img) and the post hero. Images that
   are already links open that link (if it points at an image, it's shown in the
   lightbox; otherwise the link is left to do its thing). Vanilla JS, no deps. */
(function () {
  "use strict";

  var SELECTOR = ".prose img, img.post-hero";
  var IMG_HREF = /\.(jpe?g|png|gif|webp|svg|avif)(\?|#|$)/i;

  function init() {
    var imgs = document.querySelectorAll(SELECTOR);
    if (!imgs.length) return;

    var overlay = document.createElement("div");
    overlay.className = "lightbox";
    overlay.setAttribute("aria-hidden", "true");
    overlay.innerHTML =
      '<button class="lightbox__close" type="button" aria-label="Close">×</button>' +
      '<img class="lightbox__img" alt="">';
    document.body.appendChild(overlay);
    var big = overlay.querySelector(".lightbox__img");

    function open(src, alt) {
      big.src = src;
      big.alt = alt || "";
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

    imgs.forEach(function (img) {
      img.classList.add("is-zoomable");
      img.addEventListener("click", function (e) {
        var link = img.closest("a");
        var full;
        if (link) {
          var href = link.getAttribute("href") || "";
          if (!IMG_HREF.test(href)) return; // link points elsewhere -- leave it
          full = href;
        } else {
          full = img.currentSrc || img.src;
        }
        e.preventDefault();
        open(full, img.getAttribute("alt"));
      });
    });

    overlay.addEventListener("click", function (e) {
      if (e.target === overlay || e.target.classList.contains("lightbox__close")) close();
    });
    document.addEventListener("keydown", function (e) {
      if (e.key === "Escape" && overlay.classList.contains("is-open")) close();
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
