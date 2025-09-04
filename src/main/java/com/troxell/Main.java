package com.troxell;

import com.troxell.functions.TensorFunction;

/**
 * <code>Main</code>: The origin of execution of the program.
 */
public final class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        // w = f(x, y, t) = xyt
        TensorFunction w = new TensorFunction(new int[] { 2, 2, 1, 2 },
                0, 0,
                0, 0,

                0, 0,
                0, 1);
        System.out.println("w(x=1, y=2, t=3) = " + w.apply(1, 2, 0, 3));

        // x = x(y, z) = yz
        TensorFunction x = new TensorFunction(new int[] { 1, 2, 2 },
                0, 0,
                0, 1);
        System.out.println("x(y=2, z=3) = " + x.apply(0, 2, 3));

        // y = y(t) = t
        TensorFunction y = new TensorFunction(new int[] { 1, 1, 1, 2 },
                0, 1);
        System.out.println("y(t=3) = " + y.apply(0, 0, 0, 3));

        // z = z(t) = t
        TensorFunction z = new TensorFunction(new int[] { 1, 1, 1, 2 },
                0, 1);
        System.out.println("z(t=3) = " + z.apply(0, 0, 0, 3));

        // dw/dt
        TensorFunction dw_dt = w.totalDiffer(3, x, y, z);
        System.out.println(dw_dt.apply(114, 22, 342, 43));
    }
}