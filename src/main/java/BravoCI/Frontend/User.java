package BravoCI.Frontend;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Id
    String id;
    private String name;
    private List<Repository> repositories = new ArrayList<>();

    public User() {}

    public User(String name) {
        this.name = name;
    }

    public User(String name, String repository) {
        this.name = name;

        System.out.println(name + " contain " + repository + "? : " + repositories.contains(repository));

        if (!repositories.contains(repository)) {
            repositories.add(new Repository(repository));
        }
    }

    public String getName() {
        return this.name;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void addRepository(String repository) {
        Repository r = new Repository(repository);
        if (!repositories.contains(r)) {
            repositories.add(r);
        }
    }

    public void addCommit(String repository, CommitInfo commit) {
        repositories.get(repositories.indexOf(repository)).add(commit);
    }

    @Override
    public boolean equals(Object user) {
        User u = (User) user;
        return this.name.equals(u.getName());
    }
}
