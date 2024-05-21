package com.daniel.indotools.handler;

import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.storage.Cache;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.UUID;
import java.util.stream.Collectors;

public class PickaxeHandler extends Cache<Pickaxe> {

    public Pickaxe findPickaxeById(UUID id) {
        return find(e -> e.getId().equals(id)).orElse(null);
    }

}
