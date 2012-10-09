package org.helyx.basics4me.lang;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class UrlUtil {
	
	private static final String HTTP_SCHEME = "http://";
	private static final String HTTPS_SCHEME = "https://";
	
	private static final String HTTP_HEADER_FIELD_USER_AGENT = "User-Agent";
	private static final String HTTP_HEADER_FIELD_LOCATION = "Location";

	private static final String DEFAULT_USER_AGENT = "Profile/MIDP-2.0 Configuration/CLDC-1.1";

	private UrlUtil() {
		super();
	}
	
	public static InputStream openStream(String url) throws IOException {
		return openStream(url, DEFAULT_USER_AGENT);
	}
	
	public static InputStream openStream(String url, String userAgent) throws IOException {
		String urlToLowerCase = url.toLowerCase();
		if (urlToLowerCase.startsWith(HTTP_SCHEME) || urlToLowerCase.startsWith(HTTPS_SCHEME)) {
			HttpConnection httpConnection = (HttpConnection)Connector.open(url);
	        httpConnection.setRequestMethod(HttpConnection.GET);
	        httpConnection.setRequestProperty(HTTP_HEADER_FIELD_USER_AGENT, userAgent);
	        
	        int httpResponseCode = httpConnection.getResponseCode();
	        int redirectCount = 0;
			if ( httpResponseCode == HttpConnection.HTTP_MOVED_PERM ||
					httpResponseCode == HttpConnection.HTTP_MOVED_TEMP ||
					httpResponseCode == HttpConnection.HTTP_SEE_OTHER ) {
				redirectCount++;
				if (redirectCount > 3) {
					String urlRedirection = httpConnection.getHeaderField(HTTP_HEADER_FIELD_LOCATION);
					httpConnection.close();
					throw new IOException("3rd try - Http Response Code: '" + httpResponseCode + "', Actual URL redirection: '" + urlRedirection + "'");
				}
				String urlRedirection = httpConnection.getHeaderField(HTTP_HEADER_FIELD_LOCATION);
				httpConnection.close();
				httpConnection = (HttpConnection)Connector.open(urlRedirection);
		        httpConnection.setRequestMethod(HttpConnection.GET);
		        httpConnection.setRequestProperty(HTTP_HEADER_FIELD_USER_AGENT, userAgent); 
		        httpResponseCode = httpConnection.getResponseCode();
			}	
			
			httpResponseCode = httpConnection.getResponseCode();
			if (httpResponseCode != HttpConnection.HTTP_OK) {
				String httpResponseMessage = httpConnection.getResponseMessage();
				String errorMessage = httpResponseCode + " - "+ httpResponseMessage;
			    throw new IOException(errorMessage);	 
			}		

			InputStream inputStream = httpConnection.openInputStream();
			return inputStream;
		}
		
		InputStream inputStream = UrlUtil.class.getResourceAsStream(url);
		return inputStream;
	}
	
}
