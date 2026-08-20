/* Column sorting for any table that opts in with `data-sortable`.

   Progressive enhancement, the same way /calendar/'s toolbar is: the server
   renders the table already in the order the page wants (the sitemap's
   articles newest first) and this script turns the header cells into sort
   buttons. Without JavaScript there are no dead controls -- a <th> stays
   plain text and the default order is the useful one.

   A column opts in with `data-sort-type="text|number"` on its <th>; a <th>
   without it is never sortable (so a links or actions column can't be
   clicked). Each cell may carry `data-sort-value` to sort on something other
   than what it shows -- a timestamp behind "Sep 7, 2020", the unformatted
   integer behind "68,330", the first author behind a list of them. Without it
   the cell's text is used.

   Loaded only by the templates that render such a table (a <script> tag in
   the layout), not from baseof.html: it is one page today and there is no
   reason for the other 4000 to fetch it.
*/
(function () {
  'use strict';

  var tables = document.querySelectorAll('table[data-sortable]');
  if (!tables.length) return;

  /* The value a row sorts on for one column. `data-sort-value` wins over the
     rendered text; a number strips the thousands separators the cell shows
     (lang.FormatNumberCustom puts them there) so "68,330" doesn't parse as 68. */
  function keyFor(row, index, type) {
    var cell = row.cells[index];
    if (!cell) return type === 'number' ? 0 : '';
    var raw = cell.getAttribute('data-sort-value');
    if (raw === null) raw = cell.textContent;
    raw = raw.replace(/^\s+|\s+$/g, '');
    if (type !== 'number') return raw.toLowerCase();
    var n = parseFloat(raw.replace(/[^0-9eE.+-]/g, ''));
    return isNaN(n) ? 0 : n;
  }

  function enhance(table) {
    var head = table.tHead;
    var body = table.tBodies[0];
    if (!head || !body) return;
    var headRow = head.rows[head.rows.length - 1];
    if (!headRow) return;

    var state = { index: -1, dir: '' };

    /* Read the server-rendered default so the first click on that column
       flips it rather than re-applying the order already on screen. */
    for (var i = 0; i < headRow.cells.length; i++) {
      var sorted = headRow.cells[i].getAttribute('aria-sort');
      if (sorted === 'ascending' || sorted === 'descending') {
        state.index = i;
        state.dir = sorted;
      }
    }

    function sortBy(index, dir) {
      var type = headRow.cells[index].getAttribute('data-sort-type') || 'text';
      var factor = dir === 'descending' ? -1 : 1;
      var rows = Array.prototype.slice.call(body.rows);
      /* Decorate-sort-undecorate: keyFor touches the DOM, and doing that
         inside the comparator is O(n log n) reads over 2000+ rows. */
      var decorated = rows.map(function (row, position) {
        return { row: row, key: keyFor(row, index, type), position: position };
      });
      decorated.sort(function (a, b) {
        if (a.key < b.key) return -1 * factor;
        if (a.key > b.key) return 1 * factor;
        /* Ties keep the order the page was rendered in -- Array.sort is
           stable in every browser this targets, but being explicit costs
           nothing and makes a re-sort of equal keys visibly a no-op. */
        return a.position - b.position;
      });
      var frag = document.createDocumentFragment();
      decorated.forEach(function (entry) { frag.appendChild(entry.row); });
      body.appendChild(frag);

      for (var i = 0; i < headRow.cells.length; i++) {
        var th = headRow.cells[i];
        if (!th.getAttribute('data-sort-type')) continue;
        th.setAttribute('aria-sort', i === index ? dir : 'none');
      }
      state.index = index;
      state.dir = dir;
    }

    Array.prototype.forEach.call(headRow.cells, function (th, index) {
      var type = th.getAttribute('data-sort-type');
      if (!type) return;

      /* The button is created here rather than rendered in the template, so
         a reader without JavaScript sees a heading and not a control that
         does nothing. */
      var button = document.createElement('button');
      button.type = 'button';
      button.className = 'sort-btn';
      while (th.firstChild) button.appendChild(th.firstChild);
      th.appendChild(button);
      if (!th.getAttribute('aria-sort')) th.setAttribute('aria-sort', 'none');

      button.addEventListener('click', function () {
        var dir;
        if (state.index === index) {
          dir = state.dir === 'ascending' ? 'descending' : 'ascending';
        } else {
          /* First click on a new column picks the direction that column is
             actually read in: newest date and biggest number first, names
             and titles A-Z. Overridable per column with data-sort-first. */
          dir = th.getAttribute('data-sort-first') ||
                (type === 'number' ? 'descending' : 'ascending');
        }
        sortBy(index, dir);
      });
    });

    table.setAttribute('data-sortable', 'ready');
  }

  Array.prototype.forEach.call(tables, enhance);
})();
