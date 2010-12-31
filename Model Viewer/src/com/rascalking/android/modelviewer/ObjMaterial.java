package com.rascalking.android.modelviewer;

import android.graphics.*;


// XXX - TODO - ambient, diffuse, specular, alpha, bump textures
public class ObjMaterial {
	public String name;
	public float alpha;
	public float[] ambient;
	public float[] diffuse;
	public float[] specular;
	public float shininess;
	public Bitmap texture;
	public Bitmap bump;
	
	public ObjMaterial() {
		super();
		this.name = "default";
		this.alpha = 1.0f;
		this.ambient = new float[] {0.2f, 0.2f, 0.2f, this.alpha};
		this.diffuse = new float[] {0.6f, 0.6f, 0.6f, this.alpha};
		this.specular = new float[] {1.0f, 1.0f, 1.0f, this.alpha};
		this.shininess = 10.0f;
		this.texture = null;
		this.bump = null;
	}

	public ObjMaterial(String name, float alpha, float[] ambient, float[] diffuse,
			float[] specular, float shininess, Bitmap texture, Bitmap bump) {
		super();
		this.name = name;
		this.alpha = alpha;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.texture = texture;
		this.bump = bump;
	}

}
