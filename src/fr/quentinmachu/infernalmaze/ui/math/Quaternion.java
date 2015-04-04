package fr.quentinmachu.infernalmaze.ui.math;

public class Quaternion extends Vector4f {
	/**
	 * Creates an identity quaternion.
	 */
	public Quaternion() {
		super();
		setIdentity();
	}

	/**
	 * Creates a quaternion with specified values.
	 */
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Creates a quaternion with specified rotation around axis
	 * 
	 * @param axis the axis-angle: (x,y,z) is the axis
	 * @param angle the angle of the rotation
	 */
	public Quaternion(Vector3f axis, float angle) {
		setFromAxisAngle(axis, angle);
	}
	
	/**
	 * Set this quaternion to the multiplication identity.
	 * 
	 * @return this
	 */
	public Quaternion setIdentity() {
		x = 0;
		y = 0;
		z = 0;
		w = 1;
		
		return this;
	}

	/**
	 * Multiplies the quaternion to another quaternion.

	 * @param other the quaternion
	 * @return Quaternion product of this * other
	 */
	public Quaternion multiply(Quaternion other) {
		Quaternion q = new Quaternion();
		
		q.x = x * other.w + w * other.x + y * other.z - z * other.y;
		q.y = y * other.w + w * other.y	+ z * other.x - x * other.z;
		q.z = z * other.w + w * other.z + x * other.y - y * other.x;
		q.w = w * other.w - x * other.x - y * other.y - z * other.z;
		
		return q;
	}

	/**
	 * Sets the value of this quaternion to the equivalent rotation of the Axis-Angle argument.
	 *
	 * @param axis the axis-angle: (x,y,z) is the axis
	 * @param angle the angle of the rotation
	 */
	public final void setFromAxisAngle(Vector3f axis, float angle) {
		float n = (float) Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
		float s = (float) (Math.sin(0.5 * angle) / n); // zero-div may occur.
		
		x = axis.x * s;
		y = axis.y * s;
		z = axis.z * s;
		w = (float) Math.cos(0.5 * angle);
	}

	/**
	 * Sets the value of this quaternion using the rotational component of the passed matrix.
	 *
	 * @param m The matrix
	 */
	public final void setFromMatrix(Matrix4f m) {
		float s;
		float tr = m.m00 + m.m11 + m.m22;
		
		if (tr >= 0.0) {
			s = (float) Math.sqrt(tr + 1.0);
			w = s * 0.5f;
			s = 0.5f / s;
			x = (m.m21 - m.m12) * s;
			y = (m.m02 - m.m20) * s;
			z = (m.m10 - m.m01) * s;
		} else {
			float max = Math.max(Math.max(m.m00, m.m11), m.m22);
			if (max == m.m00) {
				s = (float) Math.sqrt(m.m00 - (m.m11 + m.m22) + 1.0);
				x = s * 0.5f;
				s = 0.5f / s;
				y = (m.m01 + m.m10) * s;
				z = (m.m20 + m.m02) * s;
				w = (m.m21 - m.m12) * s;
			} else if (max == m.m11) {
				s = (float) Math.sqrt(m.m11 - (m.m22 + m.m00) + 1.0);
				y = s * 0.5f;
				s = 0.5f / s;
				z = (m.m12 + m.m21) * s;
				x = (m.m01 + m.m10) * s;
				w = (m.m02 - m.m20) * s;
			} else {
				s = (float) Math.sqrt(m.m22 - (m.m00 + m.m11) + 1.0);
				z = s * 0.5f;
				s = 0.5f / s;
				x = (m.m20 + m.m02) * s;
				y = (m.m12 + m.m21) * s;
				w = (m.m10 - m.m01) * s;
			}
		}
	}
	
	/**
	 * Converts this quaternion to a rotational matrix.
	 * The result is stored in result. 4th row and 4th column values are
	 * untouched. Note: the result is created from a normalized version of this
	 * quat.
	 * 
	 * @param result The Matrix4f to store the result in.
	 * @return the rotation matrix representation of this quaternion.
	 */
	public Matrix4f toRotationMatrix() {
		float norm = norm();
		    
		// we explicitly test norm against one here, saving a division
		// at the cost of a test and branch. Is it worth it?
		float s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;
		
		// compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
		// will be used 2-4 times each.
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		float xx = x * xs;
		float xy = x * ys;
		float xz = x * zs;
		float xw = w * xs;
		float yy = y * ys;
		float yz = y * zs;
		float yw = w * ys;
		float zz = z * zs;
		float zw = w * zs;
		
		// using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
		return new Matrix4f(
			new Vector4f(1 - (yy + zz), (xy - zw), (xz + yw), 0f), 
			new Vector4f((xy + zw), 1 - (xx + zz), (yz - xw), 0f),
			new Vector4f( (xz - yw), (yz + xw), 1 - (xx + yy), 0f),
			new Vector4f(0f, 0f, 0f, 1f)
		);
	}
}