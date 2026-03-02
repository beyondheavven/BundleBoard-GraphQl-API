package com.source.bundleboard.user.repository;

import com.source.bundleboard.user.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {


}
