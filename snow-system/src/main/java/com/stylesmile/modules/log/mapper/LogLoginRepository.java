package com.stylesmile.modules.log.mapper;

import com.stylesmile.modules.log.entity.LogLogin;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户mapper
 *
 * @author chenye
 * @date 2019/2/26
 */
@Repository
public interface LogLoginRepository extends R2dbcRepository<LogLogin, Integer> {

}
