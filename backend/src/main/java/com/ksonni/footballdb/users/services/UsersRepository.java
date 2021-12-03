package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.queryparser.QueryableRepository;
import com.ksonni.footballdb.users.domain.User;

public interface UsersRepository extends QueryableRepository<User, String> {

    User findByEmailId(String emailId);

}
