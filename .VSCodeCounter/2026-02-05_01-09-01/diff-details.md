# Diff Details

Date : 2026-02-05 01:09:01

Directory d:\\Code\\freshmart-irs\\backend

Total : 55 files,  -394 codes, 4 comments, 40 blanks, all -350 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [backend/pom.xml](/backend/pom.xml) | XML | 11 | 0 | -1 | 10 |
| [backend/src/main/java/com/freshmart/irs/auth/JwtAuthFilter.java](/backend/src/main/java/com/freshmart/irs/auth/JwtAuthFilter.java) | Java | 3 | 0 | 0 | 3 |
| [backend/src/main/java/com/freshmart/irs/common/PhoneNumberValidator.java](/backend/src/main/java/com/freshmart/irs/common/PhoneNumberValidator.java) | Java | 23 | 4 | 6 | 33 |
| [backend/src/main/java/com/freshmart/irs/config/OpenApiConfig.java](/backend/src/main/java/com/freshmart/irs/config/OpenApiConfig.java) | Java | 12 | 0 | 1 | 13 |
| [backend/src/main/java/com/freshmart/irs/service/AuditService.java](/backend/src/main/java/com/freshmart/irs/service/AuditService.java) | Java | -30 | 0 | -6 | -36 |
| [backend/src/main/java/com/freshmart/irs/service/AuthService.java](/backend/src/main/java/com/freshmart/irs/service/AuthService.java) | Java | -115 | 0 | -8 | -123 |
| [backend/src/main/java/com/freshmart/irs/service/PasswordPolicy.java](/backend/src/main/java/com/freshmart/irs/service/PasswordPolicy.java) | Java | -24 | 0 | -2 | -26 |
| [backend/src/main/java/com/freshmart/irs/service/RolesService.java](/backend/src/main/java/com/freshmart/irs/service/RolesService.java) | Java | -26 | 0 | -2 | -28 |
| [backend/src/main/java/com/freshmart/irs/service/UserRoleService.java](/backend/src/main/java/com/freshmart/irs/service/UserRoleService.java) | Java | -82 | 0 | -9 | -91 |
| [backend/src/main/java/com/freshmart/irs/service/UsersService.java](/backend/src/main/java/com/freshmart/irs/service/UsersService.java) | Java | -234 | 0 | -21 | -255 |
| [backend/src/main/java/com/freshmart/irs/service/impl/AuditServiceImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/AuditServiceImpl.java) | Java | 36 | 3 | 8 | 47 |
| [backend/src/main/java/com/freshmart/irs/service/impl/AuthServiceImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/AuthServiceImpl.java) | Java | 140 | 3 | 17 | 160 |
| [backend/src/main/java/com/freshmart/irs/service/impl/PasswordPolicyImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/PasswordPolicyImpl.java) | Java | 30 | 3 | 4 | 37 |
| [backend/src/main/java/com/freshmart/irs/service/impl/RolesServiceImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/RolesServiceImpl.java) | Java | 36 | 3 | 7 | 46 |
| [backend/src/main/java/com/freshmart/irs/service/impl/UserRoleServiceImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/UserRoleServiceImpl.java) | Java | 94 | 3 | 14 | 111 |
| [backend/src/main/java/com/freshmart/irs/service/impl/UsersServiceImpl.java](/backend/src/main/java/com/freshmart/irs/service/impl/UsersServiceImpl.java) | Java | 266 | 3 | 34 | 303 |
| [backend/src/main/resources/application.yml](/backend/src/main/resources/application.yml) | YAML | 0 | 0 | 1 | 1 |
| [backend/src/main/resources/logback.xml](/backend/src/main/resources/logback.xml) | XML | 13 | 3 | 2 | 18 |
| [backend/src/test/java/com/freshmart/irs/service/AuthServiceTest.java](/backend/src/test/java/com/freshmart/irs/service/AuthServiceTest.java) | Java | 1 | 0 | 0 | 1 |
| [backend/src/test/java/com/freshmart/irs/service/UsersServiceTest.java](/backend/src/test/java/com/freshmart/irs/service/UsersServiceTest.java) | Java | 1 | 0 | 0 | 1 |
| [backend/target/classes/application.yml](/backend/target/classes/application.yml) | YAML | 0 | 0 | 1 | 1 |
| [backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class](/backend/target/classes/com/freshmart/irs/api/AuthController$PermissionsResponse.class) | Java | 3 | 0 | 0 | 3 |
| [backend/target/classes/com/freshmart/irs/api/AuthController$RegisterRequest.class](/backend/target/classes/com/freshmart/irs/api/AuthController$RegisterRequest.class) | Java | -8 | 0 | 0 | -8 |
| [backend/target/classes/com/freshmart/irs/api/AuthController.class](/backend/target/classes/com/freshmart/irs/api/AuthController.class) | Java | -8 | 0 | 0 | -8 |
| [backend/target/classes/com/freshmart/irs/api/RolesController.class](/backend/target/classes/com/freshmart/irs/api/RolesController.class) | Java | 1 | 0 | 0 | 1 |
| [backend/target/classes/com/freshmart/irs/api/UsersController.class](/backend/target/classes/com/freshmart/irs/api/UsersController.class) | Java | -6 | 0 | 0 | -6 |
| [backend/target/classes/com/freshmart/irs/auth/AuthContextHolder.class](/backend/target/classes/com/freshmart/irs/auth/AuthContextHolder.class) | Java | -4 | 0 | 0 | -4 |
| [backend/target/classes/com/freshmart/irs/auth/JwtAuthFilter.class](/backend/target/classes/com/freshmart/irs/auth/JwtAuthFilter.class) | Java | 1 | 0 | 0 | 1 |
| [backend/target/classes/com/freshmart/irs/common/BizException.class](/backend/target/classes/com/freshmart/irs/common/BizException.class) | Java | -1 | 0 | 0 | -1 |
| [backend/target/classes/com/freshmart/irs/common/PhoneNumberValidator.class](/backend/target/classes/com/freshmart/irs/common/PhoneNumberValidator.class) | Java | 23 | 0 | 0 | 23 |
| [backend/target/classes/com/freshmart/irs/config/OpenApiConfig.class](/backend/target/classes/com/freshmart/irs/config/OpenApiConfig.class) | Java | -4 | 7 | 0 | 3 |
| [backend/target/classes/com/freshmart/irs/entity/AuditEventEntity.class](/backend/target/classes/com/freshmart/irs/entity/AuditEventEntity.class) | Java | -10 | 0 | 0 | -10 |
| [backend/target/classes/com/freshmart/irs/entity/RoleEntity.class](/backend/target/classes/com/freshmart/irs/entity/RoleEntity.class) | Java | -4 | 0 | 0 | -4 |
| [backend/target/classes/com/freshmart/irs/entity/UserEntity.class](/backend/target/classes/com/freshmart/irs/entity/UserEntity.class) | Java | -10 | 0 | 0 | -10 |
| [backend/target/classes/com/freshmart/irs/entity/UserRoleEntity.class](/backend/target/classes/com/freshmart/irs/entity/UserRoleEntity.class) | Java | -2 | 0 | 0 | -2 |
| [backend/target/classes/com/freshmart/irs/service/AuditService.class](/backend/target/classes/com/freshmart/irs/service/AuditService.class) | Java | -23 | 0 | 0 | -23 |
| [backend/target/classes/com/freshmart/irs/service/AuthService.class](/backend/target/classes/com/freshmart/irs/service/AuthService.class) | Java | -78 | 0 | 0 | -78 |
| [backend/target/classes/com/freshmart/irs/service/PasswordPolicy.class](/backend/target/classes/com/freshmart/irs/service/PasswordPolicy.class) | Java | -17 | 0 | 1 | -16 |
| [backend/target/classes/com/freshmart/irs/service/RolesService.class](/backend/target/classes/com/freshmart/irs/service/RolesService.class) | Java | -41 | 0 | -2 | -43 |
| [backend/target/classes/com/freshmart/irs/service/UserRoleService.class](/backend/target/classes/com/freshmart/irs/service/UserRoleService.class) | Java | -57 | 0 | 0 | -57 |
| [backend/target/classes/com/freshmart/irs/service/UsersService.class](/backend/target/classes/com/freshmart/irs/service/UsersService.class) | Java | -111 | -24 | 0 | -135 |
| [backend/target/classes/com/freshmart/irs/service/impl/AuditServiceImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/AuditServiceImpl.class) | Java | 27 | 0 | 0 | 27 |
| [backend/target/classes/com/freshmart/irs/service/impl/AuthServiceImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/AuthServiceImpl.class) | Java | 80 | 0 | 0 | 80 |
| [backend/target/classes/com/freshmart/irs/service/impl/PasswordPolicyImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/PasswordPolicyImpl.class) | Java | 20 | 0 | 0 | 20 |
| [backend/target/classes/com/freshmart/irs/service/impl/RolesServiceImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/RolesServiceImpl.class) | Java | 48 | 0 | 1 | 49 |
| [backend/target/classes/com/freshmart/irs/service/impl/UserRoleServiceImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/UserRoleServiceImpl.class) | Java | 70 | 0 | 1 | 71 |
| [backend/target/classes/com/freshmart/irs/service/impl/UsersServiceImpl.class](/backend/target/classes/com/freshmart/irs/service/impl/UsersServiceImpl.class) | Java | 122 | 0 | 0 | 122 |
| [backend/target/classes/logback.xml](/backend/target/classes/logback.xml) | XML | 13 | 3 | 2 | 18 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowMySqlTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowMySqlTest.xml) | XML | -196 | 0 | -3 | -199 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.AuthFlowTest.xml) | XML | -109 | 0 | -6 | -115 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.PasswordHashGeneratorTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.PasswordHashGeneratorTest.xml) | XML | -68 | 0 | 0 | -68 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.SeedPasswordHashTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.SeedPasswordHashTest.xml) | XML | -64 | 0 | 0 | -64 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.security.JwtServiceTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.security.JwtServiceTest.xml) | XML | -68 | 0 | 0 | -68 |
| [backend/target/surefire-reports/TEST-com.freshmart.irs.security.PasswordPolicyTest.xml](/backend/target/surefire-reports/TEST-com.freshmart.irs.security.PasswordPolicyTest.xml) | XML | -71 | 0 | 0 | -71 |
| [backend/target/test-classes/com/freshmart/irs/service/AuthServiceTest.class](/backend/target/test-classes/com/freshmart/irs/service/AuthServiceTest.class) | Java | 3 | -7 | 0 | -4 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details