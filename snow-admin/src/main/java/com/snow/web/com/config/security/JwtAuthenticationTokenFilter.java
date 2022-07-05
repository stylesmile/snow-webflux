package com.snow.web.com.config.security;

import com.snow.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.snow.common.core.domain.model.LoginUser;
import com.snow.common.utils.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * token过滤器 验证token有效性
 * 
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter implements WebFilter {
//    private static final Logger logger = LoggerFactory.getLogger(AuthenticationJwtTokenFilter.class);

    @Autowired
    private TokenService tokenService;

    public JwtAuthenticationTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            LoginUser loginUser = tokenService.getLoginUser(exchange.getRequest());
            if (StringUtils.isNotNull(loginUser)) {
                tokenService.verifyToken(loginUser);
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                return Mono.just(authenticationToken)
                    .flatMap(auth -> chain.filter(exchange))
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken))
                    .log();
            }
        } catch (Exception e) {
//            logger.debug("Exception: ", e);
        }

        return chain.filter(exchange);
    }



//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException
//    {
//        LoginUser loginUser = tokenService.getLoginUser(request);
//        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
//        {
//            tokenService.verifyToken(loginUser);
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//        chain.doFilter(request, response);
//    }
}
