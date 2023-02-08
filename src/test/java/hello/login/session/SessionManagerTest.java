package hello.login.session;

import hello.login.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;

import static hello.login.session.SessionManager.SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class SessionManagerTest {

    private SessionManager sessionManager = new SessionManager();

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Member member;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        member = new Member("loginId", "name", "password");
        member.setId(1L);
    }

    @Test
    void createSessionTest() {
        //when
        sessionManager.createSession(member, response);
        String mySessionId = response.getCookie(SESSION_COOKIE_NAME).getValue();

        //then
        System.out.println("mySessionId = " + mySessionId);
        assertThat(mySessionId.length()).isEqualTo(36);
    }

    @Test
    void getSessionTest() {
        //when
        sessionManager.createSession(member, response);
        request.setCookies(response.getCookies());
        Object sessionValue = sessionManager.getSession(request);

        //then
        assertThat(sessionValue).isEqualTo(member);
    }

    @Test
    void expireSessionTest() {
        //when
        sessionManager.createSession(member, response);
        request.setCookies(response.getCookies());
        sessionManager.expireSession(request);
        Object sessionValue = sessionManager.getSession(request);
        //then
        assertThat(sessionValue).isNull();
    }
}
