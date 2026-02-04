# Diff Details

Date : 2026-02-04 03:53:36

Directory d:\\Code\\freshmart-irs\\backend

Total : 52 files,  1203 codes, 5 comments, 198 blanks, all 1406 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [backend/pom.xml](/backend/pom.xml) | XML | 37 | 0 | 2 | 39 |
| [backend/src/main/java/com/freshmart/irs/FreshmartIrsBackendApplication.java](/backend/src/main/java/com/freshmart/irs/FreshmartIrsBackendApplication.java) | Java | 5 | 0 | -1 | 4 |
| [backend/src/main/java/com/freshmart/irs/api/AuthController.java](/backend/src/main/java/com/freshmart/irs/api/AuthController.java) | Java | 20 | 0 | 2 | 22 |
| [backend/src/main/java/com/freshmart/irs/common/BizException.java](/backend/src/main/java/com/freshmart/irs/common/BizException.java) | Java | 11 | 0 | 4 | 15 |
| [backend/src/main/java/com/freshmart/irs/common/GlobalExceptionHandler.java](/backend/src/main/java/com/freshmart/irs/common/GlobalExceptionHandler.java) | Java | 26 | 0 | 6 | 32 |
| [backend/src/main/java/com/freshmart/irs/config/MybatisPlusConfig.java](/backend/src/main/java/com/freshmart/irs/config/MybatisPlusConfig.java) | Java | 15 | 0 | 3 | 18 |
| [backend/src/main/java/com/freshmart/irs/config/TraceIdFilter.java](/backend/src/main/java/com/freshmart/irs/config/TraceIdFilter.java) | Java | 3 | 0 | -1 | 2 |
| [backend/src/main/java/com/freshmart/irs/dto/auth/RegisterRequest.java](/backend/src/main/java/com/freshmart/irs/dto/auth/RegisterRequest.java) | Java | 11 | 0 | 3 | 14 |
| [backend/src/main/java/com/freshmart/irs/dto/user/UserUpdateRequest.java](/backend/src/main/java/com/freshmart/irs/dto/user/UserUpdateRequest.java) | Java | 3 | 0 | -1 | 2 |
| [backend/src/main/java/com/freshmart/irs/entity/AuditEventEntity.java](/backend/src/main/java/com/freshmart/irs/entity/AuditEventEntity.java) | Java | 79 | 0 | 30 | 109 |
| [backend/src/main/java/com/freshmart/irs/entity/RoleEntity.java](/backend/src/main/java/com/freshmart/irs/entity/RoleEntity.java) | Java | 54 | 0 | 21 | 75 |
| [backend/src/main/java/com/freshmart/irs/entity/UserEntity.java](/backend/src/main/java/com/freshmart/irs/entity/UserEntity.java) | Java | 69 | 0 | 27 | 96 |
| [backend/src/main/java/com/freshmart/irs/entity/UserRoleEntity.java](/backend/src/main/java/com/freshmart/irs/entity/UserRoleEntity.java) | Java | 49 | 0 | 18 | 67 |
| [backend/src/main/java/com/freshmart/irs/mapper/AuditEventMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/AuditEventMapper.java) | Java | 5 | 0 | 3 | 8 |
| [backend/src/main/java/com/freshmart/irs/mapper/RoleMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/RoleMapper.java) | Java | 5 | 0 | 3 | 8 |
| [backend/src/main/java/com/freshmart/irs/mapper/UserMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/UserMapper.java) | Java | 5 | 0 | 3 | 8 |
| [backend/src/main/java/com/freshmart/irs/mapper/UserRoleMapper.java](/backend/src/main/java/com/freshmart/irs/mapper/UserRoleMapper.java) | Java | 5 | 0 | 3 | 8 |
| [backend/src/main/java/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.java](/backend/src/main/java/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.java) | Java | 30 | 0 | 6 | 36 |
| [backend/src/main/java/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.java](/backend/src/main/java/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.java) | Java | 30 | 0 | 6 | 36 |
| [backend/src/main/java/com/freshmart/irs/security/JwtAuthenticationFilter.java](/backend/src/main/java/com/freshmart/irs/security/JwtAuthenticationFilter.java) | Java | 55 | 0 | 6 | 61 |
| [backend/src/main/java/com/freshmart/irs/security/JwtPrincipal.java](/backend/src/main/java/com/freshmart/irs/security/JwtPrincipal.java) | Java | 21 | 0 | 7 | 28 |
| [backend/src/main/java/com/freshmart/irs/security/JwtProperties.java](/backend/src/main/java/com/freshmart/irs/security/JwtProperties.java) | Java | 26 | 0 | 9 | 35 |
| [backend/src/main/java/com/freshmart/irs/security/JwtService.java](/backend/src/main/java/com/freshmart/irs/security/JwtService.java) | Java | 53 | 0 | 12 | 65 |
| [backend/src/main/java/com/freshmart/irs/security/SecurityConfig.java](/backend/src/main/java/com/freshmart/irs/security/SecurityConfig.java) | Java | 49 | 0 | 7 | 56 |
| [backend/src/main/java/com/freshmart/irs/service/AuditEventService.java](/backend/src/main/java/com/freshmart/irs/service/AuditEventService.java) | Java | 30 | 0 | 5 | 35 |
| [backend/src/main/resources/application.yml](/backend/src/main/resources/application.yml) | YAML | 14 | 0 | 2 | 16 |
| [backend/target/classes/application.yml](/backend/target/classes/application.yml) | YAML | 14 | 0 | 2 | 16 |
| [backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class](/backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class) | Java | 10 | 0 | 0 | 10 |
| [backend/target/classes/com/freshmart/irs/api/AuthController.class](/backend/target/classes/com/freshmart/irs/api/AuthController.class) | Java | 4 | 0 | 0 | 4 |
| [backend/target/classes/com/freshmart/irs/common/BizException.class](/backend/target/classes/com/freshmart/irs/common/BizException.class) | Java | 7 | 0 | 0 | 7 |
| [backend/target/classes/com/freshmart/irs/common/GlobalExceptionHandler.class](/backend/target/classes/com/freshmart/irs/common/GlobalExceptionHandler.class) | Java | 15 | 0 | 0 | 15 |
| [backend/target/classes/com/freshmart/irs/config/MybatisPlusConfig.class](/backend/target/classes/com/freshmart/irs/config/MybatisPlusConfig.class) | Java | 12 | 0 | 0 | 12 |
| [backend/target/classes/com/freshmart/irs/dto/auth/RegisterRequest.class](/backend/target/classes/com/freshmart/irs/dto/auth/RegisterRequest.class) | Java | 16 | 0 | 0 | 16 |
| [backend/target/classes/com/freshmart/irs/entity/AuditEventEntity.class](/backend/target/classes/com/freshmart/irs/entity/AuditEventEntity.class) | Java | 36 | 0 | 0 | 36 |
| [backend/target/classes/com/freshmart/irs/entity/RoleEntity.class](/backend/target/classes/com/freshmart/irs/entity/RoleEntity.class) | Java | 20 | 0 | 0 | 20 |
| [backend/target/classes/com/freshmart/irs/entity/UserEntity.class](/backend/target/classes/com/freshmart/irs/entity/UserEntity.class) | Java | 28 | 0 | 0 | 28 |
| [backend/target/classes/com/freshmart/irs/entity/UserRoleEntity.class](/backend/target/classes/com/freshmart/irs/entity/UserRoleEntity.class) | Java | 16 | 0 | 0 | 16 |
| [backend/target/classes/com/freshmart/irs/mapper/AuditEventMapper.class](/backend/target/classes/com/freshmart/irs/mapper/AuditEventMapper.class) | Java | 2 | 0 | 1 | 3 |
| [backend/target/classes/com/freshmart/irs/mapper/RoleMapper.class](/backend/target/classes/com/freshmart/irs/mapper/RoleMapper.class) | Java | 2 | 0 | 1 | 3 |
| [backend/target/classes/com/freshmart/irs/mapper/UserMapper.class](/backend/target/classes/com/freshmart/irs/mapper/UserMapper.class) | Java | 2 | 0 | 1 | 3 |
| [backend/target/classes/com/freshmart/irs/mapper/UserRoleMapper.class](/backend/target/classes/com/freshmart/irs/mapper/UserRoleMapper.class) | Java | 2 | 0 | 1 | 3 |
| [backend/target/classes/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.class](/backend/target/classes/com/freshmart/irs/security/ApiResponseAccessDeniedHandler.class) | Java | 15 | 0 | 0 | 15 |
| [backend/target/classes/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.class](/backend/target/classes/com/freshmart/irs/security/ApiResponseAuthenticationEntryPoint.class) | Java | 16 | 0 | 0 | 16 |
| [backend/target/classes/com/freshmart/irs/security/JwtAuthenticationFilter.class](/backend/target/classes/com/freshmart/irs/security/JwtAuthenticationFilter.class) | Java | 40 | 0 | 0 | 40 |
| [backend/target/classes/com/freshmart/irs/security/JwtPrincipal.class](/backend/target/classes/com/freshmart/irs/security/JwtPrincipal.class) | Java | 9 | 0 | 0 | 9 |
| [backend/target/classes/com/freshmart/irs/security/JwtProperties.class](/backend/target/classes/com/freshmart/irs/security/JwtProperties.class) | Java | 9 | 0 | 0 | 9 |
| [backend/target/classes/com/freshmart/irs/security/JwtService$ParsedToken.class](/backend/target/classes/com/freshmart/irs/security/JwtService$ParsedToken.class) | Java | 9 | 0 | 0 | 9 |
| [backend/target/classes/com/freshmart/irs/security/JwtService.class](/backend/target/classes/com/freshmart/irs/security/JwtService.class) | Java | 35 | 0 | 0 | 35 |
| [backend/target/classes/com/freshmart/irs/security/SecurityConfig.class](/backend/target/classes/com/freshmart/irs/security/SecurityConfig.class) | Java | 36 | 5 | 0 | 41 |
| [backend/target/classes/com/freshmart/irs/service/AuditEventService.class](/backend/target/classes/com/freshmart/irs/service/AuditEventService.class) | Java | 26 | 0 | 0 | 26 |
| [backend/target/maven-archiver/pom.properties](/backend/target/maven-archiver/pom.properties) | Java Properties | 3 | 0 | 1 | 4 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowTest.xml) | XML | 109 | 0 | 6 | 115 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details