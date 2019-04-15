package BravoCI.Frontend;

public class CommitInfo {
    private String hashCommit;
    private String date;
    private String result;
    private boolean check;

    public CommitInfo() {}

    public CommitInfo(String hashCommit, String date, String result, boolean check) {
        this.hashCommit = hashCommit;
        this.date = date;
        this.result = result;
        this.check = check;
    }

    public String getHashCommit() {
        return hashCommit;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public boolean isCheck() {
        return check;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
