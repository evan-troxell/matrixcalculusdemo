package com.troxell;

import com.troxell.functions.TensorFunction;

/**
 * <code>Main</code>: The origin of execution of the program.
 */
public final class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        // ///// w = f(x, y, t) = xyt
        // TensorFunction w = new TensorFunction(new int[] { 2, 2, 1, 2 },
        // 0, 0,
        // 0, 0,

        // 0, 0,
        // 0, 1);
        // System.out.println("w(x=1, y=2, t=3) = " + w.apply(1, 2, 0, 3));

        // ///// x = x(y, z) = yz
        // TensorFunction x = new TensorFunction(new int[] { 1, 2, 2 },
        // 0, 0,
        // 0, 1);
        // System.out.println("x(y=2, z=3) = " + x.apply(0, 2, 3));

        // ///// y = y(t) = t
        // TensorFunction y = new TensorFunction(new int[] { 1, 1, 1, 2 },
        // 0, 1);
        // System.out.println("y(t=3) = " + y.apply(0, 0, 0, 3));

        // ///// z = z(t) = t
        // TensorFunction z = new TensorFunction(new int[] { 1, 1, 1, 2 },
        // 0, 1);
        // System.out.println("z(t=3) = " + z.apply(0, 0, 0, 3));

        // ///// dw/dt|x=114, y=22, z=342, t=43| = 351754.0
        // TensorFunction dw_dt = w.totalDiffer(3, x, y, z);
        // System.out.println(dw_dt.apply(114, 22, 342, 43));

        // System.out.println(dw_dt.toString("dw_dt", "x", "y", "z", "t"));
        // System.out.println(dw_dt.gradient().toString("dw_dt", "x", "y", "z", "t"));

        // TensorFunction w = TensorFunction
        // .parse("f(x, y, z) = x^2 + y^2 + z^2 - xyz");
        // System.out.println(w.toString("f"));
        // System.out.println(w.gradient());
        // System.out.println(f.apply(a));
        // TensorFunction x = TensorFunction.parse("bob(x, y, z)=y*y+z");
        // TensorFunction y = TensorFunction.parse("fred(x,y,z)=z");
        // TensorFunction z = null;
        // System.out.println(f.toString("f", "x", "y", "z"));
        // System.out.println();
        TensorFunction f = TensorFunction.pow(TensorFunction.parse("f(x, y) = x + y"), 1.0 / 3, 20, 5);
        System.out.println(f.toLaTeXString("G"));
    }
}