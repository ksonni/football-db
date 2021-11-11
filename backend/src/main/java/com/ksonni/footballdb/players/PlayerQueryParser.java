package com.ksonni.footballdb.players;

import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.FilterQueryComponentSupplier;

import java.lang.reflect.Field;

public class PlayerQueryParser extends DefaultQueryParser<Player> {

    public PlayerQueryParser(Class<Player> objectType) {
        super(objectType);
    }

    @Override
    public FilterQueryComponentSupplier<Player> getQueryComponentSupplier(Field field) {
        var type = field.getType();

        if (type.isAssignableFrom(Side.class)) {
            return (key, value) -> new SideFilterQueryComponent(key, value);
        } else if (type.isAssignableFrom(WorkRate.class)) {
            return (key, value) -> new WorkRateFilterQueryComponent(key, value);
        }

        return super.getQueryComponentSupplier(field);
    }
    
}
