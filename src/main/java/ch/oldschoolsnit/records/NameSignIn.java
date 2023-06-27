package ch.oldschoolsnit.records;

public class NameSignIn
{
	private String runescapeName;
	private String apiKey;
	private Long accountHash;
	private Integer accountType;

	public NameSignIn(String runescapeName, String apiKey, Long accountHash, Integer accountType)
	{
		this.runescapeName = runescapeName;
		this.apiKey = apiKey;
		this.accountHash = accountHash;
		this.accountType = accountType;
	}
}
