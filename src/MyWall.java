import java.io.UnsupportedEncodingException;
import java.util.Timer;

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
import com.futurice.tantalum3.Worker;
import com.futurice.tantalum3.log.L;
import com.futurice.tantalum3.net.HttpGetter;
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
	
	public MyWall() {
		super(2);
		backStack = new BackStack(this);
		timer = new Timer();
		// TODO Auto-generated constructor stub
		display = Display.getDisplay(this);
		
		mainForm = new Form("GDrive");
		fileForm = new Form("Details");
		fileForm.addCommand(Commands.BACK);
		
		fetchTokenCommand = new Command("Fetch token" , Command.SCREEN, 0);
		mainForm.addCommand(fetchTokenCommand);
		listFilesCommand = new Command("List files on GDrive" , Command.SCREEN, 0);
		mainForm.addCommand(listFilesCommand);
		startAuthCommand = new Command("Start auth flow", Command.SCREEN,0);
		mainForm.addCommand(startAuthCommand);
		
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
		
		ses.setAuthListener(this);		
	}

	public void tokenReceived(String accessToken) {
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
	
	protected void pauseApp() {
		// TODO Auto-generated method stub

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
				ses.fetchTokenForSession();
				// TODO Auto-generated method stub				
			}
		});
		// TODO Auto-generated method stub
		
	}
	 
	private void populateDetails(JSONObject o) {
		Form f = fileForm;
	
		try {
			f.append(new StringItem("Name", o.getString("originalFilename")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private class ListFilesTask extends HttpGetter {
		
		public ListFilesTask(String url) {
			// TODO Auto-generated constructor stub
			super(url,0);
		}

		
		public Object doInBackground(Object in) {
			// TODO Auto-generated method stub
			
			Object out= super.doInBackground(in);
			byte[] bytes = (byte[]) out;						
		
			String json;
			try {
				json = new String(bytes, "UTF-8");
				JSONObject o = new JSONObject(json);
				JSONArray items = o.getJSONArray("items");
		        int len = items.length();

		        mainForm.delete(0);
		        for (int i = 0; i < len; i += 1) {
		        	JSONObject obj = items.getJSONObject(i);
		        	final String title = obj.getString("title");
		        	final String link = obj.getString("selfLink");
		        	final String mimeType = obj.getString("mimeType");
		        	String label;
		        	if (mimeType.equals("application/vnd.google-apps.folder")) {
		        		label = title+"/";		        		
		        	} else {
		        		label = title;
		        	}
		        	 
		        	final StringItem it = new StringItem("", label,Item.HYPERLINK);
		            		            
		    		it.setDefaultCommand(
		   		         new Command("Set", Command.ITEM, 1));
		    			
		    		
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
							/*
							try {
								platformRequest(fetchUrl);
							} catch (ConnectionNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							*/
							
							
						}
					});
		    		
		        	
		        	
		        	mainForm.append(it);		            
		        }
				
				L.i("",o.toString(2));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			return out;
		}
		
		
	}
	private Object store;
	public void commandAction(Command arg0, Displayable arg1) {
		// TODO Auto-generated method stub
		if (arg0 == fetchTokenCommand) {
			L.i("", "Fetching token");
			ses.fetchTokenForSession();
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
		
	}

	private void startListFiles() {
		L.i("", "Listing files");
		String url = "https://www.googleapis.com/drive/v2/files?maxResults=20&access_token="+ses.getAccessToken();
		ListFilesTask t = new ListFilesTask(url);
		store = t;
		Worker.fork(t);
	}


}
