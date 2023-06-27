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

//additional modifications made by WKRP for the OldSchoolSnitch plugin.

package ch.oldschoolsnit.models;

import ch.oldschoolsnit.Constants;
import ch.oldschoolsnit.TextureColor;
import java.io.File;
import java.util.Formatter;
import net.runelite.api.Model;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OBJExporter
{

	private final static String PATH = Constants.modelFolder.toString();

	public static void export(Model m, String name)
	{
		try
		{
			File folder = new File(PATH);

			if (!folder.exists())
			{
				folder.mkdir();
			}
			exportModel(m, PATH, name);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private static void exportModel(Model m, String folderPath, String name) throws FileNotFoundException
	{
		if (m == null)
			return;

		name = name.replace(" ", "_");
		// Open writers
		StringBuilder sbObj = new StringBuilder();
		StringBuilder sbMtl = new StringBuilder();
		Formatter fm = new Formatter(sbMtl);

		sbObj.append("# Made by RuneLite Model-Dumper Plugin");
		sbObj.append("o ").append(name);
		sbObj.append("\n");

		// Write vertices
		for (int vi=0; vi < m.getVerticesCount(); ++vi)
		{
			// Y and Z axes are flipped
			int vx = m.getVerticesX()[vi];
			int vy = -m.getVerticesY()[vi];
			int vz = -m.getVerticesZ()[vi];
			sbObj.append("v ").append(vx).append(" ").append(vy).append(" ").append(vz);
			sbObj.append("\n");
		}

		// Write faces
		List<Color> knownColors = new ArrayList<>();
		int prevMtlIndex = -1;
		for (int fi=0; fi < m.getFaceCount(); ++fi)
		{
			// determine face color (textured or colored?)
			Color c;
			int textureId = -1;
			if (m.getFaceTextures() != null)
				textureId = m.getFaceTextures()[fi];
			if (textureId != -1)
			{
				// get average color of texture
				c = TextureColor.getColor(textureId);
			}
			else
			{
				// get average color of vertices
				int c1 = m.getFaceColors1()[fi];
				int c2 = m.getFaceColors2()[fi];
				int c3 = m.getFaceColors3()[fi];
				c = JagexColor.HSLtoRGBAvg(c1, c2, c3);
			}

			// see if our color already has a mtl
			int ci = knownColors.indexOf(c);
			if (ci == -1)
			{
				// add to known colors
				ci = knownColors.size();
				knownColors.add(c);

				// int to float color conversion
				double r = (double) c.getRed() / 255.0d;
				double g = (double) c.getGreen() / 255.0d;
				double b = (double) c.getBlue() / 255.0d;

				// write mtl
				sbMtl.append("newmtl c").append(ci);
				sbMtl.append("\n");
				fm.format("Kd %.4f %.4f %.4f\n", r, g, b);
				sbMtl.append("Ka 0 0 0");
				sbMtl.append("\n");
				sbMtl.append("Ns 0");
				sbMtl.append("\n");
			}

			// only write usemtl if the mtl has changed
			if (prevMtlIndex != ci)
			{
				sbObj.append("usemtl c").append(ci);
				sbObj.append("\n");
			}

			// OBJ vertices are indexed by 1
			int vi1 = m.getFaceIndices1()[fi] + 1;
			int vi2 = m.getFaceIndices2()[fi] + 1;
			int vi3 = m.getFaceIndices3()[fi] + 1;
			sbObj.append("f ").append(vi1).append(" ").append(vi2).append(" ").append(vi3);
			sbObj.append("\n");

			prevMtlIndex = ci;
		}

		PrintWriter obj = new PrintWriter(Constants.objPath.toFile());
		PrintWriter mtl = new PrintWriter(Constants.mtlPath.toFile());

		obj.print(sbObj);
		mtl.print(sbMtl);

		// flush output buffers
		obj.flush();
		mtl.flush();
		obj.close();
		mtl.close();
	}

}
