/* foojay.io — primary navigation
   Dropdown panels open on hover (fine pointers) and on click/keyboard for
   everyone else; below 900px the same markup becomes an off-canvas drawer with
   accordion panels. No dependencies. */
(function () {
  var header = document.querySelector('[data-nav]');
  if (!header) return;

  var toggle   = header.querySelector('[data-nav-toggle]');
  var nav      = document.getElementById('primary-nav');
  var backdrop = document.querySelector('[data-nav-backdrop]');
  var items    = Array.prototype.slice.call(header.querySelectorAll('[data-nav-item]'));
  var hoverOK  = window.matchMedia('(hover: hover) and (pointer: fine)');
  var isMobile = function () { return window.matchMedia('(max-width: 900px)').matches; };
  var closeTimer;

  function setItem(item, open) {
    item.dataset.open = open ? 'true' : 'false';
    var trigger = item.querySelector('[data-nav-trigger]');
    if (trigger) trigger.setAttribute('aria-expanded', open ? 'true' : 'false');
  }

  function closeAll(except) {
    items.forEach(function (item) { if (item !== except) setItem(item, false); });
  }

  items.forEach(function (item) {
    var trigger = item.querySelector('[data-nav-trigger]');
    if (!trigger) return;

    trigger.addEventListener('click', function (e) {
      e.preventDefault();
      var open = item.dataset.open === 'true';
      closeAll(item);
      setItem(item, !open);
    });

    item.addEventListener('mouseenter', function () {
      if (!hoverOK.matches || isMobile()) return;
      clearTimeout(closeTimer);
      closeAll(item);
      setItem(item, true);
    });

    item.addEventListener('mouseleave', function () {
      if (!hoverOK.matches || isMobile()) return;
      closeTimer = setTimeout(function () { setItem(item, false); }, 140);
    });

    // Tabbing out of the panel closes it.
    item.addEventListener('focusout', function (e) {
      if (isMobile()) return;
      if (!item.contains(e.relatedTarget)) setItem(item, false);
    });
  });

  function setDrawer(open) {
    document.body.dataset.navOpen = open ? 'true' : 'false';
    if (toggle) {
      toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
      toggle.setAttribute('aria-label', open ? 'Close menu' : 'Open menu');
    }
    if (backdrop) { if (open) { backdrop.removeAttribute('hidden'); } else { backdrop.setAttribute('hidden', ''); } }
    document.documentElement.style.overflow = open ? 'hidden' : '';
    if (!open) closeAll();
  }

  if (toggle) {
    toggle.addEventListener('click', function () {
      setDrawer(document.body.dataset.navOpen !== 'true');
    });
  }
  if (backdrop) backdrop.addEventListener('click', function () { setDrawer(false); });

  document.addEventListener('click', function (e) {
    if (isMobile()) return;
    if (!header.contains(e.target)) closeAll();
  });

  document.addEventListener('keydown', function (e) {
    if (e.key !== 'Escape') return;
    var open = items.filter(function (i) { return i.dataset.open === 'true'; });
    if (open.length) {
      open.forEach(function (i) {
        setItem(i, false);
        var t = i.querySelector('[data-nav-trigger]');
        if (t) t.focus();
      });
    } else if (document.body.dataset.navOpen === 'true') {
      setDrawer(false);
      if (toggle) toggle.focus();
    }
  });

  window.addEventListener('resize', function () {
    if (!isMobile() && document.body.dataset.navOpen === 'true') setDrawer(false);
  });

  /* Collapsible header search. The field is a magnifier until asked for, so
     the bar does not carry an always-open 210px input; the drawer copy is left
     alone because CSS keeps it open there. The button is a real submit, so a
     no-JS visitor lands on the search page instead of poking a dead icon —
     which is why "open" is a preventDefault on the FIRST click only. */
  var searchForms = Array.prototype.slice.call(header.querySelectorAll('[data-search]'))
    .filter(function (form) { return !form.closest('.header-actions--mobile'); });

  searchForms.forEach(function (form) {
    var trigger = form.querySelector('[data-search-toggle]');
    var input   = form.querySelector('input[type="search"]');
    if (!trigger || !input) return;

    function setOpen(open) {
      form.dataset.open = open ? 'true' : 'false';
      trigger.setAttribute('aria-expanded', open ? 'true' : 'false');
      if (open) input.focus(); else input.blur();
    }

    trigger.addEventListener('click', function (e) {
      var open = form.dataset.open === 'true';
      // Open, or close again when there is nothing to search for. With a query
      // typed, the click falls through and the form submits.
      if (!open || !input.value.trim()) { e.preventDefault(); setOpen(!open); }
    });

    input.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') { setOpen(false); trigger.focus(); }
    });

    document.addEventListener('click', function (e) {
      if (form.dataset.open !== 'true' || form.contains(e.target)) return;
      if (!input.value.trim()) setOpen(false);
    });
  });

  // Subtle elevation once the page scrolls under the sticky header.
  var onScroll = function () {
    header.dataset.scrolled = window.scrollY > 4 ? 'true' : 'false';
  };
  onScroll();
  window.addEventListener('scroll', onScroll, { passive: true });
})();

/* foojay.io — colour scheme toggle
   The initial value is set by the inline script in baseof.html (before paint);
   this only wires the buttons and persists the choice. */
(function () {
  var root = document.documentElement;
  var buttons = document.querySelectorAll('[data-theme-toggle]');
  if (!buttons.length) return;

  var media = window.matchMedia('(prefers-color-scheme: dark)');

  function isDark() { return root.getAttribute('data-theme') === 'dark'; }

  function sync() {
    var dark = isDark();
    buttons.forEach(function (b) {
      b.setAttribute('aria-label', dark ? 'Switch to light theme' : 'Switch to dark theme');
      b.setAttribute('title', dark ? 'Switch to light theme' : 'Switch to dark theme');
    });
  }

  function set(theme, persist) {
    root.setAttribute('data-theme', theme);
    if (persist) { try { localStorage.setItem('foojay-theme', theme); } catch (e) {} }
    sync();
  }

  buttons.forEach(function (b) {
    b.addEventListener('click', function () { set(isDark() ? 'light' : 'dark', true); });
  });

  // Follow the OS while the visitor has never made an explicit choice.
  media.addEventListener('change', function (e) {
    var stored = null;
    try { stored = localStorage.getItem('foojay-theme'); } catch (err) {}
    if (!stored) set(e.matches ? 'dark' : 'light', false);
  });

  sync();
})();
