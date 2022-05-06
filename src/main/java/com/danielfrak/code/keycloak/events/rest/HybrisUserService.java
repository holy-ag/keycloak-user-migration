package com.danielfrak.code.keycloak.events.rest;

import com.danielfrak.code.keycloak.events.dtos.HybrisUser;

public interface HybrisUserService {
	boolean addHybrisUser(HybrisUser user);
	boolean updateHybrisUser(HybrisUser user);
}
