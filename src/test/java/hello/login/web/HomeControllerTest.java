package hello.login.web;

import hello.login.domain.item.ItemRepository;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class)
public class HomeControllerTest {
    private MockMvc mvc;

    @SpyBean
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new HomeController(memberRepository))
                .build();
    }

    @Test
    void homeTest() throws Exception {
        ResultActions perform = mvc.perform(get("/"));

        perform.andDo(print())
                .andExpect(view().name("home"))
                .andExpect(cookie().doesNotExist("memberId"));
    }

    @Test
    void homeLoginTest() throws Exception {
        //given
        Member loginMember = memberRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> memberRepository.save(new Member("loginId", "name", "password!")));

        //when
        ResultActions perform = mvc.perform(get("/")
                .cookie(new Cookie("memberId", String.valueOf(loginMember.getId())))
        );

        //then
        perform.andDo(print())
                .andExpect(view().name("loginHome"))
                .andExpect(model().attribute("member", loginMember));
    }

    @Test
    void logoutTest() throws Exception {
        ResultActions perform = mvc.perform(post("/logout"));

        perform.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().maxAge("memberId", 0));
    }
}
