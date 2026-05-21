package Util;

import java.util.Scanner;

public class InputUtil {

    private InputUtil() {
    }

    public static int lerInt(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);

                String entrada = sc.nextLine();

                if (entrada == null || entrada.trim().isEmpty()) {
                    System.out.println("Entrada vazia. Digite um número.");
                    continue;
                }

                return Integer.parseInt(entrada.trim());

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números.");
            }
        }
    }

    public static String lerTexto(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);

            String entrada = sc.nextLine();

            if (entrada != null && !entrada.trim().isEmpty()) {
                return entrada.trim();
            }

            System.out.println("Entrada inválida. O texto não pode estar vazio.");
        }
    }
}