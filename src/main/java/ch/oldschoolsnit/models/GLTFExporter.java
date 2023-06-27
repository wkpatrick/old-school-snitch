package ch.oldschoolsnit.models;

import ch.oldschoolsnit.Constants;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jgltf.obj.model.ObjGltfModelCreator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import net.runelite.api.Model;

public class GLTFExporter
{
	public static void exportObj(Model playerModel){
		final String modelName = Constants.modelName;
		OBJExporter.export(playerModel, modelName);
	}
	public static void convertGLTF(){
		//The models and tmp folder are created for us by OBJExporter, so I do not need to create them here.
		final String modelFolder = Constants.modelFolder.toString();
		final String modelNameWithExt = Constants.modelName + ".obj";

		String outputFileName = Constants.gltfPath.toString();
		final int indicesComponentType = GltfConstants.GL_UNSIGNED_SHORT;
		final boolean oneMeshPerPrimitive = false;

		URI objUri = Paths.get(modelFolder, modelNameWithExt).toUri();

		GltfModel gltfModel;
		ObjGltfModelCreator gltfModelCreator = new ObjGltfModelCreator();
		gltfModelCreator.setIndicesComponentType(indicesComponentType);
		gltfModelCreator.setOneMeshPerPrimitive(oneMeshPerPrimitive);
		try
		{
			gltfModel = gltfModelCreator.create(objUri);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		GltfModelWriter gltfModelWriter = new GltfModelWriter();

		File outputFile = new File(outputFileName);
		File parentFile = outputFile.getParentFile();
		if (parentFile != null)
		{
			parentFile.mkdirs();
		}

		try
		{
			gltfModelWriter.writeEmbedded(gltfModel, outputFile);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
