package com.troxell.numbers;

public final class Real implements MatNumber {

    private final double value;

    public Real(double value) {

        this.value = value;
    }

    public final Complex toComplex() {

        return new Complex(value, 0.0);
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

    @Override
    public String toString() {

        return Double.toString(value);
    }
    
}
