package com.troxell.functions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;

public final class VectorFunction extends MatFunction<Vector> {

    private final TensorFunction[] functions;

    public final int size() {

        return functions.length;
    }

    private final int numArgs;

    /**
     * Retrieves the number of arguments in this <code>VectorFunction</code>
     * instance.
     * 
     * @return <code>int</code>: The number of dimensions in the <code>coeffs</code>
     *         field of this <code>VectorFunction</code> instance.
     */
    @Override
    public final int getNumArgs() {

        return numArgs;
    }

    public VectorFunction(TensorFunction... functions) {

        this.functions = functions;

        int length = 0;

        for (TensorFunction f : functions) {

            if (f.getNumArgs() > length) {

                length = f.getNumArgs();
            }
        }

        numArgs = length;
    }

    public static final VectorFunction parse(String definition) {

        String[] components = definition.split("=");
        if (components.length != 2) {

            return null;
        }

        return parse(parseDeclaration(components[0]), components[1]);
    }

    public static final VectorFunction parse(List<String> params, String expression) {

        expression = expression.replace(" ", "");
        if (!expression.startsWith("<") || !expression.endsWith(">")) {

            return null;
        }

        expression = expression.substring(1, expression.length() - 1);
        TensorFunction[] functions = Arrays.stream(expression.split(",")).map(s -> TensorFunction.parse(params, s))
                .toArray(TensorFunction[]::new);

        return new VectorFunction(functions);
    }

    @Override
    public final Vector apply(double... args) {

        MatNumber[] vals = new MatNumber[functions.length];
        for (int i = 0; i < functions.length; i++) {

            vals[i] = functions[i].apply(args);
        }

        return new Vector(vals);
    }

    @Override
    public final Vector apply(MatNumber... args) {

        MatNumber[] vals = new MatNumber[functions.length];
        for (int i = 0; i < functions.length; i++) {

            vals[i] = functions[i].apply(args);
        }

        return new Vector(vals);
    }

    @Override
    public final Vector apply(Vector args) {

        MatNumber[] vals = new MatNumber[functions.length];
        for (int i = 0; i < functions.length; i++) {

            vals[i] = functions[i].apply(args);
        }

        return new Vector(vals);
    }

    @Override
    public final VectorFunction differ(int mode, int n) {

        TensorFunction[] primes = new TensorFunction[size()];
        for (int i = 0; i < primes.length; i++) {

            primes[i] = functions[i].differ(mode, n);
        }

        return new VectorFunction(primes);
    }

    @Override
    public final VectorFunction integ(int mode, int n) {

        TensorFunction[] primes = new TensorFunction[size()];
        for (int i = 0; i < primes.length; i++) {

            primes[i] = functions[i].integ(mode, n);
        }

        return new VectorFunction(primes);
    }

    public final TensorFunction dot(VectorFunction f) {

        TensorFunction sum = TensorFunction.ZERO;

        for (int i = 0; i < functions.length && i < f.functions.length; i++) {

            if (functions[i] == TensorFunction.ZERO || f.functions[i] == TensorFunction.ZERO) {

                continue;
            }

            sum = sum.add(functions[i].multiply(f.functions[i]));
        }

        return sum;
    }

    @Override
    public final String toString() {

        return "<" + Arrays.stream(functions).map(TensorFunction::toString).collect(Collectors.joining(", ")) + ">";
    }

    @Override
    public MatFunction<Vector> multiply(TensorFunction function) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'multiply'");
    }

    @Override
    public MatFunction<Vector> multiply(double scalar) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'multiply'");
    }

    @Override
    public MatFunction<Vector> multiply(MatNumber scalar) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'multiply'");
    }

    @Override
    public MatFunction<Vector> divide(TensorFunction function) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'divide'");
    }

    @Override
    public MatFunction<Vector> divide(double scalar) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'divide'");
    }

    @Override
    public MatFunction<Vector> divide(MatNumber scalar) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'divide'");
    }
}
