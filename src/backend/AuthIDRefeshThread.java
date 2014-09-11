package backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

public class AuthIDRefeshThread extends Thread {
	private long sleepTime;
	private String refreshToken;
	
	public AuthIDRefeshThread(int in1, String in2) {
		sleepTime=in1;
		refreshToken=in2;
	}

	@Override
	public void run() {
		super.run();
		synchronized(this){
			try {
				Thread.sleep((sleepTime-600)*1000);
			} catch (InterruptedException e) {
				return;
			}
			//refresh token here
			String tokenEndpoint = "https://api.soundcloud.com/oauth2/token";
			
			InputStream [] parameters = new InputStream [3];
			
			try{
				InputStream [] byteParams = {new ByteArrayInputStream(new StringBuilder("client_id=").append(SoundCloudAPIManager.clientid).toString().getBytes("UTF-8")),
											 new ByteArrayInputStream(new StringBuilder("&client_secret=").append(SoundCloudAPIManager.clientSecret).toString().getBytes("UTF-8")),
											 new ByteArrayInputStream(new StringBuilder("&refresh_token=").append(refreshToken).toString().getBytes("UTF-8")),
											 new ByteArrayInputStream(new StringBuilder("&grant_type=refresh_token").toString().getBytes("UTF-8"))};
				parameters = byteParams;
			}
			catch(UnsupportedEncodingException e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			
			String response = "";
			
			try{
				response = SoundCloudAPIManager.executeHTTPRequest("POST", tokenEndpoint, parameters);
			}
			catch(HTTPException e){
				if(e.getHTTPcode() == 401){
					for(int i=0; i<3; i++){
						try{
							response = SoundCloudAPIManager.executeHTTPRequest("POST", tokenEndpoint, parameters);
						}
						catch(HTTPException ex){
							continue;
						}
						catch(IOException ex){
							e.printStackTrace();
							System.out.println(e.getMessage());
							return;
						}
					}
				}
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

			JSONObject formattedResponse = new JSONObject(response);
			
			SoundCloudAPIManager.authid = formattedResponse.getString("access_token");
			
			System.out.println(response);
			
			int newRefreshTime = formattedResponse.getInt("expires_in");
			String newRefreshToken = formattedResponse.getString("refresh_token");
			
			AuthIDRefeshThread replacement=new AuthIDRefeshThread(newRefreshTime, newRefreshToken);
			replacement.start();
		}
		
	}

}
