package com.danielfrak.code.keycloak.events;

import com.danielfrak.code.keycloak.events.converter.HybrisUserConverter;
import com.danielfrak.code.keycloak.events.dtos.HybrisUser;
import com.danielfrak.code.keycloak.events.rest.UserService;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;

import java.util.Arrays;
import java.util.List;

public class HybrisEventsLegacyProvider implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(HybrisEventsLegacyProvider.class);
    private final KeycloakSession session;
    private final RealmProvider model;
    private final HybrisUserConverter hybrisUserConverter;
    private final UserService userService;

    public HybrisEventsLegacyProvider(final KeycloakSession session, final UserService restService) {
        this.session = session;
        this.model = session.realms();
        this.hybrisUserConverter = new HybrisUserConverter();
        this.userService = restService;
    }

    @Override
    public void onEvent(final Event event) {

        if (isRegisterEvent(event)) {
            userService.addHybrisUser(getHybrisUser(event));
            LOG.infof("## NEW %s EVENT", event.getType());
            LOG.info("-----------------------------------------------------------");
        }

        if (isUserUpdateEvent(event)) {
            userService.updateHybrisUser(getHybrisUser(event));
            LOG.infof("## NEW %s EVENT", event.getType());
            LOG.info("-----------------------------------------------------------");
        }
    }

    private HybrisUser getHybrisUser(final Event event) {
        RealmModel realm = this.model.getRealm(event.getRealmId());
        UserModel newRegisteredUser = this.session.users().getUserById(event.getUserId(), realm);
        return hybrisUserConverter.convert(newRegisteredUser);
    }

    private boolean isRegisterEvent(Event event) {
        return EventType.REGISTER.equals(event.getType());
    }

    private boolean isUserUpdateEvent(Event event) {
        final List<EventType> eventTypes = Arrays.asList(
                EventType.UPDATE_EMAIL,
                EventType.UPDATE_PROFILE
        );
        return eventTypes.contains(event.getType());
    }

    @Override
    public void onEvent(final AdminEvent adminEvent, final boolean b) {

    }

    @Override
    public void close() {

    }
}
