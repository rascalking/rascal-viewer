package com.rascalking.android.modelviewer;

public class ObjFace {
	public int[] vertexIndices;
	public int[] normalIndices;
	public int[] texCoordIndices;
	public String material;
	
	public static class byMaterial implements java.util.Comparator {
		@Override
		public int compare(Object arg0, Object arg1) {
			int arg0hc = (((ObjFace)arg0).material == null) ? 0 : ((ObjFace)arg0).material.hashCode();
			int arg1hc = (((ObjFace)arg1).material == null) ? 0 : ((ObjFace)arg1).material.hashCode();

			return arg0hc - arg1hc;
		}
	}
	
	public ObjFace(int[] vertices, int[] texCoords, int[] normals, String material) {
		super();
		this.vertexIndices = vertices;
		this.texCoordIndices = texCoords;
		this.normalIndices = normals;
		this.material = material;
	}
}
