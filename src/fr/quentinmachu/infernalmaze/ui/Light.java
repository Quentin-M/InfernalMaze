package fr.quentinmachu.infernalmaze.ui;

import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;

public class Light {
	private Vector4f position;
	private Vector4f diffuse;
	private Vector4f specular;
	private float constantAttenuation, linearAttenuation, quadraticAttenuation;
	private float spotCutoff, spotExponent;
	private Vector3f spotDirection;
	
	public Light(Vector4f position, Vector4f diffuse, Vector4f specular, float constantAttenuation, float linearAttenuation, float quadraticAttenuation, float spotCutoff, float spotExponent, Vector3f spotDirection) {
		this.position = position;
		this.diffuse = diffuse;
		this.specular = specular;
		this.constantAttenuation = constantAttenuation;
		this.linearAttenuation = linearAttenuation;
		this.quadraticAttenuation = quadraticAttenuation;
		this.spotCutoff = spotCutoff;
		this.spotExponent = spotExponent;
		this.spotDirection = spotDirection;
	}

	/**
	 * @return the position
	 */
	public Vector4f getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector4f position) {
		this.position = position;
	}

	/**
	 * @return the diffuse
	 */
	public Vector4f getDiffuse() {
		return diffuse;
	}

	/**
	 * @param diffuse the diffuse to set
	 */
	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	/**
	 * @return the specular
	 */
	public Vector4f getSpecular() {
		return specular;
	}

	/**
	 * @param specular the specular to set
	 */
	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}

	/**
	 * @return the constantAttenuation
	 */
	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	/**
	 * @param constantAttenuation the constantAttenuation to set
	 */
	public void setConstantAttenuation(float constantAttenuation) {
		this.constantAttenuation = constantAttenuation;
	}

	/**
	 * @return the linearAttenuation
	 */
	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	/**
	 * @param linearAttenuation the linearAttenuation to set
	 */
	public void setLinearAttenuation(float linearAttenuation) {
		this.linearAttenuation = linearAttenuation;
	}

	/**
	 * @return the quadraticAttenuation
	 */
	public float getQuadraticAttenuation() {
		return quadraticAttenuation;
	}

	/**
	 * @param quadraticAttenuation the quadraticAttenuation to set
	 */
	public void setQuadraticAttenuation(float quadraticAttenuation) {
		this.quadraticAttenuation = quadraticAttenuation;
	}

	/**
	 * @return the spotCutoff
	 */
	public float getSpotCutoff() {
		return spotCutoff;
	}

	/**
	 * @param spotCutoff the spotCutoff to set
	 */
	public void setSpotCutoff(float spotCutoff) {
		this.spotCutoff = spotCutoff;
	}

	/**
	 * @return the spotExponent
	 */
	public float getSpotExponent() {
		return spotExponent;
	}

	/**
	 * @param spotExponent the spotExponent to set
	 */
	public void setSpotExponent(float spotExponent) {
		this.spotExponent = spotExponent;
	}

	/**
	 * @return the spotDirection
	 */
	public Vector3f getSpotDirection() {
		return spotDirection;
	}

	/**
	 * @param spotDirection the spotDirection to set
	 */
	public void setSpotDirection(Vector3f spotDirection) {
		this.spotDirection = spotDirection;
	}
}