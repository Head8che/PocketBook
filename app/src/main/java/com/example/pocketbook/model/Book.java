package com.example.pocketbook.model;

public class Book implements Comparable<Book>{
    private String id;
    private String title;
    private String author;
    private String ISBN;
    private String owner;
    private String comment;
    private String status;
    private String photo;

    /**
     * Firestore constructor
     *  to populate a book
     */
    public Book() {}

    /**
     * Minimum arg constructor for Book
     * @param id : uniquely identifies the book in the db
     * @param title : title of book
     * @param author : author of book
     * @param ISBN : ISBN as a String
     * @param owner : userId of the book's owner
     */
    public Book(String id, String title, String author, String ISBN, String owner) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.owner = owner;
    }

    /**
     * Maximum arg constructor for Book
     * @param id : unique book id
     * @param title : title of book
     * @param author : author of book
     * @param ISBN : isbn retrieved as String of Book
     * @param owner : userId of the book's owner
     * @param comment : Comment set by owner
     * @param status : indicates availability of book (available, requested, accepted, borrowed)
     * @param photo : photo of book by owner
     */
    public Book(String id, String title, String author, String ISBN, String owner,
                String comment, String status, String photo ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.owner = owner;
        this.comment = comment;
        this.status = status;
        this.photo = photo;
    }

    /* Getter Functions */
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getISBN() { return ISBN; }
    public String getOwner() { return owner; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public String getPhoto() { return photo; }

    /* Setter Functions */
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setComment(String comment) { this.comment = comment; }
    public void setStatus(String status) { this.status = status; }
    public void setPhoto(String photo) { this.photo = photo; }

    /* Allows comparison between bookIds as Strings
    *   Overrides compareTo method in Comparable
    * */
    @Override
    public int compareTo(com.example.pocketbook.model.Book book) {
        return this.id.compareTo(book.getId());
    }

}
