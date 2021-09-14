package com.example.mvcdemo;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class LocalDateScalar {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static GraphQLScalarType INSTANCE;

    public LocalDateScalar() {
    }

    static {
        Coercing<LocalDate, String> coercing = new Coercing<LocalDate, String>() {

            public String serialize(Object input) throws CoercingSerializeException {

                Object temporalAccessor;

                if (input instanceof TemporalAccessor) {
                    temporalAccessor = (TemporalAccessor) input;
                } else {
                    if (!(input instanceof String)) {
                        throw new CoercingSerializeException("Expected a 'String' or 'java.time.temporal.TemporalAccessor'");
                    }

                    temporalAccessor = this.parseLocalDate(input.toString(), CoercingSerializeException::new);
                }

                try {
                    return LocalDateScalar.dateFormatter.format((TemporalAccessor) temporalAccessor);
                } catch (DateTimeException var4) {
                    throw new CoercingSerializeException("Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'.");
                }
            }

            public LocalDate parseValue(Object input) throws CoercingParseValueException {

                Object temporalAccessor;

                if (input instanceof TemporalAccessor) {
                    temporalAccessor = (TemporalAccessor) input;
                } else {
                    if (!(input instanceof String)) {
                        throw new CoercingParseValueException("Expected a 'String' or 'java.time.temporal.TemporalAccessor ");
                    }

                    temporalAccessor = this.parseLocalDate(input.toString(), CoercingParseValueException::new);
                }

                try {
                    return LocalDate.from((TemporalAccessor) temporalAccessor);
                } catch (DateTimeException var4) {
                    throw new CoercingParseValueException("Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'.");
                }
            }

            public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
                if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException("Expected AST type 'StringValue'");
                } else {
                    return this.parseLocalDate(((StringValue) input).getValue(), CoercingParseLiteralException::new);
                }
            }

            public Value<?> valueToLiteral(Object input) {
                String s = this.serialize(input);
                return StringValue.newStringValue(s).build();
            }

            private LocalDate parseLocalDate(String s, Function<String, RuntimeException> exceptionMaker) {
                try {
                    TemporalAccessor temporalAccessor = LocalDateScalar.dateFormatter.parse(s);
                    return LocalDate.from(temporalAccessor);
                } catch (DateTimeParseException var4) {
                    throw (RuntimeException) exceptionMaker.apply("Invalid RFC3339 full date value : '" + s + "'. because of : '" + var4.getMessage() + "'");
                }
            }
        };

        INSTANCE = GraphQLScalarType.newScalar().name("LocalDate").description("An RFC-3339 compliant Full Date Scalar").coercing(coercing).build();
    }
}
