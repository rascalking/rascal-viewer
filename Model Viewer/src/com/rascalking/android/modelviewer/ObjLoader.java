package com.rascalking.android.modelviewer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import android.content.Context;
import android.graphics.BitmapFactory;


// XXX - TODO - clean up the parser, it's sloppy as hell
// DANGER WILL ROBINSON! Here be no validation nor error handling!

public class ObjLoader {
	private static Pattern whitespacePattern = Pattern.compile("\\s+");
	private static Pattern facePattern = Pattern.compile("(\\d+)?/(\\d+)?/(\\d+)?");
	
	private static ObjFace parseFace(String v1, String v2, String v3, String material) {
		Matcher matcher;
		int[] vertices = new int[3];
		int[] texcoords = null;
		int[] normals = null;
		
		// XXX - TODO - yes, this is ugly, figure out java array slices later
		matcher = facePattern.matcher(v1);
		if (matcher.find()) {
			vertices[0] = Integer.parseInt(matcher.group(1))-1;
			if (matcher.group(2) != null) {
				texcoords = new int[3];
				texcoords[0] = Integer.parseInt(matcher.group(2))-1;
			}
			if (matcher.group(3) != null) {
				normals = new int[3];
				normals[0] = Integer.parseInt(matcher.group(3))-1;
			}
		}
		
		matcher = facePattern.matcher(v2);
		if (matcher.find()) {
			vertices[1] = Integer.parseInt(matcher.group(1))-1;
			if (matcher.group(2) != null)
				texcoords[1] = Integer.parseInt(matcher.group(2))-1;
			if (matcher.group(3) != null)
				normals[1] = Integer.parseInt(matcher.group(3))-1;
		}
		
		matcher = facePattern.matcher(v3);
		if (matcher.find()) {
			vertices[2] = Integer.parseInt(matcher.group(1))-1;
			if (matcher.group(2) != null)
				texcoords[2] = Integer.parseInt(matcher.group(2))-1;
			if (matcher.group(3) != null)
				normals[2] = Integer.parseInt(matcher.group(3))-1;
		}
		
		return new ObjFace(vertices, texcoords, normals, material);
	}
	
	private static HashMap<String,ObjMaterial> loadMaterials(Context ctx, String mtlFileName) {
		HashMap<String,ObjMaterial> materials = new HashMap<String,ObjMaterial>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(mtlFileName)));
			//reader = new BufferedReader(new InputStreamReader(new FileInputStream(mtlFileName)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String line;
		ObjMaterial currentMaterial = null;
		
		try {
			while ((line = reader.readLine()) != null)
			{
				String[] tokens;
				
				if (line.equals("") || line.startsWith("#"))
					continue;
				
				tokens = whitespacePattern.split(line);
				if (tokens[0].equals("newmtl")) {
					if (currentMaterial != null)
						materials.put(currentMaterial.name, currentMaterial);
					currentMaterial = new ObjMaterial();
					currentMaterial.name = tokens[1];
				} else if (tokens[0].equals("Ka")) {
					float[] temp = new float[4];
					for (int i=0; i<3; i++) {
						temp[i] = Float.parseFloat(tokens[i+1]);
					}
					temp[3] = currentMaterial.alpha;
					currentMaterial.ambient = temp;
				} else if (tokens[0].equals("Kd")) {
					float[] temp = new float[4];
					for (int i=0; i<3; i++) {
						temp[i] = Float.parseFloat(tokens[i+1]);
					}
					temp[3] = currentMaterial.alpha;
					currentMaterial.diffuse = temp;
				} else if (tokens[0].equals("Ks")) {
					float[] temp = new float[4];
					for (int i=0; i<3; i++) {
						temp[i] = Float.parseFloat(tokens[i+1]);
					}
					temp[3] = currentMaterial.alpha;
					currentMaterial.specular = temp;
				} else if (tokens[0].equals("Ns")) {
					currentMaterial.shininess = Float.parseFloat(tokens[1]);
				} else if (tokens[0].equals("d") || tokens[0].equals("tr")) {
					currentMaterial.alpha = Float.parseFloat(tokens[1]);
					currentMaterial.ambient[3] = currentMaterial.alpha;
					currentMaterial.diffuse[3] = currentMaterial.alpha;
					currentMaterial.specular[3] = currentMaterial.alpha;
				} else if (tokens[0].equals("map_Ka") || tokens[0].equals("map_Kd") ) {
					// XXX - TODO - implement multitexturing
					if (currentMaterial.texture == null) {
						String textureFileName =
							new File(new File(mtlFileName).getParent(), tokens[1]).getPath();
						currentMaterial.texture = BitmapFactory.decodeStream(ctx.getAssets().open(textureFileName));
						if (currentMaterial.texture == null)
							throw new RuntimeException("Unable to load texture file "+textureFileName);
					}
				} else if (tokens[0].equals("bump")) {
					if (currentMaterial.bump == null) {
						String bumpFileName =
							new File(new File(mtlFileName).getParent(), tokens[1]).getPath();
						currentMaterial.bump = BitmapFactory.decodeStream(ctx.getAssets().open(bumpFileName));
						if (currentMaterial.bump == null)
							throw new RuntimeException("Unable to load texture file "+bumpFileName);
					}
				} else {
					// XXX - TODO - we don't support this yet
				}
			}
		} catch (IOException e) {
			// XXX - TODO - Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		if (currentMaterial != null)
			materials.put(currentMaterial.name, currentMaterial);
		
		return materials;
	}
	
	public static ObjModel loadObject(Context ctx, String objFileName) {
		ObjModel model = new ObjModel();
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		ArrayList<Float> normals = new ArrayList<Float>();
		ArrayList<ObjFace> faces = new ArrayList<ObjFace>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(objFileName)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String line;
		
		float[] minmax = new float[] {
			Float.MAX_VALUE, Float.MIN_VALUE,
			Float.MAX_VALUE, Float.MIN_VALUE,
			Float.MAX_VALUE, Float.MIN_VALUE,
		};

		HashMap<String, ObjMaterial> materials = new HashMap<String, ObjMaterial>();
		ObjMaterial defaultMat = new ObjMaterial();
		materials.put("default", defaultMat);
		ObjMaterial currentMat = defaultMat;
		
		try {
			while ((line = reader.readLine()) != null)
			{
				String[] tokens;
				
				if (line.equals("") || line.startsWith("#"))
					continue;
				
				tokens = whitespacePattern.split(line);
				if (tokens[0].equals("v")) {
					for (int i=1; i<4; i++) {
						Float n = Float.parseFloat(tokens[i]);
						if (n < minmax[(i-1)*2])
							minmax[(i-1)*2] = n;
						if (n > minmax[(i-1)*2+1])
							minmax[(i-1)*2+1] = n;
						vertices.add(n);
					}
				} else if (tokens[0].equals("vt")) {
					for (int i=1; i<3; i++) {
						texCoords.add(Float.parseFloat(tokens[i]));
						// XXX - TODO - handle 3d textures
					}
				} else if (tokens[0].equals("vn")) {
					for (int i=1; i<4; i++) {
						normals.add(Float.parseFloat(tokens[i]));
					}
				} else if (tokens[0].equals("f")) {
					if (tokens.length == 4) {
						faces.add(parseFace(tokens[1], tokens[2], tokens[3], currentMat.name));
					} else if (tokens.length == 5) {
						// XXX - TODO - verify that the quad is convex
						faces.add(parseFace(tokens[1], tokens[2], tokens[3], currentMat.name));
						faces.add(parseFace(tokens[1], tokens[3], tokens[4], currentMat.name));
					} else {
						// XXX - TODO - raise an error cleanly
					}
				} else if (tokens[0].equals("mtllib")){
					String mtlFileName = new File(new File(objFileName).getParent(), tokens[1]).getPath();
					materials.putAll(loadMaterials(ctx, mtlFileName));
				} else if (tokens[0].equals("usemtl")) {
					currentMat = materials.get(tokens[1]);					
				} else {
					// XXX - TODO - we don't support that yet
				}

			}
		} catch (IOException e) {
			// XXX - TODO - Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		model.vertices = new float[vertices.size()];
		for (int i=0; i<model.vertices.length; i++)
			model.vertices[i] = vertices.get(i);
		if (texCoords.size() > 0) {
			model.texCoords = new float[texCoords.size()];
			for (int i=0; i<model.texCoords.length; i++)
				model.texCoords[i] = texCoords.get(i);
		}
		if (normals.size() > 0) {
			model.normals = new float[normals.size()];
			for (int i=0; i<model.normals.length; i++)
				model.normals[i] = normals.get(i);
		}
		model.faces = (faces.size() > 0) ? faces : null;
		model.materials = materials;
		model.minmax = minmax;
		return model;
	}

}
