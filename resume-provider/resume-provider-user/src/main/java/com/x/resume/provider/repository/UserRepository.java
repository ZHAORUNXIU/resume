package com.x.resume.provider.repository;

import com.x.resume.common.repository.BaseRepository;
import com.x.resume.model.domain.user.UserDO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户Service
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
@Repository
public interface UserRepository extends BaseRepository<UserDO, Long> {

    Optional<UserDO> getById(Long id);

    Optional<UserDO> getByUserId(Long userId);

    Optional<UserDO> getByPhone(String paygateID);

    List<UserDO> findByIdIn(List<Long> ids);

    List<UserDO> findByIdInAndState(List<Long> ids, Integer state);

}
