package com.lauro.stickynote.dto;

import java.util.Map;

public record UserInfoDto(String accesToken, String refreshToken, String idToken, Map<String, Object> claims) {
}
