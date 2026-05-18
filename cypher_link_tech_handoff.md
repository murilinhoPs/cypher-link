# CYPHER_LINK — Technical Handoff v2.0

```
// CLASSIFIED TRANSMISSION — PROJECT DOSSIER
Stack: ClojureScript + UIx v2 + Shadow-CLJS · Static SPA · Vercel / GitHub Pages
```

| Campo | Valor |
|---|---|
| Projeto | CYPHER_LINK — Cyberpunk Message Cipher |
| Versão | 2.0 — atualizado com layout two-panel + Cyber Runner palette |
| Stack | ClojureScript + UIx v2 + Shadow-CLJS |
| Deploy | Vercel (recomendado) ou GitHub Pages |
| Status | PRÉ-DESENVOLVIMENTO — handoff para implementação |

---

## 01 — Visão Geral

### Objetivo

CYPHER_LINK é uma SPA estática de criptografia de mensagens para uso em campanhas de RPG cyberpunk. O sistema funciona 100% no browser — sem backend, sem banco de dados, sem autenticação.

- Escrever mensagens e criptografá-las com uma keyword pessoal
- Gerar um **link compartilhável** com a mensagem cifrada embutida no hash da URL
- Abrir o link e **descriptografar** usando a mesma keyword
- Visual de **terminal hacker diegético** — parece uma ferramenta real da campanha

### Regras de negócio da cifra

A cifra é uma substituição simétrica por pares de letras:

1. Cada letra única da keyword é **pareada com a letra seguinte** no alfabeto (`z` pareia com `y`)
2. O mapa é **bidirecional**: se `a→b` então `b→a`
3. `cipher(cipher(x, kw), kw) == x` — cifrar duas vezes retorna ao original
4. Letras fora do mapa, espaços, números e pontuação **passam sem alteração**
5. Input é **case-insensitive**; output é sempre minúsculo

### Fluxo principal do usuário

1. **Digita keyword** no campo do input panel (esquerda)
2. Escreve a **mensagem** no textarea abaixo
3. Pressiona **Enter** ou clica **`[ ENCRYPT ]`** → output aparece no painel direito
4. Clica **`[ COPY ]`** para copiar o texto cifrado, ou **`[ GENERATE LINK ]`** → copia URL com `#msg=` no hash
5. **`[ PURGE ]`** limpa keyword, mensagem e output de uma vez
6. Destinatário abre o link → vê o texto cifrado → insere keyword → clica **`[ DECRYPT ]`**

---

## 02 — Stack e Dependências

### Por que essa stack?

- **Zero backend** — cifra é puramente client-side; link sharing via hash da URL não vai para servidor
- **Deploy trivial** — `git push` → Vercel detecta saída estática, zero configuração
- **Reutilização** — código da cifra é idêntico ao CLI Clojure; porta diretamente para `.cljs`
- **Escalabilidade opcional** — migração para `uix-ssr-hybrid-template` sem reescrever lógica de negócio
- **PWA** — service worker + `manifest.json` para instalar como app no mobile

### Dependências

| Pacote | Versão | Função |
|---|---|---|
| ClojureScript | 1.11.60 | Linguagem — compila para JS |
| UIx v2 (`com.pitch/uix.core` + `com.pitch/uix.dom`) | 1.4.9 | React wrapper idiomático para CLJS — Clojars, não Maven Central |
| Shadow-CLJS | 2.28.23 | Build tool + hot reload + bundler |
| React | 18.2.0 | Runtime do UIx (via npm) |
| react-dom | 18.2.0 | DOM renderer |

### `package.json`

```json
{
  "dependencies": {
    "react":     "^18.2.0",
    "react-dom": "^18.2.0"
  },
  "devDependencies": {
    "shadow-cljs": "^2.28.23"
  }
}
```

> `uix` não vai via npm — vem via Clojars como dependência JVM. Não inclua `uix` no `package.json`.

---

## 03 — Estrutura de Pastas

```
cypher-link/
├── src/
│   └── cypher/
│       ├── core.cljs          ;; entry point + router
│       ├── cipher.cljs        ;; lógica da cifra (portada do CLI)
│       ├── url.cljs           ;; encode/decode base64url + hash
│       ├── store.cljs         ;; estado global (ratoms)
│       └── ui/
│           ├── layout.cljs    ;; terminal container, scanlines
│           ├── encrypt.cljs   ;; view principal de encriptação
│           ├── decrypt.cljs   ;; view de leitura de link
│           └── shared.cljs    ;; componentes reutilizáveis
├── resources/
│   └── public/
│       ├── index.html         ;; shell HTML mínimo
│       ├── main.css           ;; tema cyberpunk
│       ├── manifest.json      ;; PWA manifest
│       └── sw.js              ;; service worker
├── shadow-cljs.edn
├── deps.edn
├── package.json
├── vercel.json
└── .github/
    └── workflows/
        └── deploy.yml         ;; GitHub Pages (alternativa ao Vercel)
```

---

## 04 — Arquitetura da Aplicação

### `shadow-cljs.edn`

```clojure
{:source-paths ["src"]
 :dependencies [[com.pitch/uix.core "1.4.9"]
                [com.pitch/uix.dom  "1.4.9"]]
 :builds
 {:app
  {:target     :browser
   :output-dir "resources/public/js"
   :asset-path "/js"
   :modules    {:main {:init-fn cypher.core/init!}}
   :devtools   {:after-load cypher.core/init!}}}}
```

### `deps.edn`

```clojure
{:paths ["src" "resources"]
 :deps  {org.clojure/clojurescript {:mvn/version "1.11.60"}
         com.pitch/uix.core        {:mvn/version "1.4.9"}
         com.pitch/uix.dom         {:mvn/version "1.4.9"}}}
```

### `index.html` — shell mínimo

```html
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>CYPHER_LINK</title>
  <!-- Share Tech Mono — apenas para o título -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="/main.css">
  <link rel="manifest"   href="/manifest.json">
</head>
<body>
  <div id="root"></div>
  <script src="/js/main.js"></script>
  <script>navigator.serviceWorker?.register("/sw.js")</script>
</body>
</html>
```

> JetBrains Mono é carregada via CSS (`@import` no `main.css`) ou via `fontsource/jetbrains-mono` npm. Share Tech Mono é carregada aqui no HTML com `preconnect` para reduzir latência — ela é crítica para o LCP (título é o maior elemento visual).

### Roteamento — hash-based

Sem biblioteca de router. Roteamento via `js/window.location.hash`:

```clojure
;; core.cljs
(ns cypher.core
  (:require [uix.core :refer [defui $]]
            [clojure.string :as str]
            [cypher.ui.encrypt :as encrypt]
            [cypher.ui.decrypt :as decrypt]))

(defn current-view []
  (let [hash js/window.location.hash]
    (cond
      (str/starts-with? hash "#msg=") :decrypt
      :else                           :encrypt)))

(defui app []
  (case (current-view)
    :decrypt ($ decrypt/view)
    :encrypt ($ encrypt/view)))

(defn init! []
  (uix.dom/render ($ app) (js/document.getElementById "root")))
```

---

## 05 — Módulos — Implementação

### `cipher.cljs` — lógica da cifra

Zero interop JS — usa apenas `clojure.string`. `str/index-of` retorna `nil` (não `-1`) quando char não está no alfabeto, então o guard usa `nil?`. Param `kw` em vez de `keyword` para não shadowear o built-in `clojure.core/keyword`.

```clojure
(ns cypher.cipher
  (:require [clojure.string :as str]))

(def ^:private alphabet "abcdefghijklmnopqrstuvwxyz")

(defn build-pair-map [kw]
  (let [kw-chars (->> kw str/lower-case distinct)]
    (reduce
      (fn [m ch]
        (let [idx (str/index-of alphabet (str ch))]
          (if (or (nil? idx) (contains? m ch))
            m
            (let [partner (get alphabet (if (= idx 25) 24 (inc idx)))]
              (if (contains? m partner)
                m
                (assoc m ch partner partner ch))))))
      {}
      kw-chars)))

(defn cipher [text kw]
  (let [pair-map (build-pair-map kw)]
    (->> text
         str/lower-case
         (map #(get pair-map % %))
         (apply str))))
```

### `url.cljs` — compartilhamento

```clojure
(ns cypher.url
  (:require [clojure.string :as str]))

(defn encode [text]
  (-> text
      js/btoa
      (str/replace "+" "-")
      (str/replace "/" "_")
      (str/replace "=" "")))

(defn decode [encoded]
  (let [pad    (- 4 (mod (count encoded) 4))
        padded (if (not= pad 4)
                 (str encoded (str/join (repeat pad "=")))
                 encoded)]
    (-> padded
        (str/replace "-" "+")
        (str/replace "_" "/")
        js/atob)))

(defn share-url [cipher-text]
  (let [base (str js/window.location.origin
                  js/window.location.pathname)]
    (str base "#msg=" (encode cipher-text))))

(defn read-from-hash []
  (let [hash js/window.location.hash]
    (when (str/starts-with? hash "#msg=")
      (decode (subs hash 5)))))
```

### `store.cljs` — estado global

O layout de dois painéis e o uptime timer adicionam novos átomos de estado:

```clojure
(ns cypher.store)

;; Cipher state
(def keyword-atom  (atom ""))
(def message-atom  (atom ""))
(def output-atom   (atom ""))

;; UI feedback state
(def copied?-atom       (atom false))   ;; [ COPY ] feedback
(def link-copied?-atom  (atom false))   ;; [ GENERATE LINK ] feedback

;; Uptime — contador de segundos desde que o app carregou
;; Usado no status badge: "UPTIME: 00:00:00"
(def uptime-atom   (atom 0))

(defn start-uptime! []
  (js/setInterval #(swap! uptime-atom inc) 1000))

(defn format-uptime [seconds]
  (let [h  (quot seconds 3600)
        m  (quot (mod seconds 3600) 60)
        s  (mod seconds 60)
        pad #(if (< % 10) (str "0" %) (str %))]
    (str (pad h) ":" (pad m) ":" (pad s))))
```

### `encrypt.cljs` — view principal

Estrutura de dois painéis com status badge, três botões e uptime:

```clojure
(ns cypher.ui.encrypt
  (:require [uix.core :refer [defui $ use-state use-effect]]
            [cypher.cipher :as cipher]
            [cypher.url    :as url]
            [cypher.store  :as store]))

(defui status-badge []
  (let [[uptime set-uptime!] (use-state 0)]
    (use-effect
      (fn []
        (let [timer (js/setInterval #(set-uptime! inc) 1000)]
          #(js/clearInterval timer)))
      #js [])
    ($ :div.status
      ($ :div.status-main "SECURE NODE ONLINE")
      ($ :div "NODE ID: CTX-7SIN")
      ($ :div.status-uptime (str "UPTIME: " (store/format-uptime uptime))))))

(defui view []
  (let [[kw          set-kw!]         (use-state "")
        [msg         set-msg!]        (use-state "")
        [out         set-out!]        (use-state "")
        [copied      set-copied!]     (use-state false)
        [link-copied set-link-copied!](use-state false)

        encrypt! (fn []
                   (when (and (seq kw) (seq msg))
                     (set-out! (cipher/cipher msg kw))))

        copy-text! (fn []
                     (when (seq out)
                       (.writeText js/navigator.clipboard out)
                       (set-copied! true)
                       (js/setTimeout #(set-copied! false) 2000)))

        gen-link!  (fn []
                     (when (seq out)
                       (.writeText js/navigator.clipboard (url/share-url out))
                       (set-link-copied! true)
                       (js/setTimeout #(set-link-copied! false) 2000)))

        purge!     (fn []
                     (set-kw! "")
                     (set-msg! "")
                     (set-out! ""))]

    ($ :div.page
      ($ :div.terminal
        ;; Terminal header — título + status badge
        ($ :div.terminal-header
          ($ :div
            ($ :h1.terminal-title "CYPHER_LINK"
              ($ :span.cursor-yellow "_"))
            ($ :p.terminal-description
              "Secure channel for encrypted messages. "
              "Write a key, compose your message, and generate a sha-able link."))
          ($ status-badge))

        ($ :div.terminal-inner
          ;; Two-panel grid
          ($ :div.terminal-grid

            ;; LEFT — Input panel
            ($ :div.panel.panel-input
              ($ :div.panel-title "> INPUT / ENCRYPTION PAYLOAD")

              ($ :div.field
                ($ :label "KEYWORD")
                ($ :input {:type          "text"
                           :id            "key"
                           :placeholder   "enter keyword..."
                           :value         kw
                           :auto-complete "off"
                           :spell-check   false
                           :on-change     #(set-kw! (.. % -target -value))}))

              ($ :div.field
                ($ :label "MESSAGE")
                ($ :textarea {:id          "message"
                              :placeholder "type your message..."
                              :value       msg
                              :on-change   #(set-msg! (.. % -target -value))
                              :on-key-down #(when (and (= (.-key %) "Enter")
                                                       (not (.-shiftKey %)))
                                              (.preventDefault %)
                                              (encrypt!))}))

              ($ :div.btn-row
                ($ :button.btn-primary {:on-click encrypt!}  "[ ENCRYPT ]")
                ($ :button.btn-secondary {:on-click copy-text!}
                  (if copied "[ COPIED ✓ ]" "[ COPY ]"))
                ($ :button.btn-danger {:on-click purge!} "[ PURGE ]")))

            ;; RIGHT — Output panel
            ($ :div.panel.panel-output
              ($ :div.panel-title.panel-title-blue "> OUTPUT / ENCODED TRANSMISSION")
              ($ :pre.output out)
              ($ :button.btn-secondary.btn-full {:on-click gen-link!}
                (if link-copied "[ LINK COPIED ✓ ]" "[ GENERATE LINK ]")))))

        ;; Footer
        ($ :div.terminal-footer
          ($ :span.warning "! WARNING: ")
          "messages intercepted without a valid keyword cannot be decrypted."
          ($ :span.end-line-yellow " // END OF LINE"))))))
```

### `decrypt.cljs` — view de leitura de link

```clojure
(ns cypher.ui.decrypt
  (:require [uix.core :refer [defui $ use-state]]
            [cypher.cipher :as cipher]
            [cypher.url    :as url]))

(defui view []
  (let [cipher-text           (url/read-from-hash)
        [kw      set-kw!]     (use-state "")
        [result  set-result!] (use-state "")]

    ($ :div.page
      ($ :div.terminal
        ;; Header — sinaliza modo de recepção
        ($ :div.terminal-header
          ($ :div
            ($ :h1.terminal-title "CYPHER_LINK"
              ($ :span.cursor-yellow "_"))
            ($ :p.terminal-description "// INCOMING TRANSMISSION"))
          ;; Status badge em modo alerta
          ($ :div.status.status-danger
            ($ :div.status-main.status-danger-text "SIGNAL INTERCEPTED")
            ($ :div "SOURCE: UNKNOWN")
            ($ :div.status-uptime "DECRYPTION REQUIRED")))

        ($ :div.terminal-inner
          ($ :div.terminal-grid

            ;; LEFT — input de keyword + botão decrypt
            ($ :div.panel.panel-input
              ($ :div.panel-title "> INPUT / DECRYPTION KEY")

              ($ :div.field
                ($ :label "ENCRYPTED MESSAGE")
                ($ :pre.cipher-preview cipher-text))

              ($ :div.field
                ($ :label "KEYWORD")
                ($ :input {:type          "text"
                           :placeholder   "enter decryption key..."
                           :value         kw
                           :auto-complete "off"
                           :spell-check   false
                           :on-change     #(set-kw! (.. % -target -value))
                           :on-key-down   #(when (= (.-key %) "Enter")
                                             (set-result! (cipher/cipher cipher-text kw)))}))

              ($ :div.btn-row
                ($ :button.btn-danger.btn-full
                  {:on-click #(set-result! (cipher/cipher cipher-text kw))}
                  "[ DECRYPT ]")))

            ;; RIGHT — output decriptado
            ($ :div.panel.panel-output
              ($ :div.panel-title.panel-title-blue "> OUTPUT / DECODED TRANSMISSION")
              ($ :pre.output result))))

        ($ :div.terminal-footer
          ($ :span.warning "! CAUTION: ")
          "keyword mismatch will produce corrupted output — verify before sharing."
          ($ :span.end-line-yellow " // END OF LINE"))))))
```

> O botão `[ DECRYPT ]` usa `btn-danger` (pink `#FF2A6D`) — sinaliza que a operação é irreversível e requer a keyword correta. O output aparece diretamente no painel direito sem animação adicional de fade (o painel já existe no layout, apenas o conteúdo do `<pre>` muda).

---

## 06 — PWA

### `manifest.json`

```json
{
  "name":             "CYPHER_LINK",
  "short_name":       "CYPHER",
  "description":      "Cyberpunk message cipher tool",
  "start_url":        "/",
  "display":          "standalone",
  "background_color": "#080A0F",
  "theme_color":      "#FFD400",
  "icons": [
    { "src": "/icon-192.png", "sizes": "192x192", "type": "image/png" },
    { "src": "/icon-512.png", "sizes": "512x512", "type": "image/png" }
  ]
}
```

> `theme_color` mudou de `#00FF88` (verde v1) para `#FFD400` (amarelo Edgerunners v2) — controla a cor da barra de status do browser/OS quando instalado como PWA.

### `sw.js` — service worker mínimo

```js
const CACHE  = "cypher-v1";
const ASSETS = ["/", "/main.css", "/js/main.js", "/manifest.json"];

self.addEventListener("install", e =>
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(ASSETS))));

self.addEventListener("fetch", e =>
  e.respondWith(
    caches.match(e.request).then(r => r || fetch(e.request))));
```

---

## 07 — Deploy

### Build para produção

```bash
# Instala dependências npm
npm install

# Compila ClojureScript para produção
npx shadow-cljs release app

# Saída em: resources/public/js/main.js
# Pasta a servir: resources/public/
```

### Vercel (recomendado)

```json
// vercel.json — SPA routing + headers de segurança
{
  "rewrites": [
    { "source": "/((?!js|css|png|svg|ico).*)", "destination": "/" }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        { "key": "X-Frame-Options",        "value": "DENY" },
        { "key": "X-Content-Type-Options", "value": "nosniff" }
      ]
    }
  ]
}
```

- Conecta o repo no Vercel → detecta pasta `resources/public` → deploy automático
- Cada push para `main` → preview deploy; merge → produção
- Domínio gratuito: `cypher-link.vercel.app`

### GitHub Pages (alternativa)

```yaml
# .github/workflows/deploy.yml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  build-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21 }
      - run: npm install
      - run: npx shadow-cljs release app
      - uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: resources/public
```

---

## 08 — MVP e Fases de Desenvolvimento

| Fase | Entregável |
|---|---|
| 0 | Setup: `shadow-cljs.edn`, `deps.edn`, `package.json`, `index.html` com Share Tech Mono |
| 1 | `cipher.cljs` + `url.cljs` — porta o CLI Clojure diretamente |
| 2 | `encrypt/view` + `decrypt/view` — estrutura de dois painéis funcional sem styling |
| 3 | `main.css` — Cyber Runner palette, gradientes radiais, glow recipes, scanline |
| 4 | `store.cljs` uptime timer + feedback states (`copied`, `link-copied`) |
| 5 | `manifest.json` + `sw.js` — PWA instalável |
| 6 | Histórico local (`localStorage`), QR code, glitch no título |
| 7+ | Deploy Vercel — domínio público |

### Atalhos de teclado

| Tecla | Ação |
|---|---|
| `Enter` | Encripta a mensagem (foco no textarea do input panel) |
| `Enter` | Decripta (foco no input de keyword da decrypt view) |
| `Shift+Enter` | Quebra de linha no textarea |
| `Enter` (mobile) | Quebra de linha (comportamento padrão mobile) |

### Classes CSS de referência — mapeamento UIx → CSS

| Classe | Componente |
|---|---|
| `.page` | Wrapper raiz — grid centering |
| `.terminal` | Container externo com scanline e gradiente |
| `.terminal-header` | Flex row título + badge |
| `.terminal-title` | `h1` com Share Tech Mono + glow |
| `.cursor-yellow` | `span` com `animation: blink` |
| `.status` | Badge "SECURE NODE ONLINE" |
| `.status-main` | Linha com `● ` prefixo verde |
| `.status-uptime` | Linha de uptime dim |
| `.terminal-grid` | Grid 2 colunas |
| `.panel` | Painel base |
| `.panel-input` | Painel esquerdo — borda amarela |
| `.panel-output` | Painel direito — borda cyan |
| `.panel-title` | Label `> INPUT / ...` em cyan |
| `.field` | Wrapper label + input |
| `.output` | `<pre>` de output cifrado |
| `.btn-primary` | `[ ENCRYPT ]` — amarelo |
| `.btn-secondary` | `[ COPY ]`, `[ GENERATE LINK ]` — cyan |
| `.btn-danger` | `[ PURGE ]`, `[ DECRYPT ]` — pink |
| `.btn-full` | Botão full-width (usado em `[ GENERATE LINK ]`) |
| `.terminal-footer` | Footer com warning + end of line |
| `.warning` | `! WARNING:` em pink bold |
| `.end-line-yellow` | `// END OF LINE` em amarelo |

---

```
// END OF TRANSMISSION — CYPHER_LINK Technical Handoff v2.0
```
