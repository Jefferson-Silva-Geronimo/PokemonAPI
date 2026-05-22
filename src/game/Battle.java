package game;

import Util.InputUtil;
import exception.InvalidMoveException;
import model.Move;
import model.Player;
import model.Pokemon;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Battle {

    private final Player player;
    private final Pokemon enemy;
    private final Scanner sc;
    private final Random random;
    private boolean fugiu;

    public Battle(Player player, Pokemon enemy) {
        this.player = player;
        this.enemy = enemy;
        this.sc = new Scanner(System.in);
        this.random = new Random();
        this.fugiu = false;
    }

    public void start() {
        System.out.println("\n==============================");
        System.out.println("Uma batalha começou!");
        System.out.println("Inimigo: " + enemy.getNome());
        System.out.println("==============================");

        while (player.getPokemon().vivo() && enemy.vivo() && !fugiu) {
            status();

            boolean acaoValida = executarTurnoJogador();

            if (!acaoValida) {
                continue;
            }

            if (enemy.vivo() && !fugiu) {
                ataqueInimigo();
            }

            status();
        }

        finalizarBatalha();
    }

    private boolean executarTurnoJogador() {
        System.out.println("\nEscolha uma ação:");
        System.out.println("1 - Usar movimento");
        System.out.println("2 - Usar poção");
        System.out.println("3 - Fugir");

        int opcao = InputUtil.lerInt(sc, "Ação: ");

        return switch (opcao) {
            case 1 -> atacar();
            case 2 -> player.usarPocao();
            case 3 -> {
                fugir();
                yield true;
            }
            default -> {
                System.out.println("Opção inválida. Escolha 1, 2 ou 3.");
                yield false;
            }
        };
    }

    private boolean atacar() {
        Pokemon pokemonJogador = player.getPokemon();
        List<Move> movimentos = pokemonJogador.getMoves();

        if (movimentos == null || movimentos.isEmpty()) {
            System.out.println("Seu Pokémon não possui movimentos disponíveis.");
            return false;
        }

        System.out.println("\nEscolha um movimento:");

        for (int i = 0; i < movimentos.size(); i++) {
            System.out.println((i + 1) + " - " + movimentos.get(i).info());
        }

        int escolha = InputUtil.lerInt(sc, "Movimento: ");

        if (escolha < 1 || escolha > movimentos.size()) {
            System.out.println("Movimento inválido.");
            return false;
        }

        Move movimento = movimentos.get(escolha - 1);

        try {
            int danoBase = movimento.usar();

            if (danoBase == 0) {
                System.out.println(pokemonJogador.getNome() + " usou " + movimento.getNome() + ", mas errou!");
                return true;
            }

            int danoFinal = calcularDano(pokemonJogador, danoBase);
            enemy.receberDano(danoFinal);

            System.out.println(pokemonJogador.getNome() + " usou " + movimento.getNome() + "!");
            System.out.println("Dano causado: " + danoFinal);

            return true;

        } catch (InvalidMoveException e) {
            System.out.println("Não foi possível usar o movimento: " + e.getMessage());
            return false;
        }
    }

    private void ataqueInimigo() {
        List<Move> movimentos = enemy.getMoves();

        if (movimentos == null || movimentos.isEmpty()) {
            ataqueInimigoBasico();
            return;
        }

        Move movimento = escolherMovimentoInimigo(movimentos);

        if (movimento == null) {
            ataqueInimigoBasico();
            return;
        }

        try {
            int danoBase = movimento.usar();

            if (danoBase == 0) {
                System.out.println(enemy.getNome() + " usou " + movimento.getNome() + ", mas errou!");
                return;
            }

            int danoFinal = calcularDano(enemy, danoBase);
            player.getPokemon().receberDano(danoFinal);

            System.out.println(enemy.getNome() + " usou " + movimento.getNome() + "!");
            System.out.println("Dano recebido: " + danoFinal);

        } catch (InvalidMoveException e) {
            ataqueInimigoBasico();
        }
    }

    private Move escolherMovimentoInimigo(List<Move> movimentos) {
        List<Move> movimentosComPp = movimentos.stream()
                .filter(Move::temPp)
                .toList();

        if (movimentosComPp.isEmpty()) {
            return null;
        }

        return movimentosComPp.get(random.nextInt(movimentosComPp.size()));
    }

    private void ataqueInimigoBasico() {
        int dano = Math.max(1, enemy.getAttack());

        player.getPokemon().receberDano(dano);

        System.out.println(enemy.getNome() + " atacou com força básica!");
        System.out.println("Dano recebido: " + dano);
    }

    private int calcularDano(Pokemon atacante, int danoBase) {
        // int dano = danoBase + atacante.getAttack();
        int dano = danoBase;
        System.out.println("Tipo do Pokemon Atacante: " + atacante.getTipo());
        return Math.max(1, dano);
    }

    private void fugir() {
        fugiu = true;
        System.out.println("Você fugiu da batalha!");
    }

    private void finalizarBatalha() {
        if (fugiu) {
            return;
        }

        if (player.getPokemon().vivo() && !enemy.vivo()) {
            System.out.println("\nVitória!");

            int xpGanho = enemy.getLevel() * 50;
            player.getPokemon().ganharXp(xpGanho);
            return;
        }

        if (!player.getPokemon().vivo()) {
            System.out.println("\nSeu Pokémon foi derrotado!");
        }
    }

    private void status() {
        System.out.println("\n========== STATUS ==========");
        System.out.println("Seu Pokémon: " + player.getPokemon().getNome());
        System.out.println("HP: " + player.getPokemon().getHp() + "/" + player.getPokemon().getMaxHp());
        System.out.println("Nível: " + player.getPokemon().getLevel());
        System.out.println("XP: " + player.getPokemon().getXp() + "/" + player.getPokemon().xpNecessarioParaProximoNivel());
        System.out.println("Poções: " + player.getPotions());
        System.out.println("Tipos: " + String.join(", ", player.getPokemon().getTipo()));

        System.out.println("----------------------------");

        System.out.println("Inimigo: " + enemy.getNome());
        System.out.println("HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
        System.out.println("Nível: " + enemy.getLevel());
        System.out.println("============================");
    }
}