package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;

import java.lang.reflect.Field;

public class PlayerQueryParser extends DefaultQueryParser<Player> {

    public PlayerQueryParser(Class<Player> objectType) {
        super(objectType);
    }

    @Override
    public FilterQueryComponentSupplier<Player> getQueryComponentSupplier(Field field) {
        var type = field.getType();

        if (type.isAssignableFrom(Side.class)) {
            return (key, value) -> new Side.SideFilterQueryComponent(key, value);
        } else if (type.isAssignableFrom(WorkRate.class)) {
            return (key, value) -> new WorkRate.WorkRateFilterQueryComponent(key, value);
        }

        return super.getQueryComponentSupplier(field);
    }
    
}
