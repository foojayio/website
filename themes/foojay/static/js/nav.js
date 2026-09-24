/* foojay.io — primary navigation
   Dropdown panels open on hover (fine pointers) and on click/keyboard for
   everyone else; on compact screens the same markup becomes an off-canvas drawer with
   accordion panels. No dependencies. */
(function () {
  var header = document.querySelector('[data-nav]');
  if (!header) return;

  header.setAttribute('data-nav-ready', '');
  var shell = document.getElementById('nav-shell');
  var closeButton = header.querySelector('[data-nav-close]');
  var backgroundState = [];
  var toggle   = header.querySelector('[data-nav-toggle]');
  var nav      = document.getElementById('primary-nav');
  var backdrop = document.querySelector('[data-nav-backdrop]');
  var items    = Array.prototype.slice.call(header.querySelectorAll('[data-nav-item]'));
  var hoverOK  = window.matchMedia('(hover: hover) and (pointer: fine)');
  var isMobile = function () { return window.matchMedia('(max-width: 64rem)').matches; };
  var closeTimer;

  function setItem(item, open) {
    item.dataset.open = open ? 'true' : 'false';
    var trigger = item.querySelector('[data-nav-trigger]');
    if (trigger) trigger.setAttribute('aria-expanded', open ? 'true' : 'false');
    var panel = item.querySelector('.nav-panel');
    if (panel) panel.inert = !open;
  }

  function closeAll(except) {
    items.forEach(function (item) { if (item !== except) setItem(item, false); });
  }

  items.forEach(function (item) {
    var trigger = item.querySelector('[data-nav-trigger]');
    if (!trigger) return;
    setItem(item, false);

    trigger.addEventListener('click', function (e) {
      e.preventDefault();
      var open = item.dataset.open === 'true';
      closeAll(item);
      setItem(item, !open);
    });

    item.addEventListener('mouseenter', function () {
      if (!hoverOK.matches || isMobile()) return;
      if (header.contains(document.activeElement) && document.activeElement.matches(':focus-visible')) return;
      clearTimeout(closeTimer);
      closeAll(item);
      setItem(item, true);
    });

    item.addEventListener('mouseleave', function () {
      if (!hoverOK.matches || isMobile()) return;
      if (item.contains(document.activeElement)) return;
      closeTimer = setTimeout(function () {
        if (!item.contains(document.activeElement)) setItem(item, false);
      }, 140);
    });

    // Tabbing out of the panel closes it.
    item.addEventListener('focusin', function () { clearTimeout(closeTimer); });
    item.addEventListener('focusout', function (e) {
      if (isMobile()) return;
      if (!item.contains(e.relatedTarget)) setItem(item, false);
    });
  });

  // The modal boundary includes its close control as well as the navigation.
  function drawerStops() {
    return Array.prototype.filter.call(
      shell.querySelectorAll('a[href], button:not([disabled]), input, select, textarea'),
      function (el) { return el.tabIndex >= 0 && !el.closest('[inert]') && el.getClientRects().length && getComputedStyle(el).visibility === 'visible'; }
    );
  }

  function setBackgroundInert(open) {
    if (open) {
      var outside = Array.from(document.body.children).filter(function (el) {
        return el !== header && el !== backdrop && !el.matches('script, style, link');
      }).concat(Array.from(shell.parentElement.children).filter(function (el) { return el !== shell; }));
      backgroundState = outside.map(function (el) { return [el, el.inert]; });
      outside.forEach(function (el) { el.inert = true; });
    } else {
      backgroundState.forEach(function (entry) { entry[0].inert = entry[1]; });
      backgroundState = [];
    }
  }

  function setDrawer(open) {
    if (open === (document.body.dataset.navOpen === 'true')) return;
    document.body.dataset.navOpen = String(open);
    toggle.setAttribute('aria-expanded', String(open));
    // Closing animation must never leave the departing drawer in the Tab order.
    shell.inert = !open && isMobile();
    if (open) {
      shell.setAttribute('role', 'dialog');
      shell.setAttribute('aria-modal', 'true');
      shell.setAttribute('aria-labelledby', 'nav-drawer-title');
      backdrop.hidden = false;
      setBackgroundInert(true);
    } else {
      shell.removeAttribute('role');
      shell.removeAttribute('aria-modal');
      shell.removeAttribute('aria-labelledby');
      backdrop.hidden = true;
      setBackgroundInert(false);
      closeAll();
    }
    document.documentElement.style.overflow = open ? 'hidden' : '';
    if (open) requestAnimationFrame(function () {
      if (document.body.dataset.navOpen === 'true' && isMobile()) closeButton.focus();
    });
    else {
      (isMobile() ? toggle : nav.querySelector('[data-nav-trigger], a[href]')).focus();
    }
  }

  closeButton.addEventListener('click', function () { setDrawer(false); });
  shell.addEventListener('keydown', function (e) {
    if (e.key !== 'Tab' || document.body.dataset.navOpen !== 'true') return;
    var stops = drawerStops(), first = stops[0], last = stops[stops.length - 1];
    if (e.shiftKey && document.activeElement === first) { last.focus(); e.preventDefault(); }
    else if (!e.shiftKey && document.activeElement === last) { first.focus(); e.preventDefault(); }
  });

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
    var focusedItem = items.find(function (i) {
      return i.dataset.open === 'true' && i.contains(e.target);
    });
    if (focusedItem) {
      setItem(focusedItem, false);
      focusedItem.querySelector('[data-nav-trigger]').focus();
      e.preventDefault();
    } else if (document.body.dataset.navOpen === 'true') {
      setDrawer(false);
      e.preventDefault();
    } else {
      // A hovered panel may be open while focus is in the page. Closing it
      // must not pull the reader back to the header.
      closeAll();
    }
  });

  window.addEventListener('resize', function () {
    var hadFocus = shell.contains(document.activeElement);
    if (!isMobile() && document.body.dataset.navOpen === 'true') setDrawer(false);
    shell.inert = isMobile() && document.body.dataset.navOpen !== 'true';
    if (shell.inert && hadFocus) toggle.focus();
  });
  shell.inert = isMobile();

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
      input.inert = !open;
      if (open) input.focus(); else input.blur();
    }
    setOpen(false);

    trigger.addEventListener('click', function (e) {
      var open = form.dataset.open === 'true';
      // Open, or close again when there is nothing to search for. With a query
      // typed, the click falls through and the form submits.
      if (!open || !input.value.trim()) { e.preventDefault(); setOpen(!open); }
    });

    input.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') {
        e.preventDefault();
        e.stopPropagation();
        setOpen(false);
        trigger.focus();
      }
    });
    form.addEventListener('focusout', function (e) {
      if (!form.contains(e.relatedTarget)) setOpen(false);
    });

    document.addEventListener('click', function (e) {
      if (form.dataset.open !== 'true' || form.contains(e.target)) return;
      if (!input.value.trim()) setOpen(false);
    });
  });

  // A wrapped header or enlarged text must not cover the drawer or anchor targets.
  new ResizeObserver(function () {
    document.documentElement.style.setProperty('--header-offset', header.getBoundingClientRect().height + 'px');
  }).observe(header);

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
