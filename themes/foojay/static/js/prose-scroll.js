/* Keyboard scrolling for prose from either content renderer. */
(function () {
  document.querySelectorAll('.prose table').forEach(function (table, index) {
    if (table.closest('.table-scroll, .sitemap-table-wrap') || table.matches('[data-sortable]')) return;
    var wrapper = document.createElement('div');
    wrapper.className = 'table-scroll prose-table-scroll';
    wrapper.tabIndex = 0;
    wrapper.setAttribute('role', 'region');
    var caption = table.caption;
    wrapper.setAttribute('aria-label', caption ? caption.textContent.trim() : 'Article table ' + (index + 1));
    table.before(wrapper);
    wrapper.appendChild(table);
  });
  document.querySelectorAll('.prose pre:not(.EnlighterJSRAW):not(.mermaid)').forEach(function (code, index) {
    code.tabIndex = 0;
    code.setAttribute('role', 'region');
    code.setAttribute('aria-label', 'Plain code sample ' + (index + 1));
  });
})();
