package io.github.harmonica;

/**
 * Vector represents a vector carrying a magnitude and a direction. We
 * represent the vector as a point from the origin (0, 0) where the magnitude
 * is the euclidean distance from the origin and the direction is the direction
 * to the point from the origin.
 */
public final class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Returns a copy of this vector, mirroring Go's value semantics. */
    public Vector copy() {
        return new Vector(x, y, z);
    }

    @Override
    public String toString() {
        return "{" + fmt(x) + " " + fmt(y) + " " + fmt(z) + "}";
    }

    /** Formats a double like Go's %v: whole numbers print without a decimal. */
    static String fmt(double v) {
        if (v == Math.rint(v) && !Double.isInfinite(v)) {
            return Long.toString((long) v);
        }
        return Double.toString(v);
    }
}
