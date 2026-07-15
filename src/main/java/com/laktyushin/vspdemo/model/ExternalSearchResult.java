package com.laktyushin.vspdemo.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ExternalSearchResult {
    List<String> values;
    String errorMessage;
}
