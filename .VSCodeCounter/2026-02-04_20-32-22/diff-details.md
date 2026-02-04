# Diff Details

Date : 2026-02-04 20:32:22

Directory d:\\Code\\freshmart-irs\\backend

Total : 91 files,  1659 codes, 271 comments, 127 blanks, all 2057 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [backend/pom.xml](/backend/pom.xml) | XML | -15 | 0 | 0 | -15 |
| [backend/src/main/java/com/freshmart/irs/FreshmartIrsBackendApplication.java](/backend/src/main/java/com/freshmart/irs/FreshmartIrsBackendApplication.java) | Java | -5 | 0 | 1 | -4 |
| [backend/src/main/java/com/freshmart/irs/api/AuthController.java](/backend/src/main/java/com/freshmart/irs/api/AuthController.java) | Java | -2 | 0 | 3 | 1 |
| [backend/src/main/java/com/freshmart/irs/api/RolesController.java](/backend/src/main/java/com/freshmart/irs/api/RolesController.java) | Java | -2 | 0 | 1 | -1 |
| [backend/src/main/java/com/freshmart/irs/api/UsersController.java](/backend/src/main/java/com/freshmart/irs/api/UsersController.java) | Java | -10 | 0 | 1 | -9 |
| [backend/src/main/java/com/freshmart/irs/auth/AuthConfig.java](/backend/src/main/java/com/freshmart/irs/auth/AuthConfig.java) | Java | 7 | 0 | 3 | 10 |
| [backend/src/main/java/com/freshmart/irs/auth/AuthContext.java](/backend/src/main/java/com/freshmart/irs/auth/AuthContext.java) | Java | 4 | 0 | 3 | 7 |
| [backend/src/main/java/com/freshmart/irs/auth/AuthContextHolder.java](/backend/src/main/java/com/freshmart/irs/auth/AuthContextHolder.java) | Java | 22 | 0 | 7 | 29 |
| [backend/src/main/java/com/freshmart/irs/auth/JwtAuthFilter.java](/backend/src/main/java/com/freshmart/irs/auth/JwtAuthFilter.java) | Java | 103 | 3 | 19 | 125 |
| [backend/src/main/java/com/freshmart/irs/auth/JwtProperties.java](/backend/src/main/java/com/freshmart/irs/auth/JwtProperties.java) | Java | 19 | 0 | 8 | 27 |
| [backend/src/main/java/com/freshmart/irs/auth/JwtService.java](/backend/src/main/java/com/freshmart/irs/auth/JwtService.java) | Java | 49 | 30 | 10 | 89 |
| [backend/src/main/java/com/freshmart/irs/common/BizException.java](/backend/src/main/java/com/freshmart/irs/common/BizException.java) | Java | 0 | 14 | 0 | 14 |
| [backend/src/main/java/com/freshmart/irs/common/GlobalExceptionHandler.java](/backend/src/main/java/com/freshmart/irs/common/GlobalExceptionHandler.java) | Java | -26 | 0 | -6 | -32 |
| [backend/src/main/java/com/freshmart/irs/config/GlobalExceptionHandler.java](/backend/src/main/java/com/freshmart/irs/config/GlobalExceptionHandler.java) | Java | 44 | 0 | 8 | 52 |
| [backend/src/main/java/com/freshmart/irs/config/MybatisPlusConfig.java](/backend/src/main/java/com/freshmart/irs/config/MybatisPlusConfig.java) | Java | 2 | 0 | 1 | 3 |
| [backend/src/main/java/com/freshmart/irs/config/OpenApiConfig.java](/backend/src/main/java/com/freshmart/irs/config/OpenApiConfig.java) | Java | -1 | 0 | 0 | -1 |
| [backend/src/main/java/com/freshmart/irs/config/TraceIdFilter.java](/backend/src/main/java/com/freshmart/irs/config/TraceIdFilter.java) | Java | -3 | 0 | 1 | -2 |
| [backend/src/main/java/com/freshmart/irs/dto/auth/RegisterRequest.java](/backend/src/main/java/com/freshmart/irs/dto/auth/RegisterRequest.java) | Java | -11 | 0 | -3 | -14 |
| [backend/src/main/java/com/freshmart/irs/entity/AuditEventEntity.java](/backend/src/main/java/com/freshmart/irs/entity/AuditEventEntity.java) | Java | 2 | 30 | 0 | 32 |
| [backend/src/main/java/com/freshmart/irs/entity/RoleEntity.java](/backend/src/main/java/com/freshmart/irs/entity/RoleEntity.java) | Java | 0 | 21 | 0 | 21 |
| [backend/src/main/java/com/freshmart/irs/entity/UserEntity.java](/backend/src/main/java/com/freshmart/irs/entity/UserEntity.java) | Java | 0 | 27 | 1 | 28 |
| [backend/src/main/java/com/freshmart/irs/entity/UserRoleEntity.java](/backend/src/main/java/com/freshmart/irs/entity/UserRoleEntity.java) | Java | 0 | 18 | 0 | 18 |
| [backend/src/main/java/com/freshmart/irs/mapper/AuditEventMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/AuditEventMapper.java) | Java | 2 | 3 | 0 | 5 |
| [backend/src/main/java/com/freshmart/irs/mapper/RoleMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/RoleMapper.java) | Java | 2 | 3 | 0 | 5 |
| [backend/src/main/java/com/freshmart/irs/mapper/UserMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/UserMapper.java) | Java | 2 | 3 | 0 | 5 |
| [backend/src/main/java/com/freshmart/irs/mapper/UserRoleMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/UserRoleMapper.java) | Java | 2 | 3 | 0 | 5 |
| [backend/src/main/java/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.java](/backend/src/main/java/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.java) | Java | -30 | 0 | -6 | -36 |
| [backend/src/main/java/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.java](/backend/src/main/java/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.java) | Java | -30 | 0 | -6 | -36 |
| [backend/src/main/java/com/freshmart/irs/security/JwtAuthenticationFilter.java](/backend/src/main/java/com/freshmart/irs/security/JwtAuthenticationFilter.java) | Java | -55 | 0 | -6 | -61 |
| [backend/src/main/java/com/freshmart/irs/security/JwtPrincipal.java](/backend/src/main/java/com/freshmart/irs/security/JwtPrincipal.java) | Java | -21 | 0 | -7 | -28 |
| [backend/src/main/java/com/freshmart/irs/security/JwtProperties.java](/backend/src/main/java/com/freshmart/irs/security/JwtProperties.java) | Java | -26 | 0 | -9 | -35 |
| [backend/src/main/java/com/freshmart/irs/security/JwtService.java](/backend/src/main/java/com/freshmart/irs/security/JwtService.java) | Java | -53 | 0 | -12 | -65 |
| [backend/src/main/java/com/freshmart/irs/security/SecurityConfig.java](/backend/src/main/java/com/freshmart/irs/security/SecurityConfig.java) | Java | -49 | 0 | -7 | -56 |
| [backend/src/main/java/com/freshmart/irs/service/AuditEventService.java](/backend/src/main/java/com/freshmart/irs/service/AuditEventService.java) | Java | -30 | 0 | -5 | -35 |
| [backend/src/main/java/com/freshmart/irs/service/AuditService.java](/backend/src/main/java/com/freshmart/irs/service/AuditService.java) | Java | 34 | 12 | 8 | 54 |
| [backend/src/main/java/com/freshmart/irs/service/AuthService.java](/backend/src/main/java/com/freshmart/irs/service/AuthService.java) | Java | 127 | 31 | 16 | 174 |
| [backend/src/main/java/com/freshmart/irs/service/PasswordPolicy.java](/backend/src/main/java/com/freshmart/irs/service/PasswordPolicy.java) | Java | 28 | 10 | 4 | 42 |
| [backend/src/main/java/com/freshmart/irs/service/RolesService.java](/backend/src/main/java/com/freshmart/irs/service/RolesService.java) | Java | 33 | 13 | 7 | 53 |
| [backend/src/main/java/com/freshmart/irs/service/UserRoleService.java](/backend/src/main/java/com/freshmart/irs/service/UserRoleService.java) | Java | 90 | 21 | 14 | 125 |
| [backend/src/main/java/com/freshmart/irs/service/UsersService.java](/backend/src/main/java/com/freshmart/irs/service/UsersService.java) | Java | 251 | 3 | 30 | 284 |
| [backend/src/main/resources/application.yml](/backend/src/main/resources/application.yml) | YAML | -3 | 0 | 0 | -3 |
| [backend/src/test/java/com/freshmart/irs/service/AuthServiceTest.java](/backend/src/test/java/com/freshmart/irs/service/AuthServiceTest.java) | Java | 78 | 0 | 24 | 102 |
| [backend/src/test/java/com/freshmart/irs/service/UsersServiceTest.java](/backend/src/test/java/com/freshmart/irs/service/UsersServiceTest.java) | Java | 75 | 0 | 19 | 94 |
| [backend/src/test/java/com/freshmart/irs/sql/SeedDataPasswordTest.java](/backend/src/test/java/com/freshmart/irs/sql/SeedDataPasswordTest.java) | Java | 11 | 0 | 4 | 15 |
| [backend/target/classes/application.yml](/backend/target/classes/application.yml) | YAML | -3 | 0 | 0 | -3 |
| [backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class](/backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class) | Java | -3 | 0 | 0 | -3 |
| [backend/target/classes/com/freshmart/irs/api/AuthController$RegisterRequest.class](/backend/target/classes/com/freshmart/irs/api/AuthController$RegisterRequest.class) | Java | 16 | 0 | 0 | 16 |
| [backend/target/classes/com/freshmart/irs/api/AuthController.class](/backend/target/classes/com/freshmart/irs/api/AuthController.class) | Java | 10 | 0 | 0 | 10 |
| [backend/target/classes/com/freshmart/irs/api/RolesController.class](/backend/target/classes/com/freshmart/irs/api/RolesController.class) | Java | -4 | 0 | 0 | -4 |
| [backend/target/classes/com/freshmart/irs/api/UsersController.class](/backend/target/classes/com/freshmart/irs/api/UsersController.class) | Java | 1 | 0 | 0 | 1 |
| [backend/target/classes/com/freshmart/irs/auth/AuthConfig.class](/backend/target/classes/com/freshmart/irs/auth/AuthConfig.class) | Java | 5 | 0 | 0 | 5 |
| [backend/target/classes/com/freshmart/irs/auth/AuthContext.class](/backend/target/classes/com/freshmart/irs/auth/AuthContext.class) | Java | 10 | 0 | 0 | 10 |
| [backend/target/classes/com/freshmart/irs/auth/AuthContextHolder.class](/backend/target/classes/com/freshmart/irs/auth/AuthContextHolder.class) | Java | 23 | 0 | 0 | 23 |
| [backend/target/classes/com/freshmart/irs/auth/JwtAuthFilter.class](/backend/target/classes/com/freshmart/irs/auth/JwtAuthFilter.class) | Java | 40 | 0 | 0 | 40 |
| [backend/target/classes/com/freshmart/irs/auth/JwtProperties.class](/backend/target/classes/com/freshmart/irs/auth/JwtProperties.class) | Java | 8 | 0 | 0 | 8 |
| [backend/target/classes/com/freshmart/irs/auth/JwtService.class](/backend/target/classes/com/freshmart/irs/auth/JwtService.class) | Java | 35 | 0 | 0 | 35 |
| [backend/target/classes/com/freshmart/irs/common/GlobalExceptionHandler.class](/backend/target/classes/com/freshmart/irs/common/GlobalExceptionHandler.class) | Java | -15 | 0 | 0 | -15 |
| [backend/target/classes/com/freshmart/irs/config/GlobalExceptionHandler$1.class](/backend/target/classes/com/freshmart/irs/config/GlobalExceptionHandler$1.class) | Java | 10 | 0 | 0 | 10 |
| [backend/target/classes/com/freshmart/irs/config/GlobalExceptionHandler.class](/backend/target/classes/com/freshmart/irs/config/GlobalExceptionHandler.class) | Java | 19 | 0 | 0 | 19 |
| [backend/target/classes/com/freshmart/irs/config/MybatisPlusConfig.class](/backend/target/classes/com/freshmart/irs/config/MybatisPlusConfig.class) | Java | -3 | 0 | 0 | -3 |
| [backend/target/classes/com/freshmart/irs/dto/auth/RegisterRequest.class](/backend/target/classes/com/freshmart/irs/dto/auth/RegisterRequest.class) | Java | -16 | 0 | 0 | -16 |
| [backend/target/classes/com/freshmart/irs/mapper/AuditEventMapper.class](/backend/target/classes/com/freshmart/irs/mapper/AuditEventMapper.class) | Java | 2 | 0 | -1 | 1 |
| [backend/target/classes/com/freshmart/irs/mapper/RoleMapper.class](/backend/target/classes/com/freshmart/irs/mapper/RoleMapper.class) | Java | 2 | 0 | -1 | 1 |
| [backend/target/classes/com/freshmart/irs/mapper/UserMapper.class](/backend/target/classes/com/freshmart/irs/mapper/UserMapper.class) | Java | 2 | 0 | -1 | 1 |
| [backend/target/classes/com/freshmart/irs/mapper/UserRoleMapper.class](/backend/target/classes/com/freshmart/irs/mapper/UserRoleMapper.class) | Java | 2 | 0 | -1 | 1 |
| [backend/target/classes/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.class](/backend/target/classes/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.class) | Java | -15 | 0 | 0 | -15 |
| [backend/target/classes/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.class](/backend/target/classes/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.class) | Java | -16 | 0 | 0 | -16 |
| [backend/target/classes/com/freshmart/irs/security/JwtAuthenticationFilter.class](/backend/target/classes/com/freshmart/irs/security/JwtAuthenticationFilter.class) | Java | -40 | 0 | 0 | -40 |
| [backend/target/classes/com/freshmart/irs/security/JwtPrincipal.class](/backend/target/classes/com/freshmart/irs/security/JwtPrincipal.class) | Java | -9 | 0 | 0 | -9 |
| [backend/target/classes/com/freshmart/irs/security/JwtProperties.class](/backend/target/classes/com/freshmart/irs/security/JwtProperties.class) | Java | -9 | 0 | 0 | -9 |
| [backend/target/classes/com/freshmart/irs/security/JwtService$ParsedToken.class](/backend/target/classes/com/freshmart/irs/security/JwtService$ParsedToken.class) | Java | -9 | 0 | 0 | -9 |
| [backend/target/classes/com/freshmart/irs/security/JwtService.class](/backend/target/classes/com/freshmart/irs/security/JwtService.class) | Java | -35 | 0 | 0 | -35 |
| [backend/target/classes/com/freshmart/irs/security/SecurityConfig.class](/backend/target/classes/com/freshmart/irs/security/SecurityConfig.class) | Java | -36 | -5 | 0 | -41 |
| [backend/target/classes/com/freshmart/irs/service/AuditEventService.class](/backend/target/classes/com/freshmart/irs/service/AuditEventService.class) | Java | -26 | 0 | 0 | -26 |
| [backend/target/classes/com/freshmart/irs/service/AuditService.class](/backend/target/classes/com/freshmart/irs/service/AuditService.class) | Java | 28 | 0 | 0 | 28 |
| [backend/target/classes/com/freshmart/irs/service/AuthService.class](/backend/target/classes/com/freshmart/irs/service/AuthService.class) | Java | 82 | 0 | 0 | 82 |
| [backend/target/classes/com/freshmart/irs/service/PasswordPolicy.class](/backend/target/classes/com/freshmart/irs/service/PasswordPolicy.class) | Java | 19 | 0 | 0 | 19 |
| [backend/target/classes/com/freshmart/irs/service/RolesService.class](/backend/target/classes/com/freshmart/irs/service/RolesService.class) | Java | 45 | 0 | 2 | 47 |
| [backend/target/classes/com/freshmart/irs/service/UserRoleService.class](/backend/target/classes/com/freshmart/irs/service/UserRoleService.class) | Java | 61 | 0 | 0 | 61 |
| [backend/target/classes/com/freshmart/irs/service/UsersService.class](/backend/target/classes/com/freshmart/irs/service/UsersService.class) | Java | 115 | 24 | 0 | 139 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowMySqlTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowMySqlTest.xml) | XML | 196 | 0 | 3 | 199 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.PasswordHashGeneratorTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.PasswordHashGeneratorTest.xml) | XML | 68 | 0 | 0 | 68 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.SeedPasswordHashTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.SeedPasswordHashTest.xml) | XML | 64 | 0 | 0 | 64 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.security.JwtServiceTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.security.JwtServiceTest.xml) | XML | 68 | 0 | 0 | 68 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.security.PasswordPolicyTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.security.PasswordPolicyTest.xml) | XML | 71 | 0 | 0 | 71 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.service.AuthServiceTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.service.AuthServiceTest.xml) | XML | 62 | 0 | 0 | 62 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.service.UsersServiceTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.service.UsersServiceTest.xml) | XML | 61 | 0 | 0 | 61 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.sql.SeedDataPasswordTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.sql.SeedDataPasswordTest.xml) | XML | 60 | 0 | 0 | 60 |
| [backend/target/test-classes/com/freshmart/irs/service/AuthServiceTest.class](/backend/target/test-classes/com/freshmart/irs/service/AuthServiceTest.class) | Java | 41 | 7 | 0 | 48 |
| [backend/target/test-classes/com/freshmart/irs/service/UsersServiceTest.class](/backend/target/test-classes/com/freshmart/irs/service/UsersServiceTest.class) | Java | 44 | 0 | 0 | 44 |
| [backend/target/test-classes/com/freshmart/irs/sql/SeedDataPasswordTest.class](/backend/target/test-classes/com/freshmart/irs/sql/SeedDataPasswordTest.class) | Java | 13 | 0 | 0 | 13 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details