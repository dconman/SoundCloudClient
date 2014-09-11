package backend;

import java.io.IOException;

@SuppressWarnings("serial")
public class HTTPException extends IOException {
	private int HTTPcode;
	private String URL;
	private String message;

	public HTTPException(int type, String URL, IOException e) {
		this.URL = URL;
		HTTPcode = type;
		this.setStackTrace(e.getStackTrace());
		message = e.getMessage();
		
	}
	public int getHTTPcode() {
		return HTTPcode;
	}
	
	public String getURL() {
		return URL;
	}

	public String getMessage() {
		return message;
	}

}
