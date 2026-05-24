package model;

/**
 * Classe que representa o jogador do jogo.
 *
 * Armazena informações como nome do jogador,
 * Pokémon atual e quantidade de poções.
 *
 * Objetivo:
 * - Gerenciar estado do jogador
 * - Controlar uso de itens (poções)
 *
 * Conceitos aplicados:
 * - POO
 * - Encapsulamento
 */
public class Player {

    private String nome;
    private Pokemon pokemon;
    private int potions;

    public Player(Pokemon pokemon) {
        this("Jogador", pokemon);
    }

    public Player(String nome, Pokemon pokemon) {
        setNome(nome);
        setPokemon(pokemon);
        this.potions = 5;
    }

    public String getNome() {
        return nome;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public int getPotions() {
        return potions;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            this.nome = "Jogador";
            return;
        }

        this.nome = nome.trim();
    }

    public void setPokemon(Pokemon pokemon) {
        if (pokemon == null) {
            throw new IllegalArgumentException("O jogador precisa ter um Pokémon.");
        }

        this.pokemon = pokemon;
    }

    public void setPotions(int potions) {
        this.potions = Math.max(0, potions);
    }

    public boolean usarPocao() {
        if (potions <= 0) {
            System.out.println("Você não possui poções.");
            return false;
        }

        if (pokemon.getHp() >= pokemon.getMaxHp()) {
            System.out.println("O HP do Pokémon já está cheio.");
            return false;
        }

        pokemon.curar(50);
        potions--;

        System.out.println("Poção usada! O Pokémon recuperou até 50 de HP.");
        System.out.println("Poções restantes: " + potions);

        return true;
    }
}