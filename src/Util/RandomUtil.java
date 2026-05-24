package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe utilitária para geração de números aleatórios.
 *
 * Usada para sorteios no jogo, como escolha de inimigos
 * e Pokémons iniciais.
 *
 * Objetivo:
 * - Centralizar lógica de aleatoriedade
 * - Garantir reutilização segura
 *
 * Conceitos aplicados:
 * - Utilitário estático
 * - Encapsulamento de lógica
 */
public class RandomUtil {

    private static final Random RANDOM = new Random();

    private RandomUtil() {
    }

    public static int sortearNumero(int minimo, int maximo) {
        if (minimo > maximo) {
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o máximo.");
        }

        return RANDOM.nextInt((maximo - minimo) + 1) + minimo;
    }

    public static List<Integer> sortearNumerosDistintos(int quantidade, int minimo, int maximo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (minimo > maximo) {
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o máximo.");
        }

        int totalDisponivel = (maximo - minimo) + 1;

        if (quantidade > totalDisponivel) {
            throw new IllegalArgumentException("Quantidade maior que o intervalo disponível.");
        }

        List<Integer> numeros = new ArrayList<>();

        for (int i = minimo; i <= maximo; i++) {
            numeros.add(i);
        }

        Collections.shuffle(numeros);

        return new ArrayList<>(numeros.subList(0, quantidade));
    }
}