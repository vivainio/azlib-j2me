import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.futurice.tantalum3.TantalumMIDlet;
import com.futurice.tantalum3.Workable;

import com.futurice.tantalum3.log.L;
import com.nokia.example.utils.BackStack;
import com.nokia.example.utils.Commands;
import com.w.AuthListener;
import com.w.AuthSession;


public class MyWall extends TantalumMIDlet implements AuthListener, CommandListener {

    private Command exitCmd; //The exit command...
    private Display display; //The phone's display
    private Form mainForm; //the main form for the application	
	 
	private AuthSession ses;
	
	private Command fetchTokenCommand;
	private Command listFilesCommand;
	
	
	
	Timer timer;
	private Command startAuthCommand;
	private StringItem startButton;
	private BackStack backStack;
	private Form fileForm;
	
	private Hashtable itemsHash;
	private Hashtable children;
	private Command resetCommand;
	
	
	public MyWall() {
		super(2);
		backStack = new BackStack(this);
		timer = new Timer();
		// TODO Auto-generated constructor stub
		display = Display.getDisplay(this);
		
		itemsHash = new Hashtable();		
		children = new Hashtable();
		
		mainForm = new Form("GDrive");
		fileForm = new Form("Details");
		fileForm.addCommand(Commands.BACK);
		fileForm.setCommandListener(this);
		
		fetchTokenCommand = new Command("Fetch token" , Command.SCREEN, 0);
		mainForm.addCommand(fetchTokenCommand);
		listFilesCommand = new Command("List files on GDrive" , Command.SCREEN, 0);
		mainForm.addCommand(listFilesCommand);
		startAuthCommand = new Command("Start auth flow", Command.SCREEN,0);
		mainForm.addCommand(startAuthCommand);
		
		resetCommand = new Command("Reset auth state", Command.SCREEN, 0);
		mainForm.addCommand(resetCommand);
		
		startButton = new StringItem("Start auth flow", "Start", Item.BUTTON);
		mainForm.append(startButton);
		startButton.setDefaultCommand(
		         new Command("Set", Command.ITEM, 1));
		
		startButton.setItemCommandListener(new ItemCommandListener() {
			
			public void commandAction(Command arg0, Item arg1) {
				// TODO Auto-generated method stub
				L.i("", "Start pressed");
				ses.startAuth();
				
			}
		});
		
		mainForm.setCommandListener(this);
				
		ses = new AuthSession();
		ses.setServerUrl("http://authorizr.herokuapp.com");
		ses.setCredId("486f832ea89e487cb9e3405b60632c31");
		ses.setAuthListener(this);
		ses.restoreStateFromDisk();
		ses.finalizeAuthIfNeeded(null);
	}

	public void tokenReceived(String accessToken) {
		// TODO Auto-generated method stub
		
		
	}
	
	protected void pauseApp() {
		// TODO Auto-generated method stub
		L.i("", "app paused");

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		display.setCurrent(mainForm);
		L.i("", "startApp");

	}
	

	public void browserLaunchNeeded(String url) {
		try {
			platformRequest(url);
			L.i("", "Done calling platformrequest");
		} catch (ConnectionNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startButton.setLabel("Click here after browser is closed");
		startButton.setText("Fetch access token");
		startButton.setItemCommandListener( new ItemCommandListener() {
			
			public void commandAction(Command arg0, Item arg1) {
				ses.fetchTokenForSession(null);
				// TODO Auto-generated method stub				
			}
		});
		// TODO Auto-generated method stub
		
	}

	private StringItem urlButton(final String text, final String url) {
		StringItem item = new StringItem("", text, Item.BUTTON);
		item.setDefaultCommand(
  		         new Command("Set", Command.ITEM, 1));
		
		item.setItemCommandListener( new ItemCommandListener() {

			public void commandAction(Command arg0, Item arg1) {
				// TODO Auto-generated method stub
				try {
					L.i("Starting download", "");
					platformRequest(url);
				} catch (ConnectionNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
		});
		return item;

		
	}
	private void populateDetails(JSONObject o) {
		Form f = fileForm;
		f.deleteAll();
	
		f.append(new StringItem("Name", o.s("originalFilename")));
		f.append(new StringItem("Size", o.s("fileSize")));
		
		f.append(urlButton("Download", o.s("webContentLink")));
		
		JSONObject exportLinks = o.o("exportLinks");
		if (exportLinks!= null) {
			Enumeration keys = exportLinks.keys();
			
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String val = exportLinks.s(key);
				if (key.startsWith("application/")) {
					key = key.substring(12);
				}
				f.append(urlButton(key, val));
			}
		}
		
		f.append(o.toString());
		
		
		
		
	}

	private Form pushPage(String title) {
		Form f = new Form(title);
		f.addCommand(Commands.BACK);
		f.setCommandListener(this);
		backStack.forward(f);
		return f;
		
	}
	/*
	private class ListFilesTask extends HttpGetter {
		
		public ListFilesTask(String url) {
			// TODO Auto-generated constructor stub
			super(url,0);
		}

		
		public Object doInBackground(Object in) throws UnsupportedEncodingException {
			// TODO Auto-generated method stub
			
			Object out= super.doInBackground(in);
			byte[] bytes = (byte[]) out;
			String json;
			json = new String(bytes, "UTF-8");
			JSONObject o = new JSONObject(json);
		
			
			parseFileList(o);
						
			return out;
		}
		
		
	}
	*/
	public void commandAction(Command arg0, Displayable arg1) {
		// TODO Auto-generated method stub
		if (arg0 == fetchTokenCommand) {
			L.i("", "Fetching token");
			ses.fetchTokenForSession(null);
		}
		if (arg0 == listFilesCommand) {
			startListFiles();
			
		}
		
		if (arg0 == startAuthCommand) {
			ses.startAuth();
		}
		
		if (arg0 == Commands.BACK) {
			backStack.back();
		}
		
		if (arg0 == resetCommand) {
			ses.resetState();
		}
		
	}
	
	
	
	
	
	private void listFiles(String parent, Form currentForm) {
		Enumeration it;
		currentForm.deleteAll();
		if (parent == null) {
			it = itemsHash.keys();
		} else {
			Vector chi = (Vector) children.get(parent);
			if (chi == null) {
				L.i("", "Empty folder");
				currentForm.append("Empty folder");
				return;
			}
			L.i("", "Item " + parent + " has childcount = " + chi.size());
			it = chi.elements();
		}
				
		
		while (it.hasMoreElements()) {
			final String k = (String) it.nextElement();
	
			
			L.i("current k=", k);
			Vector v = (Vector) children.get(k);
			if (v == null) {
				L.i("no children for", k);
				//continue;
			}
			
			
			
			String title;
			final JSONObject ob = (JSONObject) itemsHash.get(k);
			
			if (hasLabel(ob, "trashed")) {
				continue;
			}
			if (ob == null) {
				title = k;
				L.i("","notfound item k="+ k);
				continue;
			} else {
				title = ob.s("title");
			}
			
			final boolean isFolder = ob.s("mimeType").equals("application/vnd.google-apps.folder");
			if (isFolder) {
				title = title + "/";
			}
			int size = 0;
			if (v != null) {
				size = v.size();
			}
			
			
			StringItem item = new StringItem("", title, StringItem.HYPERLINK);

			L.i("", "Adding stingitem title=" + title);
    		item.setDefaultCommand(
	   		         new Command("Set", Command.ITEM, 1));
    		
    		item.setItemCommandListener( new ItemCommandListener() {
				
				public void commandAction(Command arg0, Item arg1) {

					
					if (isFolder) {
					 
						handleFolderSelect(k);
					} else {
						handleFileSelect(k);
					}
						
					// TODO Auto-generated method stub
					
				}
			});
//			
			currentForm.append(item);
			
			//mainForm.append(title + ": " + size + "\n"); 
			
		}	
		try {
			JSONObject pi = getItem(parent);
			if (pi != null)
				pi.put("__fetched", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private boolean hasLabel(JSONObject ob, String string) {
		// TODO Auto-generated method stub
		try {
			if (ob.o("labels").getBoolean(string)) {
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private JSONObject getItem(String id ) {
		if (id == null)
			return null;
		return (JSONObject) itemsHash.get(id);
	}
	
	protected void handleFileSelect(String k) {
		// TODO Auto-generated method stub
		JSONObject it = getItem(k);
		L.i("", "Selecting file " + it.s("title"));
		populateDetails(it);
		backStack.forward(fileForm);
		
	}

	protected void handleFolderSelect(final String k) {
		// TODO Auto-generated method stub
		JSONObject fo = getItem(k);
		final Form f = pushPage(fo.s("title"));
		if (getItem(k).optBoolean("__fetched")) {
			L.i("", "Already fetched " + k);			
			listFiles(k, f);
			return;
		}		
		f.append("Fetching files...");
		startQuery("'" + k + "' in parents", new Workable() {
			
			public Object exec(Object in) {
				
				
				// TODO Auto-generated method stub
				listFiles(k,f);
				return null;
			}
		});
		
	}

	private void startQuery(String query, final Workable resultHnd) {
		L.i("startQuery", query);
		String qe = AuthSession.urlEncode(query);
		
		String url = "https://www.googleapis.com/drive/v2/files?" +
				"maxResults=30" + 
				"&q=" + qe + 
				"&access_token="+ses.getAccessToken();
		
		
//		ListFilesTask t = new ListFilesTask(url);
		
		ses.getJSONAsync(url, new Workable() {
			public Object exec(Object in) {
				// TODO Auto-generated method stub
				final JSONObject res = (JSONObject) in;
				checkError(res);
				parseFileList(res);
				resultHnd.exec(res);
				return null;
			}
		});
						
		
	}
	
	protected void checkError(JSONObject res) {
		// TODO Auto-generated method stub
		if (res.has("error")) {
			try {
				int code = res.o("error").getInt("code");
				if (code == 401) {
					L.i("", "Auth expired");
					ses.reauthenticate();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
				
		
		
	}

	private void startListFiles() {
		L.i("", "Listing files");
		 // "mimeType = 'application/vnd.google-apps.folder'",
		startQuery("'root' in parents",  
				
				new Workable() {
					
					public Object exec(Object in) {
						// TODO Auto-generated method stub
						listFiles(null, mainForm);
						return null;
					}
				});
	}

	public void tokenAvailable(String accessToken, String refreshToken) {
		// TODO Auto-generated method stub
		startButton.setLabel("Ready to list files!");
		startButton.setText("Go!");
		startButton.setItemCommandListener(new ItemCommandListener() {
			
			public void commandAction(Command arg0, Item arg1) {
				// TODO Auto-generated method stub
				startListFiles();
				
			}
		});
		
	}

	private void parseFileList(JSONObject o) {
		try {
			JSONArray items = o.getJSONArray("items");
		    int len = items.length();

		    //mainForm.delete(0);
		    for (int i = 0; i < len; i += 1) {
		    	JSONObject obj = items.getJSONObject(i);
		    	//L.i("Handling", obj.toString(2));		        	
		    	final String idd = obj.s("id");
		    	
		    	itemsHash.put(idd, obj);
		    	final String title = obj.s("title");
		    	final String link = obj.s("selfLink");
		    	final String mimeType = obj.s("mimeType");
		    	
		    	JSONArray par = obj.a("parents");
		    	String parent;
		    	if (par.length() > 0) {
		    		parent = par.o(0).s("id");
		    	} else {
		    		parent = "";		        		
		    	}
		    	L.i("", "Handling" + idd + ": " + title + " par " + parent);
		    	Vector clist;
		    	if (children.containsKey(parent)) {
		    		clist = (Vector) children.get(parent);
		    		
		    	} else {
		    		L.i("", "New vect for " + parent);
		    		clist = new Vector();
		    		children.put(parent, clist);
		    	}
		    	clist.addElement(idd);
		    	
		    	/*
		    	String label;
		    	if (mimeType.equals("application/vnd.google-apps.folder")) {
		    		label = title+"/";		        		
		    	} else {
		    		label = title;
		    	}
		    	*/

		    	/*
		    	final StringItem it = new StringItem("", label,Item.HYPERLINK);
		        		            
				it.setDefaultCommand(
			         new Command("Set", Command.ITEM, 1));
				*/
				
		    	/*
				it.setItemCommandListener(new ItemCommandListener() {
					
					public void commandAction(Command arg0, Item arg1) {
						// TODO Auto-generated method stub
						L.i("", "Will fetch " + title + " " + link);
						String tgt = link + "?access_token="+ses.getAccessToken();
						L.i("Url", tgt);
						JSONObject resp = ses.getJSON(tgt);
						backStack.forward(fileForm);
						populateDetails(resp);
						String fetchUrl = null;
						try {
							fetchUrl = resp.getString("webContentLink");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}							
						
					}
				});
				*/
				
		    	
		    	
		    	//mainForm.append(it);		            
		    }
			
		    
		    //listFolders("");
			L.i("",o.toString(2));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
