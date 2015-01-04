package iz.simplewebview.data;

public class Settings {
    public String homeUrl = "http://www.google.com";
    public String password;

    @Override
    public String toString() {
        return "Settings{" +
                "homeUrl='" + homeUrl + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
