package com.rascalking.android.modelviewer;


public class Vec {
	public float[] values;
	
	public Vec(int count) {
		values = new float[count];
	}
	
	public Vec(float[] values) {
		this.values = values;
	}
	
	public static Vec add(Vec v1, Vec v2) {
		Vec result = new Vec(Math.max(v1.values.length, v2.values.length));
		
		for (int i=0; i<result.values.length; i++) {
			if (v1.values.length > i) {
				result.values[i] = v1.values[i];
			} else {
				result.values[i] = 0.0f;
			}
			
			if (v2.values.length > i) {
				result.values[i] += v2.values[i];
			}			
		}
		return result;
	}
	
	public static Vec sub(Vec v1, Vec v2) {
		Vec result = new Vec(Math.max(v1.values.length, v2.values.length));
		
		for (int i=0; i<result.values.length; i++) {
			if (v1.values.length > i) {
				result.values[i] = v1.values[i];
			} else {
				result.values[i] = 0.0f;
			}
			
			if (v2.values.length > i) {
				result.values[i] -= v2.values[i];
			}			
		}
		return result;
	}
	
	public static float dot(Vec v1, Vec v2) {
		float result = 0.0f;
		
		for (int i=0; i<Math.min(v1.values.length, v2.values.length); i++) {
			result += v1.values[i]*v2.values[i];
		}
		
		return result;
	}
	
	public static Vec cross(Vec v1, Vec v2) {
		if (v1.values.length != 3 || v2.values.length != 3) {
			throw new UnsupportedOperationException();
		}
		
		Vec result = new Vec(3);
		result.values[0] = v1.values[1]*v2.values[2] - v1.values[2]*v2.values[1];
		result.values[1] = v1.values[2]*v2.values[0] - v1.values[0]*v2.values[2];
		result.values[2] = v1.values[0]*v2.values[1] - v1.values[1]*v2.values[0];
		return result;
	}

}
