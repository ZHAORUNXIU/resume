package com.x.resume.provider.repository;

import com.x.resume.model.domain.user.UserMongoDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongoDO, String> {

    Optional<UserMongoDO> findByUserId(Long userId);

    Optional<UserMongoDO> findByPhone(String phone);
}
