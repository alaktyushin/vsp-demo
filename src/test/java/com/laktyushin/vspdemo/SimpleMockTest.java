package com.laktyushin.vspdemo;

import com.laktyushin.vspdemo.model.ExternalSearchResult;
import com.laktyushin.vspdemo.model.Request;
import com.laktyushin.vspdemo.service.ExternalRequestServiceImpl;
import com.laktyushin.vspdemo.service.SearchBDServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;

import static com.laktyushin.vspdemo.model.BdIndex.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest()
@ExtendWith(MockitoExtension.class)
class SimpleMockTest {

    @Mock
    private ExternalRequestServiceImpl externalRequestService;

    @Test
    void contextLoads() {
    }

    @Test
    public void whenAllRequestsSucceeded() throws ExecutionException, InterruptedException {
        Request request = Request.builder().header("Some Header").body("Some Body").build();

        when(externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of("smth-A")).build());
        when(externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of("smth-B")).build());
        when(externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of("smth-C")).build());

        Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);

        CompletableFuture<ExternalSearchResult> cfA = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request), delayedExecutor);
        CompletableFuture<ExternalSearchResult> cfB = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request));
        CompletableFuture<ExternalSearchResult> cfC = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request));

        CompletableFuture<Void> allCf = CompletableFuture.allOf(cfA, cfB, cfC);

        assertFalse(allCf.isDone());

        allCf.join();

        assertFalse(allCf.isCancelled());
        assertTrue(allCf.isDone());
        assertFalse(allCf.isCompletedExceptionally());

        assertEquals("smth-A", new SearchBDServiceImpl(externalRequestService).internalSearch(request));
    }

    @Test
    public void whenOneRequestSucceededAndOthersNotFound() throws ExecutionException, InterruptedException {
        Request request = Request.builder().header("Some Header").body("Some Body").build();

        when(externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("NOT_FOUND").build());
        when(externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of("smth-B")).build());
        when(externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("NOT_FOUND").build());

        Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);

        CompletableFuture<ExternalSearchResult> cfA = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request), delayedExecutor);
        CompletableFuture<ExternalSearchResult> cfB = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request));
        CompletableFuture<ExternalSearchResult> cfC = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request));

        CompletableFuture<Void> allCf = CompletableFuture.allOf(cfA, cfB, cfC);

        assertFalse(allCf.isDone());

        allCf.join();

        assertFalse(allCf.isCancelled());
        assertTrue(allCf.isDone());
        assertFalse(allCf.isCompletedExceptionally());

        assertEquals("smth-B", new SearchBDServiceImpl(externalRequestService).internalSearch(request));
    }

    @Test
    public void whenNoRequestSucceededAndOneOtherError() throws ExecutionException, InterruptedException {
        Request request = Request.builder().header("Some Header").body("Some Body").build();

        when(externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("NOT_FOUND").build());
        when(externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("BAD_REQUEST").build());
        when(externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("FORBIDDEN").build());

        Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);

        CompletableFuture<ExternalSearchResult> cfA = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request), delayedExecutor);
        CompletableFuture<ExternalSearchResult> cfB = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request));
        CompletableFuture<ExternalSearchResult> cfC = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request));

        CompletableFuture<Void> allCf = CompletableFuture.allOf(cfA, cfB, cfC);

        assertFalse(allCf.isDone());

        allCf.join();

        assertFalse(allCf.isCancelled());
        assertTrue(allCf.isDone());
        assertFalse(allCf.isCompletedExceptionally());

        assertEquals("NOT_FOUND", new SearchBDServiceImpl(externalRequestService).internalSearch(request));
    }

    @Test
    public void whenNoRequestSucceededAndAllOtherError() throws ExecutionException, InterruptedException {
        Request request = Request.builder().header("Some Header").body("Some Body").build();

        when(externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("TIMEOUT").build());
        when(externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("BAD_REQUEST").build());
        when(externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request))
                .thenReturn(ExternalSearchResult.builder().values(List.of()).errorMessage("FORBIDDEN").build());

        Executor delayedExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS);

        CompletableFuture<ExternalSearchResult> cfA = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_A.getIndex(), request), delayedExecutor);
        CompletableFuture<ExternalSearchResult> cfB = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_B.getIndex(), request));
        CompletableFuture<ExternalSearchResult> cfC = CompletableFuture.supplyAsync(
                () -> externalRequestService.externalServiceSearch(INDEX_C.getIndex(), request));

        CompletableFuture<Void> allCf = CompletableFuture.allOf(cfA, cfB, cfC);

        assertFalse(allCf.isDone());

        allCf.join();

        assertFalse(allCf.isCancelled());
        assertTrue(allCf.isDone());
        assertFalse(allCf.isCompletedExceptionally());
        assertEquals("FAILED_PRECONDITION", new SearchBDServiceImpl(externalRequestService).internalSearch(request));
    }
}
