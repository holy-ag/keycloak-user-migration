package com.danielfrak.code.keycloak.events.converter;

import com.danielfrak.code.keycloak.events.dtos.HybrisUser;
import org.keycloak.models.UserModel;

import java.util.Optional;

public class HybrisUserConverter {

	public HybrisUser convert(final UserModel user) {
		final HybrisUser.HybrisUserBuilder builder = HybrisUser.builder()
				.id(user.getId())
				.email(user.getEmail())
				.emailVerified(user.isEmailVerified())
				.country("de")
				.firstName(user.getFirstName())
				.lastName(user.getLastName());

		if (user.getAttributeStream("department").findFirst().isPresent()) {
			builder.department(user.getAttributeStream("department").findFirst().get());
		}

		return builder.build();

	}
}
