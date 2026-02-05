package com.freshmart.irs.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.freshmart.irs.auth.AuthContext;
import com.freshmart.irs.auth.AuthContextHolder;
import com.freshmart.irs.auth.JwtService;
import com.freshmart.irs.common.BizException;
import com.freshmart.irs.common.ErrorCode;
import com.freshmart.irs.dto.auth.MeResponse;
import com.freshmart.irs.entity.UserEntity;
import com.freshmart.irs.enums.UserStatus;
import com.freshmart.irs.mapper.UserMapper;
import com.freshmart.irs.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private RolesService rolesService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private AuditService auditService;

    @AfterEach
    void tearDown() {
        AuthContextHolder.clear();
    }

    @Test
    void me_enabled_returnsContextDataAndChecksStatusOnly() {
        AuthService authService = new AuthServiceImpl(userMapper, userRoleService, rolesService, jwtService, passwordPolicy, auditService);

        AuthContextHolder.set(new AuthContext(1L, "admin", List.of("ADMIN", "CUSTOMER")));

        UserEntity statusOnly = new UserEntity();
        statusOnly.setId(1L);
        statusOnly.setUsername("admin");
        statusOnly.setStatus(1);
        when(userMapper.selectOne(ArgumentMatchers.<Wrapper<UserEntity>>any())).thenReturn(statusOnly);

        MeResponse me = authService.me();

        Assertions.assertEquals(1L, me.id());
        Assertions.assertEquals("admin", me.username());
        Assertions.assertNull(me.phone());
        Assertions.assertNull(me.email());
        Assertions.assertEquals(UserStatus.ENABLED, me.status());
        Assertions.assertEquals(List.of("ADMIN", "CUSTOMER"), me.roles());

        verify(userMapper).selectOne(ArgumentMatchers.<Wrapper<UserEntity>>any());
        verifyNoInteractions(userRoleService);
    }

    @Test
    void me_disabled_throwsForbidden() {
        AuthService authService = new AuthServiceImpl(userMapper, userRoleService, rolesService, jwtService, passwordPolicy, auditService);

        AuthContextHolder.set(new AuthContext(1L, "admin", List.of("ADMIN")));

        UserEntity statusOnly = new UserEntity();
        statusOnly.setId(1L);
        statusOnly.setStatus(0);
        when(userMapper.selectOne(ArgumentMatchers.<Wrapper<UserEntity>>any())).thenReturn(statusOnly);

        BizException ex = Assertions.assertThrows(BizException.class, authService::me);
        Assertions.assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void permissions_returnsRolesFromTokenContext() {
        AuthService authService = new AuthServiceImpl(userMapper, userRoleService, rolesService, jwtService, passwordPolicy, auditService);

        AuthContextHolder.set(new AuthContext(1L, "admin", List.of("ADMIN")));

        Assertions.assertEquals(List.of("ADMIN"), authService.permissions());
        verifyNoInteractions(userMapper);
        verifyNoInteractions(userRoleService);
    }
}
