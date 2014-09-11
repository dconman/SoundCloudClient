package frontend;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONObject;

import backend.SoundCloudAPIManager;


public class TrackInfo {
	/**
	 * Creates and shows shell for displaying info on a song.
	 * @param JSONObject A JSONObject object that contains the info to be displayed.
	 * @author Philip
	 */
	public static void openTrackInfoBox(final JSONObject songInfo){
		final Shell shell = new Shell(SoundCloudGUI.mainShell, SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
		shell.setLayout(new GridLayout());
		shell.setText("Track Info");
		shell.addListener(SWT.Dispose, new Listener(){ //Runs each time this shell's dispose function is called.
			public void handleEvent(Event e){
				synchronized (SoundCloudGUI.InfoBoxes) {
					SoundCloudGUI.InfoBoxes.remove(shell);
				}
			}
		});

		ScrolledComposite mainScroll = new ScrolledComposite(shell, SWT.BORDER |  SWT.V_SCROLL);
		mainScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    final Composite innerScroll = new Composite(mainScroll, SWT.WRAP);
	    innerScroll.setLayout(new GridLayout(1, true));
	    

		Label albumArt=new Label(innerScroll,SWT.NONE);
		Label titleLabel=new Label(innerScroll,SWT.WRAP);
		final Label artistLabel=new Label(innerScroll,SWT.WRAP);
		Label genreLabel=new Label(innerScroll,SWT.WRAP);
		Label durationLabel=new Label(innerScroll,SWT.WRAP);
		Composite actionComposite=new Composite(shell,SWT.NONE);
		Button playButton=new Button(actionComposite,SWT.PUSH);
		Button enQueueButton=new Button(actionComposite,SWT.PUSH);		
		
		{
			albumArt.setLayoutData(new GridData(100, 100));
			SoundCloudGUI.getImage(albumArt, songInfo.optString("artwork_url", songInfo.getJSONObject("user").optString("avatar_url")), 100, 100, false);
		}
		
		{
			titleLabel.setText(songInfo.getString("title"));
			titleLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
			FontData fontData = titleLabel.getFont().getFontData()[0];
			Font font = new Font(shell.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
			titleLabel.setFont(font);
		}
		
		{
			artistLabel.setText(songInfo.getJSONObject("user").getString("username"));
			
			artistLabel.addListener(SWT.MouseEnter, new Listener() {
				public void handleEvent(Event e) {
					artistLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA));

				}
			});
			artistLabel.addListener(SWT.MouseExit, new Listener() {
				public void handleEvent(Event e) {
					artistLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

				}
			});
			artistLabel.addListener(SWT.MouseUp, new Listener() {
				public void handleEvent(Event e) {
					UserInfoBox.openInfoBox(songInfo.getLong("user_id"));
				}
			});
		}
		
		{
			genreLabel.setText(songInfo.optString("genre", ""));
		}
		
		{
			int milliseconds = songInfo.getInt("duration");
			int seconds = (milliseconds / 1000) % 60 ;
			int minutes = ((milliseconds / (1000*60)) % 60);
			int hours   = ((milliseconds / (1000*60*60)) % 24);
			String time = "%02d:%02d";
			if(hours > 0){
				time += ":%02d";
				durationLabel.setText(String.format(time, hours, minutes, seconds));
			}
			else{
				durationLabel.setText(String.format(time, minutes, seconds));
			}
		}
		
		{
			actionComposite.setLayout(new RowLayout());
		}
			
		{			
			RowData data = new RowData();
			playButton.setLayoutData(data);
			playButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e){
					UIQueue.moveToTop(songInfo);
				}
			});
			playButton.setText("Play");
		}
		
		{
			RowData data = new RowData();
			enQueueButton.setLayoutData(data);
			enQueueButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e){
					UIQueue.addToQueue(songInfo);
				}
			});
			enQueueButton.setText("Add to queue");
		}		
		
		if(SoundCloudAPIManager.isLoggedIn() == true){
			final Button likeButton=new Button(actionComposite,SWT.PUSH);
			RowData data = new RowData();
			likeButton.setLayoutData(data);
			if(songInfo.has("user_favorite") == false){
				new Thread(){
					int trackid;
					String clientid, authid;
					public void run(){
						String URL 	= "https://api.soundcloud.com/tracks/" + trackid
									+ ".json?"
									+ "&client_id=" + clientid
									+ "&oauth_token=" + authid;
						try{
							JSONObject temp = new JSONObject (SoundCloudAPIManager.executeHTTPRequest("GET", URL, null));
							if(temp.getBoolean("user_favorite")){
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										likeButton.setText("Unlike");
										shell.layout();
									}
								});
							}
							else{
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										likeButton.setText("Like");
										shell.layout();
									}
								});
							}
						}catch(IOException e){
							e.printStackTrace();
						}
					}
					public Thread init(int trackid, String clientid, String authid){
						this.trackid = trackid;
						this.clientid = clientid;
						this.authid = authid;	
						return this;
					}
				}.init(songInfo.getInt("id"), SoundCloudAPIManager.clientid, SoundCloudAPIManager.authid).start();
			}
			else{
				if(songInfo.getBoolean("user_favorite") == false){
					likeButton.setText("Like");
				}
				else{
					likeButton.setText("Unlike");
				}
			}
			likeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					new Thread(){
						public void run(){
							try{
								if(SoundCloudAPIManager.toggleTrackLike(songInfo.getLong("id")) == true){
									Display.getDefault().asyncExec(new Runnable() {
										@Override
										public void run() {
											likeButton.setText("Unlike");
											shell.layout(true);
										}
									});
								}
								else{
									Display.getDefault().asyncExec(new Runnable() {
										@Override
										public void run() {
											likeButton.setText("Like");
										}
									});
								}
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}.start();
				}
			});
		}
	
		
	    mainScroll.setContent(innerScroll);
	    mainScroll.setExpandHorizontal(true);
	    mainScroll.setExpandVertical(true);
	    mainScroll.setMinSize(innerScroll.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    
		synchronized (SoundCloudGUI.InfoBoxes) {
			SoundCloudGUI.InfoBoxes.add(shell);
		}
		shell.pack();
		shell.open();
	}
}
