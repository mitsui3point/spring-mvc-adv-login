package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerTest {
    private MockMvc mvc;

    @SpyBean
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
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
}
