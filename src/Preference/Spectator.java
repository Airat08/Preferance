package Preference;

public class Spectator {
    private String firstName;
    private String secondName;

    public Spectator(String firstName, String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(firstName).append(" ")
                .append(secondName);
        return stringBuilder.toString();
    }
}
