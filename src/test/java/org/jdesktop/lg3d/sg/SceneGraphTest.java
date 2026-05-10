package org.jdesktop.lg3d.sg;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for LG3D Scene Graph.
 */
public class SceneGraphTest {

    @Test
    public void testTransform3DIdentity() {
        Transform3D t = new Transform3D();
        double[] m = t.getMatrix();

        assertEquals(1.0, m[0], 0.0001);
        assertEquals(0.0, m[1], 0.0001);
        assertEquals(0.0, m[10], 0.0001);
        assertEquals(1.0, m[15], 0.0001);
    }

    @Test
    public void testTransform3DTranslation() {
        Transform3D t = new Transform3D();
        t.setTranslation(1.0, 2.0, 3.0);

        double[] m = t.getMatrix();
        assertEquals(1.0, m[12], 0.0001);
        assertEquals(2.0, m[13], 0.0001);
        assertEquals(3.0, m[14], 0.0001);
    }

    @Test
    public void testTransform3DClone() {
        Transform3D t1 = new Transform3D();
        t1.setTranslation(1, 2, 3);

        Transform3D t2 = t1.clone();
        t2.setTranslation(4, 5, 6);

        double[] m1 = t1.getMatrix();
        double[] m2 = t2.getMatrix();

        assertEquals(1.0, m1[12], 0.0001);
        assertEquals(4.0, m2[12], 0.0001);
    }

    @Test
    public void testNodeAddChild() {
        Group parent = new Group("parent");
        Group child = new Group("child");

        assertEquals(0, parent.numChildren());
        parent.addChild(child);
        assertEquals(1, parent.numChildren());
    }

    @Test
    public void testNodeDetach() {
        Group parent = new Group("parent");
        Group child = new Group("child");

        parent.addChild(child);
        assertEquals(1, parent.numChildren());

        child.detach();
        assertEquals(0, parent.numChildren());
    }

    @Test
    public void testTransformGroup() {
        TransformGroup tg = new TransformGroup();
        Transform3D t = new Transform3D();
        t.setTranslation(1, 2, 3);

        tg.setTransform(t);

        Transform3D t2 = tg.getTransform();
        assertEquals(1.0, t2.getMatrix()[12], 0.0001);
    }

    @Test
    public void testShape3D() {
        TriangleArray geom = new TriangleArray(3, 0);
        Shape3D shape = new Shape3D(geom);

        assertEquals(geom, shape.getGeometry());
        assertEquals(3, shape.getGeometry().getVertexCount());
    }

    @Test
    public void testBranchGroup() {
        BranchGroup root = new BranchGroup();
        TransformGroup child = new TransformGroup("child");

        root.addChild(child);
        assertEquals(1, root.numChildren());

        root.detach();
        assertEquals(0, root.numChildren());
    }
}