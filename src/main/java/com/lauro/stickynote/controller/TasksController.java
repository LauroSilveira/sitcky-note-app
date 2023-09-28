package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/taskDtos")
@Slf4j
public class TasksController {

    @GetMapping("/user")
    public ResponseEntity<UserInfoDto> userInfo(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client,
                                                @AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(new UserInfoDto(client.getAccessToken().getTokenValue(), client.getRefreshToken().getTokenValue(), oidcUser.getIdToken().getTokenValue(),
                oidcUser.getClaims()));
    }
}
