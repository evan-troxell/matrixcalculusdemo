package com.troxell.mat;

import java.util.Arrays;

import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

public final class Tensor {

    private final int[] dimensions;

    public final int[] getDimensions() {

        return dimensions.clone();
    }

    private final MatNumber data[];

    public Tensor(int[] dimensions) {

        this.dimensions = dimensions.clone();
        data = new MatNumber[product(dimensions)];
        Arrays.fill(data, MatNumber.ZERO);
    }

    public Tensor(int[] dimensions, double... data) {

        this(dimensions);
        for (int i = 0; i < data.length; i++) {

            this.data[i] = new Real(data[i]);
        }
    }

    public Tensor(int[] dimensions, MatNumber... data) {

        this(dimensions);
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

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
        tensor.iterate(0, indices, mode, ind -> {

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

    @FunctionalInterface
    private interface IndexConsumer {

        void accept(int[] indices);
    }

    private void iterate(int dim, int[] indices, int skip, IndexConsumer consumer) {

        if (dim >= dimensions.length) {

            consumer.accept(indices);
            return;
        }

        if (dim == skip) {

            iterate(dim + 1, indices, skip, consumer);
            return;
        }

        for (int i = 0; i < dimensions[dim]; i++) {

            indices[dim] = i;
            iterate(dim + 1, indices, skip, consumer);
        }
    }

    public static int product(int[] v) {

        int i = 1;

        for (int n : v) {

            i *= n;
        }

        return i;
    }

    public static final int index(int[] indices, int[] dimensions) {

        int index = 0;
        int factor = 1;

        // [5, 3, 2]
        // [a, b, c]

        // a + 5b + 15c

        for (int i = 0; i < dimensions.length; i++) {

            index += indices[i] * factor;
            factor *= dimensions[i];
        }

        return index;
    }

    @Override
    public final String toString() {

        return "Tensor(dimensions=" + Arrays.toString(dimensions) + ", data=" + Arrays.toString(data) + ")";
    }
}
