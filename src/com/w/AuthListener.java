package com.w;

public interface AuthListener {
	void browserLaunchNeeded(String url);
	void tokenReceived(String accessToken, String refreshToken);

}
