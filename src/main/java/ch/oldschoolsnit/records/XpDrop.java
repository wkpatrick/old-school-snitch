package ch.oldschoolsnit.records;

public class XpDrop
{
	private String skill;
	private Integer amount;
	private Integer currentTotal;
	private String apiKey;
	private Long accountHash;

	public XpDrop(String skill, Integer amount, Integer currentTotal, String apiKey, Long accountHash)
	{
		this.skill = skill;
		this.amount = amount;
		this.currentTotal = currentTotal;
		this.apiKey = apiKey;
		this.accountHash = accountHash;
	}
}

