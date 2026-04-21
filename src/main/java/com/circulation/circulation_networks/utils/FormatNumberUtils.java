package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.EnergyAmount;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("unused")
public final class FormatNumberUtils {
    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> {
        DecimalFormat format = new DecimalFormat("#,###.##");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format;
    });
    private static final ThreadLocal<Int2ObjectMap<NumberFormat>> NUMBER_FORMATS = ThreadLocal.withInitial(Int2ObjectOpenHashMap::new);

    private FormatNumberUtils() {
    }

    public static String formatFloat(float value, int decimalFraction) {
        return formatDouble(value, decimalFraction);
    }

    public static String formatDouble(double value, int decimalFraction) {
        int normalizedFraction = Math.max(0, decimalFraction);
        Int2ObjectMap<NumberFormat> formats = NUMBER_FORMATS.get();
        NumberFormat format = formats.get(normalizedFraction);
        if (format == null) {
            format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(normalizedFraction);
            formats.put(normalizedFraction, format);
        }
        return format.format(value);
    }

    public static String formatDecimal(double value) {
        return DECIMAL_FORMAT.get().format(value);
    }

    public static String formatNumber(long value) {
        return formatNumber(value, 2);
    }

    public static String formatItemCount(long value) {
        return formatNumber(value, 0);
    }

    public static String formatNumber(EnergyAmount amount) {
        if (amount.fitsLong()) {
            return formatNumber(amount.asLongExact());
        }
        BigInteger abs = amount.asBigInteger().abs();
        String digits = abs.toString();
        int exponent = digits.length() - 1;
        float mantissa = Integer.parseInt(digits.substring(0, 3)) / 100f;
        return (amount.isNegative() ? "-" : "") + mantissa + "E" + exponent;
    }

    public static String formatNumber(long value, int decimalFraction) {
        if (value < 0) {
            return "-" + formatNumber(value == Long.MIN_VALUE ? Long.MAX_VALUE : -value, decimalFraction);
        }
        if (value < 1_000L) {
            return String.valueOf(value);
        } else if (value < 1_000_000L) {
            return formatFloat((float) value / 1_000L, decimalFraction) + "K";
        } else if (value < 1_000_000_000L) {
            return formatDouble((double) value / 1_000_000L, decimalFraction) + "M";
        } else if (value < 1_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000L, decimalFraction) + "G";
        } else if (value < 1_000_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000_000L, decimalFraction) + "T";
        } else if (value < 1_000_000_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000_000_000L, decimalFraction) + "P";
        } else {
            return formatDouble((double) value / 1_000_000_000_000_000_000L, decimalFraction) + "E";
        }
    }

    public static String formatPercent(double num1, double num2) {
        if (num2 == 0) {
            return "0%";
        }
        return formatDouble((num1 / num2) * 100D, 2) + "%";
    }

}
