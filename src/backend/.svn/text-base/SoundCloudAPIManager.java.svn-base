package backend;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class SoundCloudAPIManager {

	public static String clientid="7bbc3f53555f454a8d5c3e4efb5db34c";
	public static String clientSecret="ec352cc9344ec3f89b432721d41b702c";
	public static String authid=null;
	static AuthIDRefeshThread refreshThread;

	/**
	 * Try to log in, if it succeeds, update authid. if fails return false
	 * 
	 * @author Philip
	 * @param username username for account
	 * @param password password for account
	 * @return whether login failed or not
	 * @throws HTTPException
	 * @throws IOException
	 */


	public static boolean login(String username, String password) throws HTTPException, IOException{
		if(username.isEmpty() || password.isEmpty() || authid != null){
			return false;
		}

		String tokenEndpoint = "https://api.soundcloud.com/oauth2/token";

		InputStream [] byteParams = {new ByteArrayInputStream(new StringBuilder("client_id=").append(clientid).toString().getBytes("UTF-8")),
				new ByteArrayInputStream(new StringBuilder("&client_secret=").append(clientSecret).toString().getBytes("UTF-8")),
				new ByteArrayInputStream(new StringBuilder("&username=").append(username).toString().getBytes("UTF-8")),
				new ByteArrayInputStream(new StringBuilder("&password=").append(password).toString().getBytes("UTF-8")),
				new ByteArrayInputStream(new StringBuilder("&grant_type=password").toString().getBytes("UTF-8"))};

		String response;

		try{
			response = executeHTTPRequest("POST", tokenEndpoint, byteParams);
		}
		catch(HTTPException e){
			if(e.getHTTPcode() == 401){
				for(int i=0; i<3; i++){
					try{
						response = executeHTTPRequest("POST", tokenEndpoint, byteParams);
					}
					catch(HTTPException ex)
					{
						continue;
					}
				}
			}
			throw e;
		}

		JSONObject formattedResponse = new JSONObject(response);
		authid = formattedResponse.getString("access_token");

		System.out.println(response);

		int secondsToRefresh = formattedResponse.getInt("expires_in");
		String refreshToken = formattedResponse.getString("refresh_token");
		if(refreshThread!=null){
			refreshThread.interrupt();
		}
		refreshThread=new AuthIDRefeshThread(secondsToRefresh, refreshToken);
		refreshThread.start();

		return true;
	}

	/**
	 * 
	 * @param query what to seach on tags, author, title, description
	 * @param offset how many results to ignore
	 * @param limit how many results to return
	 * @return a json array of tracks, preferably with tracks that are not streamable or downloadable
	 * @author Aaron
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */

	public static JSONArray searchTracks(String query, int offset, int limit) throws IOException{
		int numTracksToPull = limit; //How many tracks to pull during the http request
		int currentTrackOffset = 0;  //the offset between httpRequests
		int currentOffset = offset;  //how many valid tracks have been offset
		int tracksFound = 0; 		 //running total of tracks found from httpRequests
		String queryString = URLEncoder.encode(query, "UTF-8"); //encode special characters
		StringBuilder jsonArrayString = new StringBuilder("["); //start building a JSONArray string

		outerloop:
			for(int j = 0; j< 3;j++)//try 3 requests then quit
			{
				numTracksToPull = numTracksToPull*2; //each request double the number of lookups
				//
				String output 	= "://api.soundcloud.com/tracks.json?"  //the query string
						+ "q=" 				+ queryString
						+ "&client_id=" 	+ clientid
						+ "&offset="		+ currentTrackOffset
						+ "&limit="			+ numTracksToPull;
				
				output = isLoggedIn() ? "https" + output + "&oauth_token="+authid : "http" + output;

				JSONArray result = new JSONArray(executeHTTPRequest("GET", output, null)); //make httprequest

				for(int i = 0; i < result.length() ; i ++)//for each json object
				{
					JSONObject currentObject = (JSONObject) result.get(i);
					//				
					//Is it streamable or downloadable?  If not, skip object
					if(currentObject.optBoolean("streamable", false)||currentObject.optBoolean("downloadable", false))
					{
						//if so, is there any offset?
						if(currentOffset > 0)
						{
							//if so, decrease offset and skip.
							currentOffset --;
						}
						else
						{
							//if not, append the json object to the end of the jsonArrayString
							jsonArrayString.append(currentObject.toString()).append(",");
							tracksFound++;
							//					
							if(tracksFound >= limit)//If the max number of tracks are found, exit both loops.
							{
								break outerloop;
							}
						}
						//					
					}
				}
			}
		if(jsonArrayString.charAt(jsonArrayString.length()-1)==',')//delete dangling comma if it exists
		{
			jsonArrayString.deleteCharAt(jsonArrayString.length()-1);
		}
		JSONArray myresult = new JSONArray(jsonArrayString + "]"); //create jsonArray
		return myresult;

	}





	public static boolean isLoggedIn(){
		return authid!=null;
	}

	public static void logout(){
		authid=null;
		if(refreshThread!=null){
			refreshThread.interrupt();
			refreshThread = null;
		}
	}


	/**
	 * Gets all profile information for the current user.
	 * 
	 * IF NO ONE IS LOGGED IN, RETURNS NULL.
	 * 
	 * @return a JSONObject for the currently logged in user. If no user is logged in, returns null.
	 * @author Dawson
	 * @throws HTTPException,IOException
	 */
	public static UserInfo getCurrentUser() throws HTTPException, IOException {
		if(authid==null) return null;

		short attempts = 0;

		while(attempts < 5) {
			try {
				String baseURLString = "http://api.soundcloud.com/me";

				String userRequest 				= baseURLString + 					  ".json?" + "client_id=" + clientid;
				String userTracksRequest 		= baseURLString + "/tracks" 		+ ".json?" + "client_id=" + clientid;
				String userPlaylistsRequest 	= baseURLString + "/playlists"	 	+ ".json?" + "client_id=" + clientid;
				String userFollowingsRequest 	= baseURLString + "/followings" 	+ ".json?" + "client_id=" + clientid;
				String userFollowersRequest 	= baseURLString + "/followers" 		+ ".json?" + "client_id=" + clientid;
				String userCommentsRequest 		= baseURLString + "/comments" 		+ ".json?" + "client_id=" + clientid;
				String userFavoritesRequest 	= baseURLString + "/favorites" 		+ ".json?" + "client_id=" + clientid;


				UserInfo myUserInfo = new UserInfo();
				JSONObject generalInfo 	= new JSONObject(SoundCloudAPIManager.executeHTTPRequest("GET", userRequest, 			null));
				myUserInfo.setGeneralInfo(generalInfo);
				JSONArray tracks 		= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userTracksRequest, 		null));
				myUserInfo.setTracks(tracks);
				JSONArray playlists 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userPlaylistsRequest, 	null));
				myUserInfo.setPlaylists(playlists);
				JSONArray followings 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFollowingsRequest, 	null));
				myUserInfo.setFollowings(followings);
				JSONArray followers 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFollowersRequest, 	null));
				myUserInfo.setFollowers(followers);
				JSONArray comments 		= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userCommentsRequest, 	null));
				myUserInfo.setComments(comments);
				JSONArray favorites 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFavoritesRequest, 	null));
				myUserInfo.setFavorites(favorites);	
				return myUserInfo;
			} catch(HTTPException e) {
				if (attempts == 4) throw e;

			} catch (IOException e) {
				if (attempts == 4) throw e;

			}
		}
		return null;
	}

	/**
	 * Executes an HTTP Request of any kind.
	 * 
	 * @param requestType A String containing your request type. Valid options are "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT", "PATCH"
	 * @param targetURL A String of the URL for the HTTP Request. For GET or HEAD, this should contain the parameters.
	 * @param urlParameters An Array of InputStreams containing the parameters to be passes. To go from a string to a stream, us new ByteArrayInputStream(YOURSTRING.getBytes("UTF-8")
	 * @return A String, the server response
	 * @throws HTTPException if the server returns an HTTP Error Code.
	 * @throws IOException if the server or the InputStream have a problem. Sometimes represents a 404
	 * 
	 * @author Dawson
	 */
	public static String executeHTTPRequest(String requestType, String targetURL, InputStream urlParameters[]) throws HTTPException, IOException {


		Pattern httpPattern=Pattern.compile("Server returned HTTP response code: (\\d{3}) for URL: (.*)\\z");
		URL url;
		HttpURLConnection connection = null;
		int bytesRead = 0;
		byte buffer[];
		try {
			buffer = new byte[1024];
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(requestType);
			if(requestType != "GET") {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				connection.setRequestProperty("Content-Language", "en-US");  
			}


			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			if(urlParameters != null){
				DataOutputStream wr = new DataOutputStream (
						connection.getOutputStream ());
				for(int i = 0; i < urlParameters.length; i++)
					while((bytesRead = urlParameters[i].read(buffer,0,1024)) > -1){
						wr.write (buffer,0,bytesRead);
					}
				wr.flush ();
				wr.close ();
			}


			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (IOException e) {

			Matcher extractor=httpPattern.matcher(e.getMessage());
			if (extractor.matches()){

				int type = Integer.parseInt(extractor.group(1));
				String URL = extractor.group(2);
				if(connection != null) {
					connection.disconnect(); 
				}
				throw new HTTPException(type, URL, e);
			}
			if(connection != null) {
				connection.disconnect(); 
			}
			throw e;

		}
		finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}

	}

	//Posts
	public static String executePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	//Gets
	public static String executeGet(String targetURL)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");


			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);


			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (IOException e) {

			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	/**
	 * Fetch comments of the current song
	 * 
	 * @param trackID is the current ID of the desired song
	 * @return A JSON Array of comments. Contains their unique ID, timestamp, creation data, and the body of the comment itself
	 * 
	 * @throws HTTPException
	 * @throws IOException
	 * 
	 * @author Zach
	 */
	public static JSONArray getTrackComments(long trackID) throws HTTPException, IOException {

		String outputURL = "https://api.soundcloud.com/tracks/" + Long.toString(trackID) + "/comments.json?&client_id=" + clientid;

		return new JSONArray(executeHTTPRequest("GET", outputURL, null));
	}

	/**
	 * Post comments to the current song
	 * 
	 * @param trackID is the current ID of the desired song
	 * @param commentBody is the comment message we are trying to post
	 * @param commentTime is the desired timestamp of the comment. A negative number here is used to post a comment with a null timestamp value
	 * 
	 * @throws HTTPException
	 * @throws IOException
	 * 
	 * @author Zach
	 */
	public static void postTrackComment(long trackID, String commentBody, long commentTime) throws HTTPException, IOException {

		if(!isLoggedIn()) {
			throw new IllegalStateException("Not logged in");
		}
		
		String outputURL = "https://api.soundcloud.com/tracks/" + Long.toString(trackID) + "/comments";

		InputStream [] params = null;

		if(commentTime >= 0) {		
			params = new InputStream []	{new ByteArrayInputStream(("comment[timestamp]=" + Long.toString(commentTime)).getBytes("UTF-8")),
					new ByteArrayInputStream(("&comment[body]=" + commentBody).getBytes("UTF-8")),
					new ByteArrayInputStream(("&oauth_token=" + authid).getBytes("UTF-8")),
					new ByteArrayInputStream(("&format=json").getBytes("UTF-8"))};		
		} else {
			params = new InputStream []	{new ByteArrayInputStream(("comment[body]=" + commentBody).getBytes("UTF-8")),
					new ByteArrayInputStream(("&oauth_token=" + authid).getBytes("UTF-8")),
					new ByteArrayInputStream(("&format=json").getBytes("UTF-8"))};			
		}

		executeHTTPRequest("POST", outputURL, params);
	}

	/**
	 * Creates a reply to the current comment, which is represented as a JSONObject
	 * 
	 * @param currentComment is the JSONObject of the comment we are replying to.
	 * 		We will use this to extract the comment time, track id, and target username.
	 * @param commentBody is the comment message we are trying to post
	 * 
	 * @throws HTTPException
	 * @throws IOException
	 * 
	 * @author Zach
	 */
	public static void postReplyComment(JSONObject currentComment, String commentBody) throws HTTPException, IOException {
		long commentTime = currentComment.getLong("timestamp");
		long trackID  = currentComment.getLong("track_id");
		String replyUserName = currentComment.getJSONObject("user").getString("username");

		commentBody = "@" + replyUserName + ":+" + commentBody;

		postTrackComment(trackID, commentBody, commentTime);

	}

	/**
	 * Delete the given comment
	 * 
	 * @param currentComment is the JSONObject representing the current comment
	 * 
	 * @throws HTTPException
	 * @throws IOException
	 * 
	 * @author Zach
	 */
	public static void deleteTackComment(JSONObject currentComment) throws HTTPException, IOException {
		long commentID = currentComment.getLong("id");
		long trackID = currentComment.getLong("track_id");

		String outputURL = "https://api.soundcloud.com/tracks/" + Long.toString(trackID) + "/comments/" + Long.toString(commentID) + "?client_id=" + clientid;

		InputStream [] params = {new ByteArrayInputStream(("&oauth_token=" + authid).getBytes("UTF-8"))};

		executeHTTPRequest("DELETE", outputURL, params);
	}
	
	/**
	 * Add or remove a song from the user's favorites list.
	 * 	First it gets the user ID and uses it to get the list of the users favorites.
	 * 	Then it checks if the current songs exists in that list.
	 * 	If true it removes it. If false it adds it.
	 * 
	 * @param trackID is the current ID of the desired song
	 * @return is a boolean value indicating if the track was liked or unliked (true is like, false is unlike)
	 * 
	 * @throws HTTPException
	 * @throws IOException
	 * 
	 * @author Zach
	 */
	public static boolean toggleTrackLike(long trackID) throws HTTPException, IOException {

		if(!isLoggedIn()) {
			throw new IllegalStateException("Not logged in");
		}
		String stringURL = "https://api.soundcloud.com/tracks/" + Long.toString(trackID) + ".json?client_id=" + clientid + "&oauth_token=" + authid;
		
		JSONObject currentTrack = new JSONObject(executeHTTPRequest("GET", stringURL, null));
		
		boolean alreadyLiked = currentTrack.getBoolean("user_favorite");
					
		String outputURL = "https://api.soundcloud.com/me/favorites/" + Long.toString(trackID);

		InputStream [] params = null;

		if(alreadyLiked) {
			params = new InputStream [] {new ByteArrayInputStream(("oauth_token=" + authid).getBytes("UTF-8")),
					new ByteArrayInputStream(("&format=json").getBytes("UTF-8")),
					new ByteArrayInputStream(("&_method=DELETE").getBytes("UTF-8"))};
		} else {
			params = new InputStream [] {new ByteArrayInputStream(("oauth_token=" + authid).getBytes("UTF-8")),
					new ByteArrayInputStream(("&format=json").getBytes("UTF-8")),
					new ByteArrayInputStream(("&_method=PUT").getBytes("UTF-8"))};			
		}

		executeHTTPRequest("POST", outputURL, params);
		
		return !alreadyLiked;
	}

	/**
	 * Returns user information on any given user using their id
	 * @param userID the id of the user to lookup
	 * @return an object that contains all associated user information
	 * @throws JSONException
	 * @throws HTTPException
	 * @throws IOException
	 */
	public static UserInfo getUser(long userID) throws JSONException, HTTPException, IOException{
		String baseURLString = "http://api.soundcloud.com/users/" + userID ;

		String userRequest 				= baseURLString + 					  ".json?" + "client_id=" + clientid;
		String userTracksRequest 		= baseURLString + "/tracks" 		+ ".json?" + "client_id=" + clientid;
		String userPlaylistsRequest 	= baseURLString + "/playlists"	 	+ ".json?" + "client_id=" + clientid;
		String userFollowingsRequest 	= baseURLString + "/followings" 	+ ".json?" + "client_id=" + clientid;
		String userFollowersRequest 	= baseURLString + "/followers" 		+ ".json?" + "client_id=" + clientid;
		String userCommentsRequest 		= baseURLString + "/comments" 		+ ".json?" + "client_id=" + clientid;
		String userFavoritesRequest 	= baseURLString + "/favorites" 		+ ".json?" + "client_id=" + clientid;



		UserInfo myUserInfo = new UserInfo();
		JSONObject generalInfo 	= new JSONObject(SoundCloudAPIManager.executeHTTPRequest("GET", userRequest, 			null));
		myUserInfo.setGeneralInfo(generalInfo);
		JSONArray tracks 		= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userTracksRequest, 		null));
		myUserInfo.setTracks(tracks);
		JSONArray playlists 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userPlaylistsRequest, 	null));
		myUserInfo.setPlaylists(playlists);
		JSONArray followings 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFollowingsRequest, 	null));
		myUserInfo.setFollowings(followings);
		JSONArray followers 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFollowersRequest, 	null));
		myUserInfo.setFollowers(followers);
		JSONArray comments 		= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userCommentsRequest, 	null));
		myUserInfo.setComments(comments);
		JSONArray favorites 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFavoritesRequest, 	null));
		myUserInfo.setFavorites(favorites);	
		return myUserInfo;
	}

	/**
	 * Uploads a track.
	 * 
	 * @param title A String with the track title
	 * @param isPublic A boolean that determines whether the uploaded track should be public (true) or private (false)
	 * @param file A File that represents the sound file to be uploaded
	 * @param progBar A ProgressBar that represents the upload progress
	 * @return A JSONObject containing track information
	 */
	public static JSONObject uploadTrack (String title, boolean isPublic, File file, ProgressBar progBar) throws HTTPException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost request=new HttpPost("https://api.soundcloud.com/tracks");
		final HttpEntity requestEntity=MultipartEntityBuilder.create()
				.addPart("track[title]", new StringBody(title, ContentType.TEXT_PLAIN))
				.addPart("track[asset_data]", new FileBody(file))
				.addPart("track[sharing]", new StringBody(isPublic ? "public" : "private", ContentType.TEXT_PLAIN))
				.addPart("format", new StringBody("json", ContentType.TEXT_PLAIN))
				.addPart("oauth_token", new StringBody(authid, ContentType.TEXT_PLAIN))
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.setCharset(Charset.forName("UTF-8")).build();
		Display.getDefault().asyncExec(new Runnable () {
			private ProgressBar myBar;
			private int fileSize;

			public Runnable init(ProgressBar pB, int fS) {
				myBar = pB;
				fileSize = fS;
				return this;
			}

			@Override
			public void run() {
				myBar.setMaximum(fileSize);
			}
		}.init(progBar, (int) file.length()));

		class ProgressiveEntity implements HttpEntity {
			private HttpEntity reqEnt;
			private ProgressBar pBar;
			public ProgressiveEntity(HttpEntity entity, ProgressBar pB) {
				reqEnt = entity;
				pBar = pB;
			}

			@Override
			@Deprecated
			public void consumeContent() throws IOException {
				reqEnt.consumeContent();                
			}
			@Override
			public InputStream getContent() throws IOException,
			IllegalStateException {
				return reqEnt.getContent();
			}
			@Override
			public Header getContentEncoding() {             
				return reqEnt.getContentEncoding();
			}
			@Override
			public long getContentLength() {
				return reqEnt.getContentLength();
			}
			@Override
			public Header getContentType() {
				return reqEnt.getContentType();
			}
			@Override
			public boolean isChunked() {             
				return reqEnt.isChunked();
			}
			@Override
			public boolean isRepeatable() {
				return reqEnt.isRepeatable();
			}
			@Override
			public boolean isStreaming() {             
				return reqEnt.isStreaming();
			}

			@Override
			public void writeTo(OutputStream outstream) throws IOException {

				class ProxyOutputStream extends FilterOutputStream {
					/**
					 * @author Stephen Colebourne
					 */

					 public ProxyOutputStream(OutputStream proxy) {
						super(proxy);    
					}
					public void write(int idx) throws IOException {
						out.write(idx);
					}
					public void write(byte[] bts) throws IOException {
						out.write(bts);
					}
					public void write(byte[] bts, int st, int end) throws IOException {
						out.write(bts, st, end);
					}
					public void flush() throws IOException {
						out.flush();
					}
					public void close() throws IOException {
						out.close();
					}
				}

				class ProgressiveOutputStream extends ProxyOutputStream {
					public ProgressiveOutputStream(OutputStream proxy) {
						super(proxy);
					}
					public void write(byte[] bts, int st, int len) throws IOException {
						Display.getDefault().asyncExec(new Runnable () {
							private ProgressBar myBar;
							private int delta;

							public Runnable init(ProgressBar pB, int d) {
								myBar = pB;
								delta = d;
								return this;
							}

							@Override
							public void run() {
								myBar.setSelection(myBar.getSelection() + delta);
							}
						}.init(pBar, len));
						out.write(bts, st, len);
					}
				}
				reqEnt.writeTo(new ProgressiveOutputStream(outstream));
			}
		};

		request.setEntity(new ProgressiveEntity(requestEntity, progBar));
		CloseableHttpResponse response=httpClient.execute(request);
		JSONObject trackInfo = null;
		try {
			HttpEntity resEntity = response.getEntity();
			trackInfo = new JSONObject(EntityUtils.toString(resEntity));
			EntityUtils.consume(resEntity);
		} finally {
			response.close();
			httpClient.close();
		}
		return trackInfo;
	}	
}