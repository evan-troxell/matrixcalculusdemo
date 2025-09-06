package com.troxell;

import java.text.DecimalFormat;

public final class MatContext {

    /**
     * <code>DecimalFormat</code>: The formatter used to ensure all printed numbers
     * are formatted in the same manner.
     */
    public static final DecimalFormat FORMATTER = createFormat(25);

    /**
     * Generates the decimal formatter.
     * 
     * @param decimals <code>int</code>: The number of decimals to be printed.
     * @return <code>DecimalFormat</code>: The created formatter.
     */
    public static final DecimalFormat createFormat(int decimals) {

        DecimalFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(decimals);
        formatter.setMinimumFractionDigits(0);
        formatter.setGroupingUsed(false);

        return formatter;
    }

    private static final String[] vars = { "x", "y", "z", "t" };

    public static final String getVar(int i) {

        if (i < vars.length) {

            return vars[i];
        }

        return "x" + i;
    }

    public static final String[] getVars(int num) {

        String[] s = new String[num];
        System.arraycopy(vars, 0, s, 0, Math.min(vars.length, s.length));
        for (int i = vars.length; i < num; i++) {

            s[i] = "x" + i;
        }

        return s;
    }
}
