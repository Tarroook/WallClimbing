package net.tarook.utils;

import java.util.function.DoubleUnaryOperator;

public enum Easing {
    LINEAR(t -> t),
    QUAD_IN(t -> t * t),
    QUAD_OUT(t -> 1 - (1 - t) * (1 - t)),
    QUAD_IN_OUT(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2),

    CUBIC_IN(t -> t * t * t),
    CUBIC_OUT(t -> 1 - Math.pow(1 - t, 3)),
    CUBIC_IN_OUT(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2),

    SINE_IN(t -> 1 - Math.cos(t * Math.PI / 2)),
    SINE_OUT(t -> Math.sin(t * Math.PI / 2)),
    SINE_IN_OUT(t -> -(Math.cos(Math.PI * t) - 1) / 2),

    EXPO_IN(t -> t == 0 ? 0 : Math.pow(2, 10 * t - 10)),
    EXPO_OUT(t -> t == 1 ? 1 : 1 - Math.pow(2, -10 * t)),

    BACK_IN(t -> { double s = 1.70158; return t * t * ((s + 1) * t - s); }),
    BACK_OUT(t -> { double s = 1.70158; return (t - 1) * (t - 1) * ((s + 1) * (t - 1) + s) + 1; }),

    SMOOTHERSTEP(t -> t * t * t * (t * (t * 6 - 15) + 10));

    private final DoubleUnaryOperator fn;

    Easing(DoubleUnaryOperator fn) {
        this.fn = fn;
    }

    public double apply(double t) {
        return fn.applyAsDouble(t);
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}