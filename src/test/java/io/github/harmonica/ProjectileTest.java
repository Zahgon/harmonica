package io.github.harmonica;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Port of harmonica's projectile_test.go. */
class ProjectileTest {

    private static final int FPS = 60;

    private static final double EQUALITY_THRESHOLD = 1e-2;

    /** Floating point comparison under {@link #EQUALITY_THRESHOLD}. */
    private static boolean equal(double a, double b) {
        return Math.abs(a - b) <= EQUALITY_THRESHOLD;
    }

    @Test
    void testNew() {
        int x = 8;
        int y = 20;
        int z = 0;

        Projectile projectile = new Projectile(Spring.fps(60),
                new Point(x, y, z), new Vector(1, 1, 0), new Vector(0, 9.81, 0));
        Point pos = projectile.update();

        assertTrue(x == (int) pos.x, "x coordinate unexpected: Want " + x + ", Got " + (int) pos.x);
        assertTrue(y == (int) pos.y, "y coordinate unexpected: Want " + y + ", Got " + (int) pos.y);
    }

    @Test
    void testUpdate() {
        Projectile projectile = new Projectile(Spring.fps(FPS),
                new Point(0, 0, 0), new Vector(5, 5, 0), new Vector(0, 0, 0));
        Point[] coordinates = {
            new Point(5.0, 5.0, 0),
            new Point(10.0, 10.0, 0),
            new Point(15.0, 15.0, 0),
            new Point(20.0, 20.0, 0),
            new Point(25.0, 25.0, 0),
            new Point(30.0, 30.0, 0),
            new Point(35.0, 35.0, 0),
        };

        for (Point c : coordinates) {
            Point pos = null;
            for (int i = 0; i < FPS; i++) {
                pos = projectile.update();
            }

            Vector pvel = projectile.velocity();
            assertTrue(equal(pvel.x, 5) && equal(pvel.y, 5) && equal(pvel.z, 0),
                    String.format("velocity unexpected: Got (%.2f, %.2f, %.2f), Want (%.2f, %.2f, %.2f)",
                            pvel.x, pvel.y, pvel.z, 5.0, 5.0, 0.0));

            assertTrue(equal(pos.x, c.x) && equal(pos.y, c.y),
                    String.format("coordinate unexpected: Want (%.2f, %.2f), Got (%.2f, %.2f)",
                            c.x, c.y, pos.x, pos.y));
        }
    }

    @Test
    void testUpdateGravity() {
        int fps = 60;
        Projectile projectile = new Projectile(Spring.fps(fps),
                new Point(0, 0, 0), new Vector(5, 5, 0), Projectile.TERMINAL_GRAVITY);

        Point[] coordinates = {
            new Point(5.0, 9.82, 0),
            new Point(10.0, 29.46, 0),
            new Point(15.0, 58.90, 0),
            new Point(20.0, 98.15, 0),
            new Point(25.0, 147.22, 0),
            new Point(30.0, 206.09, 0),
            new Point(35.0, 274.77, 0),
        };

        for (Point c : coordinates) {
            Point pos = null;
            for (int f = 0; f < fps; f++) {
                pos = projectile.update();
            }

            double yacc = projectile.acceleration().y;
            assertTrue(equal(yacc, Projectile.TERMINAL_GRAVITY.y),
                    String.format("Y acceleration unexpected: Want %.2f, Got %.2f",
                            Projectile.TERMINAL_GRAVITY.y, yacc));

            assertTrue(equal(pos.x, c.x) && equal(pos.y, c.y),
                    String.format("coordinate unexpected: Want (%.2f, %.2f), Got (%.2f, %.2f)",
                            c.x, c.y, pos.x, pos.y));
        }
    }
}
