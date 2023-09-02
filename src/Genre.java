import java.util.HashMap;

public class Genre {

    // Genre variables
    private String name;
    private Integer id;
    private HashMap<String, Integer> genres = new HashMap<String, Integer>();

    public Genre(){}

    public Genre(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (genres.containsKey(name.toLowerCase())) {
            this.name = name;
        } else {
            this.name = name;
            genres.put(name.toLowerCase(), id);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Genre Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("ID:" + getId());
        sb.append(".");
        return sb.toString();
    }
}