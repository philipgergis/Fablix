import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Star {

    // Star Variables
    private String name;
    private int birthYear;
    private String id;

    List<Movie> myMovies;

    public Star(){
        myMovies = new ArrayList<Movie>();
    }

    public Star(String id, String name) {
        this.name = name;
        this.id  = id;

    }
    public int getYear() {
        return birthYear;
    }

    public void setYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void addMovie(Movie m) {
        this.myMovies.add(m);
    }
    public String getMovies() {
        StringBuffer sb = new StringBuffer();

        Iterator<Movie> it = myMovies.iterator();

        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append(',');
        }

        return sb.toString();
    }

    public List<Movie> getGenreList(){
        return this.myMovies;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star's Movie Details - ");
        sb.append("ID:" + getId());
        sb.append(", ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(". ");

        return sb.toString();
    }

}