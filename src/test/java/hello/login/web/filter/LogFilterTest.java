package hello.login.web.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogFilterTest {
    @InjectMocks
    private LogFilter logFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private FilterConfig config;

    @Test
    void logFilterTest() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/");
        logFilter.init(config);
        logFilter.doFilter(request, response, chain);
        logFilter.destroy();
        verify(chain).doFilter(request,response);
    }
}
