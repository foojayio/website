/* Keep the focused event fully inside the horizontal strip. Chromium can
   expose only the leading edge of a line-clamped link during native Tab
   scrolling; a second, immediate reveal uses its complete rendered box. */
(function () {
  document.querySelectorAll('.event-strip').forEach(function (strip) {
    strip.addEventListener('focusin', function (event) {
      if (!event.target.matches('a:focus-visible')) return;
      event.target.scrollIntoView({ block: 'nearest', inline: 'nearest', behavior: 'instant' });
    });
  });
})();
