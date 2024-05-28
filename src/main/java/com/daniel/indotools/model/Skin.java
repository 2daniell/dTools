package com.daniel.indotools.model;

import com.daniel.indotools.objects.enums.SkinType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Skin {

    private final UUID id;
    private final UUID owner;
    private SkinType skinType;

    public Skin(UUID owner, SkinType skinType) {
        this.owner = owner;
        this.skinType = skinType;
        this.id = UUID.randomUUID();
    }
}
