package com.troxell.mat;

import java.util.Arrays;

import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

/**
 * <code>Tensor</code>: A class representing a tensor of n dimensions.
 */
public final class Tensor {

    /**
     * <code>int[]</code>: The dimensions of this <code>Tensor</code> instance.
     */
    private final int[] dimensions;

    public final int getNumDimensions() {

        return dimensions.length;
    }

    public final int getDimension(int i) {

        return dimensions[i];
    }

    /**
     * Retrieves the dimensions of this <code>Tensor</code> instance.
     * 
     * @return <code>int[]</code>: A copy of the
     *         <code>dimensions/code> field of this <code>Tensor</code> instance.
     */
    public final int[] getDimensions() {

        return dimensions.clone();
    }

    /**
     * <code>MatNumber[]</code>: The data contained within this <code>Tensor</code>
     * instance.
     */
    private final MatNumber[] data;

    public final MatNumber get(int i) {

        return data[i];
    }

    public final MatNumber get(int[] indices) {

        return get(index(indices, dimensions));
    }

    /**
     * Creates a new instance of the <code>Tensor</code> class.
     * 
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>Tensor</code> instance.
     */
    public Tensor(int[] dimensions) {

        this.dimensions = dimensions.clone();
        data = new MatNumber[product(dimensions)];
        Arrays.fill(data, MatNumber.ZERO);
    }

    /**
     * Creates a new instance of the <code>Tensor</code> class.
     * 
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>Tensor</code> instance.
     * @param data       <code>double...</code>: The data contained within this
     *                   <code>Tensor</code>
     */
    public Tensor(int[] dimensions, double... data) {

        this(dimensions);
        for (int i = 0; i < data.length; i++) {

            this.data[i] = new Real(data[i]);
        }
    }

    /**
     * Creates a new instance of the <code>Tensor</code> class.
     * 
     * @param dimensions <code>int[]</code>: The dimensions of this
     *                   <code>Tensor</code> instance.
     * @param data       <code>MatNumber...</code>: The data contained within this
     *                   <code>Tensor</code>
     */
    public Tensor(int[] dimensions, MatNumber... data) {

        this(dimensions);
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * Adds another tensor to this <code>Tensor</code> instance, resizing the
     * resulting dimensions to fit both.
     * 
     * @param tensor <code>Tensor</code>: The tensor to add.
     * @return <code>Tensor</code>: The calculated tensor.
     */
    public final Tensor add(Tensor tensor) {

        int[] newDims = fit(this, tensor);

        tensor = tensor.resize(newDims);

        MatNumber[] newData = resize(newDims).data;
        for (int i = 0; i < newData.length; i++) {

            newData[i] = newData[i].add(tensor.data[i]);
        }

        return new Tensor(newDims, newData);
    }

    /**
     * Subtracts another tensor from this <code>Tensor</code> instance, resizing the
     * resulting dimensions to fit both.
     * 
     * @param tensor <code>Tensor</code>: The tensor to subtract.
     * @return <code>Tensor</code>: The calculated tensor.
     */
    public final Tensor subtract(Tensor tensor) {

        int[] newDims = fit(this, tensor);

        tensor = tensor.resize(newDims);

        MatNumber[] newData = resize(newDims).data;
        for (int i = 0; i < newData.length; i++) {

            newData[i] = newData[i].subtract(tensor.data[i]);
        }

        return new Tensor(newDims, newData);
    }

    public final Tensor multiply(MatNumber scalar) {

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < newData.length; i++) {

            newData[i] = data[i].multiply(scalar);
        }

        return new Tensor(dimensions, newData);
    }

    public final Tensor divide(MatNumber scalar) {

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < newData.length; i++) {

            newData[i] = data[i].divide(scalar);
        }

        return new Tensor(dimensions, newData);
    }

    /**
     * Calculates the smallest possible dimensions which can fit the non-zero
     * (<code>0.0</code>) values of this <code>Tensor</code> instance.
     * 
     * @return <code>int[]</code>: The calculated dimensions.
     */
    public final int[] condense() {

        // Find min and max indices for each dimension where data is non-zero.
        int n = dimensions.length;
        int[] newDims = new int[n];

        int[] indices = new int[n];
        for (int flat = 0; flat < data.length; flat++) {

            // Skip over zero values.
            if (data[flat].equals(MatNumber.ZERO)) {

                continue;
            }

            // Map each flat-dimensional index to a multi-dimensional index.
            int rem = flat;
            for (int d = 0; d < n; d++) {
                indices[d] = rem % dimensions[d];
                if (indices[d] >= newDims[d]) {

                    newDims[d] = indices[d] + 1;
                }

                rem /= dimensions[d];
            }
        }

        return newDims;
    }

    /**
     * Calculates the smallest possible dimensions which can fit both the
     * <code>t1</code> parameter and the <code>t2</code> parameter.
     * 
     * @param t1 <code>Tensor</code>: The first tensor.
     * @param t2 <code>Tensor</code>: The second tensor.
     * @return <code>int[]</code>: The calculated dimensions.
     */
    public static final int[] fit(Tensor t1, Tensor t2) {

        int[] newDims = new int[Math.max(t1.dimensions.length, t2.dimensions.length)];

        System.arraycopy(t1.dimensions, 0, newDims, 0, t1.dimensions.length);

        for (int i = 0; i < t2.dimensions.length; i++) {

            if (newDims[i] < t2.dimensions[i]) {

                newDims[i] = t2.dimensions[i];
            }
        }

        return newDims;
    }

    /**
     * Resizes this <code>Tensor</code> instance to a new set of dimensions.
     * 
     * @param newDims <code>int[]</code>: The new dimensions to resize to.
     * @return <code>Tensor</code>: The calculated tensor.
     */
    public final Tensor resize(int[] newDims) {

        Tensor t = this;
        if (Arrays.equals(dimensions, newDims)) {

            return t;
        }

        if (newDims.length > dimensions.length) {

            t = t.expand(Arrays.copyOfRange(newDims, dimensions.length, newDims.length));
        }

        for (int i = 0; i < newDims.length; i++) {

            if (t.dimensions[i] == newDims[i]) {

                continue;
            }

            Matrix m = createResizeMatrix(t.dimensions[i], newDims[i], 0);
            t = modeProduct(m, t, i);
        }

        return t;
    }

    /**
     * Resizes this <code>Tensor</code> instance to a new set of dimensions and
     * shifts it by a fixed value in each dimension.
     * 
     * @param newDims <code>int[]</code>: The new dimensions to resize to.
     * @param offset  <code>int[]</code>: The offsets to apply.
     * @return <code>Tensor</code>: The calculated tensor.
     */
    public final Tensor resize(int[] newDims, int[] offset) {

        Tensor t = this;
        if (newDims.length > dimensions.length) {

            t = t.expand(Arrays.copyOfRange(newDims, dimensions.length, newDims.length));
        }

        for (int i = 0; i < newDims.length; i++) {

            if (t.dimensions[i] == newDims[i] && offset[i] == 0) {

                continue;
            }

            Matrix m = createResizeMatrix(t.dimensions[i], newDims[i], offset[i]);
            t = modeProduct(m, t, 0); // TODO
        }

        return t;
    }

    /**
     * Expands this <code>Tensor</code> instance through a set number of new
     * dimensions.
     * 
     * @param nextDims <code>int...</code>: The next dimensions of this
     *                 <code>Tensor</code> instance.
     * @return <code>Tensor</code>: The expanded tensor.
     */
    public final Tensor expand(int... nextDims) {

        int newSize = data.length * product(nextDims);

        int[] dims = new int[dimensions.length + nextDims.length];
        System.arraycopy(dimensions, 0, dims, 0, dimensions.length);
        System.arraycopy(nextDims, 0, dims, dimensions.length, nextDims.length);

        MatNumber[] newData = new MatNumber[newSize];
        if (newSize == 0) {

            return new Tensor(dims, newData);
        }

        Arrays.fill(newData, data.length, newSize, MatNumber.ZERO);
        System.arraycopy(data, 0, newData, 0, data.length);

        return new Tensor(dims, newData);
    }

    /**
     * Creates a new <code>Matrix</code> instance which, when applied through matrix
     * multiplication, rescales a vector to a new fixed length with a set number of
     * empty (<code>0.0</code>) cells before the first copied value.
     * 
     * @param oldSize <code>int</code>: The size of the initial vector.
     * @param newSize <code>int</code>: The overall size of the new vector.
     * @param before  <code>int</code>: The number of empty cells to include before
     *                the first copied value.
     * @return <code>Matrix</code>: The calculated transformation matrix.
     */
    public final Matrix createResizeMatrix(int oldSize, int newSize, int before) {

        double[] newData = new double[newSize * oldSize];
        for (int i = 0; i < oldSize && i < newSize - before; i++) {

            newData[i * (oldSize) + before * oldSize + i] = 1.0;
        }

        return new Matrix(newSize, oldSize, newData);
    }

    /**
     * Calculates the dot product between this <code>Tensor</code> instance and
     * another of equal dimensions. This is an nth-dimensional equivalent to the dot
     * product of vectors, wherein the products between cells in this
     * <code>Tensor</code> instance and their corresponding cells in the
     * <code>tensor</code> parameter are summed over.
     * 
     * @param tensor <code>Tensor</code>: The tensor to multiply by.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public final MatNumber innerProduct(Tensor tensor) {

        if (!Arrays.equals(dimensions, tensor.dimensions)) {

            return null;
        }

        MatNumber sum = MatNumber.ZERO;
        for (int i = 0; i < data.length; i++) {

            sum = sum.add(data[i].multiply(tensor.data[i]));
        }

        return sum;
    }

    /**
     * Calculates the mode product between a <code>Matrix</code> instance and a
     * <code>Tensor</code> instance along a specified mode. This represents the
     * matrix multiplication of each strand in the <code>tensor</code> parameeter
     * along the <code>mode</code> parameter dimension.
     * 
     * @param matrix <code>Matrix</code>: The matrix to multiply.
     * @param tensor <code>Tensor</code>: The tensor to multiply by.
     * @param mode   <code>int</code>: The mode to multiply along.
     * @return <code>Tensor</code>: The resulting tensor.
     */
    public static final Tensor modeProduct(Matrix matrix, Tensor tensor, int mode) {

        if (mode < 0 || mode >= tensor.dimensions.length) {

            return null;
        }

        int M = tensor.dimensions[mode];
        if (M != matrix.getCols()) {

            return null;
        }

        int L = matrix.getRows();

        int[] newDimensions = tensor.dimensions.clone();
        newDimensions[mode] = L;

        MatNumber[] resultData = new MatNumber[product(newDimensions)];
        Arrays.fill(resultData, MatNumber.ZERO);

        int[] indices = new int[tensor.dimensions.length];
        tensor.iterateMode(0, indices, mode, ind -> {

            for (int l = 0; l < L; l++) {

                MatNumber sum = MatNumber.ZERO;
                for (int m = 0; m < M; m++) {

                    ind[mode] = m;
                    sum = sum.add(tensor.data[index(ind, tensor.dimensions)].multiply(matrix.get(l, m)));
                }

                ind[mode] = l;
                resultData[index(ind, newDimensions)] = sum;
            }
        });

        return new Tensor(newDimensions, resultData);
    }

    /**
     * <code>IndexConsumer</code>: A functional interface used to perform an
     * operation on each index of this <code>Tensor</code> instance.
     */
    @FunctionalInterface
    private interface IndexConsumer {

        void accept(int[] indices);
    }

    /**
     * Iterates along each dimension of this <code>Tensor</code> instance except for
     * the provided mode.
     * 
     * @param dim      <code>int</code>: The current dimension.
     * @param indices  <code>int[]</code>: The current indices.
     * @param mode     <code>int</code>: The dimension to skip.
     * @param consumer <code>IndexConsumer</code>: The function to call at the end
     *                 of each iteration.
     */
    private void iterateMode(int dim, int[] indices, int mode, IndexConsumer consumer) {

        if (dim >= dimensions.length) {

            consumer.accept(indices);
            return;
        }

        if (dim == mode) {

            iterateMode(dim + 1, indices, mode, consumer);
            return;
        }

        for (int i = 0; i < dimensions[dim]; i++) {

            indices[dim] = i;
            iterateMode(dim + 1, indices, mode, consumer);
        }
    }

    /**
     * Calculates the product of a list of integers.
     * 
     * @param v <code>int[]</code>: The list of integers whose product to calculate.
     * @return <code>int</code>: The calculated number.
     */
    public static int product(int[] v) {

        int i = 1;

        for (int n : v) {

            i *= n;
        }

        return i;
    }

    /**
     * Calculates the net index from a list of indices and given dimensions.
     * 
     * @param indices    <code>int[]</code>: The indices to calculate from.
     * @param dimensions <code>int[]</code>: The dimensions to calculate from.
     * @return <code>int</code>: The calculated index.
     */
    public static final int index(int[] indices, int[] dimensions) {

        int index = 0;
        int factor = 1;

        for (int i = 0; i < dimensions.length && i < indices.length; i++) {

            index += indices[i] * factor;
            factor *= dimensions[i];
        }

        return index;
    }

    /**
     * Retrieves the string representation of this <code>Tensor</code> instance.
     * 
     * @return <code>String</code>: The string representation of this
     *         <code>Tensor</code> instance in the following form:<br>
     *         <code>Dimensions: [3, 3], Data: [a, b, c, d, e, f, g, h, i]]</code>.
     */
    @Override
    public final String toString() {

        return "Dimensions: " + Arrays.toString(dimensions) + ", Data: " + Arrays.toString(data);
    }
}
