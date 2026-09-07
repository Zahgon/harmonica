package io.github.harmonica;

/**
 * Projectile is the representation of a projectile that has a position on a
 * plane, an acceleration, and velocity. This defines simple physics
 * projectile motion.
 *
 * <p>For background on projectile motion see:
 * <a href="https://en.wikipedia.org/wiki/Projectile_motion">Projectile motion</a>.
 *
 * <pre>{@code
 *     // Run once to initialize.
 *     Projectile projectile = new Projectile(
 *         Spring.fps(60),
 *         new Point(6.0, 100.0, 0.0),
 *         new Vector(2.0, 0.0, 0.0),
 *         new Vector(2.0, -9.81, 0.0));
 *
 *     // Update on every frame.
 *     Point pos = projectile.update();
 * }</pre>
 */
public final class Projectile {
    /**
     * Gravity is a utility vector that represents gravity in 2D and 3D
     * contexts, assuming that the coordinate plane's origin is located in the
     * bottom-left corner.
     */
    public static final Vector GRAVITY = new Vector(0, -9.81, 0);

    /**
     * TerminalGravity is a utility vector that represents gravity where the
     * coordinate plane's origin is on the top-right corner.
     */
    public static final Vector TERMINAL_GRAVITY = new Vector(0, 9.81, 0);

    private final Point pos;
    private final Vector vel;
    private final Vector acc;
    private final double deltaTime;

    /**
     * Creates a new projectile. It accepts a frame rate and initial values for
     * position, velocity, and acceleration.
     *
     * @param deltaTime           the time step to operate on (e.g. {@link Spring#fps(int)})
     * @param initialPosition     the initial position
     * @param initialVelocity     the initial velocity
     * @param initialAcceleration the initial acceleration
     */
    public Projectile(double deltaTime, Point initialPosition, Vector initialVelocity,
                      Vector initialAcceleration) {
        this.pos = initialPosition.copy();
        this.vel = initialVelocity.copy();
        this.acc = initialAcceleration.copy();
        this.deltaTime = deltaTime;
    }

    /**
     * Updates the position and velocity values for the given projectile. Call
     * this after constructing a Projectile to update values.
     *
     * @return the updated position
     */
    public Point update() {
        pos.x += (vel.x * deltaTime);
        pos.y += (vel.y * deltaTime);
        pos.z += (vel.z * deltaTime);

        vel.x += (acc.x * deltaTime);
        vel.y += (acc.y * deltaTime);
        vel.z += (acc.z * deltaTime);

        return pos.copy();
    }

    /** Returns the position of the projectile. */
    public Point position() {
        return pos.copy();
    }

    /** Returns the velocity of the projectile. */
    public Vector velocity() {
        return vel.copy();
    }

    /** Returns the acceleration of the projectile. */
    public Vector acceleration() {
        return acc.copy();
    }
}
