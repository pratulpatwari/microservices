package dev.pratul;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import dev.pratul.config.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class CorrelationInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		template.header(Constants.CORRELATION_ID_HEADER_NAME, MDC.get(Constants.CORRELATION_ID_LOG_VAR_NAME));
	}
}
