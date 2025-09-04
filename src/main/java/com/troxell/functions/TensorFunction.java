package com.troxell.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.troxell.mat.Matrix;
import com.troxell.mat.Tensor;
import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

/**
 * <code>TensorFunction</code>: A class representing a function defined using
 * its polynomial coefficients as stored in a tensor.
 */
public class TensorFunction {

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

    public static final TensorFunction ZERO = new TensorFunction(new Tensor(new int[] {}, 0));

    public static final TensorFunction ONE = new TensorFunction(new Tensor(new int[] {}, 1));

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
     * Creates a new instance of the <code>TensorFunction</code> class using
     * coefficients.
     * 
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>TensorFunction</code> instance.
     * @param coeffs     <code>double...</code>: The coefficient list of this
     *                   <code>TensorFunction</code> instance.
     */
    public TensorFunction(int[] dimensions, double... coeffs) {

        this(COEFFICIENTS, dimensions, coeffs);
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
            factorIterate(0, MatNumber.ONE, 0, 1, dimensions, coeffs);
        }
        this.coeffs = new Tensor(dimensions, coeffs);
    }

    /**
     * Creates a new instance of the <code>TensorFunction</code> class using
     * coefficients.
     * 
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>TensorFunction</code> instance.
     * @param coeffs     <code>MatNumber...</code>: The coefficient list of this
     *                   <code>TensorFunction</code> instance.
     */
    public TensorFunction(int[] dimensions, MatNumber... coeffs) {

        this(COEFFICIENTS, dimensions, coeffs);
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

    /**
     * Iterates along each dimension of a tensor to calculate the coefficients from
     * the derivatives.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param coefficient <code>MatNumber</code>: The current coefficient.
     * @param index       <code>int</code>: The current index in the coefficient
     *                    array.
     * @param indCoeff    <int</code>: The index coefficient for the current
     *                    dimension.
     * @param dimensions  <int[]</code>: The dimensions to iterate over.
     * @param coeffs      <code>MatNumber[]</code>: The coefficient array to fill.
     */
    private static void factorIterate(int dim, MatNumber coefficient, int index, int indCoeff, int[] dimensions,
            MatNumber[] coeffs) {

        // If the dimension is out of bounds, set the coefficient.
        if (dim >= dimensions.length) {

            coeffs[index] = coeffs[index].multiply(coefficient);

            return;
        }

        int length = dimensions[dim];

        // Iterate over each coefficient in this dimension.
        for (int i = 0; i < length; i++) {

            // Recurse to the next dimension.
            factorIterate(dim + 1, coefficient, index + i * indCoeff, indCoeff * length, dimensions, coeffs);

            // Divide the coefficient by the next factor.
            coefficient = coefficient.divide(i + 1);
        }
    }

    /**
     * Applies a set of arguments to this <code>TensorFunction</code> instance.
     * 
     * @param args <code>double...</code>: The arguments to apply.
     * @return <code>MatNumber</code>: The resulting value.
     */
    public final MatNumber apply(double... args) {

        return apply(new Vector(args));
    }

    /**
     * Applies a set of arguments to this <code>TensorFunction</code> instance.
     * 
     * @param args <code>MatNumber...</code>: The arguments to apply.
     * @return <code>MatNumber</code>: The resulting value.
     */
    public final MatNumber apply(MatNumber... args) {

        return apply(new Vector(args));
    }

    /**
     * Applies a vector of arguments to this <code>TensorFunction</code> instance.
     * 
     * @param args <code>Vector</code>: The arguments to apply.
     * @return <code>MatNumber</code>: The resulting value.
     */
    public final MatNumber apply(Vector args) {

        int[] dimensions = coeffs.getDimensions();
        int length = dimensions.length;

        // If there are not enough provided arguments, return null.
        if (args.getSize() < length) {

            return null;
        }

        // If there are too many provided arguments, trim them.
        if (args.getSize() > length) {

            MatNumber[] newArgs = new MatNumber[length];
            for (int i = 0; i < length; i++) {

                newArgs[i] = args.get(i);
            }

            args = new Vector(newArgs);
        }

        Tensor v = param(args, dimensions);
        return coeffs.innerProduct(v);
    }

    /**
     * Generates a tensor of exponential terms for a given set of arguments.
     * 
     * @param args       <code>Vector</code>: The arguments to apply.
     * @param dimensions <code>int[]</code>: The dimensions of the tensor to fill.
     * @return <code>Tensor</code>: The calcalted tensor.
     */
    public static final Tensor param(Vector args, int[] dimensions) {

        int size = Tensor.product(dimensions);
        MatNumber[] vals = new MatNumber[size];

        expIterate(0, MatNumber.ONE, args, 0, 1, dimensions, vals);
        return new Tensor(dimensions, vals);
    }

    /**
     * Iterates along each dimension of a tensor to calculate the exponents from a
     * set of arguments.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param coefficient <code>MatNumber</code>: The current coefficient.
     * @param args        <code>Vector</code>: The arguments to apply.
     * @param index       <code>int</code>: The current index in the coefficient
     *                    array.
     * @param indCoeff    <int</code>: The index coefficient for the current
     *                    dimension.
     * @param dimensions  <int[]</code>: The dimensions to iterate over.
     * @param coeffs      <code>MatNumber[]</code>: The coefficient array to fill.
     */
    private static void expIterate(int dim, MatNumber coeff, Vector args, int index, int indCoeff, int[] dimensions,
            MatNumber[] coeffs) {

        if (dim >= dimensions.length) {

            coeffs[index] = coeff;
            return;
        }

        MatNumber pow = args.get(dim);
        int length = dimensions[dim];

        for (int i = 0; i < length; i++) {

            expIterate(dim + 1, coeff, args, index + i * indCoeff, indCoeff * length, dimensions, coeffs);
            coeff = coeff.multiply(pow);
        }
    }

    /**
     * Computes the sum of this <code>TensorFunction</code> instance and another.
     * 
     * @param function <code>TensorFunction</code>: The function to add.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction add(TensorFunction function) {

        Tensor t = coeffs.add(function.coeffs);
        return new TensorFunction(t);
    }

    /**
     * Computes the difference between this <code>TensorFunction</code> instance and
     * another.
     * 
     * @param function <code>TensorFunction</code>: The function to subtract.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction subtract(TensorFunction function) {

        Tensor t = coeffs.subtract(function.coeffs);
        return new TensorFunction(t);
    }

    /**
     * Computes the product between this <code>TensorFunction</code> instance and
     * another.
     * 
     * @param function <code>TensorFunction</code>: The function to multiply by.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction multiply(TensorFunction function) {

        Tensor p = multiply(this.coeffs, function.coeffs);
        return new TensorFunction(p);
    }

    /**
     * Computes the polynomial product between two tensors.
     * 
     * @param t1 <code>Tensor</code>: The first tensor to multiply.
     * @param t2 <code>Tensor</code>: The second tensor to multiply.
     * @return <code>Tensor</code>: The calculated tensor.
     */
    private static Tensor multiply(Tensor t1, Tensor t2) {

        int[] t1Dims = t1.getDimensions();
        int[] t2Dims = t2.getDimensions();

        // The new tensor should start as a constant in all dimensions.
        int[] newDimensions = new int[Math.max(t1Dims.length, t2Dims.length)];
        Arrays.fill(newDimensions, 1);

        // Add the degrees of the first tensor.
        for (int i = 0; i < t1Dims.length; i++) {

            newDimensions[i] += t1Dims[i] - 1;
        }

        // Add the degrees of the second tensor.
        for (int i = 0; i < t2Dims.length; i++) {

            newDimensions[i] += t2Dims[i] - 1;
        }

        // Create the data and fill with zeros.
        int newSize = Tensor.product(newDimensions);
        MatNumber[] newCoeffs = new MatNumber[newSize];
        Arrays.fill(newCoeffs, MatNumber.ZERO);

        // Iterate through each index in the first tensor.
        multiplyIterateFirst(0, t1, 0, 1, newDimensions, 0, 1, (coeff, index) -> {

            // Iterate through each index in the second tensor.
            multiplyIterateSecond(0, coeff, t2, 0, 1, newDimensions, index, 1, newCoeffs);
        });

        return new Tensor(newDimensions, newCoeffs);
    }

    /**
     * <code>MultiplyConsumer</code>: A functional interface for consuming
     * coefficients and indices in the first stage of polynomial multiplication.
     */
    @FunctionalInterface
    private interface MultiplyConsumer {

        /**
         * Accepts a coefficient and index.
         * 
         * @param coeff <code>MatNumber</code>: The coefficient calculated in the first
         *              stage.
         * @param index <code>int</code>: The index offset calculated in the first
         *              stage.
         */
        public void accept(MatNumber coeff, int index);
    }

    /**
     * Iterates through the first tensor in polynomial multiplication.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param first       <code>Tensor</code>: The first tensor.
     * @param indexOld    <code>int</code>: The current index in the first tensor.
     * @param indCoeffOld <code>int</code>: The index coefficient for the first
     *                    tensor.
     * @param newDims     <code>int[]</code>: The dimensions of the new tensor.
     * @param indexNew    <code>int</code>: The current index in the new tensor.
     * @param indCoeffNew <code>int</code>: The index coefficient for the new
     *                    tensor.
     * @param p           <code>MultiplyConsumer</code>: The consumer to apply at
     *                    the end of
     *                    iteration in the first tensor.
     */
    private static void multiplyIterateFirst(int dim, Tensor first, int indexOld, int indCoeffOld, int[] newDims,
            int indexNew, int indCoeffNew, MultiplyConsumer p) {

        if (dim >= first.getNumDimensions()) {

            MatNumber coeff = first.get(indexOld);
            if (!coeff.equals(MatNumber.ZERO)) {

                p.accept(coeff, indexNew);
            }

            return;
        }

        int oldLength = first.getDimension(dim);
        int newLength = newDims[dim];

        for (int i = 0; i < oldLength; i++) {

            multiplyIterateFirst(dim + 1, first, indexOld + i * indCoeffOld, indCoeffOld * oldLength, newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, p);
        }
    }

    /**
     * Iterates through the second tensor in polynomial multiplication.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param coeff       <code>MatNumber</code>: The coefficient from the first
     *                    tensor.
     * @param second      <code>Tensor</code>: The second tensor.
     * @param indexOld    <code>int</code>: The current index in the second tensor.
     * @param indCoeffOld <code>int</code>: The index coefficient for the second
     *                    tensor.
     * @param newDims     <code>int[]</code>: The dimensions of the new tensor.
     * @param indexNew    <code>int</code>: The current index in the new tensor.
     * @param indCoeffNew <code>int</code>: The index coefficient for the new
     *                    tensor.
     * @param data        <code>MatNumber[]</code>: The data array of the new
     *                    tensor.
     */
    private static void multiplyIterateSecond(int dim, MatNumber coeff, Tensor second, int indexOld, int indCoeffOld,
            int[] newDims, int indexNew, int indCoeffNew, MatNumber[] data) {

        if (dim >= second.getNumDimensions()) {

            coeff = coeff.multiply(second.get(indexOld));

            if (!coeff.equals(MatNumber.ZERO)) {

                data[indexNew] = data[indexNew].add(coeff);
            }

            return;
        }

        for (int i = 0; i < second.getDimension(dim); i++) {

            multiplyIterateSecond(dim + 1, coeff, second, indexOld + i * indCoeffOld,
                    indCoeffOld * second.getDimension(dim), newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newDims[dim], data);

        }
    }

    /**
     * Calculates the nth power of this <code>TensorFunction</code> instance.
     * 
     * @param n <code>int</code>: The exponent to apply.
     * @return <code>TensorFunction</code>: The calculated function.
     */
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

    /**
     * Computes the quotient between this <code>TensorFunction</code> instance and
     * another.
     * 
     * @param function <code>TensorFunction</code>: The function to divide by.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction divide(TensorFunction function) {

        // TODO: Implement divison.
        return null;
    }

    /**
     * Calculates the composition of this <code>TensorFunction</code> instance and
     * another.
     * 
     * @param function <code>TensorFunction</code>: The function to compose with.
     * @param mode     <code>int</code>: The variable index to compose over.
     * @return <code>TensorFunction</code>: The calculated function.
     */
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

        // Iterate through each degree of f.
        for (int i = 0; i < dims[mode]; i++) {

            // Iterate through each index in this tensor except in the mode dimension.
            composeFirstIterate(0, mode, coeffs, i * lengthOld, 1, newDims, 0, 1, (coeff, index) -> {

                // Iterate through each index in f^i.
                composeSecondIterate(0, coeff, f[0], 0, 1, newDims, index, 1, newData);
            });

            // Adjust for the next power of f.
            if (i < maxModeDegree) {

                f[0] = multiply(f[0], function.coeffs);
            }
        }

        return new TensorFunction(new Tensor(newDims, newData));
    }

    /**
     * <code>ComposeConsumer</code>: A functional interface for consuming
     * coefficients and indices in the first stage of polynomial composition.
     */
    @FunctionalInterface
    private interface ComposeConsumer {

        /**
         * Accepts a coefficient and index.
         * 
         * @param coeff <code>MatNumber</code>: The coefficient calculated in the first
         *              stage.
         * @param index <code>int</code>: The index offset calculated in the first
         *              stage.
         */
        public void accept(MatNumber coeff, int index);
    }

    /**
     * Iterates through the first tensor in polynomial composition.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param mode        <code>int</code>: The mode dimension to skip.
     * @param first       <code>Tensor</code>: The first tensor.
     * @param indexOld    <code>int</code>: The current index in the first tensor.
     * @param indCoeffOld <code>int</code>: The index coefficient for the first
     *                    tensor.
     * @param newDims     <code>int[]</code>: The dimensions of the new tensor.
     * @param indexNew    <code>int</code>: The current index in the new tensor.
     * @param indCoeffNew <code>int</code>: The index coefficient for the second
     *                    tensor.
     * @param p           <code>ComposeConsumer</code>: The consumer to apply at the
     *                    end of iteration in the first tensor.
     */
    private static void composeFirstIterate(int dim, int mode, Tensor first, int indexOld, int indCoeffOld,
            int[] newDims,
            int indexNew, int indCoeffNew, ComposeConsumer p) {

        if (dim >= first.getNumDimensions()) {

            p.accept(first.get(indexOld), indexNew);
            return;
        }

        int oldLength = first.getDimension(dim);
        int newLength = newDims[dim];

        if (dim == mode) {

            composeFirstIterate(dim + 1, mode, first, indexOld, indCoeffOld * oldLength, newDims,
                    indexNew, indCoeffNew * newLength, p);
            return;
        }

        for (int i = 0; i < oldLength; i++) {

            composeFirstIterate(dim + 1, mode, first, indexOld + i * indCoeffOld, indCoeffOld * oldLength, newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, p);
        }
    }

    /**
     * Iterates through the second tensor in polynomial composition.
     * 
     * @param dim         <code>int</code>: The current dimension.
     * @param coeff       <code>MatNumber</code>: The coefficient from the first
     *                    tensor.
     * @param second      <code>Tensor</code>: The second tensor.
     * @param indexOld    <code>int</code>: The current index in the second tensor.
     * @param indCoeffOld <code>int</code>: The index coefficient for the second
     *                    tensor.
     * @param newDims     <code>int[]</code>: The dimensions of the new tensor.
     * @param indexNew    <code>int</code>: The current index in the new tensor.
     * @param indCoeffNew <code>int</code>: The index coefficient for the new
     *                    tensor.
     * @param data        <code>MatNumber[]</code>: The data array of the new
     *                    tensor.
     */
    private static void composeSecondIterate(int dim, MatNumber coeff, Tensor second, int indexOld, int indCoeffOld,
            int[] newDims, int indexNew, int indCoeffNew, MatNumber[] data) {

        if (dim >= second.getNumDimensions()) {

            data[indexNew] = data[indexNew].add(second.get(indexOld).multiply(coeff));
            return;
        }

        int oldLength = second.getDimension(dim);
        int newLength = newDims[dim];

        for (int i = 0; i < oldLength; i++) {

            composeSecondIterate(dim + 1, coeff, second, indexOld + i * indCoeffOld, indCoeffOld * oldLength, newDims,
                    indexNew + i * indCoeffNew, indCoeffNew * newLength, data);
        }
    }

    /**
     * Calculates the nth derivative of this <code>TensorFunction</code> instance
     * with respect to a given mode.
     * 
     * @param mode <code>int</code>: The mode to differentiate along.
     * @param n    <code>int</code>: The order of the derivative.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction differ(int mode, int n) {

        int[] dimensions = coeffs.getDimensions();

        if (mode >= dimensions.length) {

            return null;
        }

        int degree = dimensions[mode] - 1;

        Matrix D = differMat(degree, n);
        return new TensorFunction(Tensor.modeProduct(D, coeffs, mode));
    }

    /**
     * Computes the total derivative of this <code>TensorFunction</code> instance,
     * composed of the provided inner (auxilary) functions, with respect to a given
     * mode.
     * 
     * @param mode  <code>int</code>: The mode to differentiate along. This mode may
     *              be an inner function.
     * @param inner <code>TensorFunction...</code>: The inner (auxilary) functions,
     *              where the index corresponds to a given mode (variable index).
     *              Inner functions which are left as <code>null</code> values or
     *              which are excluded from the list are treated as independent
     *              variables.
     * @return <code>TensorFunction</code>: The calculated function, or the total
     *         derivative of this <code>TensorFunction</code> instance.
     */
    public final TensorFunction totalDiffer(int mode, TensorFunction... inner) {

        List<Set<Integer>> dependencies = new ArrayList<>();

        // Iterate through all input functions to find dependency hierarchy.
        for (int i = 0; i < inner.length; i++) {

            TensorFunction func = inner[i];
            HashSet<Integer> deps = new HashSet<>();

            // If the function is null, it is an independent variable.
            if (func != null) {

                // Dimensions which are not constant are dependencies.
                for (int dim = 0; dim < func.coeffs.getNumDimensions(); dim++) {

                    if (func.coeffs.getDimension(dim) > 1) {

                        deps.add(dim);
                    }
                }
            }

            dependencies.add(i, deps);
        }

        HashSet<Integer> validated = new HashSet<>();
        HashSet<Integer> seen = new HashSet<>();

        for (int i = 0; i < inner.length; i++) {

            if (!validateModeDependencies(i, dependencies, validated, seen)) {

                return null;
            }
        }

        for (int i = dependencies.size(); i <= mode; i++) {

            dependencies.add(new HashSet<>());
        }

        HashMap<Integer, Map<Integer, TensorFunction>> computed = new HashMap<>();
        return totalDiffer(mode, inner, dependencies, computed);
    }

    /**
     * Validates the dependency hierarchy at a given mode.
     * 
     * @param mode         <code>int</code>: The mode to validate.
     * @param dependencies <code>List&lt;Set&lt;Integer&gt;&gt;</code>: The list of
     *                     dependencies for each mode. Every set in the list
     *                     describes the list of functions that the mode of that
     *                     index is dependent on.
     * @param validated    <code>Set&lt;Integer&gt;</code>: The set of modes which
     *                     have already been validated.
     * @param seen         <code>Set&lt;Integer&gt;</code>: The set of modes which
     *                     have already been traced along in the current validation
     *                     path. If a mode is repeated in this set, a cycle has been
     *                     detected.
     * @return <code>boolean</code>: Whether or not the mode hierarchy is valid.
     */
    private static boolean validateModeDependencies(int mode, List<Set<Integer>> dependencies, Set<Integer> validated,
            Set<Integer> seen) {

        // If the mode hierarchy has already been validated, return true.
        if (validated.contains(mode)) {

            return true;
        }

        // If the mode is out of bounds, it is an independent variable and has no
        // dependencies.
        if (dependencies.size() <= mode) {

            for (int i = dependencies.size(); i <= mode; i++) {

                dependencies.add(new HashSet<>());
            }
        }

        // If the mode has already been seen but not validated, there is a cycle.
        if (seen.contains(mode)) {

            System.out.println("Cycle detected at mode " + mode);
            return false;
        }

        seen.add(mode);

        // If the mode is not defined, it is an independent variable and has no
        // dependencies.
        if (dependencies.get(mode) == null) {

            dependencies.set(mode, new HashSet<>());
        }

        HashSet<Integer> newDeps = new HashSet<>();

        // Recursively validate all dependencies.
        for (int i : dependencies.get(mode)) {

            if (!validateModeDependencies(i, dependencies, validated, seen)) {

                return false;
            }

            newDeps.addAll(dependencies.get(i));
        }

        dependencies.get(mode).addAll(newDeps);

        // Mark this node as validated.
        seen.remove(mode);
        validated.add(mode);
        return true;
    }

    /**
     * Computes the total derivative of this <code>TensorFunction</code> instance,
     * composed of the provided inner (auxilary) functions, with respect to a given
     * mode. This method assumes that the dependency hierarchy has already been
     * validated and relies on a cache of previously computed derivatives.
     * 
     * @param mode         <code>int</code>: The mode to differentiate along. This
     *                     mode may be an inner function.
     * @param inner        <code>TensorFunction...</code>: The inner (auxilary)
     *                     functions, where the index corresponds to a given mode
     *                     (variable index). Inner functions which are left as
     *                     <code>null</code> values or which are excluded from the
     *                     list are treated as independent variables.
     * @param dependencies <code>List&lt;Set&lt;Integer&gt;&gt;</code>: The list of
     *                     dependencies for each mode. Every set in the list
     *                     describes the list of functions that the mode of that
     *                     index is dependent on.
     * @param computed     <code>Map&lt;Integer, Map&lt;Integer, TensorFunction&gt;&gt;</code>:
     *                     The set of previously computed total derivatives.
     * @return <code>TensorFunction</code>: The calculated function, or the total
     *         derivative of this <code>TensorFunction</code> instance.
     */
    private TensorFunction totalDiffer(int mode, TensorFunction[] inner,
            List<Set<Integer>> dependencies, Map<Integer, Map<Integer, TensorFunction>> computed) {

        // Sum over the partial derivatives of f with respect to each variable, times
        // the derivative of each variable with respect to the denominator.
        TensorFunction f_prime = ZERO;
        for (int i = 0; i < coeffs.getNumDimensions(); i++) {

            if (coeffs.getDimension(i) < 2) {

                continue;
            }

            f_prime = f_prime.add(differ(i, 1).multiply(
                    totalDifferIterate(i, mode, inner, dependencies, computed)));
        }

        return f_prime;
    }

    /**
     * Computes the total derivative of a given mode <code>num</code> with respect
     * to another mode <code>denom</code> by summing over the products between the
     * partial derivatives with respect to each of the arguments and the total
     * derivatives of the arguments with respect to <code>denom</code>.
     * 
     * @param num          <code>int</code>: The mode to differentiate.
     * @param denom        <code>int</code>: The mode to differentiate along. This
     *                     should be a variable the <code>num</code> mode is
     *                     dependent on.
     * @param inner        <code>TensorFunction...</code>: The inner (auxilary)
     *                     functions, where the index corresponds to a given mode
     *                     (variable index). Inner functions which are left as
     *                     <code>null</code> values or which are excluded from the
     *                     list are treated as independent variables.
     * @param dependencies <code>List&lt;Set&lt;Integer&gt;&gt;</code>: The list of
     *                     dependencies for each mode. Every set in the list
     *                     describes the list of functions that the mode of that
     *                     index is dependent on.
     * @param computed     <code>Map&lt;Integer, Map&lt;Integer, TensorFunction&gt;&gt;</code>:
     *                     The set of previously computed total derivatives.
     * @return <code>TensorFunction</code>: The calculated function, or the total
     *         derivative of the <code>num</code> mode with respect to the
     *         <code>denom</code> mode.
     */
    private static TensorFunction totalDifferIterate(int num, int denom, TensorFunction[] inner,
            List<Set<Integer>> dependencies, Map<Integer, Map<Integer, TensorFunction>> computed) {

        // dx/dx = 1
        if (num == denom) {

            return ONE;
        }

        Map<Integer, TensorFunction> computedNum = computed.computeIfAbsent(num, _ -> new HashMap<>());
        if (computedNum.containsKey(denom)) {

            return computedNum.get(denom);
        }

        // If it is an independent variable, check if the denominator is in terms of the
        // numerator.
        if (dependencies.get(num).isEmpty()) {

            Map<Integer, TensorFunction> computedDenom = computed.computeIfAbsent(denom, _ -> new HashMap<>());

            // If they are independent of each other, the derivative of both is 0.
            if (!dependencies.get(denom).contains(num)) {

                // Mark both derivatives as 0.
                computedNum.put(denom, ZERO);
                computedDenom.put(num, ZERO);

                return ZERO;
            }

            System.out.println("DENOM SWITCHED : d" + num + "/d" + denom + " -> d" + denom + "/d" + num);

            // If the denominator is in terms of the numerator, the derivative is the
            // reciprocal of the denominator's derivative with respect to the numerator.
            // For example:
            // y = f(x, t)
            // x is independent
            // dx/dy = 1 / (dy/dx)
            // = 1 / (∂y/∂x + ∂y/∂t * dt/dx)
            // If t is independent of x, then dt/dx = 0 and dx/dy = 1 / (∂y/∂x)

            // f is guaranteed to exist since it has num as a dependency.
            TensorFunction f = inner[denom];
            TensorFunction f_prime = f.totalDiffer(num, inner, dependencies, computed);
            TensorFunction f_prime_recip = ONE.divide(f_prime);
            computedNum.put(denom, f_prime_recip);
            computedDenom.put(num, f_prime);

            return f_prime_recip;
        }

        TensorFunction f = inner[num];

        TensorFunction f_prime = f.totalDiffer(denom, inner, dependencies, computed);
        computedNum.put(denom, f_prime);
        return f_prime;
    }

    /**
     * Generates a differentiation matrix of a given order for an nth degree
     * polynomial.
     * 
     * @param n <code>int</code>: The degree of the polynomial.
     * @param k <code>int</code>: The order of the derivative.
     * @return <code>Matrix</code>: The calculated matrix.
     */
    public static final Matrix differMat(int n, int k) {

        // Assume k fewer rows than degrees + 1.
        int rows = n + 1 - k;

        // Assume 1 more column than degrees.
        int cols = n + 1;

        double[] data = new double[rows * cols];
        for (int i = 0; i < rows; i++) {

            double num = 1.0;

            // Adjust for the compound nature of the kth derivative.
            for (int j = 0; j < k; j++) {

                num *= (i + j + 1);
            }

            // Adjust the horizontal shift.
            data[i * cols + i + k] = num;
        }

        return new Matrix(rows, cols, data);
    }

    /**
     * Calculates the nth integral of this <code>TensorFunction</code> instance with
     * respect to a given mode.
     * 
     * @param mode <code>int</code>: The mode to integrate along.
     * @param n    <code>int</code>: The order of the integral.
     * @return <code>TensorFunction</code>: The calculated function.
     */
    public final TensorFunction integ(int mode, int n) {

        int[] dimensions = coeffs.getDimensions();

        if (mode >= dimensions.length) {

            return null;
        }

        int degree = dimensions[mode] - 1;

        Matrix I = integMat(degree, n);
        return new TensorFunction(Tensor.modeProduct(I, coeffs, mode));
    }

    /**
     * Generates an integration matrix of a given order for an nth degree
     * polynomial.
     * 
     * @param n <code>int</code>: The degree of the polynomial.
     * @param k <code>int</code>: The order of the integral.
     * @return <code>Matrix</code>: The calculated matrix.
     */
    public static final Matrix integMat(int n, int k) {

        // Assume k more rows than degrees + 1.
        int rows = n + 1 + k;

        // Assume 1 more column than degrees.
        int cols = n + 1;

        double[] data = new double[rows * cols];
        for (int i = 0; i < cols; i++) {

            double num = 1.0;

            for (int j = 0; j < k; j++) {

                // Adjust for the compound nature of the kth integral.
                num *= (i + j + 1);
            }

            // Adjust the vertical shift.
            data[i + cols * (i + k)] = 1.0 / num;
        }

        return new Matrix(rows, cols, data);
    }

    /**
     * Retrieves the string representation of this <code>TensorFunction</code>
     * instance.
     * 
     * @return <code>String</code>: The string representation of this
     *         <code>TensorFunction</code> instance in the following form:<br>
     *         <code>Dimensions: [3, 3], Data: [a, b, c, d, e, f, g, h, i]]</code>.
     */
    @Override
    public final String toString() {

        return coeffs.toString();
    }
}
