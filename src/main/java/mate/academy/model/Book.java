package mate.academy.model;

import java.math.BigDecimal;
import java.util.List;

public class Book {
    private Long id;
    private String title;
    private BigDecimal price;
    private LiteraryFormat format;
    private List<Author> authors;

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LiteraryFormat getFormat() {
        return format;
    }

    public void setFormat(LiteraryFormat format) {
        this.format = format;
    }

    @Override
    public String toString() {
        //String a =  authors.stream().reduce((ac,e) -> ac + e.toString());
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", format=" + format +
                ", authors=" + authors +
                '}';
    }
}

