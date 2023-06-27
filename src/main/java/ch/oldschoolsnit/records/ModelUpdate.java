package ch.oldschoolsnit.records;

public class ModelUpdate
{
	private String minifiedGltf;
	private Long accountHash;
	private String apiKey;

	public ModelUpdate(String minifiedGltf, Long accountHash, String apiKey)
	{
		this.minifiedGltf = minifiedGltf;
		this.accountHash = accountHash;
		this.apiKey = apiKey;
	}
}
