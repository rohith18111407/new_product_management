package com.ProductManagement.beststore.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


//Class that can abstract all the JWT related stuff, allows create JWT given a userDetail object
@Service
public class JwtUtil {
	
	@Value("${spring.security.secret-value}")
	private String SECRET_KEY;
	
	@Value("${spring.security.expiration-value}")
	private long EXPIRATION_TIME;
	
	public String extractUsername(String token)
	{
		return extractClaim(token,Claims::getSubject);
	}
	
	public Date extractExpiration(String token)
	{
		return extractClaim(token,Claims::getExpiration);
	}
	
	//claimsResolver used to find what the claims are
	public <T> T extractClaim(String token,Function<Claims,T> claimsResolver)
	{
		final Claims claim=extractAllClaims(token);
		return claimsResolver.apply(claim);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}
	
	private Boolean isTokenExpired(String token)
	{
		return extractExpiration(token).before(new Date());
	}
	
	
	//subject is the person who is being authenticated
	//EXPIRATION_TIME = 1000 millisec * 60 sec * 60 min * 10  (10 hours from now)
	private String createToken(Map<String,Object> claims,String subject)
	{
		return Jwts
				.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+	EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256,SECRET_KEY)
				.compact();
	}
	
	public String generateToken(Object userDetails)
	{
		Map<String,Object> claims=new HashMap<>();
		if(userDetails instanceof UserDetails)
		{
			return createToken(claims,((UserDetails) userDetails).getUsername());
		}
		else
		{
			throw new IllegalArgumentException("Invalid user details type");
		}
			
	}
	
	// checks whether the Username is valid and the token is not expired
	public Boolean validateToken(String token,UserDetails userDetails)
	{
		final String username=extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
