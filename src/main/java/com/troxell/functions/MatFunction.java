package com.troxell.functions;

import java.util.List;

import com.troxell.MatContext;
import com.troxell.mat.Vector;
import com.troxell.numbers.MatNumber;

public abstract class MatFunction<K> {

    protected static final String LEADING_PARAM_CHAR_PATTERN = "[A-Za-z]";

    protected static final String TRAILING_PARAM_CHAR_PATTERN = "[A-Za-z0-9]";

    protected static final String PARAM_PATTERN = LEADING_PARAM_CHAR_PATTERN + TRAILING_PARAM_CHAR_PATTERN + '*';

    protected static final String PARAM_DECLARATION_PATTERN = PARAM_PATTERN + "(?:," + PARAM_PATTERN + ")*";

    public abstract int getNumArgs();

    public abstract K apply(double... args);

    public abstract K apply(MatNumber... args);

    public abstract K apply(Vector args);

    public abstract MatFunction<K> multiply(TensorFunction function);

    public abstract MatFunction<K> multiply(double scalar);

    public abstract MatFunction<K> multiply(MatNumber scalar);

    public abstract MatFunction<K> divide(TensorFunction function);

    public abstract MatFunction<K> divide(double scalar);

    public abstract MatFunction<K> divide(MatNumber scalar);

    public abstract MatFunction<K> differ(int mode, int n);

    public abstract MatFunction<K> integ(int mode, int n);

    @Override
    public abstract String toString();

    public final String toLaTeXString() {

        return toLaTeX(toString());
    }

    public final String toString(String name) {

        return header(name, getNumArgs()) + " = " + toString();
    }

    public final String toLaTeXString(String name) {

        return toLaTeX(toString(name));
    }

    private static String header(String name, int vars) {

        return name + "(" + String.join(", ", MatContext.getVars(vars)) + ")";
    }

    private static String toLaTeX(String s) {

        s = s.replaceAll("\\^(\\d+)", "^{$1}");
        s = s.replace("(", "\\left(");
        s = s.replace(")", "\\right)");

        return s;
    }

    public static final List<String> parseDeclaration(String declaration) {

        declaration = declaration.replace(" ", "");
        if (!declaration.contains("(") || !declaration.endsWith(")")) {

            return null;
        }
        String params = declaration.substring(declaration.indexOf('(') + 1, declaration.length() - 1);

        if (!params.matches(PARAM_DECLARATION_PATTERN)) {

            return null;
        }

        return List.of(params.split(","));
    }
}
