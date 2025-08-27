package com.troxell.numbers;

public interface MatNumber {

    public static final Real ZERO = new Real(0.0);
    public static final Real ONE = new Real (1.0);
    
    public MatNumber add(MatNumber b);

    public MatNumber subtract(MatNumber b);

    public MatNumber multiply(MatNumber b);

    public MatNumber multiply(double b);

    public MatNumber divide(MatNumber b);

    public MatNumber divide(double b);

    public double real();

    public double imag();

    public double abs();

    @Override
    public String toString();

    public static MatNumber number(String s) {

        s = s.replaceAll("\\s+", "");
        
        double real = 0.0;
        double imag = 0.0;

        int i = 0;
        while (i < s.length()) {

            int sign = 1;
            while (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
                if (s.charAt(i) == '-') {
                    
                    sign *= -1;
                }
                i++;
            }

            String number = "";
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                number += s.charAt(i);
                i++;
            }

            boolean isImaginary = false;
            if (i < s.length() && s.charAt(i) == 'i') {

                isImaginary = true;
                i++;
            }

            if (number.isEmpty()) {

                number = isImaginary ? "1" : "0";
            }

            double value = sign * Double.parseDouble(number);
            if (isImaginary) {

                imag += value;
            } else {

                real += value;
            }
        }

        if (imag == 0.0) {

            return new Real(real);
        } else {

            return new Complex(real, imag);
        }
    }
}
