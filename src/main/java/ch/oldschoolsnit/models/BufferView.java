package ch.oldschoolsnit.models;

public class BufferView
{
	//The index of the respective buffer this view references
	public int buffer;
	public int byteOffset;
	public int byteLength;
	public int target;

	public BufferView(int bufferId, int byteOffset, int byteLength, int targetDataType){
		this.buffer = bufferId;
		this.byteOffset = byteOffset;
		this.byteLength = byteLength;
		this.target = targetDataType;

	}

}
