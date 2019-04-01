package BravoCI.Queue;

public class Package {
    private String userName;
    private String userRepository;

    public Package(String userName, String userRepository) {
        this.userName = userName;
        this.userRepository = userRepository;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRepository() {
        return userRepository;
    }

    @Override
    public String toString() {
        return userName + ":" + userRepository;
    }
}
