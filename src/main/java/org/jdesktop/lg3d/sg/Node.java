package org.jdesktop.lg3d.sg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all scene graph nodes.
 * Replaces java3d.Node with modern JOGL-based implementation.
 */
public abstract class Node extends SceneGraphObject {

    private List<Node> children;
    private Node parent;
    private boolean alive = true;
    private boolean lit = true;
    private boolean pickable = true;
    private Transform3D localTransform;
    private Transform3D worldTransform;

    public Node() {
        super();
        this.children = new ArrayList<>();
        this.localTransform = new Transform3D();
        this.worldTransform = new Transform3D();
    }

    public Node(String name) {
        super(name);
        this.children = new ArrayList<>();
        this.localTransform = new Transform3D();
        this.worldTransform = new Transform3D();
    }

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int numChildren() {
        return children.size();
    }

    public void addChild(Node child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (child == this) {
            throw new IllegalArgumentException("Cannot add a node as its own child");
        }
        if (child.parent != null) {
            child.detach();
        }
        children.add(child);
        child.setParent(this);
        child.onAddToGraph();
    }

    public void removeChild(Node child) {
        if (children.remove(child)) {
            child.setParent(null);
            child.onRemoveFromGraph();
        }
    }

    public void detach() {
        if (parent != null) {
            parent.removeChild(this);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public boolean isPickable() {
        return pickable;
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }

    public Transform3D getLocalTransform() {
        return localTransform;
    }

    public void setLocalTransform(Transform3D transform) {
        this.localTransform.set(transform);
    }

    public Transform3D getWorldTransform() {
        return worldTransform;
    }

    void updateWorldTransform() {
        if (parent != null) {
            worldTransform.mul(parent.getWorldTransform(), localTransform);
        } else {
            worldTransform.set(localTransform);
        }
        for (Node child : children) {
            child.updateWorldTransform();
        }
    }

    @Override
    public void update(long frameTime) {
        updateWorldTransform();
        super.update(frameTime);
        for (Node child : children) {
            child.update(frameTime);
        }
    }

    @Override
    public void render(JoglRenderer renderer) {
        for (Node child : children) {
            child.render(renderer);
        }
    }
}