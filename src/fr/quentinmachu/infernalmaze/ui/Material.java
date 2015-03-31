package fr.quentinmachu.infernalmaze.ui;

import fr.quentinmachu.infernalmaze.ui.math.Vector3f;

public class Material {
	private Vector3f ambient;
	private Vector3f diffuse;
	private Vector3f specular;
	private float shininess;
	
	public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}
	
	/**
	 * @return the ambient
	 */
	public Vector3f getAmbient() {
		return ambient;
	}
	
	/**
	 * @param ambient the ambient to set
	 */
	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}
	
	/**
	 * @return the diffuse
	 */
	public Vector3f getDiffuse() {
		return diffuse;
	}
	
	/**
	 * @param diffuse the diffuse to set
	 */
	public void setDiffuse(Vector3f diffuse) {
		this.diffuse = diffuse;
	}
	
	/**
	 * @return the specular
	 */
	public Vector3f getSpecular() {
		return specular;
	}
	
	/**
	 * @param specular the specular to set
	 */
	public void setSpecular(Vector3f specular) {
		this.specular = specular;
	}
	
	/**
	 * @return the shininess
	 */
	public float getShininess() {
		return shininess;
	}
	
	/**
	 * @param shininess the shininess to set
	 */
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
}
