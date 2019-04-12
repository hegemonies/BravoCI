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

        if (!repositories.contains(new Repository(repository))) {
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

    public Repository getRepository(String repository_name) {
        for (Repository repo : repositories) {
            if (repo.getName().equals(repository_name)) {
                return repo;
            }
        }

        return null;
    }

    public void addCommit(String repository, CommitInfo commit) {
//        repositories.get(repositories.indexOf(repository)).add(commit);
        for (Repository repo : repositories) {
            if (repo.getName().equals(repository)) {
                repo.add(commit);
            }
        }
    }

    public void changeCommit(String repository, CommitInfo commit) {
        for (Repository repo : repositories) {
            if (repo.getName().equals(repository)) {
                repo.changeCommit(commit);
            }
        }
    }

    public CommitInfo getCommit(String repository, String hash) {
        for (Repository _repository : repositories) {
            if (_repository.getName().equals(repository)) {
                return _repository.getCommitInfo(hash);
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object user) {
        User u = (User) user;
        return this.name.equals(u.getName());
    }
}
