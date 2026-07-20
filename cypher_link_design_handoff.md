# CYPHER_LINK v2 — Design Handoff

```text
// VISUAL SYSTEM & UX SPECIFICATION
Product label: CYPHER_LINK v2.0
Reference revision: Sites version 5
Reference URL: https://cypher-link-v2.murilinhorps.chatgpt.site/
Visual baseline: 3 responsive reference boards (Transmitir, Receber, Protocolo)
```

| Campo | Especificação |
|---|---|
| Produto | CYPHER_LINK — Cyberpunk Message Cipher |
| Interface | Dashboard operacional full-screen |
| Plataformas | Desktop, tablet e mobile web |
| Tema | Cyberpunk noir, terminal sci-fi e telemetria diegética |
| Idioma | Português do Brasil com labels operacionais em inglês |
| Documento | Especificação visual e comportamental pronta para implementação |

### Referências visuais normativas

As três pranchas abaixo são a fonte visual final deste handoff. Em caso de conflito entre texto legado e imagem, prevalece a prancha correspondente, respeitando as regras de acessibilidade deste documento.

| Módulo | Arquivo de referência |
|---|---|
| Transmitir | `doc/visual-references/cypher-link-transmit-reference.png` |
| Receber | `doc/visual-references/cypher-link-receive-reference.png` |
| Protocolo | `doc/visual-references/cypher-link-protocol-reference.png` |

Cada arquivo apresenta desktop à esquerda e mobile à direita. A moldura externa arredondada do telefone pertence somente à apresentação da prancha; a interface implementada continua com cantos retos.

---

## 01 — Visão do produto

CYPHER_LINK simula uma estação de comunicação segura para campanhas de RPG cyberpunk. O produto cifra e restaura mensagens localmente, gera links com payload e explica seu protocolo sem apresentar a cifra como segurança real.

### Princípios

- **Operacional:** interface parece ferramenta de campo, não landing page.
- **Diegético:** labels, índices, relay e telemetria pertencem à narrativa.
- **Legível:** efeitos neon, ruído e scanlines nunca competem com conteúdo.
- **Local first:** keyword permanece no dispositivo e não acompanha o link.
- **Três domínios cromáticos:** amarelo = ação, cyan = dados, pink = risco.
- **Cantos técnicos:** sem cards arredondados; recortes diagonais substituem radius.
- **Responsivo por reorganização:** desktop usa sidebar; mobile usa sub-header horizontal.

### Arquitetura de informação

O produto possui três módulos persistentes:

1. **TRANSMITIR / TX** — compor mensagem, aplicar keyword, cifrar e compartilhar.
2. **RECEBER / RX** — inserir payload, aplicar keyword e restaurar mensagem.
3. **PROTOCOLO / ::** — explicar cifra simétrica, link sem chave e limite OPSEC.

---

## 02 — Design tokens

### Cores fundamentais

```css
:root {
  --bg:             #07090E;
  --bg-deep:        #040508;
  --surface:        #0D1119;
  --surface-raised: #111722;
  --border:         #202A39;
  --border-soft:    #6277922E;

  --yellow:         #FFD400;
  --yellow-soft:    #FFD4001F;
  --pink:           #FF2A6D;
  --pink-soft:      #FF2A6D1C;
  --cyan:           #05D9E8;
  --cyan-soft:      #05D9E817;
  --green:          #00FF88;
  --green-soft:     #00FF8814;

  --text:           #E4E2D8;
  --text-muted:     #7E8B9D;
  --text-faint:     #495568;
}
```

### Semântica de cor

| Token | Papel |
|---|---|
| `--yellow` | Marca, módulo ativo, CTA principal, keyword e foco primário |
| `--cyan` | Payload, outputs, links, ações secundárias e dados recebidos |
| `--pink` | Purge, warnings, erros, OPSEC e estados perigosos |
| `--green` | Node online, relay ativo, signal ready e conclusão válida |
| `--text` | Títulos, mensagem original e conteúdo principal |
| `--text-muted` | Descrições, labels secundárias e instruções |
| `--text-faint` | Índices, atalhos, metadados e elementos inativos |

Nunca usar dois acentos concorrentes na mesma ação. Indicadores podem combinar label neutra com valor semântico colorido.

### Superfícies

| Superfície | Valor |
|---|---|
| App | `--bg` com grid técnico de 42×42px |
| Topbar | `rgba(7,9,14,.84)` desktop; `rgba(7,9,14,.96)` mobile |
| Sidebar | `rgba(8,11,17,.70)` |
| Painel | `rgba(12,16,24,.90)` + gradiente branco de 2% |
| Campo | `rgba(3,5,9,.65–.70)` |
| Footer | `--bg-deep` |

O background do app combina:

- radial amarelo no canto superior esquerdo: `rgba(255,212,0,.07)`;
- radial cyan à direita: `rgba(5,217,232,.055)`;
- duas linhas de grid com opacidade de 1%;
- camada de ruído visual passiva.

### Tipografia

A aplicação carrega variáveis Geist Sans e Geist Mono. O contrato de produção deve mapear explicitamente:

```css
--font-ui:   "Geist Sans", system-ui, sans-serif;
--font-mono: "Geist Mono", "JetBrains Mono", monospace;
```

| Elemento | Fonte | Tamanho desktop | Peso / tracking |
|---|---|---:|---|
| Marca `CYPHER_LINK_` | UI | 17px | 770 / `0.10em` |
| Versão | Mono | 9px | 400 / `0.08em` |
| Título do módulo | UI | `clamp(30px,4vw,48px)` | 730 / `0.035em` |
| Descrição | UI | 12px | 400 / line-height 1.65 |
| Eyebrow | Mono | 10px | 400 / `0.17em` |
| Nav item | Mono | 11px | 400 / `0.08em` |
| Panel kicker | Mono | 9px | 400 / `0.13em` |
| Field label | Mono | 8px | 400 / `0.16em` |
| Textarea | UI | 13px | 400 / line-height 1.75 |
| Keyword | Mono | 11px | 400 |
| Botões principais | Mono | 9px | 750 / `0.10em` |
| Telemetria | Mono | 7–10px | 400 / `0.08–0.10em` |

Mobile reduz marca para 12px e 11px até 390px; título para `clamp(26px,8vw,32px)`; nav para 9px e 8px até 390px. Inputs e textareas mantêm `16px` CSS para leitura e para impedir zoom automático no iOS; labels e telemetria podem usar 8–10px.

### Cantos e recortes

- `border-radius: 0` em todos os elementos estruturais e controles.
- Marca: recorte diagonal por `clip-path`, com cortes de 22%.
- Status: recortes de 10px em cantos opostos.
- Painéis: recortes de 13px em cantos opostos no desktop.
- Cards de Protocolo: recorte superior direito de 12px.
- Em mobile, `.terminal-panel` remove `clip-path` para maximizar área útil.

### Sombras e glow

```css
--shadow-panel:
  0 24px 60px rgba(0,0,0,.24),
  inset 0 0 40px rgba(0,0,0,.16);

--glow-brand: 0 0 22px rgba(255,212,0,.18);
--glow-focus: 0 0 18px rgba(255,212,0,.055);
--glow-cyan:  0 0 18px rgba(5,217,232,.08);
--glow-pink:  0 0 16px rgba(255,42,109,.10);
--glow-green: 0 0 9px #00FF88;
```

Glows são discretos e vinculados a estado. Não aplicar bloom global.

---

## 03 — Shell da aplicação

### Anatomia desktop

```text
┌──────────────────────────── TOPBAR 76px ─────────────────────────────┐
│ [MARK] CYPHER_LINK_ v2.0                         [● NODE][UPTIME]    │
├──────── SIDEBAR 218px ─────┬──────────────── CONTENT ────────────────┤
│ // CHANNEL                 │ // EYEBROW                              │
│ 01 TRANSMITIR          TX  │ TÍTULO DO MÓDULO /                     │
│ 02 RECEBER             RX  │ descrição                         SCORE │
│ 03 PROTOCOLO           ::  │                                        │
│                            │ painéis do módulo                       │
│ ● RELAY / ACTIVE           │                                        │
├────────────────────────────┴─────────────────────────────────────────┤
│ GLOBAL FOOTER 36px                             // END OF LINE        │
└──────────────────────────────────────────────────────────────────────┘
```

- Shell ocupa viewport inteira; não usa card central.
- Topbar é fixed.
- Workspace inicia após topbar com `margin-top: 76px`.
- Workspace usa `218px minmax(0,1fr)`.
- Content usa `max-width: 1280px`, centralizado dentro da área principal.
- Footer possui 36px e encerra shell visualmente.
- App usa `overflow: hidden` no desktop; conteúdo precisa caber ou possuir scroll controlado.

### Tablet — até 1040px

- Sidebar reduz de 218px para 178px.
- Content usa padding horizontal de 28px.
- Composer, receive layout e cards de protocolo viram uma coluna.
- Output preserva contexto e aparece após input.

### Mobile — até 760px

```text
┌──────────── FIXED HEADER 60px ────────────┐
│ [MARK] CYPHER_LINK_              [● NODE] │
├──────────── STICKY NAV 52px ──────────────┤
│ TRANSMITIR TX │ RECEBER RX │ PROTOCOLO :: │
├───────────────────────────────────────────┤
│ EYEBROW                                   │
│ TÍTULO / + descrição + status contextual │
│                                           │
│ painéis empilhados                        │
├───────────────────────────────────────────┤
│                         // END OF LINE   │
└───────────────────────────────────────────┘
```

- Workspace vira bloco e inicia em 60px.
- Nav vira sub-header sticky em `top: 60px`.
- Content usa `padding: 24px 12px calc(42px + safe-area-bottom)`.
- Painéis usam 13px de padding e perdem recortes diagonais.
- Eyebrow, descrição, status contextual, contadores e métricas permanecem visíveis.
- Footer oculta a telemetria e mantém somente `// END OF LINE`, alinhado ao final.
- Não existe navegação inferior; nav permanece diretamente abaixo do header.
- Não existe overflow horizontal planejado: três itens dividem largura igualmente.
- A página possui scroll vertical natural; header e sub-header permanecem visíveis enquanto os painéis percorrem o viewport.

---

## 04 — Header

### Desktop

| Propriedade | Valor |
|---|---|
| Altura | 76px |
| Position | `fixed; top: 0; inset-inline: 0` |
| Z-index | 20 |
| Padding | `0 30px` |
| Background | `rgba(7,9,14,.84)` |
| Backdrop | `blur(8px)` |
| Border | `1px solid --border` na base |
| Layout | Flex horizontal, `space-between`, centralizado verticalmente |

Uma linha amarela de 118px fica na base esquerda com glow de 12px.

#### Brand

- Botão sem background/borda.
- Marca de 36×36px em amarelo, símbolo escuro `Cᴸ` e pixel pink 5×5px.
- Gap de 12px entre mark e nome.
- Nome amarelo com glow discreto.
- Versão 9px em `--text-faint`, alinhada ao topo.
- Clique volta para Transmitir; conteúdo não compartilhado exige confirmação antes de ser descartado.

#### Status

- Desktop: `min-width: 386px`, altura 34px.
- Fundo green 3.5%, borda green 30% e recorte de 10px.
- Indicador circular de 6px com pulso de 1.8s.
- Texto `SECURED NODE ONLINE`, separador e uptime.
- Status fica à direita; não há outro acionador no header final.

#### Scroll

- Topbar permanece fixed durante scroll.
- Deve reservar 76px no fluxo via margem do workspace.
- O backdrop blur só existe no desktop; mobile usa fundo mais opaco.

### Mobile

| Propriedade | Valor |
|---|---|
| Altura | 60px |
| Padding | `0 max(12px, env(safe-area-inset-left/right))` |
| Background | `rgba(7,9,14,.96)` |
| Backdrop | Nenhum |
| Brand mark | 30×30px |
| Brand name | 12px; 11px até 390px |
| Status | 32×32px, somente indicador visual |

- Versão fica oculta.
- Texto, separador e uptime do status ficam ocultos.
- Nome acessível do status permanece `Status da rede: online`.
- Header usa `min-width: 0` nos filhos para evitar corte.
- Linha amarela reduz para 76px e respeita safe area esquerda.

---

## 05 — Navegação

### Desktop sidebar

| Propriedade | Valor |
|---|---|
| Largura | 218px; 178px até 1040px |
| Background | `rgba(8,11,17,.70)` |
| Border | 1px `--border` à direita |
| Padding | `34px 17px 22px` |
| Comportamento | Persistente, não retrátil |
| Section label | `// CHANNEL`, 9px, tracking `0.17em` |

A sidebar não é fixed independentemente: pertence ao grid abaixo do topbar. O footer do relay fica no fim usando `margin-top: auto`.

#### Nav item

- Altura mínima: 50px.
- Grid: `26px 1fr 18px`.
- Conteúdo: índice, label principal e código `TX`, `RX` ou `::`.
- Fonte: 11px; tracking `0.08em`.
- Transição: 160ms.
- Ícones tradicionais não são usados; os códigos terminais atuam como identificadores.

#### Estados

| Estado | Tratamento |
|---|---|
| Default | `--text-muted`, borda transparente |
| Hover | `--text`, background branco 2% |
| Active | amarelo, borda amarelo 32%, gradiente yellow 11% → 1.6% |
| Active marker | inset amarelo de 2px à esquerda |
| Focus-visible | outline amarelo 1px, offset 3px |

O item ativo também colore índice e código. Estado não depende somente de cor: borda e posição do marcador mudam.

### Mobile sub-header nav

- Ativado em `≤760px`.
- Sidebar vira grid de três colunas iguais.
- Position sticky em `top: 60px`, z-index 19.
- Altura mínima por item: 52px; 49px até 390px.
- Label centralizada com o código `TX`, `RX` ou `::` abaixo.
- Índices, `// CHANNEL` e relay ficam ocultos.
- Separador vertical entre itens usa `--border-soft`.
- Active ganha borda inferior amarela de 2px e inset glow superior.
- Não há scroll horizontal; labels precisam caber entre 320px e 430px.
- Touch target ocupa célula inteira, mínimo efetivo de 49px de altura.

---

## 06 — Telas e fluxos

### Transmitir

Título: `COMPOR SINAL /`.

Desktop usa `1.14fr / 0.86fr`, gap 18px:

- **ENCRYPTION PAYLOAD:** textarea com linhas numeradas, contador, keyword, SHOW/HIDE, geração de chave, CTA e Purge.
- **ENCODED TRANSMISSION:** métricas, payload, Copy Payload, Generate Secure Link e warning.

Fluxo:

```text
idle → editing → ready → staged → copied/shared
                        └────────→ purge → idle
```

Mobile empilha input e output com gap de 12px. Linhas numeradas, contador `83/1024`, métricas `PAIR MAP`/`PAYLOAD` e `SIGNAL READY` permanecem. Apenas keyboard help pode ser removido. A ordem é:

1. heading e descrição;
2. badge `LOCAL CIPHER / PAIR//OFFGRID`, alinhado à direita;
3. painel Encryption Payload completo;
4. painel Encoded Transmission completo;
5. warning;
6. footer com `// END OF LINE`.

No painel de entrada mobile, `GENERATE KEY` ocupa uma linha; `ENCRYPT & STAGE` ocupa a largura útil; `PURGE` aparece abaixo. No output, `COPY PAYLOAD` e `GENERATE SECURE LINK` dividem a mesma linha quando houver ao menos 340px úteis e empilham abaixo disso.

### Receber

Título: `RECEBER SINAL /`.

- Painel Incoming Payload em cyan.
- Textarea `ENCODED_SIGNAL`.
- Campo `SHARED_KEYWORD`.
- CTA `DECODE SIGNAL`.
- Output `DECRYPTED MESSAGE` em verde.
- Estado vazio: `NO DECODED SIGNAL_`.
- Resultado válido: `INTEGRITY 100%` somente quando integridade for verificável.

Mobile mantém eyebrow e descrição, apresenta `SCAN STATUS / NO TRACE` como faixa de largura total e empilha Incoming Payload antes de Decrypted Message. `LISTENING` e `INTEGRITY 100%` permanecem nos panel bars. Textarea, keyword, Decode Signal, empty state e Copy Decoded Message não podem ser omitidos.

### Protocolo

Título: `PROTOCOLO /`.

Três cards:

1. `PARES SIMÉTRICOS` — `Aa ⇄ Bb`.
2. `LINK SEM A CHAVE` — `MSG → LINK`.
3. `MESMO CAMINHO` — `2× = ORIG`.

Cada card usa linha inferior semântica: amarelo, cyan e verde. Após cards:

- bloco `OPSEC NOTE` em pink;
- texto de segurança narrativa;
- CTA `START TRANSMISSION >`.

Cards empilham em tablet e mobile na ordem 01 → 02 → 03. No mobile, cada card mantém índice, diagrama, título, texto e linha inferior semântica. O OPSEC vira bloco vertical e `START TRANSMISSION >` ocupa a largura disponível abaixo do texto.

---

## 07 — Componentes

### Terminal panel

- Border: `1px solid --border`.
- Background: superfície 90% + gradiente branco 2%.
- Padding: 18px desktop; 13px mobile.
- Shadow: `--shadow-panel`.
- Marcador vertical: 1×74px com glow.
- Pixel de estado: 4×4px no canto superior direito.
- Input usa amarelo; output/receive usa cyan.

### Panel bar

- Min-height 27px.
- Border-bottom `--border-soft`.
- Padding-bottom 13px; margin-bottom 17px.
- Kicker 9px, uppercase e tracking `0.13em`.
- Mobile: min-height 22px, padding-bottom 10px, kicker 8px.

### Textarea

- Wrapper mínimo 258px desktop; 116px mobile em Transmitir e Receber.
- Grid desktop: 32px para linhas + conteúdo.
- Background `rgba(3,5,9,.70)`.
- Focus-within: borda yellow 62% + glow.
- Textarea 13px/1.75 desktop; 16px/1.55 mobile. A escala aparente compacta vem do viewport, não de fonte abaixo de 16px no controle editável.
- Resize desativado em composição; permitido verticalmente no payload recebido.

### Keyword field

- Shell com 43px de altura.
- Prompt `$` em green, 13px.
- Input amarelo 11px, flexível e sem outline nativo.
- Keyword mascarada por padrão.
- SHOW/HIDE usa botão inline de 7px e borda esquerda.
- Focus-within colore borda amarelo 56%.

### Botões

Base: cantos retos, uppercase, tracking `0.10em`, transição 150ms.

| Variante | Visual | Hover |
|---|---|---|
| Primary | 47px, fundo amarelo, texto `#0B0C06` | `#FFE74D` + glow amarelo |
| Secondary | borda/texto cyan | cyan-soft + glow cyan |
| Link | borda/texto amarelo | yellow-soft + glow amarelo |
| Danger | borda pink 42%, texto pink | pink-soft + glow pink |

Mobile: primary 45px; danger 40px; ações de output usam duas colunas quando couberem e uma coluna abaixo de 340px úteis.

### Output

- Métricas em duas colunas tanto no desktop quanto no mobile.
- Área codificada: mínimo 297px desktop; 168px mobile.
- Texto amarelo 12px/1.78; mobile 13px/1.6.
- Border amarelo 34%; background com yellow 5.5%.
- Output decodificado troca para green e mínimo de 362px desktop.

### Warning strip

- Grid `31px 1fr`.
- Fundo pink-soft e borda pink 26%.
- Ícone `!` em pink com glow.
- Texto 8px/1.55; mobile 7px/1.45.
- Até 390px, texto limita a 42px de altura.

### Toast

- Desktop: fixed, bottom 55px, right 24px.
- Mobile: left/right 14px; bottom usa safe area.
- Fundo green-black 96%, borda green 42%.
- Entrada de 200ms.
- Sucesso usa `role="status"`; erro deve usar `role="alert"` e pink.

### Global footer

- Desktop: 36px, padding horizontal 29px, telemetria 7px.
- Mobile: mínimo 35px, padding 10×15px, conteúdo alinhado à direita.
- Mobile oculta `ENCRYPTION: LOCAL`, `TRACE: DISABLED` e `PACKET LOSS: 0.0%`; exibe somente `// END OF LINE`.
- End line usa green com glow de 8px.

---

## 08 — Responsividade

| Faixa | Header | Navegação | Conteúdo |
|---|---|---|---|
| `>1040px` | 76px | Sidebar 218px | Painéis em grid |
| `761–1040px` | 76px | Sidebar 178px | Painéis empilhados |
| `391–760px` | 60px | Sub-header sticky 52px | Padding 12px, painéis empilhados |
| `≤390px` | 60px | Sub-header sticky 49px | Descrição visível, controles compactos e fontes editáveis de 16px |
| `≤500px` | 60px | Igual | Status reduzido a 32px |

### Regras de overflow e safe area

- Nenhum overflow horizontal entre 320px e 1440px.
- Elementos flex/grid usam `min-width: 0`.
- Header respeita safe areas laterais.
- Content reserva `env(safe-area-inset-bottom)`.
- Tabs dividem espaço igualmente; não usar carrossel ou scroll horizontal.
- Sticky nav precisa preservar heading ao navegar por âncoras/foco.

---

## 09 — Acessibilidade e interação

- Um `h1` por módulo; cards/painéis usam `h2` quando aplicável.
- `nav` recebe nome `Navegação principal`.
- Item ativo usa `aria-current="page"`.
- Focus visible global: outline amarelo 1px, offset 3px.
- Status possui nome acessível mesmo quando texto visual está oculto.
- SHOW/HIDE anuncia `Exibir keyword` ou `Ocultar keyword`.
- Inputs possuem labels persistentes.
- Erros não dependem apenas de pink.
- Indicadores online/ready incluem texto ou nome acessível.
- `prefers-reduced-motion` reduz pulso, cursor e transições.
- Zoom da página não pode ser bloqueado.

### Teclado

| Tecla | Ação |
|---|---|
| Enter | Cifra quando foco está no contexto permitido |
| Shift+Enter | Nova linha na mensagem |
| Enter no Receive | Tenta restaurar sinal |
| Tab / Shift+Tab | Navega em ordem visual |

---

## 10 — Critérios de aceite

### Identidade

- [ ] Tokens correspondem exatamente à seção 02.
- [ ] Nenhum componente estrutural usa border-radius.
- [ ] Recortes, grid, ruído e glows permanecem discretos.
- [ ] Amarelo, cyan, pink e green mantêm papéis semânticos.

### Header

- [ ] Desktop mede 76px e permanece fixed.
- [ ] Mobile mede 60px e não corta marca/status.
- [ ] Workspace reserva altura correta do header.
- [ ] Status mantém nome acessível no modo compacto.

### Navegação

- [ ] Sidebar possui 218px ou 178px conforme breakpoint.
- [ ] Sidebar não é retrátil.
- [ ] Mobile transforma sidebar em sub-header sticky.
- [ ] Tabs medem 52px ou 49px e não geram overflow.
- [ ] Tabs mobile preservam os códigos `TX`, `RX` e `::` abaixo dos labels.
- [ ] Hover, active e focus são visualmente distintos.

### Telas

- [ ] Transmitir, Receber e Protocolo preservam estado ao alternar.
- [ ] Input e output empilham em até 1040px.
- [ ] Link recebido abre Receber com payload preenchido.
- [ ] Mobile preserva descrições, contadores, status e métricas dos painéis.
- [ ] Footer mobile exibe somente `// END OF LINE`, alinhado ao final.
- [ ] Nenhum controle editável mobile usa fonte menor que 16px.

### Viewports de validação

- [ ] 320×568
- [ ] 375×812
- [ ] 390×844
- [ ] 430×932
- [ ] 768×1024
- [ ] 1024×768
- [ ] 1280×800
- [ ] 1440×900

---

```text
// END OF VISUAL SPECIFICATION — CYPHER_LINK v2
```
