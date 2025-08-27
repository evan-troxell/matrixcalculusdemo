package com.troxell;

import com.troxell.functions.TensorFunction;

public final class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        
        TensorFunction f = new TensorFunction(TensorFunction.DERIVATIVES, new int[] {10, 10}, new double[] {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        });


        // [0, 0, 0]
        // [0, 0, 1]


        System.out.println(f.differ(0, 9));
        System.out.println(f.differ(1, 9));
        System.out.println(f.differ(0, 9).differ(1, 9));
    }
}