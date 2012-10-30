package com.w;

import java.io.UnsupportedEncodingException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.futurice.tantalum3.Task;
import com.futurice.tantalum3.Workable;
import com.futurice.tantalum3.Worker;
import com.futurice.tantalum3.log.L;
import com.futurice.tantalum3.net.json.JSONGetter;
import com.futurice.tantalum3.net.json.JSONModel;
import com.futurice.tantalum3.rms.RMSUtils;


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
    private String serverUrl;
    
    public String getServerUrl() {
		return serverUrl;
	}


	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	private String currentState;
    private String accessToken;
	private String refreshToken;
	private String credId;
    
    public String getCredId() {
		return credId;
	}


	public void setCredId(String credId) {
		this.credId = credId;
	}


	public String getAccessToken() {
    	if (currentState != "access_token_ok") {
    		L.i("", "Access token not yet available, state = " + currentState);
    		return null;
    	}
    	
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
    

	
	private JSONObject getStateJson() {
		JSONObject o = new JSONObject();
		try {
			o.put("state", currentState);
			o.put("access_token", accessToken);
			o.put("refresh_token", refreshToken);
			o.put("session_id", sessionid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return o;
		
		
		
		
	}
	public void storeStateToDisk() {
		byte[] state;
		try {
			JSONObject stateJson = getStateJson();
			state = stateJson.toString().getBytes("UTF-8");
			L.i("Storing state", stateJson.toString(2));
			RMSUtils.write("authorizr_state", stateJson.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void restoreStateFromDisk() {
		byte[] state;
		state = RMSUtils.read("authorizr_state");
		if (state == null) {			
			currentState = "uninitialized";
			return;
		}
		try {
			JSONObject o = new JSONObject(new String(state, "UTF-8"));
			L.i("Restored stata", o.toString(2));
			accessToken = o.s("access_token");
			refreshToken = o.s("refresh_token");
			currentState = o.s("state");
			sessionid = o.s("session_id");
					
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalizeAuthIfNeeded(Workable done) {
		if (getAccessToken() != null) {
			return;
		}
		if (currentState.equals("browser_launched")) {
			fetchTokenForSession(done);
		} else {
			L.i("AuthSession" , "Can't finalize, unknown state: " + currentState);
		}
		
		
	}
	

	
    private void initAuth() {
    	
    	sessionid = "";
    	serverUrl = "http://authorizr.herokuapp.com";    	
    	String url = serverUrl + "/api/v1/create_session/" + credId + "?" + 
    			"access_type=offline";
    			//"access_type=offline&approval_prompt=force";
    			
    			
    	/*
    	bld = new StringBuffer( server_url + "/api/v1/create_session/?");
    	firstArg = true;
    	setArg("auth_endpoint", "https://accounts.google.com/o/oauth2/auth");
    	setArg("token_endpoint","https://accounts.google.com/o/oauth2/token");
    	setArg("resource_endpoint","https://www.googleapis.com/oauth2/v1");
        setArg("redirect_uri",  server_url+"/login/google");
        setArg("scope", "https://www.googleapis.com/auth/drive");
        setArg("cred_id", "5d40ed679c394a1ba03ff6704a6c6e67" );
        */
        //System.out.println("url " + bld.toString());

        //String url = bld.toString();

    	currentState = "create_session";
    	JSONObject resp = getJSON(url);
    	/*
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
		*/
		try {
			sessionid = resp.getString("session_id");
			String loginuri = resp.getString("url");
			currentState = "browser_launched";
			storeStateToDisk();
			authListener.browserLaunchNeeded(loginuri);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
		
		
		
		
		
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
    	
    	String access_token_url = serverUrl+"/api/v1/fetch_access_token/" + sessionid + "/";
    	JSONObject resp = getJSON(access_token_url);
    	
    	try {
			accessToken = resp.getString("access_token");
			refreshToken = resp.s("refresh_token");
			authListener.tokenReceived(accessToken, refreshToken);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	currentState = "access_token_ok";
    	storeStateToDisk();
        L.i("", "Access token now " + accessToken);
                
    }
    private class FetchTokenTask extends Task {

		protected Object doInBackground(Object in) {
			// TODO Auto-generated method stub
			doFetchToken();
			return accessToken;
			
		}
    	
    }
    
    public void fetchTokenForSession(Workable done) {
    	FetchTokenTask task = new FetchTokenTask();
    	if (done != null) {
    		task.finished(done);
    	}
    		
    	Worker.fork(task);    	
    	
    	
    }
    private class StartAuthTask extends Task {

		protected Object doInBackground(Object in) {
			// TODO Auto-generated method stub
			initAuth();
			return null;
		}
    	
    }

    
    public JSONObject getJSON(String url) {
    	JSONModel mdl = new JSONModel();
    	JSONGetter g = new JSONGetter(url, mdl, 0);
    	g.doInBackground(null);
    	return mdl.jsonObject;
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
