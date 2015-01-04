package iz.simplewebview.data;

public final class Favorite {
    public static final Favorite HOME;
    static {
        HOME = new Favorite();
        HOME.name ="Home";
        HOME.url = null;
        HOME.important = false;
    }

    public long id;
    public String name;
    public String url;
    public boolean important;


    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", important=" + important +
                '}';
    }
}
