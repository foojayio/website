/* Narrow a table's rows to what you type.

   Wiring is declarative, so the same script serves any table:

     <div class="list-filter" data-filter-wrap hidden>
       <input data-filter-for="authors-table" data-filter-count="authors-count"
              data-filter-noun="authors">
     </div>
     <table id="authors-table" data-filter-cols="0,2"> ...

   `data-filter-cols` limits which columns are searched (default: all of
   them). It is worth setting: on the sitemap's articles table an unrestricted
   search would match the digits of a view count, so typing 42 would pull up
   every article read 42 times alongside the ones about Java 42.

   The haystack is read from the cells and cached on first use rather than
   rendered into a `data-filter` attribute per row. On a 2000-row table that
   attribute would be ~170KB of duplicated text in the HTML, and a second copy
   of a title is a second thing that can disagree with the first.

   Progressive enhancement, like the sort buttons next door: the box is
   `hidden` in the markup and this script reveals it (the `[data-filter-wrap]`
   around it, so the label and the live count go with it), and a reader without
   JavaScript gets the full table rather than a search box that does nothing.

   Multi-term AND, not one substring: "frank javafx" finds Frank's JavaFX
   articles, which is what someone typing two words means.
*/
(function () {
  'use strict';

  var inputs = document.querySelectorAll('input[data-filter-for]');
  if (!inputs.length) return;

  function wire(input) {
    var table = document.getElementById(input.getAttribute('data-filter-for'));
    if (!table || !table.tBodies[0]) return;
    var body = table.tBodies[0];
    var status = document.getElementById(input.getAttribute('data-filter-count') || '');

    var cols = null;
    var declared = table.getAttribute('data-filter-cols');
    if (declared) {
      cols = declared.split(',').map(function (n) { return parseInt(n, 10); });
    }

    /* Cached per row, and read lazily: the first keystroke pays for reading
       2000 rows out of the DOM, and nothing pays if the box is never used. */
    function haystack(row) {
      var cached = row.__filterText;
      if (cached !== undefined) return cached;
      var text = '';
      var list = cols || Array.prototype.map.call(row.cells, function (_, i) { return i; });
      for (var i = 0; i < list.length; i++) {
        var cell = row.cells[list[i]];
        if (cell) text += ' ' + cell.textContent;
      }
      cached = text.replace(/\s+/g, ' ').toLowerCase();
      row.__filterText = cached;
      return cached;
    }

    var total = body.rows.length;
    var noun = input.getAttribute('data-filter-noun') || 'rows';

    function apply() {
      var terms = input.value.trim().toLowerCase().split(/\s+/).filter(Boolean);
      var rows = body.rows;
      var shown = 0;
      for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        var match = true;
        if (terms.length) {
          var text = haystack(row);
          for (var t = 0; t < terms.length; t++) {
            if (text.indexOf(terms[t]) === -1) { match = false; break; }
          }
        }
        row.hidden = !match;
        if (match) shown++;
      }
      if (status) {
        status.textContent = terms.length
          ? shown.toLocaleString() + ' of ' + total.toLocaleString() + ' ' + noun
          : '';
      }
    }

    /* One update per frame: a fast typist fires several input events between
       paints, and each one walks every row. */
    var queued = false;
    input.addEventListener('input', function () {
      if (queued) return;
      queued = true;
      requestAnimationFrame(function () { queued = false; apply(); });
    });

    var wrap = input.closest('[data-filter-wrap]') || input;
    wrap.hidden = false;
  }

  Array.prototype.forEach.call(inputs, wire);
})();
