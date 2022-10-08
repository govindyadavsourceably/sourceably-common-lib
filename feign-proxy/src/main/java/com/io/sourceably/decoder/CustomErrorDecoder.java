package com.io.sourceably.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class CustomErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder delegate = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		HttpHeaders responseHeaders = response.headers().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				entry -> new ArrayList<>(entry.getValue()), (a, b) -> b, HttpHeaders::new));

		HttpStatus statusCode = HttpStatus.valueOf(response.status());

		if (null == response.body() && 404 == response.status()) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
		}

		byte[] responseBody;
		try {

			//System.out.println("Error response.body() : " + response.body());

			responseBody = StreamUtils
					.copyToByteArray(response.body() != null ? response.body().asInputStream() : null);
		} catch (IOException e) {

			throw new RuntimeException("Failed to process response body.", e);
		}
		String statusText = String.format("%s,%s",
				response.request().url().substring(response.request().url().lastIndexOf("/") + 1), methodKey);
		if (response.status() >= 400 && response.status() <= 499) {
			return new HttpClientErrorException(statusCode, statusText, responseHeaders, responseBody, null);
		}
		if (response.status() >= 500 && response.status() <= 599) {
			return new HttpServerErrorException(statusCode, statusText, responseHeaders, responseBody, null);
		}
		return delegate.decode(methodKey, response);
	}
}
