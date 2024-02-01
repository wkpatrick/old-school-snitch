package ch.oldschoolsnit.models;

import java.util.ArrayList;

public class Primitive
{
	public Attribute attributes;

	/*
	By default, the geometry data is assumed to describe a triangle mesh. For the case of indexed geometry,
	this means that three consecutive elements of the indices accessor are assumed to contain the indices of a single triangle.
	For non-indexed geometry, three elements of the vertex attribute accessors are assumed to contain the attributes of the three vertices of a triangle.
	 */
	public int indices;
	public int material; //The id of the material for the mesh.
	public int mode = 4;
}
