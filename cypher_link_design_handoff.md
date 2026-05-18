# CYPHER_LINK — Design Handoff v2.0

```
// DESIGN DOSSIER — CLASSIFIED
Aesthetic: CYBER RUNNER · Platform: Desktop-wide + Mobile · Mode: DARK ONLY
```

| Campo | Valor |
|---|---|
| Projeto | CYPHER_LINK — Cyberpunk Message Cipher |
| Versão | 2.0 — atualizado com CSS final + referência visual |
| Aesthetic | Cyber Runner — Edgerunners Yellow + Cyan + Pink |
| Plataforma | Desktop wide (1080px) + Mobile responsivo, PWA instalável |
| Tema | Dark only — sem light mode |
| Idioma UI | Inglês (estética de terminal cyberpunk) |
| Fontes | Share Tech Mono (título) + JetBrains Mono (corpo) |

---

## 01 — Aesthetic Direction

### Conceito central

CYPHER_LINK não é um webapp genérico de criptografia. É uma **ferramenta diegética** — parece existir dentro da ficção da campanha de RPG. O design transmite que o usuário está acessando um terminal de hacker em um mundo cyberpunk noir: fundo quase negro, atmosfera criada por gradientes radiais sutis nos cantos, e uma paleta de três cores de acento com papéis distintos e bem definidos.

A versão 2.0 abandona o terminal de coluna única estreita e adota um **layout de duas colunas lado a lado** em desktop — INPUT à esquerda, OUTPUT à direita — com o terminal expandido até `1080px`. O título usa `Share Tech Mono` para diferenciação visual máxima do corpo.

### Mood keywords

- **CYBER RUNNER** — Edgerunners yellow como cor de ação; cyan para dados; pink para perigo
- **ATMOSFÉRICO** — fundo com gradientes radiais coloridos nos cantos, não flat preto
- **DIEGÉTICO** — parece uma ferramenta real da campanha, não um app moderno
- **DUAS ZONAS CLARAS** — input panel amarelo-bordado vs output panel cyan-bordado
- **PARANÓICO** — warning em pink pulsante no footer, status de node online
- **ZERO DECORAÇÃO VAZIA** — todo elemento tem função ou reforça a narrativa

### Mudanças da v1 → v2

| Aspecto | v1 | v2 |
|---|---|---|
| Layout | Coluna única, 640px | Duas colunas side-by-side, 1080px |
| Paleta primária | Verde terminal `#00FF88` | Amarelo Edgerunners `#FFD400` |
| Título | JetBrains Mono bold | Share Tech Mono (display font) |
| Fundo | Preto flat | Gradientes radiais nos cantos |
| Painéis | Sem separação visual | Input panel (amarelo) vs Output panel (cyan) |
| Status | Ausente | Badge "SECURE NODE ONLINE" com bolinha verde |
| Footer | Simples | Warning em pink + `// END OF LINE` em amarelo |

### Anti-patterns — o que NÃO fazer

- ❌ Gradientes coloridos genéricos em área de conteúdo (apenas nos cantos do body)
- ❌ Cards com bordas arredondadas e sombras suaves
- ❌ Fontes sans-serif modernas (Inter, Roboto, SF Pro)
- ❌ Ícones SVG decorativos sem função
- ❌ Usar amarelo e cyan e pink no mesmo elemento — cada cor tem seu domínio
- ❌ Qualquer elemento que quebre a ilusão de "terminal real"

---

## 02 — Color Palette

### Backgrounds

| Token | Hex | Uso |
|---|---|---|
| `--bg` | `#080A0F` | Fundo global — quase negro, levemente azulado |
| `--surface` | `#0F111A` | Fundo do terminal e painéis |
| `--border` | `#1B2230` | Bordas neutras, separadores, grid |

O fundo não é flat — recebe **quatro gradientes radiais** que criam atmosfera:

```css
body {
  background:
    radial-gradient(circle at top left,    rgba(255,212,0,  0.075), transparent 28%),
    radial-gradient(circle at bottom right,rgba(5,217,232,  0.085), transparent 36%),
    radial-gradient(circle at 80% 12%,     rgba(255,42,109, 0.05),  transparent 24%),
    radial-gradient(circle at 12% 85%,     rgba(5,217,232,  0.055), transparent 26%),
    var(--bg);
}
```

O terminal em si também recebe gradiente sutil interno:

```css
.terminal {
  background:
    linear-gradient(135deg, rgba(255,212,0,  0.032), transparent 32%),
    linear-gradient(315deg, rgba(5,217,232,  0.035), transparent 34%),
    linear-gradient(45deg,  rgba(5,217,232,  0.026), transparent 38%),
    var(--surface);
}
```

### Accents — três cores, três papéis, domínios separados

| Token | Hex | Nome | Domínio exclusivo |
|---|---|---|---|
| `--accent` | `#FFD400` | Edgerunners Yellow | Título, CTAs primários, inputs ativos, output cifrado, glow principal |
| `--accent2` | `#FF2A6D` | Danger Pink | Erros, alertas, warnings, botão PURGE, decrypt danger states |
| `--accent3` / `--accent4` | `#05D9E8` | Cyan Data | Output panel, labels de status, botões secundários, links, NODE ONLINE |

> **Regra de domínio**: cada cor de acento tem sua zona de responsabilidade na UI. Amarelo = ação de input. Cyan = dados de output/status. Pink = perigo/erro. Nunca misture dois acentos no mesmo componente.

### Status especial — verde de node ativo

A bolinha `● SECURE NODE ONLINE` usa verde `#00FF88` exclusivamente para indicar que o sistema está online. Não é um quarto acento geral — é um indicador de estado pontual.

```css
.status-main::before {
  content: '● ';
  color: #00ff88;
  text-shadow: 0 0 8px rgba(0,255,136,0.85), 0 0 18px rgba(0,255,136,0.24);
}
```

### Text

| Token | Hex | Uso |
|---|---|---|
| `--text` | `#D8D6C8` | Corpo de texto — levemente quente (bege) |
| `--text-dim` | `#647080` | Placeholders, comentários, labels dim, metadados |

### Glow recipes

```css
--glow:      0 0 8px  rgba(255,212,0,  0.86),
             0 0 20px rgba(255,212,0,  0.24),
             0 0 32px rgba(5,217,232,  0.10);   /* amarelo + toque cyan */

--glow-cyan: 0 0 8px  rgba(5,217,232,  0.75),
             0 0 20px rgba(5,217,232,  0.18);

--glow-pink: 0 0 8px  rgba(255,42,109, 0.8),
             0 0 20px rgba(255,42,109, 0.2);
```

---

## 03 — Typography

### Duas fontes, papéis distintos

A v2 introduz uma segunda fonte para criar hierarquia visual mais forte entre título e corpo, sem abandonar o feel monospace:

| Fonte | Uso | Import |
|---|---|---|
| **Share Tech Mono** | Título `CYPHER_LINK_` — display, impacto visual | Google Fonts |
| **JetBrains Mono** | Todo o resto — corpo, labels, inputs, outputs, botões | Google Fonts ou fontsource |

```css
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap');
/* JetBrains Mono via fontsource/jetbrains-mono npm ou Google Fonts */
```

O título usa `Share Tech Mono` porque é mais condensada e blocuda — cria o impacto de logotype que `JetBrains Mono` (projetada para código, mais aberta) não consegue na mesma escala.

### Escala tipográfica

| Elemento | Tamanho | Fonte | Peso | Cor |
|---|---|---|---|---|
| `.terminal-title` (CYPHER_LINK_) | `clamp(1.7rem, 5vw, 3.2rem)` | Share Tech Mono | 400 | `--accent` amarelo + glow |
| Panel titles (`> INPUT / ...`) | `0.75rem` | JetBrains Mono | 400 | `--accent3` cyan |
| Labels de campo (`KEYWORD`, `MESSAGE`) | `0.70rem` | JetBrains Mono | 400 | `--text-dim` |
| Input / textarea text | `0.95rem` | JetBrains Mono | 400 | `--accent` amarelo |
| Output cifrado | `0.95rem` | JetBrains Mono | 400 | `--accent` amarelo |
| Status badge | `0.75rem` | JetBrains Mono | 400 | `--accent3` cyan |
| Uptime | `0.68rem` | JetBrains Mono | 400 | `--text-dim` |
| Descrição header | `0.82rem` | JetBrains Mono | 400 | `--text-dim` |
| Footer / warnings | `0.75rem` | JetBrains Mono | 400/700 | `--text-dim` / `--accent2` |
| Botões | `0.75rem` | JetBrains Mono | 400 | acento do botão |

### Regras tipográficas

- `letter-spacing: 0.06em` no título; `0.08–0.12em` em labels UPPERCASE
- `letter-spacing: 0.1em` nos botões — reforça o feel de comando
- `line-height: 1.6` para texto corrido e description; `1.5` para textarea
- Nunca use italic — terminais não têm italic
- UPPERCASE para: panel titles, labels de campo, botões, status badge
- O cursor `▊` ou `_` no título pisca com `animation: blink 1s steps(2) infinite`

---

## 04 — Spacing & Layout

### Container e grid

| Propriedade | Valor |
|---|---|
| `.page` | `min-height: 100vh; display: grid; place-items: center; padding: 2rem` |
| `.terminal` max-width | `1080px` — wide terminal, ocupa a tela |
| `.terminal` padding | `1.5rem` |
| `.terminal-grid` | `display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem` |
| Alinhamento | Centralizado na viewport com `place-items: center` |

O layout de duas colunas é a mudança estrutural mais importante da v2. Em desktop, input e output ficam lado a lado. Em mobile (`≤ 720px`), colapsa para coluna única.

### Escala de espaçamento

| Valor | Uso |
|---|---|
| `0.28rem` | Gap interno do status badge (linhas) |
| `0.35rem` | Margin-bottom de labels |
| `0.65rem` | Padding vertical de inputs |
| `0.75rem` | Gap entre botões na `.btn-row` |
| `1rem` | Padding dos painéis `.panel`; margin entre campos `.field` |
| `1.25rem` | Gap do `.terminal-grid` |
| `1.5rem` | Padding do `.terminal` |
| `2rem` | Padding do `.page` |

### Estrutura de painéis

Os dois painéis têm **estilos de borda distintos** que sinalizam seu papel:

```
┌──────────────────────────────────────────────────────────────────────┐
│  // CYPHER_LINK v1.0 — SECURE CHANNEL               [STATUS BADGE]  │
│──────────────────────────────────────────────────────────────────────│
│  CYPHER_LINK_▊                  Secure channel for...               │
│──────────────────────────────────────────────────────────────────────│
│                                                                      │
│  ┌─ INPUT PANEL (amarelo) ──┐   ┌─ OUTPUT PANEL (cyan) ────────┐    │
│  │ > INPUT / ENCRYPTION...  │   │ > OUTPUT / ENCODED...         │    │
│  │                          │   │                               │    │
│  │  KEYWORD                 │   │  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐  │    │
│  │  [neon-dragon__________] │   │  │ Imcnntro cnfjrmdno...    │  │    │
│  │                          │   │  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘  │    │
│  │  MESSAGE                 │   │                               │    │
│  │  [Encontro confirmado... │   │  [ GENERATE LINK ]           │    │
│  │   Leve o pacote...___]   │   └──────────────────────────────┘    │
│  │                          │                                        │
│  │  [ENCRYPT] [COPY] [PURGE]│                                        │
│  └──────────────────────────┘                                        │
│                                                                      │
│  ! WARNING: messages intercepted...         // END OF LINE          │
└──────────────────────────────────────────────────────────────────────┘
```

### Bordas dos painéis

| Painel | Border | Box-shadow |
|---|---|---|
| `.panel-input` | `1px solid rgba(255,212,0, 0.72)` | `0 0 8px rgba(255,212,0,0.16), 0 0 18px rgba(255,212,0,0.08)` |
| `.panel-output` | `1px solid rgba(5,217,232, 0.82)` | `0 0 8px rgba(5,217,232,0.18), 0 0 18px rgba(5,217,232,0.10)` |
| `.panel` (base) | `1px solid var(--border)` | `inset 0 0 24px rgba(0,0,0,0.2)` |

O terminal externo tem sombra longa para elevação:

```css
.terminal {
  box-shadow:
    0 0 0 1px rgba(255,212,0, 0.035),
    0 0 0 2px rgba(5,217,232, 0.018),
    0 30px 80px rgba(0,0,0, 0.52);
}
```

---

## 05 — Components

### Terminal Header

O header tem três zonas horizontais separadas por `border-bottom: 1px solid --border`:

```
CYPHER_LINK_▊                              ┌─────────────────────┐
                                           │ ● SECURE NODE ONLINE│
Secure channel for encrypted messages.    │ NODE ID: CTX-7SIN   │
Write a key, compose your message, and    │ UPTIME: 12:45:32    │
generate a sha-able link.                 └─────────────────────┘
```

**Título `.terminal-title`:**

| Propriedade | Valor |
|---|---|
| Font | Share Tech Mono, 400 |
| Size | `clamp(1.7rem, 5vw, 3.2rem)` |
| Color | `--accent` `#FFD400` |
| text-shadow | `var(--glow)` — amarelo brilhante |
| text-transform | `uppercase` |
| letter-spacing | `0.06em` |
| Cursor piscante | `▊` ou `_` com `animation: blink 1s steps(2) infinite` |

**Status Badge `.status`:**

| Propriedade | Valor |
|---|---|
| Border | `1px solid rgba(5,217,232, 0.9)` |
| Background | `rgba(5,217,232, 0.06)` |
| Box-shadow | `0 0 8px rgba(5,217,232,0.85), 0 0 20px rgba(5,217,232,0.24)` |
| Padding | `0.55rem 0.8rem` |
| Font-size | `0.75rem` |
| Color | `--accent3` cyan |
| Bolinha | `● ` prefixo verde `#00FF88` com glow verde |
| Uptime | `0.68rem`, `--text-dim`, `letter-spacing: 0.08em` |

---

### Panel Titles

```
> INPUT / ENCRYPTION PAYLOAD
> OUTPUT / ENCODED TRANSMISSION
```

| Propriedade | Valor |
|---|---|
| Prefixo `> ` | Cor `--accent3` cyan com `var(--glow-cyan)` |
| Texto | `--accent3` cyan, UPPERCASE, `0.75rem`, `letter-spacing: 0.12em` |
| Margin-bottom | `1rem` |

---

### Input Fields (Keyword + Message)

```
  KEYWORD
  ──────────────────────────────────────────
  neon-dragon▊

  MESSAGE
  ──────────────────────────────────────────
  Encontro confirmado na estação 7.
  Leve o pacote e aguarde instruções.
  Confidencial.▊
```

| Propriedade | Valor |
|---|---|
| `background` | `transparent` |
| `border` | `none` |
| `border-bottom` | `1px solid --border` default |
| `border-bottom` focus | `1px solid --accent` amarelo |
| `box-shadow` focus | `0 2px 0 -1px var(--accent)` |
| `color` | `--accent` `#FFD400` amarelo |
| `placeholder color` | `rgba(255,212,0, 0.45)` — amarelo 45% opaco |
| `caret-color` | `--accent` amarelo |
| `font` | JetBrains Mono `0.95rem` |
| `padding` | `0.65rem 0` |
| Label acima | `--text-dim`, `0.70rem`, UPPERCASE, `letter-spacing: 0.08em` |
| Textarea `min-height` | `180px` desktop; `150px` mobile |
| Textarea `line-height` | `1.5` |
| Textarea `resize` | `vertical` (diferente da v1 que era `none`) |

**Atributos HTML obrigatórios:**
```html
autocomplete="off"
autocorrect="off"
autocapitalize="off"
spellcheck="false"
```

---

### Botões — três variantes

A v2 tem três tipos de botão com cores distintas, todos na `.btn-row` flex:

```
[ ENCRYPT ]    [ COPY ]    [ PURGE ]
  amarelo        cyan         pink
```

**Specs compartilhadas:**

| Propriedade | Valor |
|---|---|
| `background` | `transparent` |
| `font` | JetBrains Mono, `0.75rem` |
| `letter-spacing` | `0.1em` |
| `text-transform` | uppercase |
| `padding` | `0.8rem 1rem` |
| `transition` | `all 0.15s` |
| Border-radius | **0** — sem arredondamento |

**`.btn-primary` — `[ ENCRYPT ]` — amarelo:**

| Estado | Estilos |
|---|---|
| Normal | `border: 1px solid --accent; color: --accent` |
| Hover | `background: --accent; color: --bg; box-shadow: var(--glow)` |

**`.btn-secondary` — `[ COPY ]` / `[ GENERATE LINK ]` — cyan:**

| Estado | Estilos |
|---|---|
| Normal | `border: 1px solid --accent3; color: --accent3` |
| Hover | `background: --accent3; color: --bg; box-shadow: var(--glow-cyan)` |

**`.btn-danger` — `[ PURGE ]` — pink:**

| Estado | Estilos |
|---|---|
| Normal | `border: 1px solid --accent2; color: --accent2` |
| Hover | `background: --accent2; color: --bg; box-shadow: var(--glow-pink)` |

Em mobile todos os botões viram `width: 100%` e o `.btn-row` usa `flex-direction: column`.

---

### Output Block — `.output`

```
┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
  Imcnntro cnfjrmdno po estnçio 7.
  Mve n pctn e ogpcrde ipstrpções.
  Cnffdensinl.
└ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
```

| Propriedade | Valor |
|---|---|
| `border` | `1px dashed rgba(255,212,0, 0.35)` — dashed, não solid |
| `min-height` | `252px` |
| `padding` | `1rem` |
| `color` | `--accent` amarelo |
| `line-height` | `1.6` |
| `white-space` | `pre-wrap` |
| `word-break` | `break-word` |
| Background | gradiente sutil: `linear-gradient(135deg, rgba(255,212,0,0.032)...) + rgba(255,212,0,0.014)` |

O output usa **borda dashed** propositalmente — transmite a ideia de zona temporária, dados em trânsito, não uma caixa definitiva.

---

### Terminal Footer

```
! WARNING: messages intercepted without a valid keyword cannot be decrypted.
                                                              // END OF LINE
```

| Elemento | Cor | Estilo |
|---|---|---|
| `! WARNING:` label | `--accent2` pink | `font-weight: 700` + glow pink |
| Texto do warning | `--accent2` pink | normal |
| `// END OF LINE` | `--accent` amarelo | `text-shadow: var(--glow)` |
| Container | `--text-dim` | `0.75rem`, `border-top: 1px solid --border` |

---

### Palette Display — rodapé visual

O CSS inclui um display da paleta no rodapé com swatches:

```css
.palette {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.4rem;
}
.swatch { min-height: 22px; border: 1px solid rgba(255,255,255,0.08); }
```

Sete swatches: `--bg`, `--surface`, `--border`, `--accent`, `--accent2`, `--accent3/4`, texto. É um elemento diegético — parece o sistema exibindo sua própria paleta de diagnóstico.

---

## 06 — Effects & Animation

### Scanline — passivo, sutil

```css
.terminal::after {
  content: '';
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 0, 0, 0.05) 2px,   /* 0.05 na v2, mais presente que v1 */
    rgba(0, 0, 0, 0.05) 4px
  );
  pointer-events: none;
}
```

### Cursor piscante no título

```css
@keyframes blink {
  50% { opacity: 0; }
}

.cursor-yellow {
  color: var(--accent);
  text-shadow: var(--glow);
  animation: blink 1s steps(2) infinite;
}
```

O cursor faz parte do título na marcação HTML: `CYPHER_LINK<span class="cursor-yellow">_</span>`

### Glow — cada acento tem seu recipe

| Elemento | Glow recipe |
|---|---|
| Input focus (border-bottom) | `box-shadow: 0 2px 0 -1px var(--accent)` |
| `.btn-primary` hover | `var(--glow)` — amarelo + toque cyan |
| `.btn-secondary` hover | `var(--glow-cyan)` — cyan |
| `.btn-danger` hover | `var(--glow-pink)` — pink |
| `.panel-input` | `0 0 8px rgba(255,212,0,0.16), 0 0 18px rgba(255,212,0,0.08)` — sempre visível (não só hover) |
| `.panel-output` | `0 0 8px rgba(5,217,232,0.18), 0 0 18px rgba(5,217,232,0.10)` — sempre visível |
| `.status` badge | `0 0 8px rgba(5,217,232,0.85), 0 0 20px rgba(5,217,232,0.24)` |
| `.status-main` bolinha | glow verde `rgba(0,255,136,...)` |
| `! WARNING` text | `0 0 6px rgba(255,42,109,0.85), 0 0 14px rgba(255,42,109,0.34)` |

> **Diferença da v1:** na v2 os painéis têm glow **permanente** (não só em hover) porque são zonas funcionais fixas, não elementos interativos transitórios.

### Animações de estado dos botões

| Animação | Especificação |
|---|---|
| Copy success | Texto `[ COPY ]` → `[ COPIED ✓ ]` por 2000ms |
| Generate Link | Texto `[ GENERATE LINK ]` → `[ LINK COPIED ✓ ]` por 2000ms |
| Purge / Clear | Limpa os campos — sem animação de transição, imediato |
| Duração máxima | 300ms para transições visuais (exceto feedback de estado) |

### Glitch effect no título — hover restrito

```css
@keyframes glitch {
  0%   { text-shadow: var(--glow); }
  20%  { text-shadow: -2px 0 #FF2A6D, 2px 0 #05D9E8, var(--glow); }
  40%  { text-shadow:  2px 0 #FF2A6D, -2px 0 #05D9E8; }
  60%  { text-shadow: -2px 0 #FFD400; }
  80%  { text-shadow:  2px 0 #05D9E8; }
  100% { text-shadow: var(--glow); }
}
```

- Apenas em `.terminal-title:hover`
- Duração: `200ms`, `animation-iteration-count: 1`
- Usa as três cores de acento no offset — amarelo, cyan, pink
- **NÃO aplicar** em inputs, outputs ou botões

---

## 07 — Mobile & Responsive

### Breakpoint único: 720px

A v2 tem um breakpoint principal em `720px` (diferente dos múltiplos da v1):

```css
@media (max-width: 720px) {
  .page             { padding: 1rem; align-items: stretch; }
  .terminal         { padding: 1rem; }
  .terminal-header  { flex-direction: column; }
  .status           { white-space: normal; width: 100%; }
  .terminal-grid    { grid-template-columns: 1fr; }  /* colapsa para 1 coluna */
  textarea          { min-height: 150px; }
  .btn-row          { flex-direction: column; }
  .btn-primary,
  .btn-secondary,
  .btn-danger       { width: 100%; }
}
```

Em mobile, a sequência visual de cima para baixo é:

1. Header com título + status badge (empilhados)
2. Input panel (keyword + message + botões)
3. Output panel (output cifrado + generate link)
4. Footer com warning

### Comportamentos específicos de mobile

- `min-height: 48px` em todos os botões para área de toque
- `font-size: 0.95rem` nos inputs — acima de 16px, evita zoom iOS
- `align-items: stretch` no `.page` — terminal ocupa toda a largura
- Status badge: `white-space: normal; width: 100%` — quebra texto em vez de overflow
- `resize: vertical` na textarea é mantido — útil em mobile landscape

### Acessibilidade

- Contraste: `#FFD400` sobre `#0F111A` — ratio ~10:1, passa WCAG AAA
- Contraste: `#05D9E8` sobre `#0F111A` — ratio ~8:1, passa WCAG AAA
- Focus visible: `border-bottom: 1px solid --accent` customizado em inputs
- `@media (prefers-reduced-motion)` — desativa glitch e cursor blink, mantém transições de cor

---

## 08 — CSS Variables — Referência Completa

```css
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap');

* { box-sizing: border-box; }

:root {
  /* Backgrounds */
  --bg:       #080A0F;
  --surface:  #0F111A;
  --border:   #1B2230;

  /* Accents */
  --accent:   #FFD400;   /* amarelo Edgerunners — ação / input */
  --accent2:  #FF2A6D;   /* pink danger — erros / purge / alertas */
  --accent3:  #05D9E8;   /* cyan — output / status / links */
  --accent4:  #05D9E8;   /* cyan secundário (alias de accent3) */

  /* Text */
  --text:     #D8D6C8;
  --text-dim: #647080;

  /* Fonts */
  --font:     'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
  /* Share Tech Mono — apenas para .terminal-title */

  /* Glow recipes */
  --glow:      0 0 8px  rgba(255, 212, 0,   0.86),
               0 0 20px rgba(255, 212, 0,   0.24),
               0 0 32px rgba(5,   217, 232, 0.10);

  --glow-cyan: 0 0 8px  rgba(5,   217, 232, 0.75),
               0 0 20px rgba(5,   217, 232, 0.18);

  --glow-pink: 0 0 8px  rgba(255, 42,  109, 0.8),
               0 0 20px rgba(255, 42,  109, 0.2);
}
```

---

## 09 — Prompts para Ferramentas de Design AI

### Prompt principal — visão geral completa

Cole no Claude Artifacts, v0, Bolt ou qualquer ferramenta de design AI:

```
Design a cyberpunk terminal cipher tool called CYPHER_LINK.

PALETTE:
- Background: #080A0F (near-black, slightly blue)
- Surface: #0F111A
- Border: #1B2230
- Primary accent: #FFD400 (Edgerunners yellow) — inputs, CTAs, output text
- Danger accent: #FF2A6D (hot pink) — errors, purge button, warnings
- Data accent: #05D9E8 (cyan) — output panel, status, secondary buttons
- Body text: #D8D6C8 | Dim text: #647080

TYPOGRAPHY:
- Title font: Share Tech Mono (Google Fonts), weight 400, uppercase
- Everything else: JetBrains Mono
- No italic anywhere. Monospace only.

LAYOUT (desktop):
- Full-width terminal container, max-width 1080px, centered
- Two-column grid inside: INPUT panel left, OUTPUT panel right, gap 1.25rem
- Terminal has subtle scanline overlay (repeating-linear-gradient)
- Body background: four radial gradients in corners (yellow top-left, cyan bottom-right,
  pink 80%/12%, cyan 12%/85%) creating atmospheric glow without solid color

HEADER (inside terminal):
- Top pseudo-element: "// CYPHER_LINK v1.0 — SECURE CHANNEL" in dim text
- Title "CYPHER_LINK_" in Share Tech Mono, #FFD400, with yellow glow text-shadow
  The trailing underscore blinks with CSS animation (steps(2), 1s, infinite)
- Description text below title in dim color
- Status badge top-right: border + background cyan, "● SECURE NODE ONLINE" where
  the ● is green (#00FF88) with green glow. Below: "NODE ID: CTX-7SIN" and
  "UPTIME: 12:45:32" in dim text

INPUT PANEL (left):
- Border: 1px solid rgba(255,212,0,0.72) + yellow box-shadow glow
- Panel title: "> INPUT / ENCRYPTION PAYLOAD" in cyan uppercase
- Fields: KEYWORD (single line input) and MESSAGE (textarea, min-height 180px)
- Labels dim uppercase. Input text and placeholder in yellow (#FFD400).
- Input focus: border-bottom turns yellow + subtle glow
- Three buttons in a row: [ENCRYPT] yellow, [COPY] cyan, [PURGE] pink
- All buttons: transparent bg, 1px solid border in their accent color,
  hover fills background with accent + glow. No border-radius.

OUTPUT PANEL (right):
- Border: 1px solid rgba(5,217,232,0.82) + cyan box-shadow glow
- Panel title: "> OUTPUT / ENCODED TRANSMISSION" in cyan uppercase
- Output area: min-height 252px, border 1px dashed rgba(255,212,0,0.35),
  yellow text, pre-wrap, subtle yellow gradient background tint
- [GENERATE LINK] button below output: full-width, cyan accent

FOOTER:
- border-top separator
- Left: "! WARNING:" in bold hot pink with pink glow + warning text
- Right: "// END OF LINE" in yellow with yellow glow
- Optional: 7-cell color palette swatch strip (diagnostic display)

MOBILE (≤ 720px):
- Single column layout, panels stack vertically
- Header stacks title above status badge
- All buttons become full-width
- Textarea min-height reduces to 150px
```

### Prompt — decrypt view

```
Same CYPHER_LINK v2 design (yellow/cyan/pink palette, Share Tech Mono title,
two-column layout at 1080px).

Header changes to "// INCOMING TRANSMISSION" instead of secure channel text.
Status badge changes to warning state: "● SIGNAL INTERCEPTED" in pink (#FF2A6D).

Left panel shows:
- Label "ENCRYPTED MESSAGE" with the cipher text in dim color (#647080) — unreadable
- KEYWORD input for decryption key (same yellow styling)
- [DECRYPT] button in pink (#FF2A6D) accent instead of yellow

Right panel shows:
- Output area empty until decryption runs
- After decryption: text appears in yellow (#FFD400) with fade-in animation

Footer warning changes to:
"! CAUTION: keyword mismatch will produce corrupted output — verify before sharing."
```

### Prompt — botões isolados (três variantes)

```
Create three cyberpunk terminal button variants for CYPHER_LINK:

Shared specs:
- Font: JetBrains Mono, 0.75rem, letter-spacing 0.1em, uppercase
- Background: transparent always (default). Fills on hover.
- Padding: 0.8rem 1rem. No border-radius. Transition: all 0.15s.

[ENCRYPT] — Primary / Yellow:
- Default: border 1px solid #FFD400, color #FFD400
- Hover: background #FFD400, color #080A0F,
  box-shadow: 0 0 8px rgba(255,212,0,0.86), 0 0 20px rgba(255,212,0,0.24)

[COPY] / [GENERATE LINK] — Secondary / Cyan:
- Default: border 1px solid #05D9E8, color #05D9E8
- Hover: background #05D9E8, color #080A0F,
  box-shadow: 0 0 8px rgba(5,217,232,0.75), 0 0 20px rgba(5,217,232,0.18)

[PURGE] — Danger / Pink:
- Default: border 1px solid #FF2A6D, color #FF2A6D
- Hover: background #FF2A6D, color #080A0F,
  box-shadow: 0 0 8px rgba(255,42,109,0.8), 0 0 20px rgba(255,42,109,0.2)
```

### Prompt — output block isolado

```
Create the cipher output block for CYPHER_LINK v2.

Container specs:
- Border: 1px dashed rgba(255,212,0,0.35) — dashed, not solid
- Min-height: 252px. Padding: 1rem.
- Background: linear-gradient(135deg, rgba(255,212,0,0.032), transparent 42%)
              + rgba(255,212,0,0.014) base tint
- Text color: #FFD400 (yellow). Font: JetBrains Mono. White-space: pre-wrap.
- Line-height: 1.6

Below the output:
- [GENERATE LINK] button full-width, cyan (#05D9E8) accent.
  On click: changes text to [LINK COPIED ✓] for 2 seconds then reverts.

The dashed border is intentional — signals temporary/in-transit data state.
```

---

```
// END OF TRANSMISSION — CYPHER_LINK Design Handoff v2.0
```
