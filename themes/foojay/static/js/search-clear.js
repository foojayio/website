/* Gives every filter box a clear (x) button. Native type=search only draws one
   in Chromium/Safari, so this replaces it and the look is the same everywhere.
   The button fires `input`, so each filter's own listener needs no changes. */
(function () {
  'use strict';

  document.querySelectorAll('.list-filter__input, .tv-channels__search').forEach(function (input) {
    var wrap = document.createElement('span');
    wrap.className = 'clearable';
    input.parentNode.insertBefore(wrap, input);
    wrap.appendChild(input);

    var button = document.createElement('button');
    button.type = 'button';
    button.className = 'clearable__button';
    button.setAttribute('aria-label', 'Clear filter');
    button.textContent = '×';
    button.hidden = true;
    wrap.appendChild(button);

    function sync() { button.hidden = !input.value; }
    input.addEventListener('input', sync);
    button.addEventListener('click', function () {
      input.value = '';
      input.dispatchEvent(new Event('input', { bubbles: true }));
      input.focus();
    });
    sync();
  });
})();
