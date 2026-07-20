# CYPHER_LINK v2 — Technical Handoff

```text
// ENGINEERING SPECIFICATION
Implementation: ClojureScript + UIx + Shadow-CLJS
Visual contract: cypher_link_design_handoff.md
Reference revision: Sites version 5
Visual baseline: doc/visual-references/*.png
```

| Campo | Especificação |
|---|---|
| Aplicação | Static SPA, browser-only |
| Linguagem | ClojureScript 1.11.60 |
| UI | UIx 1.4.9 + React 18 |
| Build | Shadow-CLJS 2.27.x |
| Styling | CSS custom properties + classes sem CSS-in-JS |
| Backend | Nenhum |
| Persistência padrão | Memória da sessão |
| Rotas | Hash routing |
| Deploy | Vercel ou GitHub Pages |

Este documento traduz a interface CYPHER_LINK v2 para requisitos implementáveis no repositório ClojureScript. A referência publicada define aparência e comportamento; a stack local permanece UIx/Shadow-CLJS.

As pranchas `cypher-link-transmit-reference.png`, `cypher-link-receive-reference.png` e `cypher-link-protocol-reference.png`, em `doc/visual-references/`, são snapshots normativos para regressão visual. A moldura arredondada do telefone não pertence ao DOM.

---

## 01 — Arquitetura

```text
index.html
   │
   ▼
cypher.core/init!
   │
   ├── cypher.router       hash → view/payload
   ├── cypher.store        estado e eventos
   ├── cypher.cipher       regra pura da cifra
   ├── cypher.url          UTF-8 + Base64URL
   └── cypher.ui.shell
          ├── header/topbar
          ├── navigation/sidebar
          ├── transmit/view
          ├── receive/view
          ├── protocol/view
          ├── toast-region
          └── global-footer
```

### Estrutura alvo

```text
src/cypher/
├── core.cljs
├── router.cljs
├── store.cljs
├── cipher.cljs
├── url.cljs
└── ui/
    ├── shell.cljs
    ├── header.cljs
    ├── navigation.cljs
    ├── transmit.cljs
    ├── receive.cljs
    ├── protocol.cljs
    └── shared.cljs

resources/public/
├── index.html
├── main.css
├── manifest.json
├── sw.js
├── icon-192.png
└── icon-512.png

test/cypher/
├── cipher_test.cljs
├── url_test.cljs
├── router_test.cljs
├── store_test.cljs
└── ui_test.cljs
```

### Migração do repositório atual

| Arquivo atual | Ação |
|---|---|
| `cipher.clj` | Substituir por implementação válida em `cipher.cljs` |
| `cypher.py` | Manter apenas como referência; não participa do build web |
| `ui/encrypt.cljs` | Migrar para `ui/transmit.cljs` |
| `ui/decrypt.cljs` | Migrar para `ui/receive.cljs` |
| `ui/layout.cljs` | Migrar para `ui/shell.cljs` |
| `store.cljs` | Implementar store reativo |
| `url.cljs` | Implementar codec Unicode/Base64URL |

---

## 02 — Execução e build

### Requisitos

- Java 21
- Node.js LTS
- npm

### Scripts

```json
{
  "scripts": {
    "dev": "shadow-cljs watch app",
    "build": "shadow-cljs release app",
    "test": "shadow-cljs compile test"
  }
}
```

```bash
npm install
npm run dev
```

Servidor local: `http://localhost:8080`.

```clojure
{:source-paths ["src" "test"]
 :dependencies [[com.pitch/uix.core "1.4.9"]
                [com.pitch/uix.dom "1.4.9"]]
 :dev-http {8080 "resources/public"}
 :builds
 {:app
  {:target :browser
   :output-dir "resources/public/js"
   :asset-path "/js"
   :modules {:main {:init-fn cypher.core/init!}}
   :devtools {:after-load cypher.core/init!}}
  :test
  {:target :node-test
   :output-to "target/test.js"
   :autorun true}}}
```

Produção gera `resources/public/js/main.js`.

---

## 03 — Roteamento

### Rotas

| Hash | View |
|---|---|
| `#/transmit` | Transmitir |
| `#/receive` | Receber vazio |
| `#/receive?msg=<payload>` | Receber com payload |
| `#/protocol` | Protocolo |
| `#msg=<payload>` | Compatibilidade legada |

### Regras

- Hash vazio normaliza para `#/transmit`.
- Navegação atualiza `window.location.hash` sem reload.
- `hashchange` atualiza módulo ativo.
- Link com payload abre Receber e hidrata `encoded-signal`.
- Keyword nunca entra em hash, query, storage ou logs.
- Rascunhos permanecem no store ao alternar módulos.
- Hash/payload inválido produz erro recuperável.

```clojure
(parse-hash "#/receive?msg=...")
;; => {:view :receive :payload "..." :legacy? false}

(navigate! :protocol)
(navigate-to-payload! encoded)
```

---

## 04 — Estado da aplicação

```clojure
{:route {:view :transmit
         :payload nil}

 :transmit {:message ""
            :keyword ""
            :keyword-visible? false
            :phase :idle
            :pair-map {}
            :cipher-text ""
            :payload-bytes 0
            :error nil}

 :receive {:encoded-signal ""
           :keyword ""
           :keyword-visible? false
           :phase :listening
           :decoded-message ""
           :transport-valid? nil
           :error nil}

 :ui {:uptime-seconds 0
      :network-status :online
      :relay-status :active
      :toast nil
      :reduced-motion? false}}
```

### Store reativo

UIx não observa um `atom` ClojureScript automaticamente. O store compartilhado deve usar:

- `defonce state*` como fonte única;
- reducer/eventos puros;
- `useSyncExternalStore` por meio de hook `use-store`;
- seletores por domínio;
- efeitos externos fora do reducer.

```clojure
(dispatch! {:type :transmit/message-changed :value value})
(use-store #(get-in % [:transmit :phase]))
```

Uptime deve ter assinatura isolada ou estado local no Header para não renderizar toda a árvore a cada segundo.

### Transições

```text
Transmitir:
:idle → :editing → :ready → :staged
  └───────────────────────── purge → :idle

Receber:
:listening → :payload-detected → :ready → :decoded
                                  └──────→ :error
```

- Alterar message/keyword depois de staged invalida output.
- Purge limpa entradas, output, métricas e erros.
- Métricas são derivadas, nunca fonte primária.
- Keyword fica mascarada por padrão em ambos os módulos.

---

## 05 — Tokens CSS

### Tokens obrigatórios

```css
:root {
  --bg: #07090e;
  --bg-deep: #040508;
  --surface: #0d1119;
  --surface-raised: #111722;
  --border: #202a39;
  --border-soft: #6277922e;

  --yellow: #ffd400;
  --yellow-soft: #ffd4001f;
  --pink: #ff2a6d;
  --pink-soft: #ff2a6d1c;
  --cyan: #05d9e8;
  --cyan-soft: #05d9e817;
  --green: #00ff88;
  --green-soft: #00ff8814;

  --text: #e4e2d8;
  --text-muted: #7e8b9d;
  --text-faint: #495568;

  --font-ui: "Geist Sans", system-ui, sans-serif;
  --font-mono: "Geist Mono", "JetBrains Mono", monospace;

  --header-h: 76px;
  --header-h-mobile: 60px;
  --sidebar-w: 218px;
  --sidebar-w-tablet: 178px;
  --mobile-nav-h: 52px;
  --mobile-nav-h-compact: 49px;
  --content-max: 1280px;
  --panel-gap: 18px;
  --mobile-gap: 12px;

  --shadow-panel:
    0 24px 60px rgba(0,0,0,.24),
    inset 0 0 40px rgba(0,0,0,.16);
}
```

### Reset e foco

```css
* { box-sizing: border-box; }

button,
input,
textarea { font: inherit; }

button:focus-visible,
input:focus-visible,
textarea:focus-visible {
  outline: 1px solid var(--yellow);
  outline-offset: 3px;
}
```

Não aplicar `border-radius` em componentes do sistema.

---

## 06 — Shell UIx

```clojure
($ :main.cypher-app
  ($ :div.noise {:aria-hidden true})
  ($ header/topbar {:network-status network-status
                    :uptime uptime
                    :on-home on-home})
  ($ :div.workspace
    ($ navigation/nav {:active-view active-view
                       :on-navigate on-navigate
                       :relay-status relay-status})
    ($ :section.content
      ($ active-view-component)))
  ($ shared/global-footer)
  ($ shared/toast-region))
```

### Shell CSS

```css
.cypher-app {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at 12% 2%, rgba(255,212,0,.07), transparent 28%),
    radial-gradient(circle at 95% 42%, rgba(5,217,232,.055), transparent 29%),
    linear-gradient(rgba(255,255,255,.01) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,.01) 1px, transparent 1px),
    var(--bg);
  background-size: auto, auto, 42px 42px, 42px 42px, auto;
}

.workspace {
  display: grid;
  grid-template-columns: var(--sidebar-w) minmax(0,1fr);
  min-height: calc(100vh - 112px);
  margin-top: var(--header-h);
}

.content {
  width: 100%;
  max-width: var(--content-max);
  margin: 0 auto;
  padding: 45px clamp(28px,4vw,68px) 62px;
}
```

---

## 07 — Header técnico

### API do componente

```clojure
($ header/topbar
  {:network-status :online
   :uptime-seconds 432
   :on-home #(navigate! :transmit)})
```

### DOM esperado

```html
<header class="topbar">
  <button class="brand" aria-label="Ir para nova transmissão">
    <span class="brand-mark" aria-hidden="true">Cᴸ</span>
    <span class="brand-name">CYPHER_LINK_</span>
    <span class="version">v2.0</span>
  </button>

  <div class="topbar-actions">
    <div class="network-status" aria-label="Status da rede: online">
      <span class="status-light" aria-hidden="true"></span>
      <span>SECURED NODE ONLINE</span>
      <span class="status-divider" aria-hidden="true"></span>
      <span class="uptime">UPTIME 00:07:12</span>
    </div>
  </div>
</header>
```

### CSS desktop

```css
.topbar {
  position: fixed;
  z-index: 20;
  inset: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: var(--header-h);
  padding: 0 30px;
  background: rgba(7,9,14,.84);
  border-bottom: 1px solid var(--border);
  backdrop-filter: blur(8px);
}

.topbar::after {
  content: "";
  position: absolute;
  bottom: -1px;
  left: 30px;
  width: 118px;
  height: 1px;
  background: var(--yellow);
  box-shadow: 0 0 12px rgba(255,212,0,.72);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.brand-mark {
  position: relative;
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  flex: 0 0 auto;
  color: var(--bg-deep);
  background: var(--yellow);
  clip-path: polygon(0 0,78% 0,100% 22%,100% 100%,22% 100%,0 78%);
  font-weight: 900;
  box-shadow: 0 0 22px rgba(255,212,0,.18);
}

.brand-name {
  color: var(--yellow);
  font-size: 17px;
  font-weight: 770;
  letter-spacing: .1em;
  text-shadow: 0 0 14px rgba(255,212,0,.2);
}

.version {
  align-self: flex-start;
  margin-top: 2px;
  color: var(--text-faint);
  font-size: 9px;
  letter-spacing: .08em;
}

.network-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 11px;
  min-width: 386px;
  height: 34px;
  padding: 0 14px;
  color: var(--green);
  background: rgba(0,255,136,.035);
  border: 1px solid rgba(0,255,136,.3);
  clip-path: polygon(0 0,calc(100% - 10px) 0,100% 10px,100% 100%,10px 100%,0 calc(100% - 10px));
  font-size: 10px;
  letter-spacing: .09em;
  box-shadow: inset 0 0 22px rgba(0,255,136,.03);
}

.status-light {
  width: 6px;
  height: 6px;
  flex: 0 0 auto;
  border-radius: 50%;
  background: var(--green);
  box-shadow: 0 0 9px var(--green);
  animation: pulse 1.8s ease-in-out infinite;
}
```

### Comportamento

- Header permanece fixed.
- Workspace compensa altura por `margin-top`.
- Clique na brand navega para Transmitir.
- Se existir rascunho não compartilhado, `on-home` solicita confirmação antes de limpar.
- Timer de uptime é cancelado ao desmontar.
- Status visual oculto no mobile preserva `aria-label`.

---

## 08 — Navegação técnica

### API

```clojure
($ navigation/nav
  {:active-view :transmit
   :relay-status :active
   :items [{:id :transmit :label "TRANSMITIR" :code "TX"}
           {:id :receive  :label "RECEBER"    :code "RX"}
           {:id :protocol :label "PROTOCOLO"  :code "::"}]
   :on-navigate navigate!})
```

### DOM

```html
<nav class="sidebar" aria-label="Navegação principal">
  <span class="nav-label">// CHANNEL</span>
  <button class="nav-item active" aria-current="page">
    <span class="nav-index">01</span>
    <span>TRANSMITIR</span>
    <span class="nav-arrow">TX</span>
  </button>
  <!-- RECEBER / PROTOCOLO -->
  <div class="sidebar-footer">...</div>
</nav>
```

### CSS desktop

```css
.sidebar {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 34px 17px 22px;
  background: rgba(8,11,17,.7);
  border-right: 1px solid var(--border);
}

.nav-item {
  position: relative;
  display: grid;
  grid-template-columns: 26px 1fr 18px;
  align-items: center;
  gap: 8px;
  min-height: 50px;
  color: var(--text-muted);
  background: transparent;
  border: 1px solid transparent;
  font-size: 11px;
  letter-spacing: .08em;
  text-align: left;
  cursor: pointer;
  transition: .16s;
}

.nav-item:hover {
  color: var(--text);
  background: rgba(255,255,255,.02);
}

.nav-item.active {
  color: var(--yellow);
  background: linear-gradient(90deg,rgba(255,212,0,.11),rgba(255,212,0,.016));
  border-color: rgba(255,212,0,.32);
  box-shadow: inset 2px 0 0 var(--yellow), 0 0 22px rgba(255,212,0,.04);
}

.nav-index {
  color: var(--text-faint);
  font-size: 9px;
}

.nav-arrow {
  justify-self: end;
  color: var(--text-faint);
  font-size: 14px;
}

.nav-item.active .nav-index,
.nav-item.active .nav-arrow { color: var(--yellow); }
```

### Regras

- Sidebar desktop é persistente e não retrátil.
- Sidebar pertence ao workspace; não recebe `position: fixed`.
- Estado ativo deriva exclusivamente da rota.
- Troca de rota não destrói rascunhos.
- Cada item é `button`; não usar div clicável.
- `aria-current="page"` existe apenas no ativo.
- Códigos TX/RX/:: substituem ícones gráficos.

---

## 09 — Layout e breakpoints

### Desktop e tablet

```css
.composer-grid,
.receive-layout {
  display: grid;
  grid-template-columns: minmax(0,1.14fr) minmax(330px,.86fr);
  align-items: stretch;
  gap: 18px;
}

.protocol-grid {
  display: grid;
  grid-template-columns: repeat(3,minmax(0,1fr));
  gap: 14px;
}

@media (max-width: 1040px) {
  .workspace {
    grid-template-columns: var(--sidebar-w-tablet) minmax(0,1fr);
  }

  .content { padding-inline: 28px; }

  .composer-grid,
  .receive-layout,
  .protocol-grid {
    grid-template-columns: 1fr;
  }
}
```

### Mobile Header/Nav

```css
@media (max-width: 760px) {
  .cypher-app {
    min-height: 100dvh;
    overflow-x: hidden;
    overflow-y: auto;
  }

  .topbar {
    top: 0;
    z-index: 20;
    height: var(--header-h-mobile);
    min-height: var(--header-h-mobile);
    padding: 0 max(12px,env(safe-area-inset-left));
    background: rgba(7,9,14,.96);
    backdrop-filter: none;
  }

  .topbar::after {
    left: max(12px,env(safe-area-inset-left));
    width: 76px;
  }

  .brand { gap: 8px; }
  .brand-mark { width: 30px; height: 30px; font-size: 11px; }
  .brand-name { font-size: 12px; letter-spacing: .07em; }
  .version { display: none; }

  .network-status {
    width: 32px;
    min-width: 32px;
    height: 32px;
    padding: 0;
    gap: 0;
  }

  .network-status > span:nth-child(2),
  .status-divider,
  .network-status .uptime {
    display: none;
  }

  .workspace {
    display: block;
    margin-top: var(--header-h-mobile);
  }

  .sidebar {
    position: sticky;
    top: var(--header-h-mobile);
    z-index: 19;
    display: grid;
    grid-template-columns: repeat(3,1fr);
    padding: 0;
    background: rgba(7,9,14,.97);
    border-right: 0;
    border-bottom: 1px solid var(--border);
  }

  .sidebar::after,
  .nav-label,
  .sidebar-footer,
  .nav-index {
    display: none;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    min-height: var(--mobile-nav-h);
    padding: 0;
    border: 0;
    border-right: 1px solid var(--border-soft);
    font-size: 9px;
    text-align: center;
  }

  .nav-arrow {
    display: block;
    color: var(--text-faint);
    font-size: 7px;
    line-height: 1;
  }

  .nav-item.active {
    border-bottom: 2px solid var(--yellow);
    box-shadow: inset 0 -8px 18px rgba(255,212,0,.05);
  }

  .content {
    padding: 24px 12px calc(42px + env(safe-area-inset-bottom));
  }

  .composer-grid,
  .receive-layout {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .content-heading p { display: block; }

  input,
  textarea {
    font-size: 16px;
  }

  .global-footer {
    min-height: 35px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 10px 15px max(10px,env(safe-area-inset-bottom));
  }

  .global-footer > :not(.end-line) { display: none; }
  .global-footer .end-line { margin-left: auto; }
}

@media (max-width: 390px) {
  .brand-name { font-size: 11px; }
  .nav-item { min-height: var(--mobile-nav-h-compact); font-size: 8px; }
}

@media (max-width: 364px) {
  .output-actions { grid-template-columns: 1fr; }
}
```

### Regras de layout

- Não usar overflow horizontal para nav mobile.
- Aplicar `min-width: 0` a filhos de flex/grid.
- Não montar duas árvores distintas de navegação; usar mesmo DOM e CSS responsivo.
- Sticky nav precisa manter z-index inferior ao header e superior ao conteúdo.
- Focus/scroll deve considerar offset total de 112px no mobile normal e 109px no compacto.
- Eyebrow, descrição, badges contextuais, counters, métricas e status de painel não são conteúdo descartável no mobile.
- A única informação opcional no composer mobile é a ajuda de atalhos de teclado.
- Os códigos `TX`, `RX` e `::` permanecem visíveis abaixo dos labels nas três tabs mobile.

---

## 10 — Componentes das telas

### Shared

| Componente | Props principais |
|---|---|
| `content-heading` | `eyebrow`, `title`, `description`, `score` |
| `terminal-panel` | `accent`, `index`, `title`, `status`, `children` |
| `field` | `id`, `label`, `error`, `hint`, `counter` |
| `keyword-field` | `value`, `visible?`, `on-toggle`, `on-change` |
| `action-button` | `variant`, `disabled?`, `on-click` |
| `signal-metric` | `label`, `value`, `tone` |
| `warning-strip` | `label`, `message` |
| `toast-region` | `message`, `kind`, `on-dismiss` |

### Transmitir

```clojure
($ transmit/view
  {:message message
   :keyword keyword
   :phase phase
   :cipher-text cipher-text
   :pair-count pair-count
   :payload-bytes payload-bytes
   :on-encrypt encrypt!
   :on-copy copy-payload!
   :on-generate-link generate-link!
   :on-purge purge!})
```

Regras:

- Limite de message: 1024 caracteres.
- Enter cifra apenas no contexto definido; Shift+Enter cria linha.
- Output e ações ficam disabled até staged.
- Feedback não altera largura de botões.
- Purge limpa estado derivado.
- Desktop usa grid `minmax(0,1.14fr) minmax(330px,.86fr)`; mobile preserva a mesma ordem do DOM e empilha input antes de output.
- `LOCAL CIPHER / PAIR//OFFGRID` é badge contextual da view, não status global.
- O contador `83/1024`, line numbers, `SIGNAL READY`, `PAIR MAP` e `PAYLOAD` continuam montados no mobile.
- Mobile: Generate Key em linha própria; Encrypt & Stage full-width; Purge abaixo; output actions em duas colunas, caindo para uma em até 364px.
- Áreas editáveis usam `font-size: 16px` em `≤760px`; textos de output usam no mínimo 13px.

### Receber

- Payload pode vir da rota ou entrada manual.
- Decode exige payload e keyword.
- Resultado inválido não pode exibir integridade positiva.
- `INTEGRITY 100%` requer checksum verificável; caso contrário usar `DECODE COMPLETE`.
- Copy decoded usa Clipboard API e toast.
- `SCAN STATUS / NO TRACE` é faixa contextual: compacta no desktop e full-width abaixo do heading no mobile.
- Ordem mobile obrigatória: heading → scan status → incoming payload → decrypted message → footer.
- `LISTENING`, `INTEGRITY 100%`, empty state e botão Copy permanecem visíveis em todas as larguras.

### Protocolo

- Conteúdo estático em três artigos.
- OPSEC note sempre visível.
- CTA navega para Transmitir.
- Cards usam `article` + `h2`.
- Desktop usa três colunas iguais; mobile preserva DOM 01, 02, 03 e empilha sem reordenação.
- Cada article contém índice, diagrama, título, descrição e barra semântica inferior.
- OPSEC usa `aside`; no mobile o CTA ocupa 100% e vem depois da cópia.

### Contratos de composição visual

```clojure
($ shared/content-heading
  {:eyebrow "// NEW SECURE TRANSMISSION"
   :title "COMPOR SINAL"
   :slash? true
   :description "Codifique a mensagem localmente. A keyword nunca acompanha o link."
   :aside ($ shared/context-badge ...)})
```

- A barra `/` é nó decorativo separado com `aria-hidden="true"`; não faz parte do texto do `h1` anunciado.
- Desktop posiciona `:aside` à direita do heading; mobile coloca abaixo da descrição e antes dos painéis.
- `terminal-panel` mantém index e status simultaneamente; nenhum deve ser removido para ganhar espaço.
- Panels e protocol cards permanecem com `border-radius: 0`; qualquer arredondamento visto na moldura do telefone é somente da prancha.

---

## 11 — Regras da cifra

1. Normalizar keyword para minúsculas.
2. Percorrer caracteres únicos na ordem original.
3. Ignorar caracteres fora de `a-z`.
4. Parear letra livre com próxima letra do alfabeto.
5. `z` tenta parear com `y`.
6. Ignorar par quando parceira já estiver ocupada.
7. Criar mapa bidirecional.
8. Substituir texto em minúsculas.
9. Preservar espaços, números, pontuação e símbolos não mapeados.

```text
cipher(cipher(text, keyword), keyword) == lowercase(text)
```

APIs:

```clojure
(build-pair-map keyword)
(cipher text keyword)
(pair-count keyword)
(payload-byte-count text)
```

Payload bytes usa UTF-8, não `.length` JavaScript.

---

## 12 — URL e clipboard

### Codec

```text
string → TextEncoder → bytes → Base64 → Base64URL
Base64URL → Base64 → bytes → TextDecoder → string
```

- `+` → `-`
- `/` → `_`
- remover/restaurar padding `=`
- suportar acentos, japonês e emoji
- retornar erro estruturado para payload inválido

```clojure
{:ok? true :value "..."}
{:ok? false :error :invalid-base64url}
```

### Clipboard

- Usar `navigator.clipboard.writeText` em contexto seguro.
- Tratar rejeição de permissão.
- Sucesso: toast `role="status"`.
- Erro: toast persistente `role="alert"`.
- Nunca copiar keyword junto com link.

---

## 13 — Acessibilidade

- `main`, `header`, `nav`, `section`, `article` e `footer` semânticos.
- Um `h1` por view.
- Nav recebe `aria-label="Navegação principal"`.
- Ativo usa `aria-current="page"`.
- Header status mantém `aria-label` no mobile.
- Brand é button com nome acessível.
- Labels conectados por `for`/`id`.
- Erros usam `aria-invalid` e `aria-describedby`.
- SHOW/HIDE atualiza nome ou `aria-pressed`.
- Focus visible segue token amarelo.
- `prefers-reduced-motion` pausa pulso e remove transições não essenciais.
- Não bloquear zoom/pinch.
- Ordem do DOM deve corresponder à ordem visual no desktop e mobile.

---

## 14 — Testes

### Unitários

- mapa não reutiliza letra em dois pares;
- `z/y` funciona quando disponível;
- cifra é involutiva;
- caracteres não mapeados são preservados;
- codec round-trip suporta Unicode;
- keyword nunca aparece na URL;
- rota legada é reconhecida.

### Estado

- troca de módulo preserva rascunho;
- alteração após staged invalida output;
- purge limpa derivados;
- link abre Receive;
- uptime cancela timer ao desmontar;
- toast expira e limpa timer corretamente.

### UI

- Header permanece fixed.
- Sidebar 218px/178px conforme viewport.
- Mobile nav fica sticky abaixo de 60px.
- Três tabs dividem largura sem overflow.
- Tabs mobile exibem `TX`, `RX` e `::` em todas as viewports suportadas.
- Hover, active e focus correspondem aos tokens.
- Brand/status não cortam em 320px.
- Input e output empilham em até 1040px.
- Footer mobile oculta a telemetria e mostra somente `// END OF LINE` no final.
- Descrição, counters, métricas e status permanecem visíveis no mobile.
- Footer mobile contém somente `// END OF LINE`, alinhado à direita.
- Receive coloca Scan Status antes dos painéis.
- Protocol mantém ordem 01/02/03 e CTA full-width no mobile.

### Matriz de viewports

```text
320×568
375×812
390×844
430×932
768×1024
1024×768
1280×800
1440×900
```

Testar teclado, reduced motion, zoom de 200% e safe areas.

Executar comparação visual também contra:

```text
doc/visual-references/cypher-link-transmit-reference.png
doc/visual-references/cypher-link-receive-reference.png
doc/visual-references/cypher-link-protocol-reference.png
```

Tolerância recomendada: até 2px em alinhamentos estruturais e até 5% em dimensões fluidas; cores dos tokens não possuem tolerância.

---

## 15 — PWA, segurança e deploy

### PWA

- `display: standalone`
- `theme_color: #FFD400`
- `background_color: #07090E`
- fornecer `icon-192.png` e `icon-512.png`
- service worker remove caches antigos em `activate`

### Segurança

- Cifra é narrativa, não criptografia moderna.
- Keyword não entra em logs, analytics, URL ou storage.
- Manter:
  - `X-Frame-Options: DENY`
  - `X-Content-Type-Options: nosniff`
  - `Referrer-Policy: no-referrer`
- Definir CSP compatível com fontes e scripts reais.

### Deploy

```bash
npm ci
npm run test
npm run build
```

Publicar `resources/public`.

---

## 16 — Plano de implementação

| Fase | Entrega |
|---|---|
| 0 | Build mínimo e namespaces válidos |
| 1 | Cipher + codec UTF-8 + testes |
| 2 | Router, store e três módulos |
| 3 | Tokens, background e shell full-screen |
| 4 | Header desktop/mobile |
| 5 | Sidebar e sub-header nav responsivos |
| 6 | Transmitir completo |
| 7 | Receber e links compartilháveis |
| 8 | Protocolo e OPSEC |
| 9 | Acessibilidade, PWA e testes de viewport |
| 10 | Build de produção e deploy |

### Definition of Done

- [ ] Build e testes passam.
- [ ] Tokens CSS coincidem com design handoff.
- [ ] Header/Nav cumprem anatomia e breakpoints.
- [ ] Não existe overflow horizontal nas viewports suportadas.
- [ ] Três módulos funcionam por teclado.
- [ ] Keyword nunca deixa o dispositivo.
- [ ] Aviso OPSEC está visível.
- [ ] PWA possui ícones reais e cache versionado.
- [ ] As três views correspondem às pranchas normativas em desktop e mobile.
- [ ] Mobile não omite informações presentes nas pranchas.

---

```text
// END OF ENGINEERING SPECIFICATION — CYPHER_LINK v2
```
