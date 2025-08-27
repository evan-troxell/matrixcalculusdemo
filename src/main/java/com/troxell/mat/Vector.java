package com.troxell.mat;

import com.troxell.numbers.MatNumber;

public final class Vector extends Matrix {

    public final int getSize() {

        return getRows();
    }

    public final MatNumber get(int index) {

        return get(index, 0);
    }

    public Vector(int size) {

        super(size, 1);
    }

    public Vector(double... data) {

        super(data.length, 1, data);
    }

    public Vector(MatNumber... data) {

        super(data.length, 1, data);
    }

    public final Vector add(Vector vector) {

        Matrix matrix = super.add(vector);
        if (matrix == null) {

            return null;
        }

        return new Vector(matrix.getData());
    }

    public final Vector subtract(Vector vector) {

        Matrix matrix = super.subtract(vector);
        if (matrix == null) {

            return null;
        }

        return new Vector(matrix.getData());
    }

    @Override
    public final Vector multiply(double scalar) {

        Matrix matrix = super.multiply(scalar);
        if (matrix == null) {

            return null;
        }

        return new Vector(matrix.getData());
    }

    @Override
    public final Vector divide(double scalar) {

        Matrix matrix = super.divide(scalar);
        if (matrix == null) {

            return null;
        }

        return new Vector(matrix.getData());
    }

    public final MatNumber dot(Vector vector) {

        return transpose().multiply(vector).get(0, 0);
    }

    public final Vector reverse() {

        MatNumber[] newData = getData();
        MatNumber inter;
        int len = newData.length;
        for (int i = 0; i < len / 2; i++) {

            inter = newData[i];
            newData[i] = newData[len - i - 1];
            newData[len - i - 1] = inter;
        }

        return new Vector(newData);
    }
}