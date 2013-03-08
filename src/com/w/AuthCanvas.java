package com.w;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.futurice.tantalum3.PlatformUtils;
import com.futurice.tantalum3.log.L;

public class AuthCanvas extends Canvas implements AuthListener {

	
	
	public AuthCanvas(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	

	protected void showNotify() {
		// TODO Auto-generated method stub
		super.showNotify();
		L.i("", "Repaint");
		if (this.onShow != null) {
			this.onShow.run();
		}
	}

	private String title;
	public String content;
	private Runnable authOkHandler;
	
	public void setAuthOkHandler(Runnable authOkHandler) {
		this.authOkHandler = authOkHandler;
	}

	AuthSession authSession;
	
	
	public void setAuthSession(AuthSession authSession) {
		this.authSession = authSession;
	}

	private MIDlet midlet;
	public MIDlet getMidlet() {
		return midlet;
	}


	public void setMidlet(MIDlet midlet) {
		this.midlet = midlet;
	}

	private Runnable onShow;
	
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public void setOnShow(Runnable onShow) {
		this.onShow = onShow;
	}

	

	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
	
		L.i("", "p" + g);
		int width = getWidth();
		int height = getHeight();
		// clear the screen (paint it white):
		g.setColor(0xffffff);
		// The first two args give the coordinates of the top
		// left corner of the rectangle. (0,0) corresponds
		// to the top left corner of the screen.
		g.fillRect(0, 0, width, height);
		// display the hello world message if appropriate:.
		Font font = g.getFont();
		L.i("", "font " + font);
		int fontHeight = font.getHeight();
		int fontWidth = font.stringWidth(content);
		// set the text color to red:
		g.setColor(255, 0, 0);
		g.setFont(font);
		// write the string in the center of the screen
		
		g.drawString(content, (width - fontWidth) / 2,
				(height - fontHeight) / 2, g.TOP | g.LEFT);
		L.i("" , "painted " + content);
				

	}


	public void browserLaunchNeeded(String url) {
		// TODO Auto-generated method stub
		setContent("Waiting for browser, url=" + url);
		try {
			
			setOnShow(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					//authSession.finalizeAuthIfNeeded(null);
					
					setContent("Fetching token");
					repaint();
					authSession.fetchTokenForSession(null);
					
				}
			});
			midlet.platformRequest(url);
		} catch (ConnectionNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	}


	
	public void tokenAvailable(String accessToken, String refreshToken) {
		// TODO Auto-generated method stub
		if (this.authOkHandler != null) {
			setContent("Authenticated");
			repaint();
			authOkHandler.run();
		}
		
	}

}
