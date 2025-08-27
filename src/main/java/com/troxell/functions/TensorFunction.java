package com.troxell.functions;

import java.util.Arrays;

import com.troxell.mat.Matrix;
import com.troxell.mat.Tensor;
import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

public final class TensorFunction {

    public static final int COEFFICIENTS = 0;
    public static final int DERIVATIVES = 1;

    private final Tensor coeffs;
    
    public TensorFunction(int mode, int[] dimensions, double... coeffs) {

        this(mode, dimensions, Arrays.stream(coeffs)
                                      .mapToObj(Real::new)
                                      .toArray(MatNumber[]::new));
    }

    public TensorFunction(int mode, int[] dimensions, MatNumber... coeffs) {

        if (mode == DERIVATIVES) {

            factor(0, MatNumber.ONE, new int[dimensions.length], dimensions, coeffs);
        }
        this.coeffs = new Tensor(dimensions, coeffs);
    }

    public TensorFunction(Tensor tensor) {

        coeffs = tensor;
    }

    private static void factor(int dim, MatNumber coefficient, int[] indices, int[] dimensions, MatNumber[] coeffs) {

        if (dim >= dimensions.length) {

            coeffs[Tensor.index(indices, dimensions)] = coefficient;

            return;
        }

        for (int i = 0; i < dimensions[dim]; i++) {

            indices[dim] = i;
            factor(dim + 1, coefficient, indices, dimensions, coeffs);
            coefficient = coefficient.divide(i + 1);
        }
    }

    public final MatNumber apply(double... args) {

        return apply(new Vector(args));
    }

    public final MatNumber apply(MatNumber... args) {

        return apply(new Vector(args));
    }

    public final MatNumber apply(Vector args) {

        int[] dimensions = coeffs.getDimensions();

        if (args.getSize() != dimensions.length) {

            return null;
        }

        Tensor v = param(args, dimensions);
        return coeffs.innerProduct(v);
    }

    public static final Tensor param(Vector args, int[] dimensions) {

        int size = Tensor.product(dimensions);
        MatNumber[] vals = new MatNumber[size];

        product(0, MatNumber.ONE, args, new int[dimensions.length], dimensions, vals);
        return new Tensor(dimensions, vals);
    }

    private static void product(int dim, MatNumber coeff, Vector args, int[] indices, int[] dimensions, MatNumber[] vals) {

        if (dim >= dimensions.length) {
        
            vals[Tensor.index(indices, dimensions)] = coeff;
            return;
        }


        MatNumber pow = args.get(dim);

        for (int i = 0; i < dimensions[dim]; i++) {

            indices[dim] = i;
            product(dim + 1, coeff, args, indices, dimensions, vals);
            coeff = coeff.multiply(pow);
        }
    }

    public final TensorFunction differ(int dim, int n) {

        int[] dimensions = coeffs.getDimensions();

        if (dim >= dimensions.length) {

            return null;
        }

        int degree = dimensions[dim] - 1;

        Matrix D = differMat(degree, n);
        return new TensorFunction(Tensor.modeProduct(D, coeffs, dim));
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

    public final TensorFunction integ(int dim, int n) {

        int[] dimensions = coeffs.getDimensions();

        if (dim >= dimensions.length) {

            return null;
        }

        int degree = dimensions[dim] - 1;

        Matrix I = integMat(degree, n);
        return new TensorFunction(Tensor.modeProduct(I, coeffs, dim));
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
