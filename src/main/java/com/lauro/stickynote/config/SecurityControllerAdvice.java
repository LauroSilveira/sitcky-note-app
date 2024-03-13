package com.lauro.stickynote.config;

import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class SecurityControllerAdvice {

    /**
     * This method is because Spring webflux does not include CSRF in requests.
     * This must be included in a part of the request (i.e. form parameter, HTTP header, etc) that is not automatically included in the HTTP request by the browser.
     * For more info, see the url below
     * <a href="https://docs.spring.io/spring-security/site/docs/5.2.5.RELEASE/reference/html/protection-against-exploits-2.html"/>
     *
     * @param exchange exchange
     * @return Mono<CsrfToken>
     */
    @ModelAttribute
    Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
        return csrfToken.doOnSuccess(token -> exchange.getAttributes()
                .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}