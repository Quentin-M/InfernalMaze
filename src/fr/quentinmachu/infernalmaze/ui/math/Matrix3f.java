/*
 * The MIT License (MIT)
 *
 * Copyright © 2015, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.quentinmachu.infernalmaze.ui.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * This class represents a 3x3-Matrix. GLSL equivalent to mat3.
 *
 * @author Heiko Brumme
 */
public class Matrix3f {

    float m00;
	float m01;
	float m02;
    float m10, m11, m12;
    float m20, m21, m22;

    /**
     * Creates a 3x3 identity matrix.
     */
    public Matrix3f() {
        setIdentity();
    }

    /**
     * Creates a 3x3 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     */
    public Matrix3f(Vector3f col1, Vector3f col2, Vector3f col3) {
        m00 = col1.x;
        m10 = col1.y;
        m20 = col1.z;

        m01 = col2.x;
        m11 = col2.y;
        m21 = col2.z;

        m02 = col3.x;
        m12 = col3.y;
        m22 = col3.z;
    }

    /**
     * Creates a 3x3 matrix from a 4x4 matrix (extracts the upper left 3x3 matrix)
     * 
     * @param matrix Matrix with values
     */
    public Matrix3f(Matrix4f matrix) {
    	m00 = matrix.m00;
    	m10 = matrix.m10;
    	m20 = matrix.m20;
    	
    	m01 = matrix.m01;
    	m11 = matrix.m11;
    	m21 = matrix.m21;
    	
    	m02 = matrix.m02;
    	m12 = matrix.m12;
    	m22 = matrix.m22;
    }
    
    /**
     * Sets this matrix to the identity matrix.
     */
    public final void setIdentity() {
        m00 = 1f;
        m11 = 1f;
        m22 = 1f;

        m01 = 0f;
        m02 = 0f;
        m10 = 0f;
        m12 = 0f;
        m20 = 0f;
        m21 = 0f;
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    public Matrix3f add(Matrix3f other) {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 + other.m00;
        result.m10 = this.m10 + other.m10;
        result.m20 = this.m20 + other.m20;

        result.m01 = this.m01 + other.m01;
        result.m11 = this.m11 + other.m11;
        result.m21 = this.m21 + other.m21;

        result.m02 = this.m02 + other.m02;
        result.m12 = this.m12 + other.m12;
        result.m22 = this.m22 + other.m22;

        return result;
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    public Matrix3f negate() {
        return multiply(-1f);
    }

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    public Matrix3f subtract(Matrix3f other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    public Matrix3f multiply(float scalar) {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 * scalar;
        result.m10 = this.m10 * scalar;
        result.m20 = this.m20 * scalar;

        result.m01 = this.m01 * scalar;
        result.m11 = this.m11 * scalar;
        result.m21 = this.m21 * scalar;

        result.m02 = this.m02 * scalar;
        result.m12 = this.m12 * scalar;
        result.m22 = this.m22 * scalar;

        return result;
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    public Vector3f multiply(Vector3f vector) {
        float x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z;
        float y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z;
        float z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z;
        return new Vector3f(x, y, z);
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    public Matrix3f multiply(Matrix3f other) {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20;
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20;
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20;

        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21;
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21;
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21;

        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22;
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22;
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22;

        return result;
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    public Matrix3f transpose() {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00;
        result.m10 = this.m01;
        result.m20 = this.m02;

        result.m01 = this.m10;
        result.m11 = this.m11;
        result.m21 = this.m12;

        result.m02 = this.m20;
        result.m12 = this.m21;
        result.m22 = this.m22;

        return result;
    }

    /**
	 * Inverts this matrix
	 * 
	 * @return The inverted matrix
	 * @throws RuntimeException if the matrix is non invertible
	 */
    public Matrix3f invert() {
		float determinant = determinant();
		if(determinant == 0) throw new RuntimeException("This matrix is not invertible");
		
		Matrix3f result = new Matrix3f();
		
		float determinant_inv = 1f/determinant;
		float t00 = this.m11 * this.m22 - this.m12* this.m21;
		float t01 = - this.m10 * this.m22 + this.m12 * this.m20;
		float t02 = this.m10 * this.m21 - this.m11 * this.m20;
		float t10 = - this.m01 * this.m22 + this.m02 * this.m21;
		float t11 = this.m00 * this.m22 - this.m02 * this.m20;
		float t12 = - this.m00 * this.m21 + this.m01 * this.m20;
		float t20 = this.m01 * this.m12 - this.m02 * this.m11;
		float t21 = -this.m00 * this.m12 + this.m02 * this.m10;
		float t22 = this.m00 * this.m11 - this.m01 * this.m10;

		result.m00 = t00 * determinant_inv;
		result.m11 = t11 * determinant_inv;
		result.m22 = t22 * determinant_inv;
		result.m01 = t10 * determinant_inv;
		result.m10 = t01 * determinant_inv;
		result.m20 = t02 * determinant_inv;
		result.m02 = t20 * determinant_inv;
		result.m12 = t21 * determinant_inv;
		result.m21 = t12 * determinant_inv;
		
		return result;
	}

    /**
     * Calculate the determinant of this matrix
     * 
	 * @return the determinant of this matrix
	 */
	public float determinant() {
		return m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
	}
	
    /**
     * Returns the Buffer representation of this vector.
     *
     * @return Vector as FloatBuffer
     */
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        buffer.put(m00).put(m10).put(m20);
        buffer.put(m01).put(m11).put(m21);
        buffer.put(m02).put(m12).put(m22);
        buffer.flip();
        return buffer;
    }
}
