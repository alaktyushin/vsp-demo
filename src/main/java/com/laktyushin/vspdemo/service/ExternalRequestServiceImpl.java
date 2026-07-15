package com.laktyushin.vspdemo.service;

import com.laktyushin.vspdemo.error.NotFoundException;
import com.laktyushin.vspdemo.model.ExternalSearchResult;
import com.laktyushin.vspdemo.model.Request;
import com.laktyushin.vspdemo.model.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@AllArgsConstructor
public class ExternalRequestServiceImpl implements ExternalRequestService {

    private final RestClient restClient;

    @Override
    public ExternalSearchResult externalServiceSearch(String bdIndex, Request searchRequest) {
        try {
            return ExternalSearchResult.builder()
                    .values(doRequest(bdIndex, searchRequest).getValues()).errorMessage("")
                    .build();
        } catch (NotFoundException e) {
            return ExternalSearchResult.builder()
                    .values(List.of()).errorMessage("NOT_FOUND")
                    .build();

        } catch (RuntimeException e) {
            return ExternalSearchResult.builder()
                    .values(List.of()).errorMessage(e.getMessage())
                    .build();
        }
    }

    private Response doRequest(String bdIndex, Request searchRequest) {
        return restClient.post()
                .uri("/" + bdIndex)
                .body(searchRequest)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new NotFoundException();
                })
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException(response.getStatusText());
                })
                .body(Response.class);
    }
}
