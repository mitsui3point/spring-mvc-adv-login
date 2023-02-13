package hello.login.web.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginCheckFilterTest {

    @InjectMocks
    private LogFilter logFilter;
    @InjectMocks
    private LoginCheckFilter loginCheckFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private static final String[] whitelistExpected = {"/", "/members/add", "/login", "/logout", "/css/file.css"};
    private static final String[] filterTargetListExpected = {"/items/add", "/items/modify"};

    @Test
    void loginCheckFilterWhiteListTest() throws ServletException, IOException {
        int index = 0;
        for (String whitelist : whitelistExpected) {
            //when
            when(request.getRequestURI()).thenReturn(whitelist);
            loginCheckFilter.doFilter(request, response, chain);
            //then
            verify(chain, times(++index)).doFilter(request, response);
            verify(response, times(0)).sendRedirect("/login?redirectURL=" + whitelist);
        }
    }

    @Test
    void loginCheckFilterNotWhiteListTest() throws ServletException, IOException {
        for (String filterTarget : filterTargetListExpected) {
            //when
            when(request.getRequestURI()).thenReturn(filterTarget);
            loginCheckFilter.doFilter(request, response, chain);
            //then
            verify(response).sendRedirect("/login?redirectURL=" + filterTarget);
            verify(chain, times(0)).doFilter(request, response);
        }
    }

    @Test
    void loginCheckFilterExceptionTest() throws ServletException, IOException {
        //when
        when(request.getRequestURI()).thenReturn("/");
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            //then
            loginCheckFilter.doFilter(request, response, null);
        });
    }
}
