package hello.login.web.interceptor;

import hello.login.domain.item.ItemRepository;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Optional;

import static hello.login.SessionConst.LOGIN_MEMBER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
public class LoginCheckInterceptorTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void itemsAccessNotAllowTest() throws Exception {

        MockHttpServletRequest request = mvc.perform(get("/items"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?redirectURL=/items"))
                .andReturn()
                .getRequest();

        HandlerExecutionChain chain = context.getBean(RequestMappingHandlerMapping.class)
                .getHandler(request);

        Optional<HandlerInterceptor> loginInterceptor = Arrays.stream(chain.getInterceptors())
                .filter(o -> o instanceof LoginCheckInterceptor)
                .findFirst();

        Assertions.assertThat(loginInterceptor).isPresent();
    }

    @Test
    void itemsAccessAllowTest() throws Exception {

        Member loginMember = Optional
                .ofNullable(loginService.login("test", "test1"))
                .orElseGet(() -> memberRepository.save(new Member("test", "test", "test1")));

        MockHttpSession session = new MockHttpSession();

        session.setAttribute(LOGIN_MEMBER, loginMember);
        MockHttpServletRequest request = mvc.perform(get("/items")
                        .session(session)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.view().name("items/items"))
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?redirectURL=/items"))
                .andReturn()
                .getRequest();

        HandlerExecutionChain chain = context.getBean(RequestMappingHandlerMapping.class)
                .getHandler(request);

        Optional<HandlerInterceptor> loginInterceptor = Arrays.stream(chain.getInterceptors())
                .filter(o -> o instanceof LoginCheckInterceptor)
                .findFirst();

        Assertions.assertThat(loginInterceptor).isPresent();
    }
}
