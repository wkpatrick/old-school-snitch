package ch.oldschoolsnit.models;

import java.nio.ByteBuffer;
import java.util.Base64;

public class Buffer
{
	public String uri = "data:application/gltf-buffer;base64,";
	public int byteLength;

	public Buffer(ByteBuffer inputBuffer)
	{
		var roBuffer = inputBuffer.asReadOnlyBuffer();
		roBuffer.position(0);
		byte[] bytes = new byte[roBuffer.limit()];
		roBuffer.get(bytes, 0, bytes.length);
		var base64 = Base64.getEncoder().encodeToString(bytes);

		uri += base64;
		byteLength = roBuffer.limit();
	}
}
