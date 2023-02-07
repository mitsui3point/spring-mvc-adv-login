package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class)
public class LoginControllerTest {
    private MockMvc mvc;

    @SpyBean
    private LoginService loginService;

    @SpyBean
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new LoginController(loginService)).build();
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
        );

        perform.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeHasNoErrors("loginForm"))
                .andExpect(redirectedUrl("/"))
        ;
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

    @AfterEach
    void tearDown() {
        memberRepository.clearStore();
    }
}
