package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;

import java.lang.reflect.Field;

public class UserQueryParser extends DefaultQueryParser<User> {

    public UserQueryParser(Class<User> objectType) {
        super(objectType);
    }

    @Override
    public FilterQueryComponentSupplier<User> getQueryComponentSupplier(Field field) {
        var type = field.getType();

        if (type.isAssignableFrom(Role.class)) {
            return (key, value) -> new Role.RoleFilterQueryComponent(key, value);
        }

        return super.getQueryComponentSupplier(field);
    }

}
