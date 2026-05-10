package org.jdesktop.lg3d.sg;

import java.util.ArrayList;
import java.util.List;

/**
 * Shape node - contains geometry for rendering.
 * Modern replacement for java3d.Shape3D using JOGL.
 */
public class Shape3D extends Node {

    private Geometry geometry;
    private Appearance appearance;
    private List<Geometry> alternateGeometry;

    public Shape3D() {
        super();
    }

    public Shape3D(String name) {
        super(name);
    }

    public Shape3D(Geometry geometry) {
        super();
        this.geometry = geometry;
    }

    public Shape3D(Geometry geometry, Appearance appearance) {
        super();
        this.geometry = geometry;
        this.appearance = appearance;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public void addAlternateGeometry(Geometry geometry) {
        if (alternateGeometry == null) {
            alternateGeometry = new ArrayList<>();
        }
        alternateGeometry.add(geometry);
    }

    public List<Geometry> getAlternateGeometry() {
        return alternateGeometry;
    }

    @Override
    public void render(JoglRenderer renderer) {
        if (geometry != null) {
            if (appearance != null) {
                renderer.setMaterial(appearance.getMaterial());
                renderer.setTransparency(appearance.getTransparency());
                if (appearance.getTexture() != null) {
                    renderer.setTexture(appearance.getTexture());
                    renderer.enable(JoglRenderer.TEXTURE);
                }
            }
            renderer.drawShape(this);
        }
        super.render(renderer);
    }
}

/**
 * Geometry - base class for 3D geometry.
 */
abstract class Geometry extends SceneGraphObject {
    protected int vertexCount;
    protected int[] vertexFormat;

    public int getVertexCount() {
        return vertexCount;
    }

    public int[] getVertexFormat() {
        return vertexFormat;
    }
}

/**
 * Triangle array geometry.
 */
class TriangleArray extends Geometry {

    private float[] coordinates;
    private float[] normals;
    private float[] colors;
    private float[] texCoords;

    public TriangleArray(int vertexCount, int vertexFormat) {
        this.vertexCount = vertexCount;
        this.vertexFormat = new int[]{vertexFormat};
        this.coordinates = new float[vertexCount * 3];
    }

    public void setCoordinates(float[] coords) {
        this.coordinates = coords;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setColors(float[] colors) {
        this.colors = colors;
    }

    public float[] getColors() {
        return colors;
    }

    public void setTextureCoordinates(float[] texCoords) {
        this.texCoords = texCoords;
    }

    public float[] getTextureCoordinates() {
        return texCoords;
    }
}

/**
 * Indexed triangle array.
 */
class IndexedTriangleArray extends Geometry {

    private float[] coordinates;
    private int[] coordinateIndices;
    private float[] normals;
    private int[] normalIndices;

    public IndexedTriangleArray(int vertexCount, int coordIndexCount, int vertexFormat) {
        this.vertexCount = vertexCount;
        this.coordinates = new float[vertexCount * 3];
        this.coordinateIndices = new int[coordIndexCount];
    }

    public void setCoordinates(float[] coords) {
        this.coordinates = coords;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinateIndices(int[] indices) {
        this.coordinateIndices = indices;
    }

    public int[] getCoordinateIndices() {
        return coordinateIndices;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public void setNormalIndices(int[] indices) {
        this.normalIndices = indices;
    }
}

/**
 * Appearance - defines rendering properties.
 */
class Appearance extends SceneGraphObject {

    private Material material;
    private Texture texture;
    private float transparency = 0;
    private PolygonAttributes polygonAttributes;
    private LineAttributes lineAttributes;
    private PointAttributes pointAttributes;

    public Appearance() {
        this.material = new Material();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getTransparency() {
        return transparency;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    public PolygonAttributes getPolygonAttributes() {
        return polygonAttributes;
    }

    public void setPolygonAttributes(PolygonAttributes attrs) {
        this.polygonAttributes = attrs;
    }

    public LineAttributes getLineAttributes() {
        return lineAttributes;
    }

    public void setLineAttributes(LineAttributes attrs) {
        this.lineAttributes = attrs;
    }
}

/**
 * Polygon rendering attributes.
 */
class PolygonAttributes {
    public enum CullFace {
        NONE, FRONT, BACK
    }

    public enum Mode {
        FILL, LINE, POINT
    }

    public CullFace cullFace = CullFace.BACK;
    public Mode mode = Mode.FILL;
    public boolean backFaceNormalReversed = false;
}

/**
 * Line rendering attributes.
 */
class LineAttributes {
    public float width = 1.0f;
    public boolean stipple = false;
    public int stipplePattern = 0xFFFF;
}

/**
 * Point rendering attributes.
 */
class PointAttributes {
    public float size = 1.0f;
    public boolean stipple = false;
}