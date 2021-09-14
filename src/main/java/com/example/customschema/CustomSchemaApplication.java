package com.example.customschema;

import com.example.model.AuthorDto;
import com.example.model.BookDto;
import com.google.common.io.Resources;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.com.google.common.base.Charsets;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@SpringBootApplication
public class CustomSchemaApplication implements ApplicationRunner {

    private static List<BookDto> bookList = new ArrayList() {
        {
            add(new BookDto("book1", "bookName1", "author1"));
        }
    };

    private static List<AuthorDto> authorList = new ArrayList() {{
        add(new AuthorDto("author1", "authorName1", LocalDate.of(1975,10,12)));
    }};

    public static void main(String[] args) {
        SpringApplication.run(CustomSchemaApplication.class);
    }

    private DataFetcher getAuthorDataFetcher() {

        DataFetcher authorDataFetcher = new DataFetcher<AuthorDto>() {
            @Override
            public AuthorDto get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

                String authorId = dataFetchingEnvironment.getArgument("id");
                return getAuthor(authorId);

            }
        };

        return authorDataFetcher;
    }

    private AuthorDto getAuthor(String authorId) {
        Optional<AuthorDto> first = authorList.stream().filter(item -> item.getAuthorId().equals(authorId)).findFirst();
        return first.get();
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("getAuthor", getAuthorDataFetcher())
                )
                .build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //1. 解析schema
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(sdl);

        //2. 构造查询方法
        RuntimeWiring runtimeWiring = buildWiring();

        //3. 创建可执行schema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        //4. 创建graphql
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        ExecutionResult executionResult = graphQL.execute("{ getAuthor ( id : \"author1\" ) }");

        System.out.println(executionResult.getData().toString());
    }
}
