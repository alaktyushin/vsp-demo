package com.laktyushin.vspdemo.service;

import com.laktyushin.vspdemo.model.ExternalSearchResult;
import com.laktyushin.vspdemo.model.Request;

public interface ExternalRequest {

    ExternalSearchResult externalServiceSearch(String bdIndex, Request searchRequest);
}
