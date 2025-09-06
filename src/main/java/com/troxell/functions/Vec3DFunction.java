package com.troxell.functions;

import java.util.Arrays;
import java.util.List;

import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;

public final class Vec3DFunction extends MatFunction<Vector> {

    private final TensorFunction x;

    private final TensorFunction y;

    private final TensorFunction z;

    private final int numArgs;

    @Override
    public final int getNumArgs() {

        return numArgs;
    }

    public Vec3DFunction(TensorFunction x, TensorFunction y, TensorFunction z) {

        this.x = x;
        this.y = y;
        this.z = z;

        numArgs = Math.max(x.getNumArgs(), Math.max(y.getNumArgs(), z.getNumArgs()));
    }

    public static final Vec3DFunction parse(String definition) {

        String[] components = definition.split("=");
        if (components.length != 2) {

            return null;
        }

        return parse(parseDeclaration(components[0]), components[1]);
    }

    public static final Vec3DFunction parse(List<String> params, String expression) {

        expression = expression.replace(" ", "");
        if (!expression.startsWith("<") || !expression.endsWith(">")) {

            return null;
        }

        expression = expression.substring(1, expression.length() - 1);
        TensorFunction[] functions = Arrays.stream(expression.split(",")).map(s -> TensorFunction.parse(params, s))
                .toArray(TensorFunction[]::new);

        if (functions.length != 3) {

            return null;
        }

        return new Vec3DFunction(functions[0], functions[1], functions[2]);
    }

    @Override
    public final Vector apply(double... args) {

        return new Vector(x.apply(args), y.apply(args), z.apply(args));
    }

    @Override
    public final Vector apply(MatNumber... args) {

        return new Vector(x.apply(args), y.apply(args), z.apply(args));
    }

    @Override
    public final Vector apply(Vector args) {

        return new Vector(x.apply(args), y.apply(args), z.apply(args));
    }

    @Override
    public final Vec3DFunction differ(int mode, int n) {

        return new Vec3DFunction(x.differ(mode, n), y.differ(mode, n), z.differ(mode, n));
    }

    @Override
    public final Vec3DFunction integ(int mode, int n) {

        return new Vec3DFunction(x.integ(mode, n), y.integ(mode, n), z.integ(mode, n));
    }

    public final TensorFunction dot(Vec3DFunction f) {

        return x.multiply(f.x).add(y.multiply(f.y)).add(z.multiply(f.z));
    }

    @Override
    public final String toString() {

        return "<" + x + ", " + y + ", " + z + ">";
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
