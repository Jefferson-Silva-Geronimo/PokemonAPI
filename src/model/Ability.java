package model;

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