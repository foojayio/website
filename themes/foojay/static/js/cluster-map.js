/* A clustered world map whose markers CARRY A COUNT -- the single definition
   used by both /java-champions/ and /jugs/.

   Both pages draw the same thing: a list of people or groups, each with a
   coordinate, on one world map. What differs is only the data, the noun and
   the popup's links, so all three are arguments and everything else lives
   here. Before this the champions map was the only one with counted badges
   and copying it to /jugs/ would have been a second definition of the
   grouping, the badge tiers and the cluster totals -- three things that must
   agree between the two maps or the same number means different things on
   each.

   ONE MARKER PER PLACE, not per item. The coordinates are city centres (or,
   for entries recording no city, a whole country), so several items genuinely
   share one point: 22 Java Champions sit on the USA centroid and 16 on
   London. Separate markers there stack into a single unclickable pin with the
   rest unreachable at any zoom -- which is exactly what /jugs/ did to the
   three German JUGs that upstream gives identical coordinates. Grouping is
   what makes all of them reachable, via a popup that lists everyone at the
   point.

   THE NUMBER ON A CLUSTER COUNTS ITEMS, NOT MARKERS. markercluster's default
   icon counts the markers it holds, which is wrong the moment a marker stands
   for more than one item: a cluster over the Benelux would read "3" while
   covering 30 people, so the number would change meaning as you zoom.
   iconCreateFunction below sums what each child marker stands for instead, so
   a total splits into smaller totals on the way in and always adds up.

   Loaded only by the two templates that draw a map, not from baseof.html --
   Leaflet is on their pages and nowhere else.

   Usage (after leaflet.js and leaflet.markercluster.js):

     foojayClusterMap({
       el: 'champions-map',
       points: [{name, lat, lng, place, url}, ...],
       singular: 'Java Champion',
       plural: 'Java Champions'
     });
*/
(function () {
  'use strict';

  /* Rounded rather than the raw float's own string form: two items in one
     city come from one geocoding cache entry and so carry byte-identical
     coordinates, but keying on the number's text would split a group the day
     one of them arrives as 51.5072 and the other as 51.50720. */
  function pointKey(item) {
    return item.lat.toFixed(5) + ',' + item.lng.toFixed(5);
  }

  function label(place, count, singular, plural) {
    var noun = count === 1 ? singular : plural;
    return (place ? place + ' — ' : '') + count + ' ' + noun;
  }

  /* A place marker and a cluster of them say the same thing to a reader --
     "this many are around here" -- so one function draws both and they differ
     only in the number and a tint. Sized by content with iconSize null and
     re-centred in CSS: "1" and "422" are different widths, and a fixed icon
     box would either clip the wide one or leave the narrow one off-centre. */
  function badge(count, extraClass) {
    var tier = count >= 50 ? 'lg' : (count >= 10 ? 'md' : 'sm');
    return L.divIcon({
      html: '<span>' + count + '</span>',
      className: 'cluster-map__marker cluster-map__marker--' + tier +
        (extraClass ? ' ' + extraClass : ''),
      iconSize: null
    });
  }

  /* Built as DOM and never as an HTML string: every name here is upstream
     data (a champion's own spelling of their name, a JUG's own title), so it
     goes in as textContent and can never become markup. */
  function popupFor(place, items, singular, plural) {
    var box = document.createElement('div');
    box.className = 'cluster-map__popup';

    function link(item) {
      if (!item.url) {
        var text = document.createElement('span');
        text.textContent = item.name;
        return text;
      }
      var anchor = document.createElement('a');
      anchor.href = item.url;
      anchor.target = '_blank';
      anchor.rel = 'noopener';
      anchor.textContent = item.name;
      return anchor;
    }

    /* A single item needs no heading counting to one and no list of length
       one -- the name IS the popup, with the place under it. */
    if (items.length === 1) {
      var only = document.createElement('strong');
      only.appendChild(link(items[0]));
      box.appendChild(only);
      if (place) {
        var where = document.createElement('div');
        where.className = 'cluster-map__place';
        where.textContent = place;
        box.appendChild(where);
      }
      return box;
    }

    var heading = document.createElement('strong');
    heading.textContent = label(place, items.length, singular, plural);
    box.appendChild(heading);

    var list = document.createElement('ul');
    items.forEach(function (item) {
      var row = document.createElement('li');
      row.appendChild(link(item));
      list.appendChild(row);
    });
    box.appendChild(list);
    return box;
  }

  window.foojayClusterMap = function (options) {
    var host = document.getElementById(options.el);
    if (!host || !window.L || !options.points || !options.points.length) return;

    var singular = options.singular || 'entry';
    var plural = options.plural || 'entries';

    var places = {};
    var order = [];
    options.points.forEach(function (item) {
      var key = pointKey(item);
      if (!places[key]) {
        places[key] = { lat: item.lat, lng: item.lng, place: item.place || '', items: [] };
        order.push(key);
      }
      places[key].items.push(item);
    });

    var map = L.map(options.el).setView([20, 0], 2);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    var markers = L.markerClusterGroup({
      maxClusterRadius: 50,
      disableClusteringAtZoom: 10,
      spiderfyOnMaxZoom: false,
      iconCreateFunction: function (cluster) {
        var total = 0;
        cluster.getAllChildMarkers().forEach(function (marker) {
          total += marker.options.itemCount || 1;
        });
        return badge(total, 'cluster-map__marker--cluster');
      }
    });

    order.forEach(function (key) {
      var place = places[key];
      var count = place.items.length;
      /* One item: its own name is more use in a tooltip than "Germany -- 1
         JUG". Several: the place and how many, with the names in the popup. */
      var title = count === 1
        ? place.items[0].name
        : label(place.place, count, singular, plural);

      var marker = L.marker([place.lat, place.lng], {
        title: title,
        alt: title,
        icon: badge(count),
        /* Read back by iconCreateFunction above. Kept on the marker rather
           than in a lookup on the side, so a cluster can total its children
           without knowing anything about the page's data. */
        itemCount: count
      });
      marker.bindPopup(popupFor(place.place, place.items, singular, plural));
      markers.addLayer(marker);
    });

    map.addLayer(markers);
  };
})();
