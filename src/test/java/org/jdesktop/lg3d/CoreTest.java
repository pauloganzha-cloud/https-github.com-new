package org.jdesktop.lg3d;

/**
 * Quick test to verify core classes work.
 */
public class CoreTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  LG3D Modern - Core Test");
        System.out.println("=".repeat(50));
        System.out.println();

        testSceneGraph();
        testWindowSystem();
        testUIComponents();
        testUtils();

        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("  All tests passed!");
        System.out.println("=".repeat(50));
    }

    static void testSceneGraph() {
        System.out.println("[Test] Scene Graph...");

        org.jdesktop.lg3d.sg.Transform3D t = new org.jdesktop.lg3d.sg.Transform3D();
        t.setTranslation(1, 2, 3);
        assert t.getMatrix()[12] == 1.0;

        org.jdesktop.lg3d.sg.Group g = new org.jdesktop.lg3d.sg.Group("TestGroup");
        org.jdesktop.lg3d.sg.TransformGroup tg = new org.jdesktop.lg3d.sg.TransformGroup();
        g.addChild(tg);
        assert g.numChildren() == 1;

        System.out.println("  ✓ Scene graph OK");
    }

    static void testWindowSystem() {
        System.out.println("[Test] Window System...");

        org.jdesktop.lg3d.wg.Window3D win = new org.jdesktop.lg3d.wg.Window3D("TestWindow");
        win.setTitle("My Window");
        assert "My Window".equals(win.getTitle());

        win.setMinimized(true);
        assert win.isMinimized();

        System.out.println("  ✓ Window system OK");
    }

    static void testUIComponents() {
        System.out.println("[Test] UI Components...");

        org.jdesktop.lg3d.wg.components.Button3D btn = new org.jdesktop.lg3d.wg.components.Button3D("Click Me");
        assert "Click Me".equals(btn.getText());

        org.jdesktop.lg3d.wg.components.Checkbox3D chk = new org.jdesktop.lg3d.wg.components.Checkbox3D("Option");
        chk.setSelected(true);
        assert chk.isSelected();

        org.jdesktop.lg3d.wg.components.Slider3D slider = new org.jdesktop.lg3d.wg.components.Slider3D();
        slider.setValue(0.5f);
        assert slider.getValue() == 0.5f;

        System.out.println("  ✓ UI components OK");
    }

    static void testUtils() {
        System.out.println("[Test] Utilities...");

        // Theme
        org.jdesktop.lg3d.utils.ThemeManager tm = org.jdesktop.lg3d.utils.ThemeManager.getInstance();
        tm.setTheme("dark");
        assert tm.getTheme().getName().equals("Dark");

        // Theme colors
        float[] accent = tm.getTheme().getColor("accent");
        assert accent.length == 4;

        // Animation Interpolators
        assert org.jdesktop.lg3d.animation.Interpolator.LINEAR.interpolate(0.5f) == 0.5f;
        assert org.jdesktop.lg3d.animation.Interpolator.EASE_IN.interpolate(0.5f) > 0;

        System.out.println("  ✓ Utils OK");
    }
}