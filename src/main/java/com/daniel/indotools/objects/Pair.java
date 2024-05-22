package com.daniel.indotools.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Pair<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K key;
    private V value;

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
