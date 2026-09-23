#!/bin/sh
# hugo.toml's baseURL is hardcoded to the real GitHub Pages URL
# (https://foojayio.github.io/website/), so every generated link carries that
# /website/ subpath. `npx serve public` serves public/ at the root of
# localhost:3000 with no such subpath, so without this override every asset
# and internal link 404s (public/website/... doesn't exist, only public/...
# does). Overriding --baseURL to match where `serve` actually serves from
# fixes it -- same trick build-deploy.yml uses for the real deploy, just
# pointed at localhost instead of the GitHub Pages URL.

# Start from an empty public/: neither Hugo nor Pagefind deletes anything, they
# only write, so a page that has since been renamed or removed (an author
# bundle, a post slug) stays on disk forever and Pagefind indexes it as if it
# were still part of the site -- a search result that leads to a page the real
# deploy 404s on. Same for the hash-named files under public/pagefind/, which
# otherwise pile up across runs and make the index look many times its true
# size. CI never sees this (a fresh runner per build); only local runs need it.
rm -rf public

hugo --baseURL "http://localhost:3000/"
npx pagefind --site public
npx serve public
