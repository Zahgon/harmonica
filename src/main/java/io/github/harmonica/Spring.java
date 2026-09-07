package io.github.harmonica;

/**
 * Spring contains a cached set of motion parameters that can be used to
 * efficiently update multiple springs using the same time step, angular
 * frequency and damping ratio.
 *
 * <p>This is a port of Ryan Juckett's simplified damped harmonic oscillator,
 * originally written in C++ and ported to Go by Charmbracelet, Inc.
 * For background on the algorithm see:
 * <a href="https://www.ryanjuckett.com/damped-springs/">Ryan Juckett's writeup</a>.
 *
 * <p>To use a Spring, construct one with the time delta (the animation frame
 * length), frequency, and damping parameters, cache the result, then call
 * {@link #update(double, double, double)} to update position and velocity
 * values for each spring that needs updating.
 *
 * <pre>{@code
 *     // First precompute spring coefficients based on your settings:
 *     double deltaTime = Spring.fps(60);
 *     Spring s = new Spring(deltaTime, 5.0, 0.2);
 *
 *     // Then, in your update loop:
 *     double[] xr = s.update(x, xVel, 10); // update the X position
 *     x = xr[0];
 *     xVel = xr[1];
 * }</pre>
 */
public final class Spring {
    /**
     * In calculus epsilon is, in vague terms, an arbitrarily small positive
     * number. We calculate the machine's epsilon value here, matching the Go
     * implementation's {@code math.Nextafter(1, 2) - 1}.
     */
    static final double EPSILON = Math.nextUp(1.0) - 1.0;

    private double posPosCoef, posVelCoef;
    private double velPosCoef, velVelCoef;

    /**
     * Returns a time delta for a given number of frames per second. This value
     * can be used as the time delta when initializing a Spring. Note that game
     * engines often provide the time delta as well, which you should use
     * instead of this function, if possible.
     *
     * <p>This mirrors Go's {@code (time.Second / time.Duration(n)).Seconds()},
     * including its integer-nanosecond truncation.
     *
     * @param n frames per second
     * @return the time delta, in seconds
     */
    public static double fps(int n) {
        return (1_000_000_000L / n) / 1e9;
    }

    /**
     * Initializes a new Spring, computing the parameters needed to simulate a
     * damped spring over a given period of time.
     *
     * @param deltaTime        the time step to advance; essentially the framerate
     * @param angularFrequency the angular frequency of motion, which affects the speed
     * @param dampingRatio     the damping ratio of motion, which determines the
     *                         oscillation, or lack thereof
     */
    public Spring(double deltaTime, double angularFrequency, double dampingRatio) {
        // Keep values in a legal range.
        angularFrequency = Math.max(0.0, angularFrequency);
        dampingRatio = Math.max(0.0, dampingRatio);

        // If there is no angular frequency, the spring will not move and we can
        // return identity.
        if (angularFrequency < EPSILON) {
            posPosCoef = 1.0;
            posVelCoef = 0.0;
            velPosCoef = 0.0;
            velVelCoef = 1.0;
            return;
        }

        if (dampingRatio > 1.0 + EPSILON) {
            // Over-damped.
            double za = -angularFrequency * dampingRatio;
            double zb = angularFrequency * Math.sqrt(dampingRatio * dampingRatio - 1.0);
            double z1 = za - zb;
            double z2 = za + zb;

            double e1 = Math.exp(z1 * deltaTime);
            double e2 = Math.exp(z2 * deltaTime);

            double invTwoZb = 1.0 / (2.0 * zb); // = 1 / (z2 - z1)

            double e1OverTwoZb = e1 * invTwoZb;
            double e2OverTwoZb = e2 * invTwoZb;

            double z1e1OverTwoZb = z1 * e1OverTwoZb;
            double z2e2OverTwoZb = z2 * e2OverTwoZb;

            posPosCoef = e1OverTwoZb * z2 - z2e2OverTwoZb + e2;
            posVelCoef = -e1OverTwoZb + e2OverTwoZb;

            velPosCoef = (z1e1OverTwoZb - z2e2OverTwoZb + e2) * z2;
            velVelCoef = -z1e1OverTwoZb + z2e2OverTwoZb;

        } else if (dampingRatio < 1.0 - EPSILON) {
            // Under-damped.
            double omegaZeta = angularFrequency * dampingRatio;
            double alpha = angularFrequency * Math.sqrt(1.0 - dampingRatio * dampingRatio);

            double expTerm = Math.exp(-omegaZeta * deltaTime);
            double cosTerm = Math.cos(alpha * deltaTime);
            double sinTerm = Math.sin(alpha * deltaTime);

            double invAlpha = 1.0 / alpha;

            double expSin = expTerm * sinTerm;
            double expCos = expTerm * cosTerm;
            double expOmegaZetaSinOverAlpha = expTerm * omegaZeta * sinTerm * invAlpha;

            posPosCoef = expCos + expOmegaZetaSinOverAlpha;
            posVelCoef = expSin * invAlpha;

            velPosCoef = -expSin * alpha - omegaZeta * expOmegaZetaSinOverAlpha;
            velVelCoef = expCos - expOmegaZetaSinOverAlpha;

        } else {
            // Critically damped.
            double expTerm = Math.exp(-angularFrequency * deltaTime);
            double timeExp = deltaTime * expTerm;
            double timeExpFreq = timeExp * angularFrequency;

            posPosCoef = timeExpFreq + expTerm;
            posVelCoef = timeExp;

            velPosCoef = -angularFrequency * timeExpFreq;
            velVelCoef = -timeExpFreq + expTerm;
        }
    }

    /**
     * Updates position and velocity values against a given target value. Call
     * this after constructing a Spring to update values.
     *
     * @param pos            the current position
     * @param vel            the current velocity
     * @param equilibriumPos the target (equilibrium) position
     * @return a two-element array of {@code {newPos, newVel}}
     */
    public double[] update(double pos, double vel, double equilibriumPos) {
        double oldPos = pos - equilibriumPos; // update in equilibrium relative space
        double oldVel = vel;

        double newPos = oldPos * posPosCoef + oldVel * posVelCoef + equilibriumPos;
        double newVel = oldPos * velPosCoef + oldVel * velVelCoef;

        return new double[] {newPos, newVel};
    }
}
