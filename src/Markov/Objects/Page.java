package Markov.Objects;


public class Page {
    public String writer;
    public String title;
    public String link;
    public String id;

    public Page() {


    }

    public Page(String title, String link) {
        this.title = title;
        this.link = link;
        this.id = link;
    }
}
