package com.troxell.functions;

import java.util.Arrays;

import com.troxell.mat.Matrix;
import com.troxell.mat.Tensor;
import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

/**
 * <code>TensorFunction</code>: A class representing a function defined using
 * its polynomial coefficients as stored in a tensor.
 */
public final class TensorFunction {

    /**
     * <code>int</code>: An enum constant representing a tensor function formed from
     * polynomial coefficients.
     */
    public static final int COEFFICIENTS = 0;

    /**
     * <code>int</code>: An enum constant representing a tensor function formed from
     * its derivatives at <code>v=[0, 0, ... , 0]</code>.
     */
    public static final int DERIVATIVES = 1;

    /**
     * <code>Tensor</code>: The coefficient tensor of this
     * <code>TensorFunction</code> instance.
     */
    private final Tensor coeffs;

    /**
     * Creates a new instance of the <code>TensorFunction</code> class.
     * 
     * @param mode       <code>int</code>: The coefficient mode type, either
     *                   <code>COEFFICIENTS</code> or <code>DERIVATIVES</code>.
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>TensorFunction</code> instance.
     * @param coeffs     <code>double...</code>: The coefficient list of this
     *                   <code>TensorFunction</code> instance.
     */
    public TensorFunction(int mode, int[] dimensions, double... coeffs) {

        this(mode, dimensions, Arrays.stream(coeffs)
                .mapToObj(Real::new)
                .toArray(MatNumber[]::new));
    }

    /**
     * Creates a new instance of the <code>TensorFunction</code> class.
     * 
     * @param mode       <code>int</code>: The coefficient mode type, either
     *                   <code>COEFFICIENTS</code> or <code>DERIVATIVES</code>.
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>TensorFunction</code> instance.
     * @param coeffs     <code>MatNumber...</code>: The coefficient list of this
     *                   <code>TensorFunction</code> instance.
     */
    public TensorFunction(int mode, int[] dimensions, MatNumber... coeffs) {

        if (mode == DERIVATIVES) {

            // Convert derivatives to coefficients.
            factor(0, MatNumber.ONE, 0, 1, dimensions, coeffs);
        }
        this.coeffs = new Tensor(dimensions, coeffs);
    }

    /**
     * Creates a new instance of the <code>TensorFunction</code> class.
     * 
     * @param mode   <code>int</code>: The coefficient mode type, either
     *               <code>COEFFICIENTS</code> or <code>DERIVATIVES</code>.
     * @param tensor <code>Tensor</code>: The coefficient tensor of this
     *               <code>TensorFunction</code> instance.
     */
    public TensorFunction(Tensor tensor) {

        coeffs = tensor;
    }

    private static void factor(int dim, MatNumber coefficient, int index, int indCoeff, int[] dimensions,
            MatNumber[] coeffs) {

        // If the dimension is out of bounds, set the coefficient.
        if (dim >= dimensions.length) {

            coeffs[index] = coefficient;

            return;
        }

        int length = dimensions[dim];

        // Iterate over each coefficient in this dimension.
        for (int i = 0; i < length; i++) {

            // Recurse to the next dimension.
            factor(dim + 1, coefficient, index + i * indCoeff, indCoeff * length, dimensions, coeffs);

            // Divide the coefficient by the next factor.
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

        product(0, MatNumber.ONE, args, 0, 1, dimensions, vals);
        return new Tensor(dimensions, vals);
    }

    private static void product(int dim, MatNumber coeff, Vector args, int index, int indCoeff, int[] dimensions,
            MatNumber[] vals) {

        if (dim >= dimensions.length) {

            vals[index] = coeff;
            return;
        }

        MatNumber pow = args.get(dim);
        int length = dimensions[dim];

        for (int i = 0; i < length; i++) {

            product(dim + 1, coeff, args, index + i * indCoeff, indCoeff * length, dimensions, vals);
            coeff = coeff.multiply(pow);
        }
    }

    public final TensorFunction add(TensorFunction function) {

        Tensor t = coeffs.add(function.coeffs);
        return new TensorFunction(t);
    }

    public final TensorFunction subtract(TensorFunction function) {

        Tensor t = coeffs.subtract(function.coeffs);
        return new TensorFunction(t);
    }

    public final TensorFunction multiply(TensorFunction function) {

        Tensor p = multiply(this.coeffs, function.coeffs);
        return new TensorFunction(p);
    }

    private static Tensor multiply(Tensor t1, Tensor t2) {

        int[] t1Dims = t1.getDimensions();
        int[] t2Dims = t2.getDimensions();

        int[] newDimensions = new int[Math.max(t1Dims.length, t2Dims.length)];
        Arrays.fill(newDimensions, 1);

        for (int i = 0; i < t1Dims.length; i++) {

            newDimensions[i] += t1Dims[i] - 1;
        }

        for (int i = 0; i < t2Dims.length; i++) {

            newDimensions[i] += t2Dims[i] - 1;
        }

        int newSize = Tensor.product(newDimensions);
        MatNumber[] newCoeffs = new MatNumber[newSize];
        Arrays.fill(newCoeffs, MatNumber.ZERO);

        multiplyIterateFirst(0, t1, 0, 1, newDimensions, 0, 1, (coeff, index) -> {

            multiplyIterateSecond(0, coeff, t2, 0, 1, index, 1, newDimensions, newCoeffs);
        });

        return new Tensor(newDimensions, newCoeffs);
    }

    @FunctionalInterface
    private interface MultiplyConsumer {

        public void accept(MatNumber coeff, int index);
    }

    private static void multiplyIterateFirst(int dim, Tensor first, int indexOld, int indCoeffOld, int[] newDims,
            int indexNew, int indCoeffNew, MultiplyConsumer p) {

        if (dim >= first.getNumDimensions()) {

            p.accept(first.get(indexOld), indexNew);
            return;
        }

        int oldLength = first.getDimension(dim);
        int newLength = newDims[dim];

        for (int i = 0; i < oldLength; i++) {

            multiplyIterateFirst(dim + 1, first, indexOld + i * indCoeffOld, indCoeffOld * oldLength, newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, p);
        }
    }

    private static void multiplyIterateSecond(int dim, MatNumber coeff, Tensor second, int indexOld, int indCoeffOld,
            int indexNew, int indCoeffNew,
            int[] newDims, MatNumber[] data) {

        if (dim >= second.getNumDimensions()) {

            data[indexNew] = data[indexNew].add(second.get(indexOld).multiply(coeff));
            return;
        }

        for (int i = 0; i < second.getDimension(dim); i++) {

            multiplyIterateSecond(dim + 1, coeff, second, indexOld + i * indCoeffOld,
                    indCoeffOld * second.getDimension(dim),
                    indexNew + i * indCoeffNew, indCoeffNew * newDims[dim], newDims, data);

        }
    }

    public final TensorFunction pow(int n) {

        if (n <= 0) {

            if (n == 0) {

                int[] dims = new int[coeffs.getNumDimensions()];
                Arrays.fill(dims, 1);
                return new TensorFunction(new Tensor(dims, 1));
            }

            return null;
        }

        Tensor t = coeffs;
        for (int i = 1; i < n; i++) {

            t = multiply(coeffs, t);
        }

        return new TensorFunction(t);
    }

    // public final TensorFunction divide(TensorFunction function) {

    // }

    public final TensorFunction compose(TensorFunction function, int mode) {

        // If the composition variable is out of bounds, return null.
        if (mode < 0 || mode >= coeffs.getNumDimensions()) {

            return null;
        }

        int[] dims = coeffs.getDimensions();
        int[] fDims = function.coeffs.getDimensions();

        int[] newDims = new int[Math.max(dims.length, fDims.length)];
        Arrays.fill(newDims, 1);

        // If the highest degree f is raised to is 0, then there is no change.
        int maxModeDegree = dims[mode] - 1;
        if (maxModeDegree <= 0) {
            return this;
        }

        // Add the dimensions of this function, except in the mode dimension.
        for (int i = 0; i < dims.length; i++) {

            if (i != mode) {

                newDims[i] += dims[i] - 1;
            }
        }

        // Add the dimensions of the function being composed, scaled by the maximum
        // degree of the mode.
        for (int i = 0; i < fDims.length; i++) {

            newDims[i] += (fDims[i] - 1) * maxModeDegree;
        }

        int newSize = Tensor.product(newDims);
        MatNumber[] newData = new MatNumber[newSize];
        Arrays.fill(newData, MatNumber.ZERO);

        final Tensor[] f = new Tensor[] { new Tensor(new int[] { 1 }, 1) };

        int lengthOld = 1;

        // Calculate the index offset for the old and new tensors.
        for (int i = 0; i < mode; i++) {

            lengthOld *= dims[i];
        }

        for (int i = 0; i < dims[mode]; i++) {

            composeFirstIterate(0, mode, coeffs, i * lengthOld, 1, newDims, 0, 1, (coeff, index) -> {

                composeSecondIterate(0, coeff, f[0], 0, 1, index, 1, newDims, newData);
            });

            // Adjust for the next power of f.
            if (i < maxModeDegree) {

                f[0] = multiply(f[0], function.coeffs);
            }
        }

        return new TensorFunction(new Tensor(newDims, newData));
    }

    @FunctionalInterface
    private interface ComposeConsumer {

        public void accept(MatNumber coeff, int index);
    }

    private static void composeFirstIterate(int dim, int mode, Tensor t1, int indexOld, int indCoeffOld, int[] newDims,
            int indexNew, int indCoeffNew, ComposeConsumer p) {

        if (dim >= t1.getNumDimensions()) {

            p.accept(t1.get(indexOld), indexNew);
            return;
        }

        int oldLength = t1.getDimension(dim);
        int newLength = newDims[dim];

        if (dim == mode) {

            composeFirstIterate(dim + 1, mode, t1, indexOld, indCoeffOld * oldLength, newDims,
                    indexNew, indCoeffNew * newLength, p);
            return;
        }

        for (int i = 0; i < oldLength; i++) {

            composeFirstIterate(dim + 1, mode, t1, indexOld + i * indCoeffOld, indCoeffOld * oldLength, newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, p);
        }
    }

    private static void composeSecondIterate(int dim, MatNumber coeff, Tensor t2, int indexOld, int indCoeffOld,
            int indexNew, int indCoeffNew, int[] newDims, MatNumber[] data) {

        if (dim >= t2.getNumDimensions()) {

            data[indexNew] = data[indexNew].add(t2.get(indexOld).multiply(coeff));
            return;
        }

        int oldLength = t2.getDimension(dim);
        int newLength = newDims[dim];

        for (int i = 0; i < oldLength; i++) {

            composeSecondIterate(dim + 1, coeff, t2, indexOld + i * indCoeffOld, indCoeffOld * oldLength,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, newDims, data);
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
