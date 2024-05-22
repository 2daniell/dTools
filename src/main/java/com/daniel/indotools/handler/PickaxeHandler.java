package com.daniel.indotools.handler;

import com.daniel.indotools.model.Pickaxe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class PickaxeHandler {

    private static final List<Pickaxe> pickaxes = new ArrayList<>();

    public static Pickaxe findPickaxeById(UUID id) {
        return find(e -> e.getId().equals(id)).orElse(null);
    }

    private static Optional<Pickaxe> find(Predicate<Pickaxe> predicate) {
        return pickaxes.stream().filter(predicate).findFirst();
    }

    public static List<Pickaxe> getPickaxes() {
        return pickaxes;
    }
}
