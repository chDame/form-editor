package com.cda.form.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public final class SecurityUtils {
	public final static String PREFIX_TOKEN = "Bearer ";
    public final static String PREFIX_ROLE = "ROLE_";
	public final static String SECRET_KEY = UUID.randomUUID().toString();

	private SecurityUtils() {

	}
	
	public static String getJWTToken(String username) {
		List<String> grantedAuthorities = new ArrayList<String>();
		if (username.equals("bridgeOfficer")) {
			grantedAuthorities.add(PREFIX_ROLE+"BRIDGEOFFICER");
		} else {
			grantedAuthorities.add(PREFIX_ROLE+"USER");
		}
		UserPrincipal principal = new UserPrincipal(username);

		String token = Jwts
				.builder()
				.setId("camundaJwt")
				.setSubject(username)
				.claim("principal", principal)
				.claim("authorities",grantedAuthorities)
				.setExpiration(new Date(System.currentTimeMillis() + 5*86400000))
				.signWith(SignatureAlgorithm.HS512,
						SECRET_KEY.getBytes()).compact();

		return PREFIX_TOKEN + token;
	}

	@SuppressWarnings("unchecked")
	public static UserPrincipal getConnectedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		LinkedHashMap<String, Object> principalUser = (LinkedHashMap<String, Object>)authentication.getPrincipal();
		String username = (String) principalUser.get("username");
		return new UserPrincipal(username);
	}

	public static Claims getClaims(String jwtToken) {
	    return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwtToken.replace(PREFIX_TOKEN, "")).getBody();
	}
	
	
}
