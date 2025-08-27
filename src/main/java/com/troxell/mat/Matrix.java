package com.troxell.mat;

import java.util.Arrays;

import com.troxell.numbers.MatNumber;
import com.troxell.numbers.Real;

public sealed class Matrix permits Vector {

    private final int rows;

    public final int getRows() {

        return rows;
    }

    private final int cols;

    public final int getCols() {

        return cols;
    }

    private final MatNumber[] data;

    public final MatNumber[] getData() {

        return data.clone();
    }

    public final MatNumber get(int row, int col) {

        return data[row * cols + col];
    }
    
    public Matrix(int rows, int cols) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        Arrays.fill(this.data, MatNumber.ZERO);
    }

    public Matrix(int rows, int cols, double... data) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        for (int i = 0; i < this.data.length; i++) {

            this.data[i] = new Real(data[i]);
        }
    }

    public Matrix(int rows, int cols, MatNumber... data) {

        this.rows = rows;
        this.cols = cols;
        this.data = new MatNumber[rows * cols];
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

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

    public Matrix multiply(double scalar) {

        Real s = new Real(scalar);

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].multiply(s);
        }

        return new Matrix(rows, cols, newData);
    }

    public Matrix divide(double scalar) {

        Real s = new Real(scalar);

        MatNumber[] newData = new MatNumber[data.length];
        for (int i = 0; i < data.length; i++) {

            newData[i] = data[i].divide(s);
        }

        return new Matrix(rows, cols, newData);
    }

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

    public final Vector multiply(Vector vector) {

        Matrix result = multiply((Matrix) vector);
        if (result == null) {

            return null;
        }

        return new Vector(result.data);
    }

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
