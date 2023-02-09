package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

public class LoginServiceTest {

    private LoginService loginService;

    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        loginService = new LoginService(memberRepository);
    }

    @Test
    void loginTest() {
        //given
        Member member = new Member("loginId1", "loginName", "password1");
        memberRepository.save(member);

        //when
        Member loginMember = loginService.login(member.getLoginId(), member.getPassword());

        //then
        Assertions.assertThat(loginMember).isEqualTo(member);
    }

    @Test
    void loginFailTest() {
        //given
        Member member = new Member("loginId1", "loginName", "password1");
        memberRepository.save(member);

        //when
        Member loginMember = loginService.login(member.getLoginId(), "pass");

        //then
        Assertions.assertThat(loginMember).isNull();
    }
}
