(function () {
  var player = document.getElementById('tv-player');
  if (!player) return;
  var PAGE = 24;
  var title = document.getElementById('tv-now-title');
  var meta = document.getElementById('tv-now-meta');
  var items = Array.prototype.slice.call(document.querySelectorAll('#tv-grid li'));
  var filters = Array.prototype.slice.call(document.querySelectorAll('.tv-filter'));
  var more = document.getElementById('tv-more');
  var heading = document.getElementById('tv-results-title');
  var count = document.getElementById('tv-results-count');
  var channel = '';
  var shown = PAGE;

  function render() {
    var match = items.filter(function (li) { return !channel || li.dataset.channel === channel; });
    items.forEach(function (li) { li.hidden = true; });
    match.slice(0, shown).forEach(function (li) { li.hidden = false; });
    more.hidden = match.length <= shown;
    count.textContent = Math.min(shown, match.length) + ' of ' + match.length + ' videos';
  }

  function play(id) {
    var frame = document.createElement('iframe');
    frame.src = 'https://www.youtube-nocookie.com/embed/' + id + '?autoplay=1&rel=0';
    frame.title = title.textContent;
    frame.allow = 'autoplay; encrypted-media; picture-in-picture; fullscreen';
    frame.allowFullscreen = true;
    frame.className = 'tv-player__frame';
    var box = document.createElement('div');
    box.className = 'tv-player tv-player--live';
    box.id = 'tv-player';
    box.appendChild(frame);
    player.replaceWith(box);
    player = box;
    return frame;
  }

  document.addEventListener('click', function (e) {
    var link = e.target.closest('#tv-player:not(.tv-player--live), .tv-card');
    if (!link || e.metaKey || e.ctrlKey || e.shiftKey) return;
    e.preventDefault();
    if (link.classList.contains('tv-card')) {
      title.textContent = link.dataset.title;
      meta.textContent = link.dataset.meta;
      player.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
    play(link.dataset.id).focus();
  });

  // ?channel=<handle> deep-links to a channel; the URL follows the selection.
  function select(btn) {
    filters.forEach(function (b) { b.setAttribute('aria-pressed', b === btn ? 'true' : 'false'); });
    channel = btn.dataset.channel;
    shown = PAGE;
    heading.textContent = channel ? btn.dataset.title : 'Latest videos';
    var url = new URL(location.href);
    if (channel) url.searchParams.set('channel', channel); else url.searchParams.delete('channel');
    history.replaceState(null, '', url);
    render();
  }

  filters.forEach(function (btn) {
    btn.addEventListener('click', function () { select(btn); });
  });

  more.addEventListener('click', function () { shown += PAGE; render(); });

  var search = document.getElementById('tv-search');
  var empty = document.getElementById('tv-channels-empty');
  search.addEventListener('input', function () {
    var q = search.value.trim().toLowerCase();
    var any = false;
    filters.forEach(function (b) {
      var hit = !b.dataset.channel ? !q : b.dataset.name.indexOf(q) !== -1;
      if (!b.dataset.channel && q) hit = false;
      b.parentNode.hidden = !hit;
      any = any || hit;
    });
    empty.hidden = any;
  });

  var wanted = (new URLSearchParams(location.search).get('channel') || '').toLowerCase();
  var initial = wanted && filters.filter(function (b) { return b.dataset.channel.toLowerCase() === wanted; })[0];
  if (initial) {
    select(initial);
    initial.scrollIntoView({ block: 'nearest' });
  } else {
    render();
  }
})();
