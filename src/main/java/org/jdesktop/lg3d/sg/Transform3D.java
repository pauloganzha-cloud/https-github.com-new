package org.jdesktop.lg3d.sg;

/**
 * 4x4 3D transformation matrix.
 * Modern replacement for javax.media.j3d.Transform3D
 */
public class Transform3D implements Cloneable {

    private double[] matrix;

    public Transform3D() {
        matrix = new double[16];
        setIdentity();
    }

    public Transform3D(double[] matrix) {
        if (matrix.length != 16) {
            throw new IllegalArgumentException("Matrix must be 4x4 (16 elements)");
        }
        this.matrix = matrix.clone();
    }

    public Transform3D(Transform3D other) {
        this.matrix = other.matrix.clone();
    }

    public void setIdentity() {
        matrix[0] = 1; matrix[1] = 0; matrix[2] = 0; matrix[3] = 0;
        matrix[4] = 0; matrix[5] = 1; matrix[6] = 0; matrix[7] = 0;
        matrix[8] = 0; matrix[9] = 0; matrix[10] = 1; matrix[11] = 0;
        matrix[12] = 0; matrix[13] = 0; matrix[14] = 0; matrix[15] = 1;
    }

    public double[] getMatrix() {
        return matrix.clone();
    }

    public void setMatrix(double[] matrix) {
        if (matrix.length != 16) {
            throw new IllegalArgumentException("Matrix must be 16 elements");
        }
        this.matrix = matrix.clone();
    }

    public void set(Transform3D other) {
        this.matrix = other.matrix.clone();
    }

    public void mul(Transform3D other) {
        mul(other, this);
    }

    public void mul(Transform3D other, Transform3D result) {
        double[] a = this.matrix;
        double[] b = other.matrix;
        double[] r = new double[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                r[i * 4 + j] =
                    a[i * 4 + 0] * b[0 * 4 + j] +
                    a[i * 4 + 1] * b[1 * 4 + j] +
                    a[i * 4 + 2] * b[2 * 4 + j] +
                    a[i * 4 + 3] * b[3 * 4 + j];
            }
        }

        result.matrix = r;
    }

    public void invert() {
        invert(this);
    }

    public void invert(Transform3D result) {
        double[] m = matrix;
        double[] r = new double[16];

        double det = m[0] * (m[5] * m[10] - m[6] * m[9]) -
                     m[1] * (m[4] * m[10] - m[6] * m[8]) +
                     m[2] * (m[4] * m[9] - m[5] * m[8]);

        if (Math.abs(det) < 1e-10) {
            result.setIdentity();
            return;
        }

        double invDet = 1.0 / det;

        r[0] = (m[5] * m[10] - m[6] * m[9]) * invDet;
        r[1] = (m[2] * m[9] - m[1] * m[10]) * invDet;
        r[2] = (m[1] * m[6] - m[2] * m[5]) * invDet;
        r[3] = 0;

        r[4] = (m[6] * m[8] - m[4] * m[10]) * invDet;
        r[5] = (m[0] * m[10] - m[2] * m[8]) * invDet;
        r[6] = (m[2] * m[4] - m[0] * m[6]) * invDet;
        r[7] = 0;

        r[8] = (m[4] * m[9] - m[5] * m[8]) * invDet;
        r[9] = (m[1] * m[8] - m[0] * m[9]) * invDet;
        r[10] = (m[0] * m[5] - m[1] * m[4]) * invDet;
        r[11] = 0;

        r[12] = -(m[12] * r[0] + m[13] * r[4] + m[14] * r[8]);
        r[13] = -(m[12] * r[1] + m[13] * r[5] + m[14] * r[9]);
        r[14] = -(m[12] * r[2] + m[13] * r[6] + m[14] * r[10]);
        r[15] = 1;

        result.matrix = r;
    }

    public void transpose() {
        double[] m = matrix;
        double[] t = new double[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                t[i * 4 + j] = m[j * 4 + i];
            }
        }
        matrix = t;
    }

    public void setTranslation(double x, double y, double z) {
        matrix[12] = x;
        matrix[13] = y;
        matrix[14] = z;
    }

    public void setRotation(double angle, double x, double y, double z) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double len = Math.sqrt(x * x + y * y + z * z);

        if (len < 1e-10) {
            setIdentity();
            return;
        }

        x /= len;
        y /= len;
        z /= len;

        matrix[0] = cos + x * x * (1 - cos);
        matrix[1] = y * x * (1 - cos) + z * sin;
        matrix[2] = z * x * (1 - cos) - y * sin;
        matrix[3] = 0;

        matrix[4] = x * y * (1 - cos) - z * sin;
        matrix[5] = cos + y * y * (1 - cos);
        matrix[6] = z * y * (1 - cos) + x * sin;
        matrix[7] = 0;

        matrix[8] = x * z * (1 - cos) + y * sin;
        matrix[9] = y * z * (1 - cos) - x * sin;
        matrix[10] = cos + z * z * (1 - cos);
        matrix[11] = 0;

        matrix[12] = 0;
        matrix[13] = 0;
        matrix[14] = 0;
        matrix[15] = 1;
    }

    public void setScale(double x, double y, double z) {
        matrix[0] = x;
        matrix[5] = y;
        matrix[10] = z;
    }

    @Override
    public Transform3D clone() {
        return new Transform3D(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transform3D[\n");
        for (int i = 0; i < 4; i++) {
            sb.append("  ");
            for (int j = 0; j < 4; j++) {
                sb.append(String.format("%.4f ", matrix[i * 4 + j]));
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}