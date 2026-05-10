package org.jdesktop.lg3d.sg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for all scene graph nodes in LG3D Modern.
 * Replaces the original Java 3D based implementation with JOGL.
 */
public abstract class SceneGraphObject {

    private String name;
    private final UUID id;
    private SceneGraphObject parent;
    private final Map<String, Object> userData;

    public SceneGraphObject() {
        this.id = UUID.randomUUID();
        this.userData = new HashMap<>();
    }

    public SceneGraphObject(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public SceneGraphObject getParent() {
        return parent;
    }

    void setParent(SceneGraphObject parent) {
        this.parent = parent;
    }

    public Map<String, Object> getUserData() {
        return userData;
    }

    public void setUserData(String key, Object value) {
        userData.put(key, value);
    }

    public Object getUserData(String key) {
        return userData.get(key);
    }

    /**
     * Called when this object is added to the scene graph.
     */
    public void onAddToGraph() {
    }

    /**
     * Called when this object is removed from the scene graph.
     */
    public void onRemoveFromGraph() {
    }

    /**
     * Update the scene graph - called each frame.
     */
    public void update(long frameTime) {
    }

    /**
     * Render this object using JOGL.
     */
    public void render(JoglRenderer renderer) {
    }
}