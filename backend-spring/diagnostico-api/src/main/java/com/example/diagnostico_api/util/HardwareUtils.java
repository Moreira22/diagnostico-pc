package com.example.diagnostico_api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cálculos usados nas métricas de hardware (memória, SSD, CPU).
 */
public final class HardwareUtils {

    private static final double BYTES_POR_GB = 1024.0 * 1024.0 * 1024.0;

    private HardwareUtils() {
    }

    public static double bytesParaGb(long bytes) {
        return arredondar(bytes / BYTES_POR_GB, 2);
    }

    public static double arredondar(double valor, int casas) {
        return BigDecimal.valueOf(valor).setScale(casas, RoundingMode.HALF_UP).doubleValue();
    }

    /** Valor em uso = total - livre. Retorna null se algum dos valores for nulo. */
    public static Double calcularUso(Double total, Double livre) {
        if (total == null || livre == null) {
            return null;
        }
        return arredondar(Math.max(total - livre, 0), 2);
    }

    /** Percentual de uso (0 a 100). Retorna 0 se o total for nulo ou zero. */
    public static double percentualUso(Double total, Double livre) {
        if (total == null || livre == null || total <= 0) {
            return 0;
        }
        double usado = Math.max(total - livre, 0);
        return arredondar(usado / total * 100, 2);
    }

    /** Garante que o percentual fique entre 0 e 100. */
    public static double limitarPercentual(double percentual) {
        return arredondar(Math.min(Math.max(percentual, 0), 100), 2);
    }
}
