package com.danielfrak.code.keycloak.events.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HybrisUserResponse {
	private String customerId;
	private String currency;
	private String country;
}
