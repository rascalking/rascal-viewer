package com.rascalking.android.modelviewer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class RenderMaterial {
	public float[] matrix = null;
	public int textureID = -1;
	public int bumpID = -1;
	public int vertexIndexStart = -1;
	public int vertexIndexCount = -1;
	private Bitmap mTexture = null;
	private Bitmap mBump = null;
	
	public RenderMaterial(ObjMaterial obj, int start, int count) {
		// set up the matrix we'll be passing to the shaders
		//     ambient diffuse specular shininess
/*		matrix = new float[] {
			obj.ambient[0], obj.diffuse[0], obj.specular[0], obj.shininess,
			obj.ambient[1], obj.diffuse[1], obj.specular[1], 0.0f,
			obj.ambient[2], obj.diffuse[2], obj.specular[2], 0.0f,
			obj.alpha, obj.alpha, obj.alpha, 0.0f,
		};
*/
		matrix = new float[] {
			obj.ambient[0], obj.ambient[1], obj.ambient[2], obj.alpha,
			obj.diffuse[0], obj.diffuse[1], obj.diffuse[2], obj.alpha,
			obj.specular[0], obj.specular[1], obj.specular[2], obj.alpha,
			obj.shininess, 0.0f, 0.0f, 0.0f,
		};
		// store the vertices this material should be applied to
		vertexIndexStart = start;
		vertexIndexCount = count;
		
		// we can't load this into texture memory until later
		mTexture = obj.texture;
		mBump = obj.bump;
	}
	
	public void loadTexture() {
		if ((mTexture == null) && (mBump == null))
			return;
		
		if (mTexture != null) {
			int[] textures = new int[1];
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glGenTextures(1, textures, 0);
			textureID = textures[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_LINEAR);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
					GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_REPEAT);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mTexture, 0);
			mTexture.recycle();
			mTexture = null;
		}
		
		if (mBump != null) {
			int[] textures = new int[1];
			GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
			GLES20.glGenTextures(1, textures, 0);
			bumpID = textures[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bumpID);

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_LINEAR);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
					GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_REPEAT);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBump, 0);
			mBump.recycle();
			mBump = null;
		}
	}
	
}
