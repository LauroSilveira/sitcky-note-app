package com.lauro.stickynote.dto;

import java.util.Map;

public record UserInfoDto(String accessToken, String refreshToken, String idToken, Map<String, Object> claims) {
}
