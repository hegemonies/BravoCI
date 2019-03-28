package BravoCI.Frontend;

public class CommitInfo {
    private String hashCommit;
    private String result;
    private boolean check;

    public CommitInfo() {}

    public CommitInfo(String hashCommit, String result, boolean check) {
        this.hashCommit = hashCommit;
        this.result = result;
        this.check = check;
    }

    public String getHashCommit() {
        return hashCommit;
    }

    public String getResult() {
        return result;
    }

    public boolean isCheck() {
        return check;
    }
}
