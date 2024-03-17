package ch.oldschoolsnit.models;

import ch.oldschoolsnit.TextureColor;
import static ch.oldschoolsnit.models.JagexColor.createPalette;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Model;

public class GLTF
{
	public int scene = 0;
	public ArrayList<Node> nodes = new ArrayList<>();
	public ArrayList<Scene> scenes = new ArrayList<>();
	public ArrayList<Mesh> meshes = new ArrayList<>();
	/*
	Too lazy to deal with byte alignment and all that, so we just have two different buffers:
	0: Vertex Buffer
	1: Facet Buffer
	 */
	public Buffer[] buffers = new Buffer[2];
	public ArrayList<Material> materials = new ArrayList<>();
	public ArrayList<BufferView> bufferViews = new ArrayList<>();
	public ArrayList<Accessor> accessors = new ArrayList<>();
	public Asset asset = new Asset();
	private final transient int VERTEX_BUFFER_ID = 0;
	private final transient int FACET_BUFFER_ID = 1;
	/*
	Stolen off of Stackoverflow:
	GL_ELEMENT_ARRAY_BUFFER is used to indicate the buffer you're presenting contains the indices of each element in the "other" (GL_ARRAY_BUFFER) buffer.
	 */
	private final transient int GL_ARRAY_BUFFER = 34962;
	private final transient int GL_ELEMENT_ARRAY_BUFFER = 34963;
	private final transient int FLOAT_BYTE_LENGTH = 4;
	private final transient int SHORT_BYTE_LENGTH = 2;
	private final transient String VEC3 = "VEC3";
	private final transient String SCALAR = "SCALAR";
	private final transient int GL_FLOAT = 5126;
	private final transient int GLTF_UNSIGNED_SHORT = 5123;
	private transient int bufferViewIndex = 0;
	private transient int accessorIndex = 0;
	private final static int[] colorPalette = createPalette(JagexColor.BRIGHTNESS_MIN);
	private transient List<Color> knownColors = new ArrayList<>();

	public GLTF(Model model)
	{
		int vertex_index = 0;
		var vertex_buffer_size = model.getFaceCount() * 3 * 3 * FLOAT_BYTE_LENGTH; //3 floats per position, 3 positions per triangle
		var vertex_buffer = ByteBuffer.allocate(vertex_buffer_size);
		vertex_buffer.order(ByteOrder.LITTLE_ENDIAN);

		var idx_buffer_size = model.getFaceCount() * 3 * SHORT_BYTE_LENGTH; // 3 positions per face.
		var index_buffer = ByteBuffer.allocate(idx_buffer_size);
		index_buffer.order(ByteOrder.LITTLE_ENDIAN);

		var faceIndexes1 = model.getFaceIndices1();
		var faceIndexes2 = model.getFaceIndices2();
		var faceIndexes3 = model.getFaceIndices3();

		var vertsX = model.getVerticesX();
		var vertsY = model.getVerticesY();
		var vertsZ = model.getVerticesZ();

		var mesh = new Mesh();

		int materialIndex = 0;
		boolean hasTextures = model.getFaceTextures() != null;

		for (int faceIndex = 0; faceIndex < model.getFaceCount(); ++faceIndex)
		{
			int vi1 = faceIndexes1[faceIndex];
			int vi2 = faceIndexes2[faceIndex];
			int vi3 = faceIndexes3[faceIndex];

			//The indexes are always local to the accessor.
			index_buffer.putShort((short) 0);
			index_buffer.putShort((short) 1);
			index_buffer.putShort((short) 2);

			int byteOffset = faceIndex * 3 * SHORT_BYTE_LENGTH;
			int byteLength = 3 * SHORT_BYTE_LENGTH;

			var bView = new BufferView(FACET_BUFFER_ID, byteOffset, byteLength, GL_ELEMENT_ARRAY_BUFFER);
			bufferViews.add(bView);
			var accessor = new Accessor(bufferViewIndex, 0, SCALAR, GLTF_UNSIGNED_SHORT, 3);
			accessors.add(accessor);
			bufferViewIndex++;

			//Then create the bufferview and accessor that is for ALL 3 verts.
			int vertByteOffset = vertex_index * 9 * FLOAT_BYTE_LENGTH;
			int vertByteLength = 9 * FLOAT_BYTE_LENGTH;

			var vertBView = new BufferView(VERTEX_BUFFER_ID, vertByteOffset, vertByteLength, GL_ARRAY_BUFFER);
			bufferViews.add(vertBView);
			var vertAccessor = new Accessor(bufferViewIndex, 0, VEC3, GL_FLOAT, 3);
			bufferViewIndex++;
			var coords = new ArrayList<Float>();

			//Add the 3 verts to the vert buffer
			var coord1 = AddVert(vi1, vertsX, vertsY, vertsZ, vertex_buffer);
			var coord2 = AddVert(vi2, vertsX, vertsY, vertsZ, vertex_buffer);
			var coord3 = AddVert(vi3, vertsX, vertsY, vertsZ, vertex_buffer);

			coords.addAll(List.of(coord1));
			coords.addAll(List.of(coord2));
			coords.addAll(List.of(coord3));

			vertAccessor.min = VecUtil.GetMin(coords);
			vertAccessor.max = VecUtil.GetMax(coords);

			accessors.add(vertAccessor);

			var prim = new Primitive();
			prim.indices = accessorIndex;
			var attr = new Attribute();
			attr.POSITION = accessorIndex + 1;
			prim.attributes = attr;

			//Create the material here

			//This is practically lifted from Bram91's obj exporter. Therefore:
			/*
			 * BSD 2-Clause License
			 *
			 * Copyright (c) 2020, bram91
			 * All rights reserved.
			 *
			 * Redistribution and use in source and binary forms, with or without
			 * modification, are permitted provided that the following conditions are met:
			 *
			 * 1. Redistributions of source code must retain the above copyright notice, this
			 *    list of conditions and the following disclaimer.
			 *
			 * 2. Redistributions in binary form must reproduce the above copyright notice,
			 *    this list of conditions and the following disclaimer in the documentation
			 *    and/or other materials provided with the distribution.
			 *
			 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
			 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
			 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
			 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
			 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
			 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
			 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
			 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
			 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
			 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
			 */

			//Sometimes we can get the color off of the texture
			Color c;
			if (hasTextures)
			{
				var textureId = model.getFaceTextures()[faceIndex];
				c = TextureColor.getColor(textureId);
			}
			//Sometimes we have to get it from the average colors of the vertices.
			else
			{
				if (model.getFaceColors3()[faceIndex] == -1)
				{
					// face should be shaded flat
					int colorIndex = model.getFaceColors1()[faceIndex];
					int rgbColor = colorPalette[colorIndex];
					c = new Color(rgbColor);
				}
				else
				{
					// get average color of vertices
					int c1 = model.getFaceColors1()[faceIndex];
					int c2 = model.getFaceColors2()[faceIndex];
					int c3 = model.getFaceColors3()[faceIndex];
					c = JagexColor.HSLtoRGBAvg(c1, c2, c3);
				}
			}

			int ci = knownColors.indexOf(c);
			var mat = new Material();
			if (ci == -1)
			{
				knownColors.add(c);

				double r = (double) c.getRed() / 255.0d;
				double g = (double) c.getGreen() / 255.0d;
				double b = (double) c.getBlue() / 255.0d;


				var roughness = new PbrMetallicRoughness();
				roughness.baseColorFactor = new double[]{r, g, b, 0.0D};
				mat.pbrMetallicRoughness = roughness;
				materials.add(mat);

				prim.material = materialIndex;
				materialIndex++;
			}
			else
			{
				prim.material = ci;
			}

			//Bram91 plagiarism end

			mesh.primitives.add(prim);

			accessorIndex += 2;
			vertex_index++;
		}

		buffers[0] = new Buffer(vertex_buffer);
		buffers[1] = new Buffer(index_buffer);
		meshes.add(mesh);
		var node = new Node();
		nodes.add(node);
		var scene = new Scene();
		scene.nodes.add(0);
		scenes.add(scene);
	}

	public Float[] AddVert(int vertIndex, int[] vertsX, int[] vertsY, int[] vertsZ, ByteBuffer vertexBuffer)
	{
		float x = (float) vertsX[vertIndex];
		float y = (float) -vertsY[vertIndex];
		float z = (float) -vertsZ[vertIndex];

		vertexBuffer.putFloat(x);
		vertexBuffer.putFloat(y);
		vertexBuffer.putFloat(z);

		return new Float[]{x, y, z};
	}

	public String ToJson(Gson gson)
	{
		var built = gson.newBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
			.create();
		return built.toJson(this);
	}
}