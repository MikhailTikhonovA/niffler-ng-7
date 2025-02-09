package student.service;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import student.config.Config;
import student.data.Databases;
import student.data.dao.impl.*;
import student.data.entity.auth.AuthUserEntity;
import student.data.entity.auth.Authority;
import student.data.entity.auth.AuthorityEntity;
import student.data.entity.user.UserEntity;
import student.model.AuthUserJson;
import student.model.AuthorityJson;
import student.model.UserJson;

import java.sql.SQLException;
import java.util.Arrays;

import static student.data.Databases.dataSource;

public class AuthDbClient {
    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    public AuthUserEntity createUser(AuthUserJson userJson, AuthorityJson authorityJson) throws SQLException {
        return Databases.transaction(connection -> {
            var authDaoJdbc = new AuthDaoJdbc(connection);
            var authorityDaoJdbc = new AuthorityDaoJdbc(connection);
            var createdUser = authDaoJdbc.createUser(AuthUserEntity.fromJson(userJson));
            authorityDaoJdbc.createAuthority(AuthorityEntity.fromJson(authorityJson, createdUser));
            return createdUser;
        }, CFG.authJdbcUrl(), null);
    }

    public void deleteUser(AuthUserEntity authUserEntity) {
        Databases.transaction(connection -> {
            var authDaoJdbc = new AuthDaoJdbc(connection);
            var authorityDaoJdbc = new AuthorityDaoJdbc(connection);
            authorityDaoJdbc.deleteUser(authUserEntity);
            authDaoJdbc.deleteUser(authUserEntity);
        }, CFG.authJdbcUrl(), null);
    }

    public UserJson createUserSpringJdbc(UserJson userJson) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(userJson.username());
        authUserEntity.setPassword(PASSWORD_ENCODER.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity result = new AuthDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).createUser(authUserEntity);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setId(result.getId());
                    authority.setAuthority(e);
                    return authority;
                }
        ).toArray(AuthorityEntity[]::new);
        new AuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).createAuthority(authorityEntities);
        return UserJson.fromEntity(
                new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl())).createUser(
                        UserEntity.fromJson(userJson)
                ), null);

    }
}
