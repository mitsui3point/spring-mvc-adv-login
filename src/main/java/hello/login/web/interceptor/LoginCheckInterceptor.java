package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static hello.login.SessionConst.LOGIN_MEMBER;
import static hello.login.web.interceptor.LogInterceptor.LOG_ID;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행   {}", requestURI);

        HttpSession session = request.getSession();
        String logId = request.getAttribute(LOG_ID).toString();

        if (isEmptySession(session)) {
            log.info("미인증 유저 식별     [{}][{}][{}]", logId, requestURI, handler);
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }
        return true;
    }

    private boolean isEmptySession(HttpSession session) {
        return session == null ||
                session.getAttribute(LOGIN_MEMBER) == null;
    }
}
