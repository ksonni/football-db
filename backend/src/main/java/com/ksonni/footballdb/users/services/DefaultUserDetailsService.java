package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(final String emailId) throws UsernameNotFoundException {
        final User user = usersRepository.findByEmailId(emailId);
        if (user == null) {
            throw new UsernameNotFoundException("Did not find user with email address: " + emailId);
        }
        return user;
    }

}
