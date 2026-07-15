package com.laktyushin.vspdemo.service;

import com.laktyushin.vspdemo.model.Request;

import java.util.concurrent.ExecutionException;

public interface SearchBD {

    String internalSearch(Request searchRequest) throws ExecutionException, InterruptedException;
}
