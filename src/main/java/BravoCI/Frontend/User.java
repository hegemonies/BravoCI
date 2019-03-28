package BravoCI.Frontend;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Id
    String id;
    String Name;
    List<CommitInfo> commits = new ArrayList<>();

    User() {}

    User(String Name) {
        this.Name = Name;
    }

    User(String Name, CommitInfo commit) {
        this.Name = Name;
        commits.add(commit);
    }

    public void addCommit(CommitInfo commit) {
        commits.add(commit);
    }

    public String getName() {
        return Name;
    }

    public List<CommitInfo> getCommits() {
        return commits;
    }
}
