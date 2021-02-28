package ca.sfu.orcus.gitlabanalyzer.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ClientInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String endpoint = request.getRequestURI();

        if (!endpoint.startsWith("/api/") && !endpoint.equals("/") && !endpoint.startsWith("/index.html") && !endpoint.contains("/static/")) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/");
            requestDispatcher.forward(request, response);
            return false;
        }
        return true;
    }
}
