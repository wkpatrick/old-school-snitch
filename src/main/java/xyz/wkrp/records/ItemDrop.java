package xyz.wkrp.records;

public class ItemDrop {
    private int itemId;
    private int amount;
    private String playerId;
    private int npcId;

    public ItemDrop(int itemId, int amount, String playerId) {
        this.itemId = itemId;
        this.amount = amount;
        this.playerId = playerId;
    }

    public ItemDrop(int itemId, int amount, int npcId, String playerId) {
        this.itemId = itemId;
        this.amount = amount;
        this.playerId = playerId;
        this.npcId = npcId;
    }
}

