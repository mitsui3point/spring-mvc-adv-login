package hello.login.web.interceptor;

import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
public class LogInterceptorTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new LogFilter(), new LoginCheckFilter())
                .build();
    }

    @Test
    void logTest() throws Exception {
        MockHttpServletRequest request = mvc.perform(get("/"))
                .andReturn()
                .getRequest();

        HandlerExecutionChain chain = context.getBean(RequestMappingHandlerMapping.class)
                .getHandler(request);

        Optional<HandlerInterceptor> logInterceptor = Arrays.stream(chain.getInterceptors())
                .filter(handlerInterceptor -> handlerInterceptor instanceof LogInterceptor)
                .findFirst();

        Assertions.assertThat(logInterceptor).isPresent();
    }
}
