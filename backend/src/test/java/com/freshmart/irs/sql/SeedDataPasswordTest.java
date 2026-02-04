package com.freshmart.irs.sql;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SeedDataPasswordTest {
    @Test
    void seedUserPasswordHash_matchesAaaa1111() {
        String hash = "$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva";
        Assertions.assertTrue(BCrypt.verifyer().verify("aaaa1111".toCharArray(), hash).verified);
    }
}

