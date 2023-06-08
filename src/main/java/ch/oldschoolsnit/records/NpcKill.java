package ch.oldschoolsnit.records;

public class NpcKill
{
	private int NpcId;
	private String apiKey;
	private Long accountHash;

	public NpcKill(int npcId, String apiKey, Long accountHash)
	{
		NpcId = npcId;
		this.apiKey = apiKey;
		this.accountHash = accountHash;
	}
}