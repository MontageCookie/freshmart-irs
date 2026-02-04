package com.freshmart.irs.auth;

import java.util.List;

public record AuthContext(long userId, String username, List<String> roles) {
}
