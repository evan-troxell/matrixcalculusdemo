package com.troxell.mat;

import com.troxell.numbers.MatNumber;

/**
 * <code>Vector</code>: A class representing a mathematical vector of dimension
 * n.
 */
public final class Vector extends Matrix {

    /**
     * Retrieves the size of this <code>Vector</code> instance.
     * 
     * @return <code>int<code>: The number of rows of the matrix backing this <code>Vector</code>
     *         instance.
     */
    public final int getSize() {

        return getRows();
    }

    /**
     * Retrieves a value from this <code>Vector</code> instance.
     * 
     * @param index <code>int</code>: The index to retrieve from.
     * @return <code>MatNumber</code>: The retrieved number.
     */
    public final MatNumber get(int index) {

        return get(index, 0);
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * 
     * @param size <code>int</code>: The size of this <code>Vector</code> instance.
     */
    public Vector(int size) {

        super(size, 1);
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * 
     * @param data <code>double...</code>: The data of this <code>Vector</code>
     *             instance.
     */
    public Vector(double... data) {

        super(data.length, 1, data);
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * 
     * @param data <code>MatNumber...</code>: The data of this <code>Vector</code>
     *             instance.
     */
    public Vector(MatNumber... data) {

        super(data.length, 1, data);
    }

    /**
     * Calculates the sum of this <code>Vector</code> instance and another.
     * 
     * @param vector <code>Vector</code>: The vector to add.
     * @return <code>Vector</code>: The resulting vector.
     */
    public final Vector add(Vector vector) {

        Matrix matrix = super.add(vector);
        if (matrix == null) {

            return null;
        }

        return new Vector(matrix.getData());
    }

    /**
     * Calculates the difference between this <code>Vector</code> instance and
     * another.
     * 
     * @param vector <code>Vector</code>: The vector to subtract.
     * @return <code>Vector</code>: The resulting vector.
     */
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

    /**
     * Calculates the dot product between this <code>Vector</code> instance and
     * another.
     * 
     * @param vector <code>Vector</code>: The vector to multiply.
     * @return <code>MatNumber</code>: The resulting number.
     */
    public final MatNumber dot(Vector vector) {

        return transpose().multiply(vector).get(0, 0);
    }
}