package backend;

import java.util.ArrayList;

import maryb.player.Player;
import maryb.player.PlayerEventListener;
import maryb.player.PlayerState;

import org.json.JSONObject;

public class Mp3Player {
	private static Player internalPlayer;
	private static ArrayList<JSONObject> currentQueue;
	private static Runnable[] onBuffer;
	private static Runnable[] onPause;
	private static Runnable[] onPlay;
	private static Runnable[] onSongEnd;

	static{
		internalPlayer=new Player();
		currentQueue=new ArrayList<JSONObject>();
		onBuffer=new Runnable[100];
		onPause=new Runnable[100];
		onPlay=new Runnable[100];
		onSongEnd=new Runnable[100];
		internalPlayer.setCurrentVolume(1f);
		internalPlayer.setListener(new PlayerEventListener(){
			PlayerState lastState=PlayerState.PAUSED;

			@Override
			public void buffer() {
				for(int i=0;i<100;i++){
					if(onBuffer[i]!=null){
						onBuffer[i].run();
					}
				}
			}

			@Override
			public void endOfMedia() {
				synchronized(internalPlayer){
					if(currentQueue.size()>1){
						try{
							currentQueue.remove(0);
							internalPlayer.pauseSync();
							JSONObject song=currentQueue.get(0);
							if(song.getBoolean("streamable")){
								internalPlayer.setSourceLocation(song.getString("stream_url")+"?client_id="+SoundCloudAPIManager.clientid);
							}else if(song.getBoolean("downloadable")){
								internalPlayer.setSourceLocation(song.getString("download_url")+"?client_id="+SoundCloudAPIManager.clientid);
							}
							internalPlayer.seek(0);
							internalPlayer.playSync();
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
				for(int i=0;i<100;i++){
					if(onSongEnd[i]!=null){
						onSongEnd[i].run();
					}
				}
			}

			@Override
			public void stateChanged() {
				if(internalPlayer.getState()!=lastState){
					if(internalPlayer.getState().equals(PlayerState.PAUSED)){
						for(int i=0;i<100;i++){
							if(onPause[i]!=null){
								onPause[i].run();
							}
						}
					}else if(internalPlayer.getState().equals(PlayerState.PLAYING)){
						for(int i=0;i<100;i++){
							if(onPlay[i]!=null){
								onPlay[i].run();
							}
						}
					}
					lastState=internalPlayer.getState();
				}
			}

		});
	}

	/**
	 * Adds a song to the current queue of songs to be played
	 * @param song the JSONObject for the song to be added
	 */
	public static void addSong(JSONObject song){
		currentQueue.add(song);
	}

	/**
	 * Gets the current queue of songs to be played, the first element of the list is the currently playing song.
	 * @return
	 */
	public static JSONObject[] getQueue(){
		return currentQueue.toArray(new JSONObject[currentQueue.size()]);
	}

	/**
	 * Gets the currently playing song
	 * @return JSONObject for the currently playing song
	 */
	public static JSONObject getCurrentlyPlaying(){
		if(currentQueue.isEmpty()){
			return null;
		}
		return currentQueue.get(0);
	}
	/**
	 * Gets the current state of the player
	 * @return An enumerated type for the state
	 */
	public static PlayerState getState(){
		return internalPlayer.getState();
	}

	/**
	 * Adds an event Handler for the when a song runs into the buffer
	 * @param in The handler, the {@link Runnable#run()} of this object will be called
	 * @return code to pass to remove the handler, -1 if the handler was not set, because 100 handlers already exist.
	 */
	public static int addOnBuffer(Runnable in){
		for(int i=0;i<100;i++){
			if(onBuffer[i]==null){
				onBuffer[i]=in;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes the handler for a song running into the buffer
	 * @param index the code returned when you added the handler.
	 */
	public static void removeOnBuffer(int index){
		onBuffer[index]=null;
	}

	/**
	 * Adds an event Handler for the when a song becomes paused for any reason
	 * @param in The handler, the {@link Runnable#run()} of this object will be called
	 * @return code to pass to remove the handler, -1 if the handler was not set, because 100 handlers already exist.
	 */
	public static int addOnPause(Runnable in){
		for(int i=0;i<100;i++){
			if(onPause[i]==null){
				onPause[i]=in;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes the handler for when a song becomes paused
	 * @param index the code returned when you added the handler.
	 */
	public static void removeOnPause(int index){
		onBuffer[index]=null;
	}

	/**
	 * Adds an event Handler for the when a song begins playing for any reason
	 * @param in The handler, the {@link Runnable#run()} of this object will be called
	 * @return code to pass to remove the handler, -1 if the handler was not set, because 100 handlers already exist.
	 */
	public static int addOnPlay(Runnable in){
		for(int i=0;i<100;i++){
			if(onPlay[i]==null){
				onPlay[i]=in;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes an event handler for when a song begins playing for any reason
	 * @param index the code returned when you added the handler.
	 */
	public static void removeOnPlay(int index){
		onPlay[index]=null;
	}

	/**
	 * Adds an event Handler for the when a song ends for any reason. Note, this event will already try to move onto the next song if possible
	 * @param in The handler, the {@link Runnable#run()} of this object will be called
	 * @return code to pass to remove the handler, -1 if the handler was not set, because 100 handlers already exist.
	 */
	public static int addOnSongEnd(Runnable in){
		for(int i=0;i<100;i++){
			if(onSongEnd[i]==null){
				onSongEnd[i]=in;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes an event handler for when a song ends for any reason. Note, this event will already try to move onto the next song if possible
	 * @param index the code returned when you added the handler.
	 */
	public static void removeOnSongEnd(int index){
		onSongEnd[index]=null;
	}

	/**
	 * Plays the currently set song. Is a non-blocking call.
	 */
	public static void play(){
		JSONObject song=currentQueue.get(0);
		if(song.getBoolean("streamable")){
			internalPlayer.setSourceLocation(song.getString("stream_url")+"?client_id="+SoundCloudAPIManager.clientid);
		}else if(song.getBoolean("downloadable")){
			internalPlayer.setSourceLocation(song.getString("download_url")+"?client_id="+SoundCloudAPIManager.clientid);
		}
		internalPlayer.play();
	}

	/**
	 * Plays the currently set song. Blocks until the song begins playing
	 * @throws InterruptedException
	 */
	public static void playSync() throws InterruptedException{
		JSONObject song=currentQueue.get(0);
		if(song.getBoolean("streamable")){
			internalPlayer.setSourceLocation(song.getString("stream_url")+"?client_id="+SoundCloudAPIManager.clientid);
		}else if(song.getBoolean("downloadable")){
			internalPlayer.setSourceLocation(song.getString("download_url")+"?client_id="+SoundCloudAPIManager.clientid);
		}
		internalPlayer.playSync();
	}

	/**
	 * Pauses the currently playing song. Is a non-blocking call.
	 */
	public static void pause(){
		internalPlayer.pause();
	}

	/**
	 * Pauses the currently playing song. Blocks until the song pauses.
	 * @throws InterruptedException
	 */
	public static void pauseSync() throws InterruptedException{
		internalPlayer.pauseSync();
	}

	/**
	 * Stops the current song, If playing, pauses, resets song to beginning. This is a non-blocking call.
	 */
	public static void stop(){
		new Thread(){
			public void run(){
				try {
					internalPlayer.pauseSync();
					internalPlayer.seek(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Stops the current song, If playing, pauses, resets song to beginning. Blocks until the song stops.
	 * @throws InterruptedException
	 */
	public static void stopSync() throws InterruptedException{
		internalPlayer.pauseSync();
		internalPlayer.seek(0);
	}

	/**
	 * Seeks to a new position in the song, may trigger buffering, may go past end of song. This call is non-blocking
	 * @param newPOS the position to seek to. 1 second is a million
	 */
	public static void seek(long newPOS){
		new Thread(){
			private long input;
			public Thread init(long in){
				input=in;
				return this;
			}
			public void run(){
				try{
					internalPlayer.pauseSync();
					internalPlayer.seek(input);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}.init(newPOS).start();
	}

	/**
	 * Seeks to a new position in the song, may trigger buffering, may go past end of song. This call is blocks until the player seeks to the new position. May block for a long time while buffering occurs.
	 * @param newPOS the position to seek to. 1 second is a million
	 * @throws InterruptedException
	 */
	public static void seekSync(long newPOS) throws InterruptedException{
		internalPlayer.pauseSync();
		internalPlayer.seek(newPOS);
	}

	/**
	 * Sets the volume to the passed value
	 * @param newVolume floating value between 0 and 1
	 */
	public static void setVolume(float newVolume){
		internalPlayer.setCurrentVolume(newVolume);
	}

	/**
	 * Gets the current volume
	 * @return floating value between 0 and 1
	 */
	public static float getVolume(){
		return internalPlayer.getCurrentVolume();
	}

	/**
	 * Gets the current position that the player is at
	 * @return current position, one million is one second
	 */
	public static long getPosition(){
		return internalPlayer.getCurrentPosition();
	}

	/**
	 * Gets the current amount of song buffered
	 * @return current buffer, one million is one second
	 */
	public static long getBuffered(){
		return internalPlayer.getCurrentBufferedTimeMcsec();
	}

	/**
	 * Adds a song to the front of the queue, the immediately switches to it (after that song ends, playback will resume from the top of the queue)
	 * @param song JSONObject of the song to play
	 */
	public static void playSong(JSONObject song){
		currentQueue.add(0, song);
		new Thread(){
			JSONObject song;
			public Thread init(JSONObject songIn){
				song=songIn;
				return this;
			}
			public void run(){
				try{
					internalPlayer.pauseSync();
					if(song.getBoolean("streamable")){
						internalPlayer.setSourceLocation(song.getString("stream_url")+"?client_id="+SoundCloudAPIManager.clientid);
					}else if(song.getBoolean("downloadable")){
						internalPlayer.setSourceLocation(song.getString("download_url")+"?client_id="+SoundCloudAPIManager.clientid);
					}
					internalPlayer.seek(0);
					internalPlayer.playSync();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}.init(song).start();

	}

	/**
	 * Clears the queue of songs, does not remove currently playing song.
	 */
	public static void clearQueue(){
		if (currentQueue.size()<2){
			return;
		}
		while(currentQueue.size()>1){
			currentQueue.remove(1);
		}
	}


	/**
	 * Stops play of the current song in the queue and moves to the next one, if it exists. This is a non-blocking call.
	 */
	public static void playNext(){
		new Thread(){
			public void run(){
				synchronized(internalPlayer){
					if(currentQueue.size()>1){
						try{
							currentQueue.remove(0);
							internalPlayer.pauseSync();
							JSONObject song=currentQueue.get(0);
							if(song.getBoolean("streamable")){
								internalPlayer.setSourceLocation(song.getString("stream_url")+"?client_id="+SoundCloudAPIManager.clientid);
							}else if(song.getBoolean("downloadable")){
								internalPlayer.setSourceLocation(song.getString("download_url")+"?client_id="+SoundCloudAPIManager.clientid);
							}
							internalPlayer.seek(0);
							internalPlayer.playSync();
							for(int i=0;i<100;i++){
								if(onSongEnd[i]!=null){
									onSongEnd[i].run();
								}
							}
						}catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}
	
	/**
	 * Removes a song from the queue, removing the currently playing song is equivalent to {@link Mp3Player#playNext()}
	 * @param index the index of the song to remove, 0 is current song, 1 is the next one, so on and so forth.
	 */
	public static void removeSong(int index){
		if(index==0 && currentQueue.size() > 1){
			playNext();
		}
		else{
			currentQueue.remove(index);
		}
	}
	
	/**
	 * Swaps the given index with the song that is before it in the queue, if given index 1, will cause a change in the currently playing song.
	 * @param index must be between (inclusive) 1 and the index of the last song
	 */
	public static void swapUp(int index){
		if(index<1||index>(currentQueue.size()-1)){
			throw new IllegalArgumentException("Index is less than 1 or greater than the index of the last song");
		}
		if(index==1){
			JSONObject song=currentQueue.get(1);
			removeSong(1);
			playSong(song);
		}else{
			JSONObject song=currentQueue.get(index);
			currentQueue.set(index,currentQueue.get(index-1));
			currentQueue.set(index-1, song);
		}
	}
}
