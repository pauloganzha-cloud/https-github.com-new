package org.jdesktop.lg3d.animation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Animation system.
 */
public class AnimationTest {

    @Test
    public void testPositionAnimation() {
        // PositionAnimation would need Component3D
        // This tests the animation base class
        Animation anim = new Animation("test", 1000) {
            @Override
            protected void onUpdate(float t) {
                assertTrue(t >= 0 && t <= 1);
            }
        };

        assertEquals("test", anim.getName());
        assertEquals(1000, anim.getDuration());
        assertFalse(anim.isRunning());
    }

    @Test
    public void testInterpolation() {
        assertEquals(0.5f, Interpolator.LINEAR.interpolate(0.5f), 0.001);
        assertEquals(0.0f, Interpolator.EASE_IN.interpolate(0.0f), 0.001);
        assertEquals(1.0f, Interpolator.EASE_OUT.interpolate(1.0f), 0.001);
    }

    @Test
    public void testAnimationLifecycle() {
        Animation anim = new Animation("lifecycle", 100);

        anim.start();
        assertTrue(anim.isRunning());

        anim.stop();
        assertFalse(anim.isRunning());
    }
}