package com.troxell.numbers;

public final class Complex implements MatNumber{

    private final double re;
    private final double im;

    public Complex(double re, double im) {

        this.re = re;
        this.im = im;
    }

    public Complex(MatNumber num) {

        this(num.real(), num.imag());
    }

    @Override
    public final Complex add(MatNumber b) {
        
        return new Complex(re + b.real(), im + b.imag());
    }

    @Override
    public final Complex subtract(MatNumber b) {
        
        return new Complex(re - b.real(), im - b.imag());
    }

    @Override
    public final Complex multiply(MatNumber b) {

        return new Complex(re * b.real() - im * b.imag(), re * b.imag() + im * b.real());
    }

    @Override
    public final Complex multiply(double b) {

        return new Complex(re * b, im * b);
    }

    @Override
    public final Complex divide(MatNumber b) {

        if (b.abs() == 0.0) {

            return null;
        }

        double denom = b.real() * b.real() + b.imag() * b.imag();
        return new Complex((re * b.real() + im * b.imag()) / denom, (im * b.real() - re * b.imag()) / denom);
    }

    @Override
    public final Complex divide(double b) {

        if (b == 0.0) {

            return null;
        }

        return new Complex(re / b, im / b);
    }

    @Override
    public final double real() {
        
        return re;
    }

    @Override
    public final double imag() {
        
        return im;
    }

    @Override
    public final double abs() {
        
        return Math.sqrt(re * re + im * im);
    }

    public final Complex conjugate() {

        return new Complex(re, -im);
    }

    @Override
    public final String toString() {

        if (im == 0.0) {

            return String.format("%f", re);
        } else if (re == 0.0) {

            return String.format("%fi", im);
        } else if (im < 0.0) {

            return String.format("%f - %.4fi", re, -im);
        } else {

            return String.format("%f + %fi", re, im);
        }
    }
}
