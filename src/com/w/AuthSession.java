package com.w;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;

import org.helyx.basics4me.io.BufferedReader;

import com.futurice.tantalum3.Task;
import com.futurice.tantalum3.Worker;
import com.futurice.tantalum3.log.L;
import com.futurice.tantalum3.net.HttpGetter;

public class AuthSession {


	
	AuthListener authListener;
	
    public void setAuthListener(AuthListener authListener) {
		this.authListener = authListener;
	}


	static public String urlEncode(String sUrl)   
    {  
         StringBuffer urlOK = new StringBuffer();  
         for(int i=0; i<sUrl.length(); i++)   
         {  
             char ch=sUrl.charAt(i);  
             switch(ch)  
             {  
                 case '<': urlOK.append("%3C"); break;  
                 case '>': urlOK.append("%3E"); break;  
                 case '/': urlOK.append("%2F"); break;  
                 case ' ': urlOK.append("%20"); break;  
                 case ':': urlOK.append("%3A"); break;  
                 case '-': urlOK.append("%2D"); break;  
                 default: urlOK.append(ch); break;  
             }   
         }  
         return urlOK.toString();  
     }      

    private StringBuffer bld;
    private boolean firstArg = false;
    private String sessionid;
    private String server_url;
    
    private String accessToken;
    
    public String getAccessToken() {
		return accessToken;
	}


	public void setArg(String key, String value) {
    	if (!firstArg) {
    		bld.append("&");
    	} else {
    		firstArg = false;
    	}
    	
    	bld.append(key);
    	bld.append("=");
		bld.append(urlEncode(value));
			//bld.append(URLEncoder.encode(value, "UTF-8"));
    }
    

    private void initAuth() {
    	
    	sessionid = "";
    	server_url = "http://authorizr.herokuapp.com";
    	bld = new StringBuffer( server_url + "/api/v1/create_session/?");
    	firstArg = true;
    	setArg("auth_endpoint", "https://accounts.google.com/o/oauth2/auth");
    	setArg("token_endpoint","https://accounts.google.com/o/oauth2/token");
    	setArg("resource_endpoint","https://www.googleapis.com/oauth2/v1");
        setArg("redirect_uri",  server_url+"/login/google");
        setArg("scope", "https://www.googleapis.com/auth/drive");
        setArg("cred_id", "5d40ed679c394a1ba03ff6704a6c6e67" );
        
        System.out.println("url " + bld.toString());

        String url = bld.toString();

        HttpGetter g = new HttpGetter(url, 0);
        byte[] bytes = (byte[]) g.doInBackground(null);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
    	Hashtable data = new Hashtable();
		for (;;) {
			
			String line;
			try {
				line = r.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if (line == null) {
				break;
			}
			int sep = line.indexOf("=");
			String k = line.substring(0,sep);
			String v = line.substring(sep+1);
			data.put(k, v);
			System.out.println("Set " + k + " to " + v); 
					
		};
		sessionid = (String) data.get("sessionid");
		String loginuri = (String) data.get("loginurl");
		authListener.browserLaunchNeeded(loginuri);
		
		
		
		
		/*
			Uri loginuri = Uri.parse((String) data.get("loginurl"));
			
			sessionid = (String) data.get("sessionid");
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, loginuri);
			startActivity(browserIntent);
			
			
			
			
			
			System.out.println("read ok!");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		*/
    }

    
     
    
    private void doFetchToken() {
    	
    	String access_token_url = server_url+"/api/v1/fetch_access_token/?sessionid=" + sessionid;
        HttpGetter g = new HttpGetter(access_token_url, 0);
        byte[] bytes = (byte[]) g.doInBackground(null);
         
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        try {
			accessToken = r.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        L.i("", "Access token now " + accessToken);
    }
    private class FetchTokenTask extends Task {

		protected Object doInBackground(Object in) {
			// TODO Auto-generated method stub
			doFetchToken();
			return accessToken;
			
		}
    	
    }
    
    public void fetchTokenForSession() {
    	Worker.fork(new FetchTokenTask());    	
    	
    	
    }
    private class StartAuthTask extends Task {

		protected Object doInBackground(Object in) {
			// TODO Auto-generated method stub
			initAuth();
			return null;
		}
    	
    }
    
    public void startAuth() {
    	System.out.println("Starting");
    	Worker.fork(new StartAuthTask());
    	
    	
    	/*
    	ses = new AuthSession();
    	ses.startAuth();
    	new InitAuthTask().execute(null, null);
    	*/
    	
    	
    	    	
    }

    /*
    private class FetchTokenTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... params) {
			String access_token_url = server_url + "/api/v1/fetch_access_token/?sessionid="+sessionid;
			try {
				BufferedReader r = getHttp(access_token_url);
				access_token = r.readLine();
				System.out.println("Token is " + access_token); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			// TODO Auto-generated method stub
			return null;
		}
    	
    
    };
    
    private class GetGDriveDataTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... params) {
			 String test_url = "https://www.googleapis.com/drive/v2/files?access_token="+access_token;
			
			 try {
				BufferedReader r = getHttp(test_url);
				for (;;) {
					String line = r.readLine();
					if (line == null)
						break;
					System.out.println(line);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			// TODO Auto-generated method stub
			return null;
		}
    
    }
    public void finishAuth(View view) {
    	new FetchTokenTask().execute(null, null);
		
		
    	
    }
    
    public void doCallApi(View view) {
    	new GetGDriveDataTask().execute(null, null);
    }
    */
}
