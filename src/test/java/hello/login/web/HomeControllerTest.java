package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static hello.login.SessionConst.LOGIN_MEMBER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        ResultActions perform = mvc.perform(get("/").session(new MockHttpSession()));

        perform.andDo(print())
                .andExpect(view().name("home"))
                .andExpect(cookie().doesNotExist(LOGIN_MEMBER));
    }

    @Test
    void homeLoginTest() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        Member member = memberRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> memberRepository.save(new Member("loginId", "name", "password!")));

        session.setAttribute(LOGIN_MEMBER, member);

        //when
        ResultActions perform = mvc.perform(get("/")
                .session(session)
        );

        //then
        perform.andDo(print())
                .andExpect(view().name("loginHome"))
                .andExpect(model().attribute("member", member))
        ;
    }
}
