package com.freshmart.irs.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.dto.user.UserUpdateRequest;
import com.freshmart.irs.entity.UserEntity;
import com.freshmart.irs.mapper.UserMapper;
import com.freshmart.irs.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private AuditService auditService;

    @AfterEach
    void tearDown() {
        AuthContextHolder.clear();
    }

    @Test
    void update_selfWrongCurrentPassword_returnsParamInvalid() {
        UsersService usersService = new UsersServiceImpl(userMapper, userRoleService, passwordPolicy, auditService);

        AuthContextHolder.set(new AuthContext(1L, "customer1", List.of("CUSTOMER")));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("customer1");
        user.setStatus(1);
        user.setPasswordHash(BCrypt.withDefaults().hashToString(12, "aaaa1111".toCharArray()));
        when(userMapper.selectById(1L)).thenReturn(user);

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                null,
                null,
                "wrong",
                "bbbb1111",
                null
        );

        BizException ex = Assertions.assertThrows(BizException.class, () -> usersService.update(1L, request));
        Assertions.assertEquals(ErrorCode.PARAM_INVALID, ex.getErrorCode());
    }

    @Test
    void update_adminDoesNotRequireCurrentPassword() {
        UsersService usersService = new UsersServiceImpl(userMapper, userRoleService, passwordPolicy, auditService);

        AuthContextHolder.set(new AuthContext(1L, "admin", List.of("ADMIN")));

        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUsername("customer1");
        user.setStatus(1);
        user.setPasswordHash(BCrypt.withDefaults().hashToString(12, "aaaa1111".toCharArray()));
        when(userMapper.selectById(2L)).thenReturn(user);
        when(userMapper.updateById(any(UserEntity.class))).thenReturn(1);

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                null,
                null,
                null,
                "bbbb1111",
                null
        );

        Assertions.assertEquals(2L, usersService.update(2L, request));
    }
}
