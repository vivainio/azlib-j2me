package com.w;

public interface AuthListener {
	void browserLaunchNeeded(String url);
	void tokenAvailable(String accessToken, String refreshToken);

}
