package student.service;

import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import student.config.Config;
import student.data.dao.AuthDao;
import student.data.dao.AuthorityDao;
import student.data.dao.UserdataDao;
import student.data.dao.impl.AuthDaoSpringJdbc;
import student.data.dao.impl.AuthorityDaoSpringJdbc;
import student.data.dao.impl.UserdataUserDaoSpringJdbc;
import student.data.entity.auth.AuthUserEntity;
import student.data.entity.auth.Authority;
import student.data.entity.auth.AuthorityEntity;
import student.data.entity.user.UserEntity;
import student.data.tmpl.XaTransactionTemplate;
import student.model.UserJson;

import java.util.Arrays;

import static student.data.tmpl.Datasources.dataSource;

public class AuthDbClient {
    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    private final AuthDao authUserDao = new AuthDaoSpringJdbc();
    private final AuthorityDao authAuthorityDao = new AuthorityDaoSpringJdbc();
    private final UserdataDao userdataDao = new UserdataUserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            dataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            dataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUserTxTemplate(UserJson userJson) {
        return txTemplate.execute(txStatus -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(userJson.username());
                    authUser.setPassword(PASSWORD_ENCODER.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser.getId());
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDao.createAuthority(authorityEntities);
                    return UserJson.fromEntity(
                            userdataDao.createUser(UserEntity.fromJson(userJson)),
                            null
                    );
                }
        );
    }

    public UserJson createUserSpringJdbc(UserJson userJson) {
        return xaTxTemplate.transaction(() -> {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setUsername(userJson.username());
                    authUserEntity.setPassword(PASSWORD_ENCODER.encode("12345"));
                    authUserEntity.setEnabled(true);
                    authUserEntity.setAccountNonExpired(true);
                    authUserEntity.setAccountNonLocked(true);
                    authUserEntity.setCredentialsNonExpired(true);

                    AuthUserEntity result = authUserDao.createUser(authUserEntity);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity authority = new AuthorityEntity();
                                authority.setId(result.getId());
                                authority.setAuthority(e);
                                return authority;
                            }
                    ).toArray(AuthorityEntity[]::new);
                    authAuthorityDao.createAuthority(authorityEntities);
                    return UserJson.fromEntity(
                            userdataDao.createUser(
                                    UserEntity.fromJson(userJson)
                            ), null);
                }
        );
    }
}
