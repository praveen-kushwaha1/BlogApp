package com.blog.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// token se related jitna bhi operation perform karna hoga 
// isme rakhenge

@Component
public class JwtTokenHelper {
	
	private String secretey = "";

	public  JwtTokenHelper() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretey = Base64.getEncoder().encodeToString(sk.getEncoded());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String generateToken(UserDetails userDetails ) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("username", userDetails.getUsername());

		String token = Jwts.builder().claims().add(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 60 * 60 * 10*1000)).and().signWith(getKey()).compact();

		return token;
	}

	private Key getKey() {

		byte[] keyBytes = Decoders.BASE64.decode(secretey);
		return Keys.hmacShaKeyFor(keyBytes);

	}

	private Claims extractAllClaims(String token) {
		Claims claims = Jwts.parser().verifyWith(decryptKey(secretey)).build().parseSignedClaims(token).getPayload();
		return claims;
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private SecretKey decryptKey(String secretey2) {
		byte[] keyBytes = Decoders.BASE64.decode(secretey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private Date extractExpairation(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		String username = extractUserName(token);
		Boolean isExpired = isTokenExpired(token);
		if (username.equals(userDetails.getUsername()) && !isExpired) {
			return true;
		}
		return false;
	}

	private Boolean isTokenExpired(String token) {
		Date expiredDate = extractExpairation(token);
		return expiredDate.before(new Date());
	}
	
}