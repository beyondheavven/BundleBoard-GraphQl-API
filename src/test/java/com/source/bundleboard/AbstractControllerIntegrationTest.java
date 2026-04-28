package com.source.bundleboard;

import com.source.bundleboard.auth.dto.AuthRequest;
import com.source.bundleboard.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.Map;

public abstract class AbstractControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    protected R2dbcEntityTemplate entityTemplate;

    @Autowired
    protected AuthService authService;


    protected String login(String username, String password) {
        return authService.authenticate(new AuthRequest(username, password))
                .map(res -> res.accessToken())
                .block();
    }

    protected HttpGraphQlTester.Response execute(String query, Map<String, Object> variables, String token) {
        HttpGraphQlTester client = (token != null) ? authorizedGraphQlTester(token) : graphQlTester;

        HttpGraphQlTester.Request<?> request = client.document(query);
        if (variables != null) {
            variables.forEach(request::variable);
        }
        return request.execute();
    }
}
