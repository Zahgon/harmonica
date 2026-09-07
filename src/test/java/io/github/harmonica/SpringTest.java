package io.github.harmonica;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Covers Spring, the projectile Position/toString surface, and the FPS helper
 * against ground-truth values captured from the running Go original.
 */
class SpringTest {

    private static final double EPS = 1e-9;

    @Test
    void testFps() {
        assertEquals(0.01666666600000000012, Spring.fps(60), EPS);
        assertEquals(0.033333332999999999713, Spring.fps(30), EPS);
    }

    @Test
    void testUnderDamped() {
        Spring s = new Spring(Spring.fps(60), 6.0, 0.5);
        double pos = 0.0;
        double vel = 0.0;
        double[][] want = {
            {0.48334148980066516, 57.002447851365964},
            {1.8669243066014047, 108.03839763965048},
            {4.0519236009226915, 153.19036999034773},
            {6.9412988174657073, 192.58897945973428},
            {10.440546590817547, 226.40711586595214},
        };
        for (int i = 0; i < want.length; i++) {
            double[] r = s.update(pos, vel, 100.0);
            pos = r[0];
            vel = r[1];
            assertEquals(want[i][0], pos, EPS, "under pos[" + i + "]");
            assertEquals(want[i][1], vel, EPS, "under vel[" + i + "]");
        }
    }

    @Test
    void testCriticallyDamped() {
        Spring s = new Spring(Spring.fps(60), 6.0, 1.0);
        double pos = 0.0;
        double vel = 0.0;
        double[][] want = {
            {0.46788397985095287, 54.290243127708735},
            {1.7523094996452642, 98.24768722543169},
            {3.6936310446821352, 133.3472759889853},
            {6.1551931260056989, 160.87680718750985},
            {9.0204004365743629, 181.95919427460592},
        };
        for (int i = 0; i < want.length; i++) {
            double[] r = s.update(pos, vel, 100.0);
            pos = r[0];
            vel = r[1];
            assertEquals(want[i][0], pos, EPS, "crit pos[" + i + "]");
            assertEquals(want[i][1], vel, EPS, "crit vel[" + i + "]");
        }
    }

    @Test
    void testOverDamped() {
        Spring s = new Spring(Spring.fps(60), 6.0, 2.0);
        double pos = 0.0;
        double vel = 0.0;
        double[][] want = {
            {0.43914418322383142, 49.36983150317112},
            {1.5534099632698144, 82.05685176870503},
            {3.1110471440821215, 103.29186652896647},
            {4.9530576908973387, 116.67549876762733},
            {6.9705201745760377, 124.68597560375409},
        };
        for (int i = 0; i < want.length; i++) {
            double[] r = s.update(pos, vel, 100.0);
            pos = r[0];
            vel = r[1];
            assertEquals(want[i][0], pos, EPS, "over pos[" + i + "]");
            assertEquals(want[i][1], vel, EPS, "over vel[" + i + "]");
        }
    }

    @Test
    void testZeroFrequencyIsIdentity() {
        Spring s = new Spring(Spring.fps(60), 0.0, 0.5);
        double[] r = s.update(5.0, 3.0, 100.0);
        assertEquals(5.0, r[0], EPS);
        assertEquals(3.0, r[1], EPS);
    }

    @Test
    void testProjectilePosition() {
        Projectile p = new Projectile(Spring.fps(60),
                new Point(6, 100, 0), new Vector(2, 0, 0), Projectile.GRAVITY);
        for (int i = 0; i < 10; i++) {
            p.update();
        }
        Point pos = p.position();
        assertEquals(6.3333333199999977, pos.x, EPS);
        assertEquals(99.877375009810009, pos.y, EPS);
        assertEquals(0.0, pos.z, EPS);
    }

    @Test
    void testPointToString() {
        assertEquals("{6 100 0}", new Point(6, 100, 0).toString());
    }

    @Test
    void testVectorToString() {
        assertEquals("{2.5 -9.81 0}", new Vector(2.5, -9.81, 0).toString());
    }
}
