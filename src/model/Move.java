package model;

import exception.InvalidMoveException;

import java.util.Random;

/**
 * Classe que representa um movimento (ataque) de um Pokémon.
 *
 * Contém informações como nome, tipo, poder, precisão
 * e quantidade de usos (PP).
 *
 * Objetivo:
 * - Representar ataques utilizados em batalha
 * - Controlar uso de PP e precisão do golpe
 *
 * Conceitos aplicados:
 * - POO
 * - Encapsulamento
 */
public class Move {

    private String nome;
    private int ppAtual;
    private int ppMax;
    private String tipo;
    private int precisao;
    private int poder;

    public Move(String nome, int ppMax, String tipo, int precisao, int poder) {
        setNome(nome);
        setPpMax(ppMax);
        setPpAtual(this.ppMax);
        setTipo(tipo);
        setPrecisao(precisao);
        setPoder(poder);
    }

    public String getNome() {
        return nome;
    }

    public int getPpAtual() {
        return ppAtual;
    }

    public int getPpMax() {
        return ppMax;
    }

    public String getTipo() {
        return tipo;
    }

    public int getPrecisao() {
        return precisao;
    }

    public int getPoder() {
        return poder;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            this.nome = "Movimento Desconhecido";
            return;
        }

        this.nome = nome.trim();
    }

    public void setPpAtual(int ppAtual) {
        if (ppAtual < 0) {
            this.ppAtual = 0;
            return;
        }

        this.ppAtual = Math.min(ppAtual, ppMax);
    }

    public void setPpMax(int ppMax) {
        this.ppMax = Math.max(1, ppMax);

        if (ppAtual > this.ppMax) {
            ppAtual = this.ppMax;
        }
    }

    public void setTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            this.tipo = "normal";
            return;
        }

        this.tipo = tipo.trim().toLowerCase();
    }

    public void setPrecisao(int precisao) {
        if (precisao < 1) {
            this.precisao = 1;
            return;
        }

        this.precisao = Math.min(precisao, 100);
    }

    public void setPoder(int poder) {
        this.poder = Math.max(0, poder);
    }

    public int usar() throws InvalidMoveException {
        validarUso();

        ppAtual--;

        int chance = new Random().nextInt(100) + 1;

        if (chance > precisao) {
            return 0;
        }

        return poder;
    }

    public boolean temPp() {
        return ppAtual > 0;
    }

    public Move copiaNova() {
        return new Move(nome, ppMax, tipo, precisao, poder);
    }

    public Move copiaComPpAtual() {
        Move copia = new Move(nome, ppMax, tipo, precisao, poder);
        copia.setPpAtual(ppAtual);
        return copia;
    }

    public String info() {
        return nome
                + " | PP: " + ppAtual + "/" + ppMax
                + " | Tipo: " + tipo
                + " | Precisão: " + precisao + "%"
                + " | Poder: " + poder;
    }

    private void validarUso() throws InvalidMoveException {
        if (nome == null || nome.isBlank()) {
            throw new InvalidMoveException("Movimento inválido: nome não definido.");
        }

        if (ppMax <= 0) {
            throw new InvalidMoveException("Movimento inválido: PP máximo incorreto.");
        }

        if (ppAtual <= 0) {
            throw new InvalidMoveException("O movimento " + nome + " está sem PP.");
        }

        if (precisao <= 0) {
            throw new InvalidMoveException("Movimento inválido: precisão incorreta.");
        }
    }

    @Override
    public String toString() {
        return info();
    }
}