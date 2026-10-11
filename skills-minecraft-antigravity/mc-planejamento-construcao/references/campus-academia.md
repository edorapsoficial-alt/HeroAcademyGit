# Campus da Academia de Heróis · planejamento de região

## Índice
1. Princípios de implantação
2. Zoneamento e relações
3. Circulação, eixos e marcos
4. Terreno, água e vegetação em escala de região
5. Catálogo de edifícios
6. Diagrama de referência
7. Ordem de construção (Runs do campus)

---

## 1. Princípios de implantação

- **Um eixo principal** liga o portão à praça e ao edifício principal. É a espinha do campus e o que dá cara de academia: simetria no centro, assimetria nas bordas.
- **Hierarquia de marcos**: 1 marco principal (torre do relógio, 45 ou mais de altura), 2 a 3 secundários (torre arcana, cúpula da biblioteca, torres do portão, 28 a 40) e o resto entre 12 e 24. Cada marco precisa de pelo menos um ponto de vista aberto de longe.
- **Compacto o bastante para andar**: caminhar leva ≈4,3 blocos/s (≈5,6 correndo). Mantenha os prédios principais a até 120 blocos do centro (≈30 s a pé) e o resto dentro de 200.
- **O terreno manda**: o edifício principal ocupa a plataforma mais alta; a arena aproveita uma depressão natural; dormitórios ficam em terraços com vista; água e jardins nas partes baixas. Terraplane o mínimo (cortes e aterros de até 4 blocos fora da pegada dos prédios).
- **Pelo menos 40% de área natural** (vegetação, água, terreno). Sem isso o campus vira maquete.
- **Escala ajustável**: as medidas abaixo são para um campus de porte médio; use ×0,7 para compacto e ×1,3 para monumental, mantendo as proporções.

---

## 2. Zoneamento e relações

| Zona | Prédios | Fica perto de | Fica longe de |
|---|---|---|---|
| Acadêmica | edifício principal, pavilhões de aulas, biblioteca, salão de missões | praça, refeitório | arena, ferraria (ruído) |
| Residencial | dormitórios, casa do diretor | refeitório, jardim, enfermaria | arena, ferraria, estábulos (barulho e cheiro) |
| Treino | arena, dojo, enfermaria | enfermaria, vestiários, refeitório | dormitórios, biblioteca |
| Serviço | portão, ferraria, estábulos e poleiro, depósitos | portão e estrada de acesso | praça e eixo nobre (ficam nas laterais) |
| Mística | torre arcana, capela, observatório | ponto alto ou isolado, jardim | arena |
| Natureza | jardim sakura, lago, estufa, bosque | dormitórios, biblioteca | — |

---

## 3. Circulação, eixos e marcos

- **Avenida principal**: 7 a 9 de largura, pavimentada, com postes de luz a cada 9 blocos.
- **Ruas secundárias**: 4 a 5 de largura. **Trilhas**: 2 a 3 (terra e cascalho).
- **Galerias cobertas** ligando aulas ↔ biblioteca ↔ refeitório; **pontes** entre torres e sobre o lago; **escadarias monumentais** em desníveis de 3 ou mais.
- **Sem becos sem saída**: todo caminho leva a um destino. Praças a cada 40 a 60 blocos de caminho, com bancos e fonte.
- **Linhas de visão**: o eixo principal termina na torre do relógio; a biblioteca e a torre arcana aparecem de pelo menos duas praças.
- **Skyline**: da entrada, a silhueta deve subir em degraus (portão → praça → edifício principal → torre), não ter um platô uniforme.

---

## 4. Terreno, água e vegetação em escala de região

- **Plataformas**: cada prédio ganha pad nivelado + talude suave de 4 a 8 blocos; muros de arrimo com hera onde o desnível passar de 3.
- **Lago** com ponte arqueada e pavilhão (casa de chá); riacho ou canal alimentando a fonte da praça, tudo em bacia fechada.
- **Cerejeiras em bosquetes** de 5 a 12 árvores perto dos jardins e dormitórios; floresta de transição (carvalho, bétula) ao redor do campus; campos de flores nas bordas.
- **Pedras decorativas** em grupos de 1 a 3 (andesite, mossy cobblestone) junto a taludes e margens.
- **Perímetro**: muralha com torres de guarda, mais um muro baixo ou cerca viva onde o terreno já protege. Portão principal com brasão e um portão secundário de serviço.

---

## 5. Catálogo de edifícios

Pegada em L×P (blocos) e altura total. “Paleta” remete a `references/paletas-de-blocos.md`.

| Edifício | Pegada × altura | Silhueta e marca | Paleta | Interiores-chave |
|---|---|---|---|---|
| Portão da Academia | 24×12; torres 7×7 × 20; arco 7 de largura × 9 de altura | duas torres com telhado cônico, arco com grade, brasão acima, ameias, ponte ou escadaria | P6 + telhado de P1 | guarita, escada espiral, sala de guarda |
| Edifício Principal | 70×34 em U ou H; corpo 22 a 24, alas 18 a 20 | simetria, torre central, escadaria monumental, rosácea, telhados cruzados em cobre oxidado, torreões nos cantos | P1 | saguão de 3 andares, sala do diretor, secretaria, salão de cerimônias |
| Torre do Relógio | 11×11 × 50 a 60 | fuste com cintas, 4 mostradores, câmara do sino com venezianas, agulha com para-raios | P1 | escada espiral, sala do mecanismo, sino |
| Pavilhões de Aulas (×2) | 40×16 × 16 (3 andares) | arcada no térreo, janelas altas, telhados cruzados com lucarnas, pátio interno | P1 (mais simples) | salas de aula ≈9×11, corredor de 4, laboratório |
| Biblioteca | 32×24 × 22; cúpula até 30 | átrio com claraboia ou cúpula, janelões, torre de leitura | P5 | estantes de 3 a 4 de altura, mezanino, mesas, nichos, balcão |
| Dormitórios (×2 a 4) | 26×14 (planta em L) × 15 | enxaimel, sacadas, chaminés, lucarnas, torreão de escada | P2 | quartos ≈5×6, sala comum com lareira, lavanderia e banho |
| Refeitório (Grande Salão) | 34×18 + cozinha 14×12 × 14 (nave com 10 livres) | telhado íngreme com vigas expostas, rosácea na empena, chaminés largas | P5 | mesas longas, mesa alta, lareiras, cozinha |
| Arena de Treino | oval 56×44 × 14 | anel de arcos, arquibancada em degraus, torres de acesso, estandartes | P4 | arena central, arquibancadas, vestiários, camarote |
| Dojo / Ginásio | 30×20 × 12 | telhado pesado de duas águas, janelas altas, piso de madeira | P4 + P2 | piso de tábuas, racks, alvos |
| Torre Arcana | Ø13 × 40 a 46 | cilindro com anéis de raios variados, cúpula de observatório, cristais flutuantes, varandas | P3 | salão de runas, laboratório, observatório, escada espiral |
| Enfermaria | 24×18 (cruz) × 12 | claro, pátio com jardim de ervas, torre pequena | P1 clara | alas de leitos, farmácia, sala de cirurgia |
| Ferraria e Oficinas | 26×16 × 11 | chaminés altas, abertura frontal, telhado assimétrico | P8 | forja, bancadas, depósito |
| Estábulos e Poleiro | 34×14 × 10 + torre 7×7 × 22 | galpão longo e torre de pouso para montarias aladas | P2 + P8 | baias, feno, selaria, plataforma de pouso |
| Estufa e Jardim de Ervas | 22×12 × 9 | abóbada de vidro nervurada, canteiros, fonte | P7 | canteiros, bancadas de alquimia, fonte |
| Salão de Missões | 28×18 × 12 | fachada com grande quadro e brasão, torre de sino pequena | P5 | quadro de missões, balcão, sala de reuniões, troféus |
| Capela dos Heróis | 20×12 × 18 | nave alta com vitrais, sineira | P1 | nave, altar, galeria de estátuas |
| Casa do Diretor | 16×12 × 10 | casa de pedra com torreão e jardim próprio | P1 + P2 | escritório, biblioteca privada, quarto |
| Praça Central + Monumento | Ø36 a 44 | anel pavimentado com brasão, fonte ou monumento (14 de altura), bancos, postes | P1 + P7 | n/a |
| Muralha e torres de guarda | espessura 3 a 4, altura 10 a 12, torres a cada 14 a 20 | ameias, torres com telhado cônico, escadas de acesso | P6 | caminho de ronda |
| Pontes e galerias | largura 5 a 7 | arcos sob o tabuleiro, guarda-corpo, lanternas; galerias com colunas e telhado | P1 ou P6 | n/a |

---

## 6. Diagrama de referência

Ajuste ao terreno real; o que importa são as relações, não as posições exatas.

```
N ↑
  Torre Arcana ········· Edifício Principal ········· Biblioteca
  (ponto alto)           + Torre do Relógio           │
                                 ║                    Pavilhões de Aulas
  Dormitórios ─ Jardim Sakura ─ Praça Central ─ Refeitório
       │                      (monumento)             │
  Enfermaria ·················· ║ ···················· Arena / Dojo
                                 ║
  Estufa / Lago ──── Portão da Academia ──── Ferraria · Estábulos
S ↓
```

---

## 7. Ordem de construção (Runs do campus)

Cada Run termina em algo testável em jogo.

1. **Run 1 — Terraplanagem, eixos e praça**: pads principais, avenida, praça com piso e monumento provisório.
2. **Run 2 — Edifício principal e torre do relógio**: define a linguagem visual de todo o campus.
3. **Run 3 — Portão e muralha**: perímetro, torres de guarda, ponte de acesso.
4. **Run 4 — Pavilhões, biblioteca e refeitório**: núcleo acadêmico, com galerias de ligação.
5. **Run 5 — Dormitórios**: variações de planta e de cor por casa.
6. **Run 6 — Arena, dojo e enfermaria**: zona de treino.
7. **Run 7 — Torre arcana, ferraria, estábulos e estufa**: marcos secundários e serviço.
8. **Run 8 — Pontes, galerias, jardins, lago e vegetação**: costura entre os prédios.
9. **Run 9 — Ambiente e QA**: iluminação noturna, props, envelhecimento, varredura de gates em todos os prédios.
