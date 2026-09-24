/* foojay.io — the three share controls that cannot be plain links
   (themes/foojay/layouts/partials/share.html renders the rest as <a href>).
   Each is in the markup already, `hidden`, so enabling one is unhiding it and
   the row keeps one order however many of them the browser supports.
   No-ops on any page without a share row. */
(function () {
  var root = document.querySelector('.page-share');
  if (!root) return;

  var url = root.getAttribute('data-share-url');
  var title = root.getAttribute('data-share-title');
  var summary = root.getAttribute('data-share-summary');
  var post = root.getAttribute('data-share-post');
  var status = root.querySelector('[data-share-status]');

  function announce(message) { if (status) status.textContent = message; }

  function enable(selector) {
    var item = root.querySelector(selector);
    if (!item) return null;
    item.hidden = false;
    return item.querySelector('button');
  }

  /* --- Copy link ------------------------------------------------------ */
  /* navigator.clipboard is https-only, so this stays hidden on a plain-http
     preview rather than offering a button that throws. */
  if (navigator.clipboard) {
    var copy = enable('[data-share-copy]');
    if (copy) {
      copy.addEventListener('click', function () {
        navigator.clipboard.writeText(url).then(function () {
          copy.textContent = 'Link copied';
          announce('Link copied to the clipboard.');
          setTimeout(function () { copy.textContent = 'Copy link'; }, 2500);
        }, function () {
          announce('Could not copy the link. Select the address bar instead.');
        });
      });
    }
  }

  /* --- The operating system's own share sheet ------------------------- */
  /* Worth its own pill even beside the named networks: on a phone it is the
     only route to WhatsApp, Signal or Slack, and it is one tap. */
  if (navigator.share) {
    var native = enable('[data-share-native]');
    if (native) {
      native.addEventListener('click', function () {
        try {
          /* An abort is what a reader closing the sheet looks like, so the
             rejection is expected and says nothing worth announcing. */
          navigator.share({ title: title, text: summary, url: url }).catch(function () {});
        } catch (e) { /* older implementations throw synchronously */ }
      });
    }
  }

  /* --- LinkedIn ------------------------------------------------------- */
  /* The href is the URL-only endpoint, which is the one a phone can use. On a
     desktop-sized screen swap in the form that actually prefills the composer
     -- see share.html for why that one cannot be the href. */
  var linkedin = root.querySelector('[data-share-linkedin-desktop]');
  if (linkedin && window.matchMedia('(min-width: 768px) and (pointer: fine)').matches) {
    linkedin.href = linkedin.getAttribute('data-share-linkedin-desktop');
  }

  /* --- Mastodon ------------------------------------------------------- */
  /* There is no mastodon.com to post to: the share URL is the reader's OWN
     server, which nothing in the build can know. So the pill opens a form,
     and the answer is remembered -- asking a returning reader to retype their
     server every time is what makes people stop using the button. */
  var form = root.querySelector('.page-share__masto');
  var masto = form ? enable('[data-share-mastodon]') : null;
  if (masto) {
    var field = form.querySelector('input');
    var error = form.querySelector('.page-share__masto-error');
    var KEY = 'foojay:mastodon-instance';

    /* Storage is unavailable in a locked-down profile or a private window in
       some browsers, and throws on access rather than returning null. */
    function remembered() {
      try { return localStorage.getItem(KEY) || ''; } catch (e) { return ''; }
    }
    function remember(host) {
      try { localStorage.setItem(KEY, host); } catch (e) { /* not fatal */ }
    }

    /* Everything people actually type: "foojay.social", "@me@foojay.social",
       "https://foojay.social/". What comes out is a bare host, or "" -- the
       value goes into a URL, so anything unrecognised is refused rather than
       cleaned up into something that might not be a host at all. */
    function hostFrom(value) {
      var v = (value || '').trim().replace(/^https?:\/\//i, '').replace(/\/.*$/, '');
      if (v.indexOf('@') !== -1) v = v.slice(v.lastIndexOf('@') + 1);
      return /^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)+$/i.test(v) ? v : '';
    }

    masto.addEventListener('click', function () {
      var opening = form.hidden;
      form.hidden = !opening;
      masto.setAttribute('aria-expanded', String(opening));
      if (!opening) return;
      field.value = remembered();
      field.focus();
      /* Selected, not just focused: a returning reader's server is already in
         the box, so the whole interaction is Enter. */
      field.select();
    });

    form.addEventListener('submit', function (event) {
      event.preventDefault();
      var host = hostFrom(field.value);
      error.hidden = !!host;
      field.setAttribute('aria-invalid', host ? 'false' : 'true');
      if (!host) { field.focus(); return; }
      remember(host);
      form.hidden = true;
      masto.setAttribute('aria-expanded', 'false');
      masto.focus();
      window.open('https://' + host + '/share?text=' + encodeURIComponent(post), '_blank', 'noopener');
    });
  }
})();
