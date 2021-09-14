package com.example.mvcdemo;

import com.example.model.AuthorDto;
import com.example.model.BookDto;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GraphQLDataFetchers {

    private static List<BookDto> bookList = new ArrayList() {
        {
            add(new BookDto("book1", "bookName1", "author1"));
        }
    };

    private static List<AuthorDto> authorList = new ArrayList() {{
        add(new AuthorDto("author1", "authorName1", LocalDate.of(1975,10,12)));
    }};

    public DataFetcher getAuthorDataFetcher() {

        DataFetcher authorDataFetcher = new DataFetcher<AuthorDto>() {
            @Override
            public AuthorDto get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                String authorId = dataFetchingEnvironment.getArgument("id");
                return getAuthor(authorId);
            }
        };

        return authorDataFetcher;
    }

    public DataFetcher getBookDataFetcher() {

        DataFetcher fetcher = new DataFetcher<BookDto>() {
            @Override
            public BookDto get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

                String id = dataFetchingEnvironment.getArgument("id");
                return getBook(id);
            }
        };

        return fetcher;
    }

    public DataFetcher getBookCountDataFetcher() {

        DataFetcher fetcher = new DataFetcher() {
            @Override
            public Integer get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                return getBookCount();
            }
        };

       return fetcher;
    }


    private BookDto getBook(String bookId){
        Optional<BookDto> first = bookList.stream().filter(item -> item.getBookId().equals(bookId)).findFirst();
        return first.get();
    }

    private int getBookCount(){
        return 1;
    }

    private AuthorDto getAuthor(String authorId){
        Optional<AuthorDto> first = authorList.stream().filter(item -> item.getAuthorId().equals(authorId)).findFirst();
        return first.get();
    }
}
