package com.danielfrak.code.keycloak.events;


import com.danielfrak.code.keycloak.events.rest.UserService;
import com.danielfrak.code.keycloak.providers.rest.rest.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;


public class HybrisEventsLegacyProviderFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "hybris-user-sync";

    @Override
    public EventListenerProvider create(final KeycloakSession session) {
        var httpClient = new HttpClient(HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()));
        var restService = new UserService(httpClient, new ObjectMapper());
        return new HybrisEventsLegacyProvider(session, restService);
    }

    @Override
    public void init(final Config.Scope config) {

    }

    @Override
    public void postInit(final KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
