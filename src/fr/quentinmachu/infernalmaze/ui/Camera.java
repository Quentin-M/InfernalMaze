package fr.quentinmachu.infernalmaze.ui;

import fr.quentinmachu.infernalmaze.ui.math.Vector3f;

public class Camera {
    private float fov;
    private float near;
    private float far;

    private Vector3f eye;
    private Vector3f center;
    private Vector3f up;

    public Camera(Vector3f eye, Vector3f center, Vector3f up, float fov, float near, float far) {
	this.eye = eye;
	this.center = center;
	this.up = up;
	this.fov = fov;
	this.near = near;
	this.far = far;
    }

    /**
     * @return the fov
     */
    public float getFov() {
	return fov;
    }

    /**
     * @param fov
     *            the fov to set
     */
    public void setFov(float fov) {
	this.fov = fov;
    }

    /**
     * @return the near
     */
    public float getNear() {
	return near;
    }

    /**
     * @param near
     *            the near to set
     */
    public void setNear(float near) {
	this.near = near;
    }

    /**
     * @return the far
     */
    public float getFar() {
	return far;
    }

    /**
     * @param far
     *            the far to set
     */
    public void setFar(float far) {
	this.far = far;
    }

    /**
     * @return the eye
     */
    public Vector3f getEye() {
	return eye;
    }

    /**
     * @param eye
     *            the eye to set
     */
    public void setEye(Vector3f eye) {
	this.eye = eye;
    }

    /**
     * @return the center
     */
    public Vector3f getCenter() {
	return center;
    }

    /**
     * @param center
     *            the center to set
     */
    public void setCenter(Vector3f center) {
	this.center = center;
    }

    /**
     * @return the up
     */
    public Vector3f getUp() {
	return up;
    }

    /**
     * @param up
     *            the up to set
     */
    public void setUp(Vector3f up) {
	this.up = up;
    }
}
