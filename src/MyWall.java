import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDletStateChangeException;

import com.futurice.tantalum3.TantalumMIDlet;
import com.w.AuthSession;
import com.w.AuthListener;


public class MyWall extends TantalumMIDlet implements AuthListener {

	 
	AuthSession ses;
	public MyWall() {
		super(2);
		// TODO Auto-generated constructor stub
		ses = new AuthSession();
		ses.setAuthListener(this);
		ses.startAuth();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub

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

}
