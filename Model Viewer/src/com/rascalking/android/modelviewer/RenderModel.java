package com.rascalking.android.modelviewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

public class RenderModel {
	// generate linear lists of matched vertices and normals
	private void genVertexArrays(ObjModel obj) {
		if (obj.faces.size()==0) // XXX - TODO - raise this cleanly
			return;	
		
		// XXX - TODO - handle missing normals
		// XXX - TODO - handle missing texcoords
		
		// number of faces * 3 vertices/face * 3 coords per vertex * 4 bytes per float
		int bufSize = obj.faces.size()*3*3*4;
		// number of faces * 3 vertices/face * 2 coords per vertex * 4 bytes per float
		int texCoordBufSize = obj.faces.size()*3*2*4;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(bufSize);
		vbb.order(ByteOrder.nativeOrder());
		mVerticesBuffer = vbb.asFloatBuffer();
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(bufSize);
		nbb.order(ByteOrder.nativeOrder());
		mNormalsBuffer = nbb.asFloatBuffer();
		
	    ByteBuffer tbb = ByteBuffer.allocateDirect(texCoordBufSize);
	    tbb.order(ByteOrder.nativeOrder());
	    mTexCoordsBuffer = tbb.asFloatBuffer();
	    
	    // sort the faces by material so we can render all the faces with that material at once
	    Collections.sort(obj.faces, new ObjFace.byMaterial());
	    
	    ArrayList<RenderMaterial> materials = new ArrayList<RenderMaterial>(obj.materials.size());
	    String objMaterial = obj.faces.get(0).material;
	    int materialBeginsAtFace = 0;
	    int materialFaceCount = 0;
	    
		for (int nFace=0; nFace<obj.faces.size(); nFace++) {
			ObjFace face = obj.faces.get(nFace);
			
			// check for a different material first
			if (!face.material.equals(objMaterial)) {
				// store the old one
				if (materialFaceCount > 0)
					materials.add(new RenderMaterial(obj.materials.get(objMaterial),
													 materialBeginsAtFace*3,
													 materialFaceCount*3));
				
				// track the new one
				objMaterial = face.material;
				materialBeginsAtFace = nFace;
				materialFaceCount = 1;
			} else {
				materialFaceCount++;
			}
			
			// now add the vertices/texcoords/normals to the buffers
			for (int nVert=0; nVert<3; nVert++) {
				// copy 3 coords from vertices[face.vertexIndices[nVert]*3]
				mVerticesBuffer.put(obj.vertices, face.vertexIndices[nVert]*3, 3);
				mNormalsBuffer.put(obj.normals, face.normalIndices[nVert]*3, 3);
				mTexCoordsBuffer.put(obj.texCoords, face.texCoordIndices[nVert]*2, 2);
			}
		}
		// store the last material
		if (materialFaceCount > 0)
			materials.add(new RenderMaterial(obj.materials.get(objMaterial),
											 materialBeginsAtFace*3,
											 materialFaceCount*3));
		mMaterials = new RenderMaterial[materials.size()];
		materials.toArray(mMaterials);
		
		mVerticesBuffer.rewind();
		mNormalsBuffer.rewind();
		mTexCoordsBuffer.rewind();
	}
	
	public void loadTextures() {
		Log.i(TAG, "Loading "+mMaterials.length+" textures");
		for (int i=0; i<mMaterials.length; i++)
			mMaterials[i].loadTexture();
		Log.i(TAG, "Finished loading textures");
	}
	
	public RenderModel(ObjModel obj) {
		Log.i(TAG, "Starting obj processing");
		genVertexArrays(obj);
		mLongestAxisLength = Math.max(Math.max(obj.minmax[1]-obj.minmax[0],
											   obj.minmax[3]-obj.minmax[2]),
									  obj.minmax[5]-obj.minmax[4]);
		Log.i(TAG, "Finished obj processing: "+
				   obj.faces.size()+ " faces, "+
				   obj.vertices.length+" vertices");
	}
	
	public RenderMaterial[] mMaterials = null;
	public FloatBuffer mVerticesBuffer = null;
	public FloatBuffer mNormalsBuffer = null;
	public FloatBuffer mTexCoordsBuffer = null;
	public float mLongestAxisLength = 1.0f;
	
    private static String TAG = "RenderModel";
}
