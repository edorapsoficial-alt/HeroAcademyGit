# Exemplo: dormitório enxaimel em L (do plano ao código)

Mostra como um plano vira passes de código com o kit. Os nomes de método são os do contrato em `references/kit-e-formas.md`; ajuste ao kit real do projeto. Código ilustrativo, não compilado.

## Plano resumido

- **Conceito**: dormitório aconchegante de enxaimel. **Marca registrada**: torreão de escada com telhado cônico, mais alto que os telhados.
- **Planta em L**: ala principal 26×11 (x 0..25, z 0..10, 3 andares) e ala lateral 11×12 (x 15..25, z 11..22, 2 andares). Piso a piso 5.
- **Telhados**: duas águas cruzadas, beiral de 1, lucarna na água da frente; torreão de escada redondo (raio 3) no canto interno do L; chaminé de pedra na empena oeste.
- **Paleta**: P2 (reboco + madeira escura + telhado de spruce). Acentos: estandarte da casa, floreiras sob as janelas, barris na entrada.
- **Interior**: 12 quartos de 2 camas, sala comum com lareira no térreo, banho.

## Código (esqueleto)

```java
public final class DormHouse implements Structure {

    public record Params(int lenX, int depthZ, int wingW, int wingZ, int floorsMain, int floorsWing) {}

    private static final int FH = 5;   // piso a piso

    @Override
    public Blueprint build(Params p, Palette pal, long seed) {
        Blueprint bp = new Blueprint(seed);
        Box main  = Box.of(0, 0, 0, p.lenX() - 1, p.floorsMain() * FH - 1, p.depthZ() - 1);
        Box wing  = Box.of(p.lenX() - p.wingW(), 0, p.depthZ(),
                           p.lenX() - 1, p.floorsWing() * FH - 1, p.depthZ() + p.wingZ() - 1);
        Box front = Box.of(0, 0, 0, p.lenX() - p.wingW() - 1, main.y1(), p.depthZ() - 1); // trecho livre da frente

        // Passe 0 — fundação e embasamento (desce até y = -3; o BuildJob corta no solo real)
        for (Box b : List.of(main, wing))
            Shapes.box(bp, pal, Role.BASE, b.x0(), -3, b.z0(), b.x1(), 0, b.z1());

        // Passe 1 — volumes: paredes com enxaimel (pilares, vigas por andar, diagonais)
        for (Box b : List.of(main, wing)) {
            Shapes.walls(bp, pal, Role.WALL, b.x0(), b.z0(), b.x1(), b.z1(), 1, b.y1());
            Shapes.timberFrame(bp, pal, b, FH);
        }

        // Passe 2 — relevo: o 2º andar avança 1 bloco na frente (jetty) e cintas entre andares
        Shapes.jetty(bp, pal, front, Direction.SOUTH, FH, 1);
        Shapes.beltCourse(bp, pal, main, FH);

        // Passe 3 — aberturas: ritmo de 4 blocos, eixo de simetria no meio do trecho livre da frente
        int zFront = p.depthZ() - 1;
        int doorX  = (front.x1() + 1) / 2;                       // cai no ritmo 3, 7, 11
        for (int f = 0; f < p.floorsMain(); f++) {
            for (int x = 3; x <= front.x1() - 2; x += 4) {
                if (f == 0 && x == doorX)
                    Shapes.portal(bp, pal, Direction.SOUTH, x, 1, zFront + 1, 3, 4);
                else
                    Shapes.window(bp, pal, Direction.SOUTH, x, f * FH + 2, zFront + (f > 0 ? 1 : 0),
                                  1, 2, WindowStyle.SHUTTERED);
            }
        }

        // Passe 4 — cobertura: águas cruzadas por mapa de alturas, beiral 1, lucarna
        RoofField roof = new RoofField();
        roof.gableAlongX(main, 1);                               // ala principal
        roof.gableAlongZ(wing, 1);                               // ala lateral; o vale sai da união por máximo
        roof.dormer(main, Direction.SOUTH, doorX, 3);            // lucarna sobre a porta
        roof.emit(bp, pal, Role.ROOF, Role.WALL);                // escadas + lajes de cumeeira + empenas
        Shapes.chimney(bp, pal, 2, 4, 1, main.y1() + 6);         // chaminé de pedra junto à empena oeste

        // Marca registrada — torreão de escada no canto interno do L, mais alto que os telhados
        int tx = p.lenX() - p.wingW() - 1, tz = p.depthZ();
        Shapes.cylinderWalls(bp, pal, Role.WALL, tx, tz, 3, 1, main.y1() + 4);
        Shapes.coneRoof(bp, pal, Role.ROOF, tx, tz, main.y1() + 5, 4, 9, 1.8);
        Shapes.stairsSpiral(bp, pal, tx, tz, 0, main.y1() + 3);
        bp.tag(Tag.LANDMARK, "torreao", Box.of(tx - 3, 0, tz - 3, tx + 3, main.y1() + 14, tz + 3));

        // Passe 5 — detalhes externos: estandarte da casa ao lado da porta, floreiras (Furniture.flowerBox)
        // sob as janelas, barris e lanterna na entrada; cada item marcado com Tag.ACCENT

        // Passe 6 — interior: pisos, corredor central de 3 de largura, quartos 5×5 com 2 camas, sala comum
        for (int f = 0; f < p.floorsMain(); f++) {
            Shapes.floor(bp, pal, Role.FLOOR, main.x0() + 1, main.z0() + 1, main.x1() - 1, main.z1() - 1, f * FH);
            Rooms.bedroomsAlongCorridor(bp, pal, main, f, FH);   // helper do projeto: divisórias, portas, camas, baús, mesa; tag ROOM
        }
        Furniture.fireplace(bp, pal, 2, 1, 4, Direction.SOUTH);  // sala comum no térreo; a chaminé externa sobe daqui
        bp.tag(Tag.ENTRANCE, "porta-principal", Box.of(doorX - 1, 0, zFront + 1, doorX + 1, 3, zFront + 1));

        // Passe 7 — luz: acrescenta lanternas e velas onde faltar (até 7 blocos dentro, 10 fora)
        Lighting.ensureCoverage(bp, pal, 7, 10);

        // Passe 9 — envelhecimento leve (prédio habitado e cuidado)
        Weathering.apply(bp, pal, 0.10);

        // Passes 10 e 11: o BuildJob roda o fix-up de estados e o Validator antes de aplicar
        return bp;
    }
}
```

## O que observar neste exemplo

- **A silhueta nasce dos volumes (passe 1) e do telhado (passe 4)**, não dos detalhes. Sem esses dois, nenhum acabamento salva o prédio.
- **Telhados cruzados por mapa de alturas** (`RoofField`): sem escadas “dentro” do sótão da outra ala e com vales corretos depois do fix-up.
- **A marca registrada vira tag `LANDMARK`** e entra no gate G10; os acentos de tema entram com `Tag.ACCENT`.
- **O interior usa tags `ROOM` e `ENTRANCE`**: é o que permite ao validador provar a alcançabilidade (G7).
- **Nada aqui chama o `Level`**. A aplicação é do `BuildJob`, que também roda o fix-up das escadas e das cercas.
- Cada passe é uma função ou um bloco isolado; ajustar o telhado não mexe nas janelas.

## Como testar (Run 1)

1. `/campus dryrun dorm 1234`: o relatório deve mostrar G1 a G3 verdes (3 ou mais alturas por lado nas elevações, planta em L, 2 níveis de profundidade na frente).
2. `/campus build dorm 0 1234` num terreno de teste e, depois, com rotação 90 e 180: telhado, torreão e jetty devem girar juntos.
3. Voe ao redor: a 100 blocos a silhueta tem torreão, duas cumeeiras e a lucarna; a 30, a frente tem ritmo de janelas com venezianas e o 2º andar avançado; a 5, floreiras, barris e a cinta entre andares.
