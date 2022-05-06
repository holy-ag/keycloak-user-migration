package com.danielfrak.code.keycloak.events.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class HybrisUser {
	private String id;
	private String email;
	private String country;
	private String firstName;
	private String lastName;
	private String department;
	private boolean emailVerified;
}
