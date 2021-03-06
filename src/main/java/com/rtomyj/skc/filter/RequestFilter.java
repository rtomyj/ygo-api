package com.rtomyj.skc.filter;

import com.rtomyj.skc.helper.Logging;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws ServletException, IOException
    {
        try
        {
            Logging.configureMDC(request);
            chain.doFilter(request, response);
        } finally
        {
            MDC.clear();
        }
    }

}
