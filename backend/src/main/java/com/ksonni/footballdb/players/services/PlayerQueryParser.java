package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.components.FilterQueryComponentSupplier;

import java.lang.reflect.Field;

public class PlayerQueryParser extends DefaultQueryParser<Player> {

    /**
     * Constructs Query objects to search for players.
     */
    public PlayerQueryParser() {
        super(Player.class);
    }

    @Override
    public FilterQueryComponentSupplier<Player> getQueryComponentSupplier(final Field field) {
        final var type = field.getType();

        if (type.isAssignableFrom(Side.class)) {
            return (key, value) -> new Side.SideFilterQueryComponent(key, value);
        } else if (type.isAssignableFrom(WorkRate.class)) {
            return (key, value) -> new WorkRate.WorkRateFilterQueryComponent(key, value);
        }

        return super.getQueryComponentSupplier(field);
    }

}
