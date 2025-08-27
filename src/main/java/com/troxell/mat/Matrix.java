package com.troxell.mat;

import java.util.Arrays;

import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

/**
 * <code>Matrix</code>: A class representing a mathematical matrix of dimensions
 * n*m.
 */
public sealed class Matrix permits Vector {

    /**
     * <code>int</code>: The number of rows in this <code>Matrix</code> instance.
     */
    private final int rows;

    /**
     * Retrieves the number of rows in this <code>Matrix</code> instance.
     * 
     * @return <code>int</code>: The <code>rows</code> field of this
     *         <code>Matrix</code> instance.
     */
    public final int getRows() {

        return rows;
    }

    /**
     * <code>int</code>: The number of columns in this <code>Matrix</code> instance.
     */
    private final int cols;

    /**
     * Retrieves the number of columns in this <code>Matrix</code> instance.
     * 
     * @return <code>int</code>: The <code>cols</code> field of this
     *         <code>Matrix</code> instance.
     */
    public final int getCols() {

        return cols;
    }

    /**
     * <code>MatNumber[]</code>: The data contained within this <code>Matrix</code>
     * instance.
     */
    private final MatNumber[] data;

    /**
     * Retrieves the data contained within this <code>Matrix</code> instance.
     * 
     * @return <code>MatNumber[]</code>: A copy of the <code>data</code> field of
     *         this <code>Matrix</code> instance.
     */
    public final MatNumber[] getData() {

        return data.clone();
    }

    /**
     * Retrieves a value from this <code>Matrix</code> instance.
     * 
     * @param row <code>int</code>: The row to retrieve from.
     * @param col <code>int</code>: The column to retrieve from.
     * @return <code>MatNumber</code>: The retrieved number.
     */
    public final MatNumber get(int row, int col) {

        return data[row * cols + col];
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class.
     * 
     * @param rows <code>int</code>: The number of rows in this <code>Matrix</code>
     *             instance.
     * @param cols <code>int</code>: The number of columns in this
     *             <code>Matrix</code> instance.
     */
    public Matrix(int rows, int cols) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        Arrays.fill(this.data, MatNumber.ZERO);
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class.
     * 
     * @param rows <code>int</code>: The number of rows in this <code>Matrix</code>
     *             instance.
     * @param cols <code>int</code>: The number of columns in this
     *             <code>Matrix</code> instance.
     * @param data <code>double...</code>: The data contained within this
     *             <code>Matrix</code> instance.
     */
    public Matrix(int rows, int cols, double... data) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        for (int i = 0; i < this.data.length; i++) {

            this.data[i] = new Real(data[i]);
        }
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class.
     * 
     * @param rows <code>int</code>: The number of rows in this <code>Matrix</code>
     *             instance.
     * @param cols <code>int</code>: The number of columns in this
     *             <code>Matrix</code> instance.
     * @param data <code>MatNumber...</code>: The data contained within this
     *             <code>Matrix</code> instance.
     */
    public Matrix(int rows, int cols, MatNumber... data) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

    /**
     * Calculates the sum of this <code>Matrix</code> instance and another.
     * 
     * @param matrix <code>Matrix</code>: The matrix to add.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public final Matrix add(Matrix matrix) {

        if (rows != matrix.rows || cols != matrix.cols) {

            return null;
        }

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].add(matrix.data[i]);
        }

        return new Matrix(rows, cols, newData);
    }

    /**
     * Calculates the difference between this <code>Matrix</code> instance and
     * another.
     * 
     * @param matrix <code>Matrix</code>: The matrix to subtract.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public final Matrix subtract(Matrix matrix) {

        if (rows != matrix.rows || cols != matrix.cols) {

            return null;
        }

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].subtract(matrix.data[i]);
        }

        return new Matrix(rows, cols, newData);
    }

    /**
     * Calculates the product between this <code>Matrix</code> instance and a
     * scalar.
     * 
     * @param scalar <code>double</code>: The scalar to multiply by.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public Matrix multiply(double scalar) {

        Real s = new Real(scalar);

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].multiply(s);
        }

        return new Matrix(rows, cols, newData);
    }

    /**
     * Calculates the quotient between this <code>Matrix</code> instance and a
     * scalar.
     * 
     * @param scalar <code>double</code>: The scalar to divide by.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public Matrix divide(double scalar) {

        Real s = new Real(scalar);

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].divide(s);
        }

        return new Matrix(rows, cols, newData);
    }

    /**
     * Calculates the product between this <code>Matrix</code> instance and another.
     * 
     * @param matrix <code>Matrix</code>: The matrix to multiply by.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public final Matrix multiply(Matrix matrix) {

        if (cols != matrix.rows) {

            return null;
        }

        int newRows = rows;
        int newCols = matrix.cols;

        int dim = cols;

        MatNumber[] newData = new MatNumber[newRows * newCols];
        int index = 0;

        for (int r = 0; r < newRows; r++) {

            for (int c = 0; c < newCols; c++) {

                MatNumber sum = MatNumber.ZERO;
                for (int i = 0; i < dim; i++) {

                    sum = sum.add(get(r, i).multiply(matrix.get(i, c)));
                }
                newData[index] = sum;
                index++;
            }
        }

        return new Matrix(newRows, newCols, newData);
    }

    /**
     * Calculates the transpose of this <code>Matrix</code> instance.
     * 
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public final Matrix transpose() {

        MatNumber[] newData = new MatNumber[data.length];
        int index = 0;

        for (int c = 0; c < cols; c++) {

            for (int r = 0; r < rows; r++) {

                newData[index] = get(r, c);
                index++;
            }
        }

        return new Matrix(cols, rows, newData);
    }

    /**
     * Calculates the product between this <code>Matrix</code> instance and a
     * vector.
     * 
     * @param vector <code>Vector</code>: The vector to multiply.
     * @return <code>Vector</code>: The resulting vector.
     */
    public final Vector multiply(Vector vector) {

        Matrix result = multiply((Matrix) vector);
        if (result == null) {

            return null;
        }

        return new Vector(result.data);
    }

    /**
     * Retrieves the string representation of this <code>Matrix</code> instance.
     * 
     * @return <code>String</code>: The string representation of this
     *         <code>Matrix</code> instance in the following form:<br>
     *         <code><pre>[[a, b, c]
     * [d, e, f]
     * [g, h, i]]
     * </pre></code>
     */
    @Override
    public final String toString() {

        String s = "[";

        for (int r = 0; r < rows; r++) {

            if (r > 0) {

                s += " ";
            }
            s += "[";
            for (int c = 0; c < cols; c++) {

                if (c > 0) {

                    s += ", ";
                }
                s += get(r, c);
            }
            s += "]";
            if (r < rows - 1) {

                s += "\n";
            }
        }

        return s + "]";
    }
}
