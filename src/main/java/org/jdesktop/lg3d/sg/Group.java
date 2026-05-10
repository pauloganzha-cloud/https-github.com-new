package org.jdesktop.lg3d.sg;

/**
 * Group node - container for child nodes.
 * Replacement for java3d.Group.
 */
public class Group extends Node {

    private boolean renderBinSort = false;
    private boolean staticTransform = true;

    public Group() {
        super();
    }

    public Group(String name) {
        super(name);
    }

    public boolean isRenderBinSort() {
        return renderBinSort;
    }

    public void setRenderBinSort(boolean sort) {
        this.renderBinSort = sort;
    }

    public boolean isStaticTransform() {
        return staticTransform;
    }

    public void setStaticTransform(boolean staticTransform) {
        this.staticTransform = staticTransform;
    }

    @Override
    public void onAddToGraph() {
        super.onAddToGraph();
    }
}

/**
 * TransformGroup - group with transform.
 * Replacement for java3d.TransformGroup.
 */
public class TransformGroup extends Group {

    private Transform3D transform;

    public TransformGroup() {
        super();
        this.transform = new Transform3D();
    }

    public TransformGroup(String name) {
        super(name);
        this.transform = new Transform3D();
    }

    public TransformGroup(Transform3D transform) {
        super();
        this.transform = new Transform3D(transform);
    }

    public Transform3D getTransform() {
        return transform;
    }

    public void setTransform(Transform3D transform) {
        this.transform.set(transform);
        setLocalTransform(transform);
    }

    public void getTransform(Transform3D transform) {
        transform.set(this.transform);
    }
}

/**
 * BranchGroup - root of a separate scene graph branch.
 */
public class BranchGroup extends Group {

    private boolean picked = true;
    private boolean collidable = true;

    public BranchGroup() {
        super();
    }

    public BranchGroup(String name) {
        super(name);
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public void detach() {
        super.detach();
    }
}

/**
 * Switch - group that can toggle children.
 */
public class Switch extends Group {

    private int whichChild = -1; // -1 means all children

    public Switch() {
        super();
    }

    public Switch(String name) {
        super(name);
    }

    public int getWhichChild() {
        return whichChild;
    }

    public void setWhichChild(int which) {
        this.whichChild = which;
    }
}