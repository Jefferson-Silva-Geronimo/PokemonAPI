package model;

/**
 * Classe que representa uma habilidade de um Pokémon.
 *
 * Armazena o nome da habilidade e se ela é oculta.
 *
 * Objetivo:
 * - Representar habilidades individuais de Pokémon
 * - Exibir informações durante o jogo
 *
 * Conceitos aplicados:
 * - Encapsulamento
 * - Modelagem de domínio
 */
public class Ability {

    private String nome;
    private boolean escondida;

    public Ability(String nome, boolean escondida) {
        setNome(nome);
        this.escondida = escondida;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEscondida() {
        return escondida;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            this.nome = "Habilidade Desconhecida";
            return;
        }

        this.nome = nome.trim();
    }

    public String info() {
        return escondida ? nome + " (oculta)" : nome;
    }
}