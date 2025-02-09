package student.data.dao;

import student.data.entity.auth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthDao {
    AuthUserEntity createUser(AuthUserEntity user);

    Optional<AuthUserEntity> findById(UUID id);

    void deleteUser(AuthUserEntity user);
}
