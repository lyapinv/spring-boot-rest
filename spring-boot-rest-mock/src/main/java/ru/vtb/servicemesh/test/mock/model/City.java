package ru.vtb.servicemesh.test.mock.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class City {

    private final Long id;
    private final String name;
    private final int population;
}