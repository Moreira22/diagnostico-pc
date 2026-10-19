package com.example.diagnostico_api.util;

import java.security.SecureRandom;

public final class SenhaUtils {

    private static final String MAIUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijkmnopqrstuvwxyz";
    private static final String NUMEROS = "23456789";
    private static final String ESPECIAIS = "@#$%&*!";
    private static final String TODOS = MAIUSCULAS + MINUSCULAS + NUMEROS + ESPECIAIS;

    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtils() {
    }

    /**
     * Gera uma senha temporária (ex.: para o primeiro acesso do técnico).
     * Garante ao menos uma maiúscula, uma minúscula, um número e um especial.
     * Caracteres ambíguos (0, O, 1, l, I) ficam de fora.
     */
    public static String gerarSenhaTemporaria(int tamanho) {
        if (tamanho < 8) {
            throw new IllegalArgumentException("O tamanho mínimo da senha é 8");
        }

        char[] senha = new char[tamanho];
        senha[0] = sortear(MAIUSCULAS);
        senha[1] = sortear(MINUSCULAS);
        senha[2] = sortear(NUMEROS);
        senha[3] = sortear(ESPECIAIS);
        for (int i = 4; i < tamanho; i++) {
            senha[i] = sortear(TODOS);
        }

        // embaralha (Fisher-Yates)
        for (int i = senha.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = senha[i];
            senha[i] = senha[j];
            senha[j] = tmp;
        }
        return new String(senha);
    }

    /** Mínimo de 8 caracteres, com maiúscula, minúscula e número. */
    public static boolean senhaForte(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }
        boolean maiuscula = senha.chars().anyMatch(Character::isUpperCase);
        boolean minuscula = senha.chars().anyMatch(Character::isLowerCase);
        boolean numero = senha.chars().anyMatch(Character::isDigit);
        return maiuscula && minuscula && numero;
    }

    private static char sortear(String conjunto) {
        return conjunto.charAt(RANDOM.nextInt(conjunto.length()));
    }
}
