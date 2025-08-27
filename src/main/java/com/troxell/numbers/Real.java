package com.troxell.numbers;

/**
 * <code>Real</code>: A class representing a real number.
 */
public final class Real implements MatNumber {

    /**
     * <code>double</code>: The value of this <code>Real</code> instance.
     */
    private final double value;

    /**
     * Creates a new instance of the <code>Real</code> class.
     * 
     * @param value The value of this <code>Real</code> instance.
     */
    public Real(double value) {

        this.value = value;
    }

    @Override
    public final MatNumber add(MatNumber b) {

        if (b instanceof Complex) {

            return new Complex(value + b.real(), b.imag());
        }

        return new Real(value + b.real());
    }

    @Override
    public final MatNumber subtract(MatNumber b) {

        if (b instanceof Complex) {

            return new Complex(value - b.real(), -b.imag());
        }

        return new Real(value - b.real());
    }

    @Override
    public final MatNumber multiply(MatNumber b) {

        if (b instanceof Complex) {

            return new Complex(value * b.real(), value * b.imag());
        }

        return new Real(value * b.real());
    }

    @Override
    public final Real multiply(double b) {

        return new Real(value * b);
    }

    @Override
    public final MatNumber divide(MatNumber b) {

        if (b.abs() == 0.0) {

            return null;
        }

        if (b instanceof Complex) {

            double denom = b.real() * b.real() + b.imag() * b.imag();
            return new Complex((value * b.real()) / denom, (value * -b.imag()) / denom);
        }

        return new Real(value / b.real());
    }

    @Override
    public final Real divide(double b) {

        if (b == 0.0) {

            return null;
        }

        return new Real(value / b);
    }

    @Override
    public final double real() {

        return value;
    }

    @Override
    public final double imag() {

        return 0.0;
    }

    @Override
    public final double abs() {

        return Math.abs(value);
    }

    /**
     * Retrieves the string representation of this <code>Real</code> instance.
     * 
     * @return <code>String</code>: The string representation of this
     *         <code>Real</code> instance.
     */
    @Override
    public String toString() {

        return Double.toString(value);
    }

}
