package ch.oldschoolsnit.records;

public class ItemDrop {
    private Integer itemId;
    private Integer amount;
    private String apiKey;
    private Long accountHash;
    private Integer npcId = null;

    public ItemDrop(int itemId, int amount, String apiKey, Long accountHash) {
        this.itemId = itemId;
        this.amount = amount;
        this.apiKey = apiKey;
        this.accountHash = accountHash;
    }

    public ItemDrop(int itemId, int amount, int npcId, String apiKey, Long accountHash) {
        this.itemId = itemId;
        this.amount = amount;
        this.npcId = npcId;
        this.apiKey = apiKey;
        this.accountHash = accountHash;
    }
}

