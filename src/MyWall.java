import java.io.UnsupportedEncodingException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.futurice.tantalum3.TantalumMIDlet;
import com.futurice.tantalum3.Worker;
import com.futurice.tantalum3.log.L;
import com.futurice.tantalum3.net.HttpGetter;
import com.w.AuthListener;
import com.w.AuthSession;


public class MyWall extends TantalumMIDlet implements AuthListener, CommandListener {

    private Command exitCmd; //The exit command...
    private Display display; //The phone's display
    private List mainList; //the main form for the application	
	 
	private AuthSession ses;
	
	private Command fetchTokenCommand;
	private Command listFilesCommand;
	
	public MyWall() {
		super(2);
		// TODO Auto-generated constructor stub
		display = Display.getDisplay(this);
		
		mainList = new List("GDrive", List.IMPLICIT);
		fetchTokenCommand = new Command("Fetch token" , Command.SCREEN, 0);
		mainList.addCommand(fetchTokenCommand);
		listFilesCommand = new Command("List files on GDrive" , Command.SCREEN, 0);
		mainList.addCommand(listFilesCommand);
		
		mainList.setCommandListener(this);
				
		ses = new AuthSession();
		ses.setAuthListener(this);
		ses.startAuth();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		display.setCurrent(mainList);

	}

	public void browserLaunchNeeded(String url) {
		try {
			platformRequest(url);
		} catch (ConnectionNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
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
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mainList.append("Hello", null);
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
			L.i("", "Listing files");
			String url = "https://www.googleapis.com/drive/v2/files?maxResults=20&access_token="+ses.getAccessToken();
			ListFilesTask t = new ListFilesTask(url);
			store = t;
			Worker.fork(t);
			
		}
		
	}

}
