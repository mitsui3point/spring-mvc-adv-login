package hello.login.web.member;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.member.MemberController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MemberControllerTest {
    private MockMvc mvc;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        mvc = MockMvcBuilders.standaloneSetup(new MemberController(memberRepository)).build();
    }

    @Test
    void addFormTest() throws Exception {
        ResultActions perform = mvc.perform(get("/members/add"));

        perform.andDo(print())
                .andExpect(view().name("members/addMemberForm"));
    }

    @Test
    void addTest() throws Exception {
        Member member = new Member("loginId1", "name1", "password1");

        ResultActions perform = mvc.perform(post("/members/add")
                .param("loginId", member.getLoginId())
                .param("password", member.getPassword())
                .param("name", member.getName())
        );

        perform.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(o -> {
                    Member result = (Member) o.getModelAndView().getModel().get("member");
                    assertThat(result).extracting("loginId").isEqualTo(member.getLoginId());
                    assertThat(result).extracting("password").isEqualTo(member.getPassword());
                    assertThat(result).extracting("name").isEqualTo(member.getName());
                })
                .andExpect(redirectedUrl("/"))
        ;
    }

    @Test
    void addFailTest() throws Exception {
        ResultActions perform = mvc.perform(post("/members/add"));

        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("members/addMemberForm"))
                .andExpect(model().attributeHasFieldErrors("member", "loginId", "name", "password"))
        ;
    }

    @AfterEach
    void tearDown() {
        memberRepository.clearStore();
    }
}
