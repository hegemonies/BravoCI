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

    void add(CommitInfo commit) {
        commitInfos.add(commit);
    }

    @Override
    public boolean equals(Object repository) {
        Repository r = (Repository) repository;
        return name.equals(r.getName());
    }
}
