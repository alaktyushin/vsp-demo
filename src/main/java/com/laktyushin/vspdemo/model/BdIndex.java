package com.laktyushin.vspdemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BdIndex {
    INDEX_A("indexA"),
    INDEX_B("indexB"),
    INDEX_C("indexC");

    private final String index;
}
