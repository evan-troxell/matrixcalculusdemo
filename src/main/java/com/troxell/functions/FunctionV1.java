package com.troxell.functions;

import java.util.Arrays;

import com.troxell.numbers.MatNumber;

/**
 * <code>TensorFunction</code>: A class representing a single-variable function
 * defined using its polynomial coefficients as stored in a vector.
 */
public final class FunctionV1 extends TensorFunction {

    public static final FunctionV1 GAMMA_ROOT = new FunctionV1(COEFFICIENTS, 1.0, -0.5772, 0.9891, -0.9075, 0.9817,
            -0.982, 0.9931, -0.996, 0.9981, -0.999, 0.9995, -0.9998, 0.9999, -0.9999, 1, -1, 1, -1, 1, -1);

    /**
     * Creates a new instance of the <code>FunctionV1</code> class.
     * 
     * @param mode   <code>int</code>: The coefficient mode type, either
     *               <code>COEFFICIENTS</code> or <code>DERIVATIVES</code>.
     * @param coeffs <code>double...</code>: The coefficient list of this
     *               <code>FunctionV1</code> instance.
     */
    public FunctionV1(int mode, double... coeffs) {

        super(mode, new int[] { coeffs.length }, coeffs);
    }

    /**
     * Creates a new instance of the <code>FunctionV1</code> class using
     * coefficients.
     * 
     * @param coeffs <code>double...</code>: The coefficient list of this
     *               <code>FunctionV1</code> instance.
     */
    public FunctionV1(double... coeffs) {

        this(COEFFICIENTS, coeffs);
    }

    /**
     * Creates a new instance of the <code>FunctionV1</code> class.
     * 
     * @param mode   <code>int</code>: The coefficient mode type, either
     *               <code>COEFFICIENTS</code> or <code>DERIVATIVES</code>.
     * @param coeffs <code>MatNumber...</code>: The coefficient list of this
     *               <code>FunctionV1</code> instance.
     */
    public FunctionV1(int mode, MatNumber... coeffs) {

        super(mode, new int[] { coeffs.length }, coeffs);
    }

    /**
     * Creates a new instance of the <code>FunctionV1</code> class using
     * coefficients.
     * 
     * @param coeffs <code>MatNumber...</code>: The coefficient list of this
     *               <code>FunctionV1</code> instance.
     */
    public FunctionV1(MatNumber... coeffs) {

        this(COEFFICIENTS, coeffs);
    }

    public static final FunctionV1 exp(int n) {

        double[] coeffs = new double[n + 1];
        Arrays.fill(coeffs, 1.0);

        return new FunctionV1(DERIVATIVES, coeffs);
    }

    public static final FunctionV1 sin(int n) {

        MatNumber[] coeffs = new MatNumber[2 * (n + 1)];
        MatNumber term = MatNumber.ONE;
        for (int i = 0; i < n + 1; i++) {

            coeffs[2 * i] = MatNumber.ZERO;
            coeffs[2 * i + 1] = term;
            term = MatNumber.ZERO.subtract(term);
        }

        return new FunctionV1(DERIVATIVES, coeffs);
    }

    public static final FunctionV1 cos(int n) {

        MatNumber[] coeffs = new MatNumber[2 * (n + 1)];
        MatNumber term = MatNumber.ONE;
        for (int i = 0; i < n + 1; i++) {

            coeffs[2 * i] = term;
            coeffs[2 * i + 1] = MatNumber.ZERO;
            term = MatNumber.ZERO.subtract(term);
        }

        return new FunctionV1(DERIVATIVES, coeffs);
    }

    public static final MatNumber gamma(double x, int n) {

        double reducedX = (x + 0.5) % 1.0 - 0.5;
        double difference = x - reducedX;

        TensorFunction approx = GAMMA_ROOT;

        for (int i = 1; i <= Math.round(x); i++) {

            approx = approx.multiply(new FunctionV1(1 + difference - i, 1.0));
        }

        return approx.differ(0, n).apply(reducedX);
    }

    public final MatNumber apply(double x) {

        return super.apply(x);
    }

    public final MatNumber apply(MatNumber x) {

        return super.apply(x);
    }

    public final TensorFunction differ(int n) {

        return super.differ(0, n);
    }

    public final TensorFunction integ(int n) {

        return super.integ(0, n);
    }
}
