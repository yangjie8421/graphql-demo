package com.example.model;

import org.springframework.stereotype.Component;

@Component
public class BookDto {
    private String bookId;
    private String bookName;
    private String authorId;

    public BookDto() {
    }

    public BookDto(String bookId, String bookName, String authorId) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorId = authorId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
