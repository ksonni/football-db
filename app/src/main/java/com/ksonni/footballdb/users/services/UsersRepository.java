package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.query.QueryableRepository;
import com.ksonni.footballdb.users.domain.User;

public interface UsersRepository extends QueryableRepository<User, String> {

    /**
     * Finds a user by email id.
     *
     * @param emailId Email id of the user.
     * @return The saved user.
     */
    User findByEmailId(String emailId);

    /**
     * Finds the first user ordered by email id.
     *
     * @return The saved user.
     */
    User findFirstByOrderByEmailIdAsc();

}
