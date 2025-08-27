package com.troxell.functions;

import java.util.Arrays;

import com.troxell.mat.Matrix;
import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

public final class MatFunction {

    public static final int COEFFICIENTS = 0;
    public static final int DERIVATIVES = 1;

    public static final MatFunction GAMMA_ROOT = new MatFunction(COEFFICIENTS, 1.0, -0.5772, 0.9891, -0.9075, 0.9817, -0.982, 0.9931, -0.996, 0.9981, -0.999, 0.9995, -0.9998, 0.9999, -0.9999, 1, -1, 1, -1, 1, -1);

    private final int degree;
    
    private final Vector coeffs;

    public final Vector getCoeffs() {

        return coeffs;
    }

    public MatFunction(int mode, double... coeffs) {

        if (mode == DERIVATIVES) {

            int fact = 1;
            for (int i = 1; i < coeffs.length; i++) {

                fact *= i;
                coeffs[i] /= fact;
            }
        }

        this.coeffs = new Vector(coeffs);

        degree = coeffs.length - 1;
    }

    public MatFunction(int mode, MatNumber... coeffs) {

        if (mode == DERIVATIVES) {

            MatNumber fact = MatNumber.ONE;
            for (int i = 1; i < coeffs.length; i++) {

                fact = fact.multiply(new Real(i));
                coeffs[i] = coeffs[i].divide(fact);
            }
        }

        this.coeffs = new Vector(coeffs);

        degree = coeffs.length - 1;
    }

    public MatFunction(double... coeffs) {

        this(new Vector(coeffs));
    }

    public MatFunction(MatNumber... coeffs) {

        this(new Vector(coeffs));
    }

    public MatFunction(Vector coeffs) {

        this.coeffs = coeffs;

        degree = coeffs.getSize() - 1;
    }

    public static final MatFunction exp(MatNumber c, int n) {

        MatNumber[] coeffs = new MatNumber[n + 1];
        MatNumber term = MatNumber.ONE;
        for (int i = 0; i < n + 1; i++) {

            coeffs[i] = term;
            term = term.multiply(c);
        }

        return new MatFunction(DERIVATIVES, coeffs);
    }

    public static final MatFunction sin(MatNumber c, int n) {

        MatNumber[] coeffs = new MatNumber[2 * (n + 1)];
        MatNumber term = c;
        for (int i = 0; i < n + 1; i++) {

            coeffs[2 * i + 1] = term;
            coeffs[2 * i] = MatNumber.ZERO;
            term = MatNumber.ZERO.subtract(term.multiply(c.multiply(c)));
        }

        return new MatFunction(DERIVATIVES, coeffs);
    }

    public static final MatFunction cos(MatNumber c, int n) {

        MatNumber[] coeffs = new MatNumber[2 * (n + 1)];
        MatNumber term = MatNumber.ONE;
        for (int i = 0; i < n + 1; i++) {

            coeffs[2 * i] = term;
            coeffs[2 * i + 1] = MatNumber.ZERO;
            term = MatNumber.ZERO.subtract(term.multiply(c.multiply(c)));
        }

        return new MatFunction(DERIVATIVES, coeffs);
    }

    public static final MatNumber gamma(double x) {

        double reducedX = (x + 0.5) % 1.0 - 0.5;
        double difference = x - reducedX;

        MatFunction approx = GAMMA_ROOT;

        for (int n = 1; n <= Math.round(x); n++) {

            approx = approx.multiply(new MatFunction(1 + difference - n, 1.0));
        }

        return approx.apply(reducedX);
    }
    
    public final MatFunction add(MatFunction function) {

        int highest = Math.max(degree, function.degree);
        
        MatNumber[] newCoeffs = new MatNumber[highest + 1];
        System.arraycopy(coeffs.getData(), 0, newCoeffs, 0, coeffs.getSize());
        Arrays.fill(newCoeffs, coeffs.getSize(), highest, MatNumber.ZERO);

        MatNumber[] fData = coeffs.getData();

        for (int i = 0; i < function.coeffs.getSize(); i++) {

            newCoeffs[i] = newCoeffs[i].add(fData[i]);
        }

        return new MatFunction(newCoeffs);
    }

    public final MatFunction subtract(MatFunction function) {

        int highest = Math.max(degree, function.degree);
        
        MatNumber[] newCoeffs = new MatNumber[highest + 1];
        System.arraycopy(coeffs.getData(), 0, newCoeffs, 0, coeffs.getSize());
        Arrays.fill(newCoeffs, coeffs.getSize(), highest, MatNumber.ZERO);

        MatNumber[] fData = coeffs.getData();

        for (int i = 0; i < function.coeffs.getSize(); i++) {

            newCoeffs[i] = newCoeffs[i].subtract(fData[i]);
        }

        return new MatFunction(newCoeffs);
    }

    public final MatFunction multiply(MatFunction function) {

        int newDegree = degree + function.degree;

        MatNumber[] f = new MatNumber[newDegree + 1];
        Arrays.fill(f, degree + 1, f.length, MatNumber.ZERO);
        System.arraycopy(coeffs.getData(), 0, f, 0, degree + 1);

        MatNumber[] g = new MatNumber[newDegree + 1];
        Arrays.fill(g, function.degree + 1, g.length, MatNumber.ZERO);
        System.arraycopy(function.coeffs.getData(), 0, g, 0, function.degree + 1);

        MatNumber[] newCoeffs = new MatNumber[newDegree + 1];

        for (int i = 0; i <= newDegree; i++) {

            MatNumber coeff = MatNumber.ZERO;

            for (int j = 0; j <= i; j++) {

                coeff = coeff.add(f[j].multiply(g[i - j]));
            }

            newCoeffs[i] = coeff;
        }

        return new MatFunction(newCoeffs);
    }

    public final MatFunction pow(int p) {

        MatFunction result = new MatFunction(1.0);

        for (int i = 0; i < p; i++) {

            result = result.multiply(this);
        }

        return result;
    }

    public static final Vector param(double x, int n) {
        
        double[] data = new double[n + 1];
        double num = 1.0;
        for (int i = 0; i <= n; i++) {
        
            data[i] = num;
            num *= x;
        }
        return new Vector(data);
    }

    public static final Vector param(MatNumber x, int n) {
        
        MatNumber[] data = new MatNumber[n + 1];
        MatNumber num = MatNumber.ONE;
        for (int i = 0; i <= n; i++) {
        
            data[i] = num;
            num = num.multiply(x);
        }
        return new Vector(data);
    }

    public final MatNumber apply(double x) {

        Vector X = param(x, degree);
        return coeffs.dot(X);
    }

    public final MatNumber apply(MatNumber x) {

        Vector X = param(x, degree);
        return coeffs.dot(X);
    }

    public final MatNumber differ(double x, int k) {

        if (k > degree) {

            return MatNumber.ZERO;
        }

        Matrix D = differMat(degree, k);
        Vector dCoeffs = D.multiply(coeffs);
        Vector X = param(x, degree - k);
        return dCoeffs.dot(X);
    }

    public final MatNumber differ(MatNumber x, int k) {

        if (k > degree) {

            return MatNumber.ZERO;
        }

        Matrix D = differMat(degree, k);
        Vector dCoeffs = D.multiply(coeffs);
        Vector X = param(x, degree - k);
        return dCoeffs.dot(X);
    }

    public static final Matrix differMat(int n, int k) {

        int rows = n + 1 - k;
        int cols = n + 1;

        double[] data = new double[rows * cols];
        for (int i = 0; i < rows; i++) {

            double num = 1.0;

            for (int j = 0; j < k; j++) {

                num *= (i + j + 1);
            }

            data[i * cols + i + k] = num;
        }

        return new Matrix(rows, cols, data);
    }

    public final MatNumber integ(double x, int k) {

        if (x == 0.0) {

            return MatNumber.ZERO;
        }

        Matrix I = integMat(degree, k);
        Vector iCoeffs = I.multiply(coeffs);
        Vector X = param(x, degree + k);
        return iCoeffs.dot(X);
    }

    public final MatNumber integ(double x, double... C) {

        MatFunction CFunc = new MatFunction(C);

        int k = C.length;
        Matrix I = integMat(degree, k);
        MatFunction integ = new MatFunction(I.multiply(coeffs));

        return integ.add(CFunc).apply(x);
    }

    public static final Matrix integMat(int n, int k) {

        int rows = n + 1 + k;
        int cols = n + 1;

        double[] data = new double[rows * cols];
        for (int i = 0; i < cols; i++) {

            double num = 1.0;

            for (int j = 0; j < k; j++) {

                num *= (i + j + 1);
            }

            data[i + cols * (i + k)] = 1.0 / num;
        }

        return new Matrix(rows, cols, data);
    }

    @Override
    public final String toString() {

        return coeffs.toString();
    }
}
