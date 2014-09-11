package backend;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * an object containing specified user information
 * @author DreyCow
 */
public class UserInfo {
	
	public JSONObject generalInfo;
	public JSONArray tracks;
	public JSONArray favorites;
	public JSONArray playlists;
	public JSONArray followings;
	public JSONArray followers;
	public JSONArray comments;
	
	public Boolean haveGeneralInfo = false;
	public Boolean haveTracks = false;
	public Boolean haveFavorites = false;
	public Boolean havePlaylists = false;
	public Boolean haveFollowings = false;
	public Boolean haveFollowers = false;
	public Boolean haveComments = false;
	
	
	public UserInfo ()throws IOException{}
	
	public void setGeneralInfo(JSONObject generalInfo)
	{
		this.generalInfo=generalInfo;
		haveGeneralInfo = true;
	}
	
	public void setTracks(JSONArray tracks)
	{
		this.tracks = tracks;
		haveTracks = true;
	}
	
	public void setFavorites(JSONArray favorites)
	{
		this.favorites=favorites;
		haveFavorites = true;
	}
	
	public void setPlaylists(JSONArray playlists)
	{
		this.playlists=playlists;
		havePlaylists = true;
	}
	
	public void setFollowings(JSONArray followings)
	{
		this.followings=followings;
		haveFollowings = true;
	}
	
	public void setFollowers(JSONArray followers)
	{
		this.followers=followers;
		haveFollowers = true;
	}

	public void setComments(JSONArray comments)
	{
		this.comments=comments;
		haveComments = true;
	}
		
}
