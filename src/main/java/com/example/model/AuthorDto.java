package com.example.model;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class AuthorDto {
    private String authorId;
    private String authorName;
    private List<BookDto> bookList;
    private LocalDate birthday;

    public List<BookDto> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookDto> bookList) {
        this.bookList = bookList;
    }

    public AuthorDto(String authorId, String authorName, List<BookDto> bookList, LocalDate birthday) {
        this.authorId   = authorId;
        this.authorName = authorName;
        this.bookList = bookList;
        this.birthday = birthday;
    }

    public AuthorDto() {
    }

    public AuthorDto(String authorId, String authorName, LocalDate birthday) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.birthday = birthday;
        this.bookList = new ArrayList<>();
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "AuthorDto{" +
                "authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
