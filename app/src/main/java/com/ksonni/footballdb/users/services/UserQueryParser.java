package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;

import java.lang.reflect.Field;

public class UserQueryParser extends DefaultQueryParser<User> {

    /**
     * Constructs Query objects to search for users.
     */
    public UserQueryParser() {
        super(User.class);
    }

    @Override
    public FilterQueryComponentSupplier<User> getQueryComponentSupplier(final Field field) {
        final var type = field.getType();

        if (type.isAssignableFrom(Role.class)) {
            return Role.RoleFilterQueryComponent::new;
        }

        return super.getQueryComponentSupplier(field);
    }

}
