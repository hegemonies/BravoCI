package BravoCI.Frontend;

import java.util.ArrayList;

class Repository {
    private String name;
    private ArrayList<CommitInfo> commitInfos = new ArrayList<>();

    public Repository() {}

    public Repository(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<CommitInfo> getCommitInfos() {
        return commitInfos;
    }

    public void add(CommitInfo commit) {
        commitInfos.add(commit);
    }

    public void changeCommit(CommitInfo new_commit) {
        for (CommitInfo commitInfo : commitInfos) {
            if (commitInfo.getHashCommit().equals(new_commit.getHashCommit())) {
                commitInfo.setResult(new_commit.getResult());
                commitInfo.setCheck(new_commit.isCheck());
            }
        }
    }

    public CommitInfo getCommitInfo(String hash) {
        for (CommitInfo commitInfo : commitInfos) {
            if (commitInfo.getHashCommit().equals(hash)) {
                return commitInfo;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object repository) {
        Repository r = (Repository) repository;
        return name.equals(r.getName());
    }
}
