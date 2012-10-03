package com.w.authdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;



public class AuthSession {

	private AuthSession ses; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    private HttpClient httpConnection;
    private String access_token;
    
    public void setArg(String key, String value) {
    	if (!firstArg) {
    		bld.append("&");
    	} else {
    		firstArg = false;
    	}
    	
    	bld.append(key);
    	bld.append("=");
    	try {
    		bld.append(urlEncode(value));
			//bld.append(URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
   public Object doInBackground(final Object in) {
        //#debug
        L.i(this.getClass().getName() + " start", url);
        ByteArrayOutputStream bos = null;
        HttpConnection httpConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean tryAgain = false;
        boolean success = false;

        try {
            httpConnection = (HttpConnection) Connector.open(url);
            httpConnection.setRequestMethod(requestMethod);
            if (postMessage != null) {
                outputStream = httpConnection.openDataOutputStream();
                outputStream.write(postMessage);
            }
            inputStream = httpConnection.openInputStream();
            final long length = httpConnection.getLength();
            if (length > 0 && length < 1000000) {
                //#debug
                L.i(this.getClass().getName() + " start fixed_length read", url + " content_length=" + length);
                int bytesRead = 0;
                byte[] bytes = new byte[(int) length];
                while (bytesRead < bytes.length) {
                    final int br = inputStream.read(bytes, bytesRead, bytes.length - bytesRead);
                    if (br > 0) {
                        bytesRead += br;
                    } else {
                        //#debug
                        L.i(this.getClass().getName() + " recieved EOF before content_length exceeded", url + ", content_length=" + length + " bytes_read=" + bytesRead);
                        break;
                    }
                }
                setResult(bytes);
                bytes = null;
            } else {
                //#debug
                L.i(this.getClass().getName() + " start variable length read", url);
                bos = new ByteArrayOutputStream();
                byte[] readBuffer = new byte[16384];
                while (true) {
                    final int bytesRead = inputStream.read(readBuffer);
                    if (bytesRead > 0) {
                        bos.write(readBuffer, 0, bytesRead);
                    } else {
                        break;
                    }
                }
                setResult(bos.toByteArray());
                readBuffer = null;
            }

            //#debug
            L.i(this.getClass().getName() + " complete", ((byte[]) getResult()).length + " bytes, " + url);
            success = true;
        } catch (IllegalArgumentException e) {
            //#debug
            L.e(this.getClass().getName() + " has a problem", url, e);
        } catch (IOException e) {
            //#debug
            L.e(this.getClass().getName() + " retries remaining", url + ", retries=" + retriesRemaining, e);
            if (retriesRemaining > 0) {
                retriesRemaining--;
                tryAgain = true;
            } else {
                //#debug
                L.i(this.getClass().getName() + " no more retries", url);
            }
        } catch (Exception e) {
            //#debug
            L.e(this.getClass().getName() + " has a problem", url, e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
            }
            try {
                httpConnection.close();
            } catch (Exception e) {
            }
            inputStream = null;
            outputStream = null;
            bos = null;
            httpConnection = null;

            if (tryAgain) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
                doInBackground(in);
            } else if (!success) {
                cancel(false);
            }
            //#debug
            L.i("End " + this.getClass().getName(), url);
            
            return getResult();
        }
    }

    private Reader getHttp(String url) throws Exception {

    	
    	HttpGet request = new HttpGet();
		URI uri = new URI(url);
		request.setURI(uri);
		HttpResponse response;
		response = httpConnection.execute(request);
		HttpEntity ent = response.getEntity();
		
		InputStream inputStream = ent.getContent();
			
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		return r;
    }
    
    private class InitAuthTask extends AsyncTask<URL, Integer, Long> {
        

		protected Long doInBackground(URL... urls) {

        	
	    	httpConnection = new DefaultHttpClient();
	    	
	    	HttpGet request = new HttpGet();
	    	server_url = "http://authorizr.herokuapp.com";
	    	bld = new StringBuilder( server_url + "/api/v1/create_session?");
	    	firstArg = true;
	    	setArg("auth_endpoint", "https://accounts.google.com/o/oauth2/auth");
	    	setArg("token_endpoint","https://accounts.google.com/o/oauth2/token");
	    	setArg("resource_endpoint","https://www.googleapis.com/oauth2/v1");
            setArg("redirect_uri",  server_url+"/login/google");
            setArg("scope", "https://www.googleapis.com/auth/drive");
            setArg("cred_id", "100" );
            
            System.out.println("url " + bld.toString());
			try {
				URI uri = new URI(bld.toString());
				request.setURI(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    			
	
			HttpResponse response;
			try {
				response = httpConnection.execute(request);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (long) 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (long) 0;
			}
			try {
				HttpEntity ent = response.getEntity();
				
				InputStream inputStream = ent.getContent();
				
				int len = (int) ent.getContentLength();
				
				BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
				
				Hashtable data = new Hashtable();
				for (;;) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					int sep = line.indexOf("=");
					String k = line.substring(0,sep);
					String v = line.substring(sep+1);
					data.put(k, v);
					System.out.println("Set " + k + " to " + v); 
							
				};
				
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
       }
    }
	
        	

    public void startAuth(View view) {
    	System.out.println("Starting");
    	ses = new AuthSession();
    	ses.startAuth();
    	new InitAuthTask().execute(null, null);
    	
    	
    	    	
    }

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
    
}
