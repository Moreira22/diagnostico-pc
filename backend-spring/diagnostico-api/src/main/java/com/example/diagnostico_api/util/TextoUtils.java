package com.example.diagnostico_api.util;

public final class TextoUtils {

    private TextoUtils() {
    }

    public static boolean isNuloOuVazio(String valor) {
        return valor == null || valor.isBlank();
    }

    /** Remove espaços e converte para minúsculas. Retorna null se a entrada for nula. */
    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /** Mantém apenas os dígitos da string. */
    public static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    /**
     * Formata telefone brasileiro: (27) 99999-9999 ou (27) 3333-4444.
     * Se não tiver 10 ou 11 dígitos, devolve só os dígitos.
     */
    public static String formatarTelefone(String telefone) {
        String d = somenteDigitos(telefone);
        if (d == null) {
            return null;
        }
        if (d.length() == 11) {
            return String.format("(%s) %s-%s", d.substring(0, 2), d.substring(2, 7), d.substring(7));
        }
        if (d.length() == 10) {
            return String.format("(%s) %s-%s", d.substring(0, 2), d.substring(2, 6), d.substring(6));
        }
        return d;
    }

    public static String truncar(String texto, int limite) {
        if (texto == null || texto.length() <= limite) {
            return texto;
        }
        return texto.substring(0, limite);
    }
}
