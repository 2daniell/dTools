package com.daniel.indotools.storage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Cache<T> extends ArrayList<T> {

    public Optional<T> find(Predicate<T> predicate) {
        return stream().filter(predicate).findFirst();
    }
}
