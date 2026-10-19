package com.example.diagnostico_api.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DataUtils {

    public static final ZoneId FUSO_PADRAO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private DataUtils() {
    }

    /** Data/hora atual no fuso padrão do sistema. */
    public static LocalDateTime agora() {
        return LocalDateTime.now(FUSO_PADRAO);
    }

    public static String formatar(LocalDateTime data) {
        return data == null ? null : data.format(FORMATO_BR);
    }

    /**
     * Indica se a última coleta é mais antiga que o limite informado.
     * Útil para identificar computadores que pararam de enviar dados.
     */
    public static boolean estaDesatualizada(LocalDateTime dataColeta, Duration limite) {
        if (dataColeta == null) {
            return true;
        }
        return Duration.between(dataColeta, agora()).compareTo(limite) > 0;
    }
}
