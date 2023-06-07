package ch.oldschoolsnit.records;

public class NameSignIn {
    private String runescapeName;
    private String apiKey;
    private Long accountHash;

    public NameSignIn(String runescapeName, String apiKey, Long accountHash) {
        this.runescapeName = runescapeName;
        this.apiKey = apiKey;
        this.accountHash = accountHash;
    }
}
