package xyz.wkrp.records;

public class XpDrop {
    private String skill;
    private Integer amount;
    private Integer currentTotal;
    private String playerId;

    public XpDrop(String skill, Integer amount, Integer currentTotal, String playerId) {
        this.skill = skill;
        this.amount = amount;
        this.currentTotal = currentTotal;
        this.playerId = playerId;
    }
}

