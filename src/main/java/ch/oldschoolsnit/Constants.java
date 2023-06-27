package ch.oldschoolsnit;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.runelite.client.RuneLite;

public class Constants
{
	public static final String modelName = "player_model";
	public static final String gltfModelName = modelName + ".gltf";
	public static final String objfModelName = modelName + ".obj";
	public static final String mtlModelName = modelName + ".mtl";
	public static final Path modelFolder = Paths.get(RuneLite.RUNELITE_DIR.getAbsolutePath(), "models", "tmp");
	public static final Path gltfPath = Paths.get(modelFolder.toString(), gltfModelName);
	public static final Path objPath = Paths.get(modelFolder.toString(), objfModelName);
	public static final Path mtlPath = Paths.get(modelFolder.toString(), mtlModelName);

}
