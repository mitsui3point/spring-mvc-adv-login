package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static hello.login.SessionConst.LOGIN_MEMBER;

@Slf4j
public class LoginCheckFilter implements Filter {
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            log.info("인증 체크 필터 시작:{}", requestURI);
            if (!isLoginCheckPath(requestURI)) {
                chain.doFilter(request, response);
                return;
            }
            log.info("인증 체크 로직 실행:{}", requestURI);
            HttpSession session = httpRequest.getSession();
            if (isEmptySession(session)) {
                log.info("미인증 유저 식별:{}", requestURI);
                httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                return;
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            log.info("인증 체크 필터 종료");
        }
    }

    private boolean isEmptySession(HttpSession session) {
        return session == null ||
                session.getAttribute(LOGIN_MEMBER) == null;
    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
