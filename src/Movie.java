import java.util.ArrayList;
import java.util.Iterator;

public class Movie {

    // Movie private variables
    private String title;
    private int year;
    private String id;
    private String director;

    ArrayList<Genre> genres;
    ArrayList<Star> stars;

    public Movie() {
        genres = new ArrayList<>();
        stars = new ArrayList<>();
    }

    public Movie(String title, String id, int year, String director) {
        this.title = title;
        this.year = year;
        this.id = id;
        this.director = director;

        genres = new ArrayList<>();
        stars = new ArrayList<>();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    public String getGenres() {
        StringBuffer sb = new StringBuffer();

        Iterator<Genre> it = genres.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append(',');
        }
        return sb.toString();
    }

    public ArrayList<Genre> getGenreList() {
        return this.genres;
    }

    public String getStars() {
        StringBuffer sb = new StringBuffer();

        Iterator<Star> it = stars.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append(',');
        }
        return sb.toString();
    }

    public ArrayList<Star> getStarList() {
        return this.stars;
    }

    public void setStars(ArrayList<Star> stars) {
        this.stars = stars;
    }

    public void addStar(Star star) {
        this.stars.add(star);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("ID:" + getId());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Genres:" + getGenres());
        sb.append(", ");
        sb.append(("Stars:" + getStars()));
        sb.append(".");

        return sb.toString();
    }
}