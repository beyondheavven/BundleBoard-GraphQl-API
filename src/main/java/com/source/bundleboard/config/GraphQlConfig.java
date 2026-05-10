package com.source.bundleboard.config;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(
                GraphQLScalarType.newScalar()
                        .name("Upload")
                        .coercing(new NoOpCoercing())
                        .build()
        );
    }

    private static class NoOpCoercing implements Coercing<Object, Object> {
        public Object serialize(Object dataFetcherResult) { return dataFetcherResult; }
        public Object parseValue(Object input) { return input; }
        public Object parseLiteral(Object input) { return input; }
    }
}
