package com.daniel.indotools.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@ToString
public class Triple<U, I, D> implements Serializable {

    private static final long serialVersionUID = 1L;

    private U first;
    private I second;
    private D third;
}
