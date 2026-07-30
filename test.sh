#!/bin/sh
# hugo.toml's baseURL is hardcoded to the real GitHub Pages URL
# (https://foojayio.github.io/website/), so every generated link carries that
# /website/ subpath. `npx serve public` serves public/ at the root of
# localhost:3000 with no such subpath, so without this override every asset
# and internal link 404s (public/website/... doesn't exist, only public/...
# does). Overriding --baseURL to match where `serve` actually serves from
# fixes it -- same trick build-deploy.yml uses for the real deploy, just
# pointed at localhost instead of the GitHub Pages URL.
hugo --baseURL "http://localhost:3000/"
npx pagefind --site public
npx serve public