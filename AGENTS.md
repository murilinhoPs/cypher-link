# CYPHER_LINK

Browser-only ClojureScript SPA (UIx + Shadow-CLJS). No backend, database, or secrets.

## Prerequisites

- Node.js LTS
- Java 21
- npm

## Commands

```bash
npm ci
npx shadow-cljs compile app   # development compile + Maven/Clojure deps
npm run dev                   # watch + HTTP server on :8080
npm run build                 # production release → resources/public/js/main.js
```

Local app: `http://localhost:8080`

## Cursor Cloud specific instructions

- `install` already runs `npm ci` and `npx shadow-cljs compile app`. Re-run those after dependency or lockfile changes.
- The Cloud Agent `start` command runs `npm run dev`. The Shadow-CLJS HTTP server listens on port **8080** and serves `resources/public`.
- Open `http://localhost:8080/index.html`. A request to `/` returns 404 with this Shadow-CLJS version; `/index.html`, `/main.css`, and `/js/main.js` are the working routes.
- If port 8080 is already taken, inspect the existing `shadow-cljs watch app` process instead of starting a second one.
- There is no login, API, or environment secret. Do not request secrets for this repo.
- UI verification: `http://localhost:8080/index.html` (encrypt/transmit) and `http://localhost:8080/index.html#msg=` (decrypt/receive). Production output lives in `resources/public`.
- Many `src/cypher` namespaces are still stubs. A successful compile does not mean the v2 UI from `cypher_link_tech_handoff.md` is implemented.
- Tests are specified in the tech handoff (`npm run test` / `shadow-cljs compile test`) but are not wired in `package.json` or `shadow-cljs.edn` yet.
