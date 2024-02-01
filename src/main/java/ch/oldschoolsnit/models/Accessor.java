package ch.oldschoolsnit.models;

import java.lang.reflect.Array;

public class Accessor
{
	public int bufferView;
	public int byteOffset;
	public String type;
	public int componentType;
	public int count;
	public Float[] max;
	public Float[] min;

	public Accessor(int bufferView, int byteOffset, String type, int componentType, int count)
	{
		this.bufferView = bufferView;
		this.byteOffset = byteOffset;
		this.type = type;
		this.componentType = componentType;
		this.count = count;
	}
}
