package com.danielfrak.code.keycloak.events.rest;

import com.danielfrak.code.keycloak.events.dtos.HybrisUser;
import com.danielfrak.code.keycloak.providers.rest.exceptions.RestUserProviderException;
import com.danielfrak.code.keycloak.providers.rest.rest.UserPasswordDto;
import com.danielfrak.code.keycloak.providers.rest.rest.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.keycloak.component.ComponentModel;

import java.io.IOException;

import static com.danielfrak.code.keycloak.providers.rest.ConfigurationProperties.URI_PROPERTY;

//package com.danielfrak.code.keycloak.events.rest;
//
//import com.danielfrak.code.keycloak.events.dtos.HybrisUser;
//
//import com.danielfrak.code.keycloak.providers.rest.rest.UserPasswordDto;
//import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.core.Response;
//
//
public class UserService implements HybrisUserService{

	private final String uri;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public UserService(HttpClient httpClient, ObjectMapper objectMapper) {
		this.httpClient = httpClient;
		this.uri = "http://192.168.178.36:9001/ocmws/v2/keycloak-user-migration/customer";
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean addHybrisUser(final HybrisUser user) {
		try {
			var json = objectMapper.writeValueAsString(user);
			var response = httpClient.post(uri, json);
			return response.getCode() == HttpStatus.SC_OK;
		} catch (IOException e) {
			throw new RestUserProviderException(e);
		}
	}

	@Override
	public boolean updateHybrisUser(final HybrisUser user) {
		try {
			var json = objectMapper.writeValueAsString(user);
			var response = httpClient.put(uri, json);
			return response.getCode() == HttpStatus.SC_OK;
		} catch (IOException e) {
			throw new RestUserProviderException(e);
		}
	}
	//
//	private final RestUserClient client;
//	public UserService(final Client restEasyClient) {
//		String uri = "http://192.168.178.36:9001/ocmws/v2/keycloak-user-migration";
//		this.client = buildClient(restEasyClient, uri);
//	}
//
//	private RestUserClient buildClient(Client restEasyClient, String uri) {
//
//		ResteasyWebTarget target = (ResteasyWebTarget) restEasyClient.target(uri);
//		return target.proxy(RestUserClient.class);
//	}
//
//	@Override
//	public boolean addHybrisUser(final HybrisUser user) {
//		try (Response response = client.addHybrisUser(user)) {
//			return response.getStatus() == 204;
//		}
//	}
//
//	@Override
//	public boolean updateHybrisUser(final HybrisUser user) {
//		try (Response response = client.updateHybrisUser(user)) {
//			return response.getStatus() == 204;
//		}
//	}
}
