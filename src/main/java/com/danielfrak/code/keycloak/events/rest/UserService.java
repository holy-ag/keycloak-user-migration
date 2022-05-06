package com.danielfrak.code.keycloak.events.rest;

import com.danielfrak.code.keycloak.events.dtos.HybrisUser;

import com.danielfrak.code.keycloak.providers.rest.rest.UserPasswordDto;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;


public class UserService implements HybrisUserService{

	private final RestUserClient client;
	public UserService(final Client restEasyClient) {
		String uri = "http://192.168.178.36:9001/ocmws/v2/keycloak-user-migration";
		this.client = buildClient(restEasyClient, uri);
	}

	private RestUserClient buildClient(Client restEasyClient, String uri) {

		ResteasyWebTarget target = (ResteasyWebTarget) restEasyClient.target(uri);
		return target.proxy(RestUserClient.class);
	}

	@Override
	public boolean addHybrisUser(final HybrisUser user) {
		final Response response = client.addHybrisUser(user);
		return response.getStatus() == 200;
	}

	@Override
	public boolean updateHybrisUser(final HybrisUser user) {
		final Response response = client.updateHybrisUser(user);
		return response.getStatus() == 200;
	}
}
