package it.cs.contact.tracing.be;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.cs.contact.tracing.be.dto.LambdaResponse;
import it.cs.contact.tracing.be.entity.Contact;
import it.cs.contact.tracing.be.repository.SecondLevelContactRepository;
import it.cs.contact.tracing.be.utils.Util;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.cs.contact.tracing.be.utils.Util.getFirst;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, LambdaResponse> {

	private static final Logger logger = LoggerFactory.getLogger(Handler.class);

	final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	final SecondLevelContactRepository secondLevelContactRepository = new SecondLevelContactRepository();

	@Override
	public LambdaResponse handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {

		Util.logEnvironment(event, context, gson);

		try {

			return processRequest(event);
		}
		catch (final Exception e) {

			logger.error("Generic Error", e);
			return LambdaResponse.builder().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
		}
	}

	private LambdaResponse processRequest(final APIGatewayProxyRequestEvent event) {

		switch (HttpMethod.valueOf(event.getHttpMethod())) {

			case GET:
				return handleGet(event);

			case POST:
				return handlePost(event);

			default:
				return LambdaResponse.builder().statusCode(HttpStatus.SC_BAD_REQUEST).build();
		}
	}


	private LambdaResponse handlePost(final APIGatewayProxyRequestEvent event) {

		final Contact body = gson.fromJson(event.getBody(), Contact.class);

		logger.info("Requested POST with body : {}", body);

		return LambdaResponse.builder().statusCode(secondLevelContactRepository.create(body) ? HttpStatus.SC_CREATED :
				HttpStatus.SC_BAD_REQUEST).build();
	}

	private LambdaResponse handleGet(final APIGatewayProxyRequestEvent event) {

		logger.info("Requested GET with Path params : {} \n and Query Params: {}", event.getPathParameters(),
				event.getQueryStringParameters());

		final String key = event.getPathParameters().getOrDefault("deviceKey", "");

		final Contact contactEntity =
				getFirst(secondLevelContactRepository.getSecondLevelContactsByDeviceKey(key)).orElse(new Contact());

		return LambdaResponse.builder().statusCode(HttpStatus.SC_OK).body(gson.toJson(contactEntity)).build();
	}
}