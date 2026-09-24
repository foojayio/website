/* Native controls around Enlighter's rendered code; the vendored bundle stays unchanged. */
(function () {
  document.querySelectorAll('.enlighter-default').forEach(function (block, index) {
    var raw = block.querySelector('.enlighter-raw');
    var code = block.querySelector('.enlighter-code');
    if (!raw || !code) return;

    var controls = document.createElement('div');
    controls.className = 'code-actions';
    var status = document.createElement('span');
    status.className = 'code-actions__status';
    status.setAttribute('role', 'status');
    code.id = 'code-sample-' + (index + 1);
    code.tabIndex = 0;
    code.setAttribute('role', 'region');
    code.setAttribute('aria-label', 'Code sample ' + (index + 1));

    function button(label, action) {
      var el = document.createElement('button');
      el.type = 'button';
      el.textContent = label;
      el.addEventListener('click', action);
      controls.appendChild(el);
      return el;
    }
    var plain = button('Plain text', function () {
      var active = block.classList.toggle('enlighter-show-rawcode');
      plain.setAttribute('aria-pressed', String(active));
    });
    plain.setAttribute('aria-pressed', 'false');
    plain.setAttribute('aria-controls', code.id);
    button('Copy code', async function () {
      try {
        await navigator.clipboard.writeText(raw.textContent);
        status.textContent = 'Code copied.';
      } catch (e) {
        // Keep a usable path when clipboard access is unavailable or denied.
        block.classList.add('enlighter-show-rawcode');
        plain.setAttribute('aria-pressed', 'true');
        code.focus();
        status.textContent = 'Copy unavailable. Select the code and copy it manually.';
      }
    });

    function link(label, href) {
      var el = document.createElement('a');
      el.textContent = label;
      el.href = href;
      el.target = '_blank';
      el.rel = 'noopener';
      controls.appendChild(el);
    }
    link('Open code', URL.createObjectURL(new Blob([raw.textContent], { type: 'text/plain;charset=utf-8' })));
    link('EnlighterJS', 'https://enlighterjs.org');
    controls.appendChild(status);
    block.prepend(controls);
  });
})();
