import javax.microedition.midlet.MIDletStateChangeException;

import com.futurice.tantalum3.TantalumMIDlet;
import com.w.AuthSession;


public class MyWall extends TantalumMIDlet {

	AuthSession ses;
	public MyWall() {
		super(2);
		// TODO Auto-generated constructor stub
		ses = new AuthSession();
		ses.startAuth();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

}
