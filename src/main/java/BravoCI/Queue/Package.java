package BravoCI.Queue;

public class Package {
    private String userName;
    private String userRepository;
    private String commitInfo;

    public Package(String userName, String userRepository, String commitInfo) {
        this.userName = userName;
        this.userRepository = userRepository;
        this.commitInfo = commitInfo;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRepository() {
        return userRepository;
    }

    public String getCommitInfo() {
        return commitInfo;
    }

    @Override
    public String toString() {
        return userName + ":" + userRepository + ":" + commitInfo;
    }
}
