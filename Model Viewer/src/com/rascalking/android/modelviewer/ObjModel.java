package com.rascalking.android.modelviewer;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjModel {
	// values loaded from the obj/mtl file
	public float[] vertices = null;
	public float[] texCoords = null;
	public float[] normals = null;
	public ArrayList<ObjFace> faces = null;
	public HashMap<String, ObjMaterial> materials = null;
	public float[] minmax = null;
}
