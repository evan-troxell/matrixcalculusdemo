package com.troxell;

import com.troxell.functions.TensorFunction;

/**
 * <code>Main</code>: The origin of execution of the program.
 */
public final class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        // TensorFunction f = new TensorFunction(TensorFunction.DERIVATIVES, new int[] {
        // 10, 10 }, new double[] {
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        // });

        // // [0, 0, 0]
        // // [0, 0, 1]

        // System.out.println(f.differ(0, 9));
        // System.out.println(f.differ(1, 9));
        // System.out.println(f.differ(0, 9).differ(1, 9));
        // System.out.println(new Matrix(3, 3, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        TensorFunction f = new TensorFunction(TensorFunction.DERIVATIVES, new int[] { 25 },
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        TensorFunction g = new TensorFunction(TensorFunction.COEFFICIENTS, new int[] { 1 },
                1);

        TensorFunction h = f.compose(g, 0);

        System.out.println(h);
        System.out.println(h.apply(2));
    }
}