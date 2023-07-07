package com.sunlifemalaysia.app4share.service;

public interface JwtService {

	public String validateTokenAndGetUsername(final String token);

}