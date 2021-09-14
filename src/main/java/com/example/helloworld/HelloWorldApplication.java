package com.example.helloworld;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {

        String schema = "type Query{hello: String} schema{query: Query}";

        //1. 解析schema
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        //2. 构造查询方法
        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        //3. 创建可执行schema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        //4. 创建graphql
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        ExecutionResult executionResult = graphQL.execute("{hello}");

        System.out.println(executionResult.getData().toString());
    }
}
