package hello.login.web.login;

import hello.login.SessionConst;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginControllerTest {
    private MockMvc mvc;
    private MemberRepository memberRepository;

    private LoginService loginService;

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        loginService = new LoginService(memberRepository);
        sessionManager = new SessionManager();
        mvc = MockMvcBuilders.standaloneSetup(new LoginController(loginService, sessionManager)).build();
    }

    @Test
    void loginForm() throws Exception {
        ResultActions perform = mvc.perform(get("/login"));

        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login/loginForm"))
                .andExpect(model().attributeHasNoErrors("loginForm"))
        ;
    }

    @Test
    void loginTest() throws Exception {
        Member member = memberRepository.save(new Member("loginId1", "name", "password1"));
        ResultActions perform = mvc.perform(post("/login")
                .param("loginId", member.getLoginId())
                .param("password", member.getPassword())
                .queryParam("redirectURL", "/items")
        );

        perform.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeHasNoErrors("loginForm"))
                .andExpect(redirectedUrl("/items"))
                .andExpect(result -> assertEqualSessionMemberAndMember(member, result));

    }

    private void assertEqualSessionMemberAndMember(Member member, MvcResult result) {
        Member sessionMember = (Member) result.getRequest()
                .getSession()
                .getAttribute(SessionConst.LOGIN_MEMBER);
        assertThat(sessionMember).isEqualTo(member);
    }

    @Test
    void loginFailTest() throws Exception {
        Member member = memberRepository.save(new Member("loginId1", "name", "password1"));
        ResultActions perform = mvc.perform(post("/login")
                .param("loginId", member.getLoginId())
                .param("password", "pass")
        );

        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login/loginForm"))
                .andExpect(model().attributeHasErrors("loginForm"))
        ;
    }

    @Test
    void logoutTest() throws Exception {
        ResultActions perform = mvc.perform(post("/logout")
                .cookie(new Cookie("mySessionId", UUID.randomUUID().toString())));

        perform.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
        ;
    }

    @AfterEach
    void tearDown() {
        memberRepository.clearStore();
    }
}
