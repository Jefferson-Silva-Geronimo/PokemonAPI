package model;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {

    private int apiId;
    private String nome;
    private int hp;
    private int maxHp;
    private int attack;
    private int level;
    private int xp;
    private boolean basico;
    private boolean evoluiu;

    private String evolutionName;
    private int evolutionMinLevel;
    private int evolutionHp;
    private int evolutionAttack;

    private final List<Move> moves;
    private final List<Ability> abilities;
    private final List<LearnedMove> learnedMoves;

    public Pokemon(int apiId, String nome, int hp, int attack) {
        setApiId(apiId);
        setNome(nome);
        setMaxHp(hp);
        setHp(hp);
        setAttack(attack);
        this.level = 1;
        this.xp = 0;
        this.basico = true;
        this.evoluiu = false;
        this.moves = new ArrayList<>();
        this.abilities = new ArrayList<>();
        this.learnedMoves = new ArrayList<>();
    }

    public Pokemon(String nome, int hp, int attack) {
        this(0, nome, hp, attack);
    }

    public int getApiId() {
        return apiId;
    }

    public String getNome() {
        return nome;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public boolean isBasico() {
        return basico;
    }

    public boolean isEvoluiu() {
        return evoluiu;
    }

    public String getEvolutionName() {
        return evolutionName;
    }

    public int getEvolutionMinLevel() {
        return evolutionMinLevel;
    }

    public int getEvolutionHp() {
        return evolutionHp;
    }

    public int getEvolutionAttack() {
        return evolutionAttack;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public List<LearnedMove> getLearnedMoves() {
        return learnedMoves;
    }

    public void setApiId(int apiId) {
        this.apiId = Math.max(0, apiId);
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            this.nome = "Pokémon Desconhecido";
            return;
        }

        this.nome = nome.trim();
    }

    public void setHp(int hp) {
        if (hp < 0) {
            this.hp = 0;
            return;
        }

        this.hp = Math.min(hp, maxHp);
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);

        if (hp > this.maxHp) {
            hp = this.maxHp;
        }
    }

    public void setAttack(int attack) {
        this.attack = Math.max(1, attack);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public void setBasico(boolean basico) {
        this.basico = basico;
    }

    public void setEvoluiu(boolean evoluiu) {
        this.evoluiu = evoluiu;
    }

    public void configurarEvolucao(String evolutionName, int evolutionMinLevel, int evolutionHp, int evolutionAttack) {
        this.evolutionName = evolutionName;
        this.evolutionMinLevel = Math.max(1, evolutionMinLevel);
        this.evolutionHp = Math.max(1, evolutionHp);
        this.evolutionAttack = Math.max(1, evolutionAttack);
    }

    public boolean temEvolucao() {
        return evolutionName != null && !evolutionName.isBlank();
    }

    public void adicionarAbility(Ability ability) {
        if (ability != null) {
            abilities.add(ability);
        }
    }

    public void adicionarLearnedMove(LearnedMove learnedMove) {
        if (learnedMove != null) {
            learnedMoves.add(learnedMove);
        }
    }

    public void adicionarMovimentoAtual(Move move) {
        if (move != null) {
            moves.add(move);
        }
    }

    public void limparMovimentosAtuais() {
        moves.clear();
    }

    public void receberDano(int dano) {
        hp -= Math.max(0, dano);

        if (hp < 0) {
            hp = 0;
        }
    }

    public void curar(int valor) {
        if (valor <= 0) {
            return;
        }

        hp += valor;

        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public boolean vivo() {
        return hp > 0;
    }

    public void ganharXp(int valor) {
        if (valor <= 0) {
            return;
        }

        xp += valor;
        System.out.println(nome + " ganhou " + valor + " XP!");

        while (xp >= xpNecessarioParaProximoNivel()) {
            xp -= xpNecessarioParaProximoNivel();
            subirLevel();
        }
    }

    private void subirLevel() {
        level++;
        maxHp += 10;
        attack += 5;
        hp = maxHp;

        System.out.println(nome + " subiu para o nível " + level + "!");

        aprenderGolpesPorNivel();
        evoluirSeNecessario();
    }

    public void prepararParaNivelInicial(int nivelInicial) {
        setLevel(nivelInicial);
        aprenderGolpesPorNivel();
        evoluirSeNecessario();
        hp = maxHp;
    }

    private void aprenderGolpesPorNivel() {
        for (LearnedMove learnedMove : learnedMoves) {
            if (learnedMove.getLevelLearnedAt() <= level) {
                adicionarMovimentoSeNaoExistir(learnedMove.getMove().copiaNova());
            }
        }

        if (moves.isEmpty()) {
            adicionarMovimentoAtual(new Move("Investida", 20, "normal", 90, 10));
        }
    }

    private void adicionarMovimentoSeNaoExistir(Move novoMovimento) {
        boolean jaExiste = moves.stream()
                .anyMatch(move -> move.getNome().equalsIgnoreCase(novoMovimento.getNome()));

        if (jaExiste) {
            return;
        }

        if (moves.size() >= 4) {
            Move removido = moves.remove(0);
            System.out.println(nome + " esqueceu " + removido.getNome() + ".");
        }

        moves.add(novoMovimento);
        System.out.println(nome + " aprendeu " + novoMovimento.getNome() + "!");
    }

    private void evoluirSeNecessario() {
        if (evoluiu || !temEvolucao()) {
            return;
        }

        if (level >= evolutionMinLevel) {
            nome = evolutionName;
            maxHp = evolutionHp + ((level - 1) * 10);
            attack = evolutionAttack + ((level - 1) * 5);
            hp = maxHp;
            evoluiu = true;

            System.out.println("Evoluiu! Agora seu Pokémon é " + nome + "!");
        }
    }

    public int xpNecessarioParaProximoNivel() {
        return level * 50;
    }

    public void mostrarStatus() {
        System.out.println("----------------------------");
        System.out.println("Pokémon: " + nome);
        System.out.println("HP: " + hp + "/" + maxHp);
        System.out.println("Ataque: " + attack);
        System.out.println("Nível: " + level);
        System.out.println("XP: " + xp + "/" + xpNecessarioParaProximoNivel());

        System.out.println("Habilidades:");
        if (abilities.isEmpty()) {
            System.out.println("- Nenhuma habilidade cadastrada.");
        } else {
            for (Ability ability : abilities) {
                System.out.println("- " + ability.info());
            }
        }

        System.out.println("Movimentos:");
        if (moves.isEmpty()) {
            System.out.println("- Nenhum movimento disponível.");
        } else {
            for (int i = 0; i < moves.size(); i++) {
                System.out.println((i + 1) + " - " + moves.get(i).info());
            }
        }

        if (temEvolucao() && !evoluiu) {
            System.out.println("Evolução: " + evolutionName + " no nível " + evolutionMinLevel);
        }

        System.out.println("----------------------------");
    }
}