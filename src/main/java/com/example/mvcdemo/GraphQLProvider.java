package com.example.mvcdemo;

import com.example.model.AuthorDto;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.com.google.common.base.Charsets;
import graphql.language.*;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {

    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;

    private GraphQL graphQL;

    private HashMap<String, String> registeredType = new HashMap<>();

    private static final String GRAPHQLTYPE_BYTE = "Byte";
    private static final String GRAPHQLTYPE_INT = "Int";
    private static final String GRAPHQLTYPE_LONG = "Long";
    private static final String GRAPHQLTYPE_STRING = "String";
    private static final String GRAPHQLTYPE_BOOLEAN = "Boolean";
    private static final String GRAPHQLTYPE_FLOAT = "Float";
    private static final String GRAPHQLTYPE_BIGDECIMAL = "BigDecimal";
    private static final String GRAPHQLTYPE_LOCALDATE = "LocalDate";
    private static final String GRAPHQLTYPE_LISTTYPE = "ListType";

    private static final HashMap<String, String> typeMapper = new HashMap<String, String>() {
        {
            put(int.class.getName(), GRAPHQLTYPE_INT);
            put(long.class.getName(), GRAPHQLTYPE_LONG);
            put(short.class.getName(), GRAPHQLTYPE_INT);
            put(byte.class.getName(), GRAPHQLTYPE_BYTE);
            put(float.class.getName(), GRAPHQLTYPE_FLOAT);
            put(double.class.getName(), GRAPHQLTYPE_FLOAT);
            put(boolean.class.getName(), GRAPHQLTYPE_BOOLEAN);
            put(java.lang.Integer.class.getName(), GRAPHQLTYPE_INT);
            put(java.lang.Long.class.getName(), GRAPHQLTYPE_LONG);
            put(java.lang.Short.class.getName(), GRAPHQLTYPE_INT);
            put(java.lang.Byte.class.getName(), GRAPHQLTYPE_BYTE);
            put(java.lang.Float.class.getName(), GRAPHQLTYPE_FLOAT);
            put(java.lang.Double.class.getName(), GRAPHQLTYPE_FLOAT);
            put(java.lang.Boolean.class.getName(), GRAPHQLTYPE_BOOLEAN);
            put(java.lang.String.class.getName(), GRAPHQLTYPE_STRING);
            put(java.util.List.class.getName(), GRAPHQLTYPE_LISTTYPE);
            put(java.util.Map.class.getName(), GRAPHQLTYPE_LISTTYPE);
            put(java.util.ArrayList.class.getName(), GRAPHQLTYPE_LISTTYPE);
            put(java.util.Arrays.class.getName(), GRAPHQLTYPE_LISTTYPE);
            put(java.time.LocalDate.class.getName(), GRAPHQLTYPE_LOCALDATE);
            put(java.math.BigDecimal.class.getName(), GRAPHQLTYPE_BIGDECIMAL);
        }
    };

    @PostConstruct
    public void init() throws IOException {

        URL url = Resources.getResource("schema1.graphqls");

        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);

        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void registerSchema(TypeDefinitionRegistry typeRegistry, Class cls) {

        // 判断是否已经被注册过，如果有的话则不用重复注册
        if (typeRegistry.getType(cls.getSimpleName()).isPresent()) {
            return;
        }

        //1. get  class name
        ObjectTypeDefinition.Builder objectTypeBuilder = ObjectTypeDefinition.newObjectTypeDefinition();
        objectTypeBuilder.name(cls.getSimpleName());

        //2. get field info
        Field[] declaredFields = cls.getDeclaredFields();

        for (Field field : declaredFields) {

            Directive directive = null;

            String typeName = typeMapper.get(field.getType().getName());
            if (!StringUtils.hasText(typeName)){
                typeName = GRAPHQLTYPE_STRING;
                System.out.println("unmapping datatype use String instead");
            }

            graphql.language.Type type = null;

            if (typeName == GRAPHQLTYPE_LISTTYPE) {

                Type genericType = field.getGenericType();

                if (null != genericType && genericType instanceof ParameterizedType) {

                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class genericClazz = (Class) pt.getActualTypeArguments()[0];

                    //todo... 如果list里面还是复杂类型
                    String genericClsName = typeMapper.get(genericClazz.getTypeName());
                    if (null == genericClsName) {
                        genericClsName = genericClazz.getSimpleName();
                    }

                    type = ListType.newListType(new TypeName(genericClsName)).build();

                    registerSchema(typeRegistry, genericClazz);

                } else {
                    System.out.println(String.format("unknown type :%s ", field.getType().getName()));
                    continue;
                }
            } else {
//                if (field.getType().getName().equals(java.util.Date.class.getName())) {
//                    directive = Directive.newDirective().name("dateFormat").build();
//                }
                type = new TypeName(typeName);
            }

            FieldDefinition.Builder fieldBuilder = FieldDefinition.newFieldDefinition()
                    .name(field.getName())
                    .type(type);

//            if (null != directive) {
//                fieldBuilder.directive(directive);
//            }

            objectTypeBuilder.fieldDefinition(fieldBuilder.build());
        }

        typeRegistry.add(objectTypeBuilder.build());
    }

    private GraphQLSchema buildSchema(String sdl) {

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);

        registerSchema(typeRegistry, AuthorDto.class);

        RuntimeWiring runtimeWiring = buildWiring();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.LocalDate)
                .type(newTypeWiring("Query")
                        .dataFetcher("getAuthor", graphQLDataFetchers.getAuthorDataFetcher())
                        .dataFetcher("getBook", graphQLDataFetchers.getBookDataFetcher())
                        .dataFetcher("getBookCount", graphQLDataFetchers.getBookCountDataFetcher())
                )
                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }
}
