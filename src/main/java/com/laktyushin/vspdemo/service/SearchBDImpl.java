package com.laktyushin.vspdemo.service;

import com.laktyushin.vspdemo.model.ExternalSearchResult;
import com.laktyushin.vspdemo.model.Request;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static com.laktyushin.vspdemo.model.BdIndex.INDEX_A;
import static com.laktyushin.vspdemo.model.BdIndex.INDEX_B;
import static com.laktyushin.vspdemo.model.BdIndex.INDEX_C;

@Service
@AllArgsConstructor
public class SearchBDImpl implements SearchBD {

    private final ExternalRequestImpl externalRequestImpl;

    @Override
    public String internalSearch(Request searchRequest) throws ExecutionException, InterruptedException {

        CompletableFuture<ExternalSearchResult> futureOfDbIndexA =
                CompletableFuture.supplyAsync(
                        () -> externalRequestImpl.externalServiceSearch(INDEX_A.getIndex(), searchRequest));
        CompletableFuture<ExternalSearchResult> futureOfDbIndexB =
                CompletableFuture.supplyAsync(
                        () -> externalRequestImpl.externalServiceSearch(INDEX_B.getIndex(), searchRequest));
        CompletableFuture<ExternalSearchResult> futureOfDbIndexC =
                CompletableFuture.supplyAsync(
                        () -> externalRequestImpl.externalServiceSearch(INDEX_C.getIndex(), searchRequest));

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(futureOfDbIndexA, futureOfDbIndexB, futureOfDbIndexC);

        combinedFuture.get();

        List<ExternalSearchResult> futures = Stream.of(futureOfDbIndexA, futureOfDbIndexB, futureOfDbIndexC)
                .map(CompletableFuture::join)
                .toList();

        for (var result : futures) {
            if (!CollectionUtils.isEmpty(result.getValues())) {
                return result.getValues().get(0);
            }
            if ("NOT_FOUND".equals(result.getErrorMessage())) {
                return "NOT_FOUND";
            }
        }
        return "FAILED_PRECONDITION";
    }
}
