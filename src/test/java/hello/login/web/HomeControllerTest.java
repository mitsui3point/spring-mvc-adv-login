package hello.login.web;

import hello.login.domain.item.ItemRepository;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import java.util.List;
import java.util.UUID;

import static hello.login.session.SessionManager.SESSION_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class)
public class HomeControllerTest {
    private MockMvc mvc;

    @SpyBean
    private MemberRepository memberRepository;

    @SpyBean
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new HomeController(memberRepository, sessionManager))
                .build();
    }

    @Test
    void homeTest() throws Exception {
        ResultActions perform = mvc.perform(get("/"));

        ResultActions home = perform.andDo(print())
                .andExpect(view().name("home"))
                .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));
    }

    @Test
    void homeLoginTest() throws Exception {
        //given
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = memberRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> memberRepository.save(new Member("loginId", "name", "password!")));

        //when
        sessionManager.createSession(member, response);
        ResultActions perform = mvc.perform(get("/")
                .cookie(response.getCookies())
        );

        //then
        perform.andDo(print())
                .andExpect(view().name("loginHome"))
                .andExpect(model().attribute("member", member))
        ;
    }
}
