package io.github.harmonica;

/**
 * Point represents a point containing the X, Y, Z coordinates of the point on
 * a plane.
 */
public final class Point {
    public double x;
    public double y;
    public double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Returns a copy of this point, mirroring Go's value semantics. */
    public Point copy() {
        return new Point(x, y, z);
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
