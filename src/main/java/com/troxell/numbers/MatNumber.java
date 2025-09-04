package com.troxell.numbers;

/**
 * <code>MatNumber</code>: An interface representing a real or complex
 * mathematical number.
 */
public interface MatNumber {

    /**
     * <code>Real</code>: The real number <code>0.0</code>.
     */
    public static final Real ZERO = new Real(0.0);

    /**
     * <code>Real</code>: The real number <code>1.0</code>.
     */
    public static final Real ONE = new Real(1.0);

    /**
     * Calculates the sum of this <code>MatNumber</code> instance and another.
     * 
     * @param b <code>MatNumber</code>: The number to add.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber add(MatNumber b);

    /**
     * Calculates the difference between this <code>MatNumber</code> instance and
     * another.
     * 
     * @param b <code>MatNumber</code>: The number to subtract.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber subtract(MatNumber b);

    /**
     * Calculates the product between this <code>MatNumber</code> instance and
     * another.
     * 
     * @param b <code>MatNumber</code>: The number to multiply by.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber multiply(MatNumber b);

    /**
     * Calculates the product between this <code>MatNumber</code> instance and
     * another.
     * 
     * @param b <code>double</code>: The number to multiply by.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber multiply(double b);

    /**
     * Calculates the quotient between this <code>MatNumber</code> instance and
     * another.
     * 
     * @param b <code>MatNumber</code>: The number to divide by.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber divide(MatNumber b);

    /**
     * Calculates the quotient between this <code>MatNumber</code> instance and
     * another.
     * 
     * @param b <code>double</code>: The number to divide by.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public MatNumber divide(double b);

    /**
     * Retrieves the real component of this <code>MatNumber</code> instance.
     * 
     * @return <code>double</code>: The real component.
     */
    public double real();

    /**
     * Retrieves the imaginary component of this <code>MatNumber</code> instance.
     * 
     * @return <code>double</code>: The imaginary component.
     */
    public double imag();

    /**
     * Determines the absolute value of this <code>MatNumber</code> instance.
     * 
     * @return <code>double</code>: The absolute value (also known as magnitude).
     */
    public double abs();

    @Override
    public boolean equals(Object obj);

    @Override
    public String toString();

    /**
     * Converts a mathematical string into a new <code>MatNumber</code> instance.
     * 
     * @param s <code>String</code>: A mathematical string of the form
     *          <code>a + b - c + ... + xi + yi - zi</code>.
     * @return <code>MatNumber</code>: The resulting real or complex number.
     */
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
