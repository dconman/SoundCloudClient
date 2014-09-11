package frontend;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import backend.HTTPException;
import backend.SoundCloudAPIManager;

public class UserInfoBox {

	/**
	 * Creates and shows shell for displaying info on a song.
	 * @param userInfo A UserInfo object that contains the info to be displayed.
	 * @author Aaron and Philip
	 */
	private static int totalHeight = 8;

	private final static Device device = Display.getCurrent ();
	private final static Color red = device.getSystemColor(SWT.COLOR_BLUE);
	private final static Color magenta = device.getSystemColor(SWT.COLOR_DARK_MAGENTA);

	public static void openInfoBox(long UserId){
		String baseURLString = "http://api.soundcloud.com/users/" + UserId ;
		String clientid = backend.SoundCloudAPIManager.clientid;
		final String userRequest 				= baseURLString + 					  ".json?" + "client_id=" + clientid;
		final String userTracksRequest 		= baseURLString + "/tracks" 		+ ".json?" + "client_id=" + clientid;
		final String userPlaylistsRequest 	= baseURLString + "/playlists"	 	+ ".json?" + "client_id=" + clientid;
		final String userFavoritesRequest 	= baseURLString + "/favorites" 		+ ".json?" + "client_id=" + clientid;


		//backend.SoundCloudAPIManager.getUser(UserId);
		final Shell shell = new Shell(frontend.SoundCloudGUI.mainShell, SWT.TITLE | SWT.RESIZE |SWT.CLOSE);

		shell.setText("User Info");
		shell.setMinimumSize(300, 300);
		shell.setSize(500, 500);
		shell.setLayout(new GridLayout(2, false));
		shell.setLayout(new FillLayout());
		
		shell.addListener(SWT.Dispose, new Listener(){ //Runs each time this shell's dispose function is called.
			public void handleEvent(Event e){
				synchronized (frontend.SoundCloudGUI.InfoBoxes) {
					frontend.SoundCloudGUI.InfoBoxes.remove(shell);
				}
			}
		});
		final ExpandBar bar = new ExpandBar (shell, SWT.V_SCROLL);
		System.out.print(bar.getStyle() + "\n");
		System.out.print(bar.getBackgroundMode()+ "\n");
		System.out.print(bar.getBackground()+ "\n");
		//bar.
		//bar.setBackground(device.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		synchronized (frontend.SoundCloudGUI.InfoBoxes) {
			frontend.SoundCloudGUI.InfoBoxes.add(shell);
		}

		shell.open();

		final ExpandItem expandItem1 = new ExpandItem (bar, SWT.FILL, 0);
		final Composite composite1 = new Composite (bar, SWT.NONE);
		expandItemInit("General Info", bar,expandItem1, composite1,2);
		final ExpandItem expandItem2 = new ExpandItem (bar, SWT.FILL, 1);
		final Composite composite2 = new Composite (bar, SWT.NONE);
		expandItemInit("Tracks", bar,expandItem2, composite2, 3);
		final ExpandItem expandItem3 = new ExpandItem (bar, SWT.FILL, 2);
		final Composite composite3 = new Composite (bar, SWT.NONE);
		expandItemInit("Favorites", bar,expandItem3, composite3, 3);
		final ExpandItem expandItem4 = new ExpandItem (bar, SWT.FILL, 3);
		final Composite composite4 = new Composite (bar, SWT.NONE);
		expandItemInit("Playlists", bar,expandItem4, composite4, 3);
		//ITEM 1-------------------------------------------------------------------------------------------------------------------

		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					final JSONObject generalInfo = new JSONObject(SoundCloudAPIManager.executeHTTPRequest("GET", userRequest,null));

					//userInfo.setGeneralInfo(generalInfo);
					Display.getDefault().asyncExec(new Runnable(){

						@Override
						public void run() {

							
							String myString =  generalInfo.getString("username");
							addItemToComposite("Username:",myString,composite1);
							addURLToComposite("SoundCloud URL:",generalInfo.getString("permalink_url"), composite1);
							
							if(generalInfo.get("country") != JSONObject.NULL)
							{
							
								addItemToComposite("Country:",generalInfo.getString("country"),composite1);
							}
							
							if(generalInfo.get("full_name") != JSONObject.NULL)
							{
								addItemToComposite("Full Name:",generalInfo.getString("full_name"),composite1);
							}
							
							if(generalInfo.get("city") != JSONObject.NULL)
							{
								addItemToComposite("City:",generalInfo.getString("city"),composite1);
							}
							
							if(generalInfo.get("description") != JSONObject.NULL)
							{
								addItemToComposite("Description:",generalInfo.getString("description"),composite1);
							}
							
							if(generalInfo.get("website") != JSONObject.NULL)
							{
								addURLToComposite("Website URL:",generalInfo.getString("website"), composite1);
							}
							
							addItemToComposite("Followers:",generalInfo.getInt("followers_count")+"", composite1);

							expandItem1.setHeight(composite1.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
							expandItem1.setControl(composite1);
							org.eclipse.swt.graphics.Point size = composite1.computeSize(bar.getClientArea().width - 8,SWT.DEFAULT);
							expandItem1.setHeight(totalHeight + size.y);
							expandItem1.setExpanded(true);
						}});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HTTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}}.start();




			//ITEM 2-------------------------------------------------------------------------------------------------------------------

			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						final JSONArray tracks = new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userTracksRequest, null));

						//userInfo.setGeneralInfo(generalInfo);
						Display.getDefault().asyncExec(new Runnable(){

							@Override
							public void run() {

								if(tracks.length()==0)
								{

									Label label = new Label (composite2, SWT.NONE);
									label.setForeground(red);
									label.setText("NO TRACKS FOUND");
								}
								for(int i = 0; (i < 10) && (i < tracks.length()) ; i ++)//for each json object
								{
									JSONObject currentTrack = (JSONObject) tracks.get(i);
									addTrackToComposite(currentTrack,composite2);
								}	

								expandItem2.setHeight(composite2.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
								expandItem2.setControl(composite2);
								org.eclipse.swt.graphics.Point size = composite2.computeSize(bar.getClientArea().width - 8,SWT.DEFAULT);
								expandItem2.setHeight(totalHeight + size.y);
							}});
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (HTTPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}}.start();



				//ITEM 3-------------------------------------------------------------------------------------------------------------------

				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {
							final JSONArray favorites 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userFavoritesRequest, 	null));

							//userInfo.setGeneralInfo(generalInfo);
							Display.getDefault().asyncExec(new Runnable(){

								@Override
								public void run() {


									JSONArray myfavorites = favorites;
									if(myfavorites.length()==0)
									{

										Label label = new Label (composite3, SWT.NONE);
										label.setForeground(red);
										label.setText("NO FAVORITES FOUND");
									}
									for(int i = 0; (i < 10) && (i < myfavorites.length()) ; i ++)//for each json object
									{
										JSONObject currentTrack = (JSONObject) myfavorites.get(i);
										addTrackToComposite(currentTrack,composite3);
									}

									expandItem3.setHeight(composite3.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
									expandItem3.setControl(composite3);
									org.eclipse.swt.graphics.Point size = composite3.computeSize(bar.getClientArea().width - 8,SWT.DEFAULT);
									expandItem3.setHeight(totalHeight + size.y);
								}});
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (HTTPException e) {
							// TODO Auto-generated catch block
							System.out.print(e.getHTTPcode());
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}}.start();
					//item1.setImage(image);

					//ITEM 4-------------------------------------------------------------------------


					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub

							try {
								final JSONArray playlists 	= new JSONArray (SoundCloudAPIManager.executeHTTPRequest("GET", userPlaylistsRequest, 	null));

								//userInfo.setGeneralInfo(generalInfo);
								Display.getDefault().asyncExec(new Runnable(){

									@Override
									public void run() {


										if(playlists.length()==0)
										{

											Label label = new Label (composite4, SWT.NONE);
											label.setForeground(red);
											label.setText("NO PLAYLISTS FOUND");
										}
										for(int i = 0; (i < 10) && (i < playlists.length()) ; i ++)//for each json object
										{
											JSONObject currentPlaylist = (JSONObject) playlists.get(i);
											addPlaylistToComposite(currentPlaylist,composite4);
										}

										expandItem4.setHeight(composite4.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
										expandItem4.setControl(composite4);
										org.eclipse.swt.graphics.Point size = composite4.computeSize(bar.getClientArea().width - 8,SWT.DEFAULT);
										expandItem4.setHeight(totalHeight + size.y);
									}});
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (HTTPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}}.start();

	}

	private static void addPlaylistToComposite(final JSONObject playlist, Composite comp) {

		final JSONArray tracks = playlist.getJSONArray("tracks");

		Label label = new Label (comp, SWT.RESIZE| SWT.WRAP);
		label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		label.setText(playlist.getString("title"));

		final Label addToQueueLabel = new Label (comp, SWT.NONE|SWT.BOLD);
		addToQueueLabel.setForeground(red);
		addToQueueLabel.setText("Add to Queue");

		Label numTrackslabel = new Label (comp, SWT.NONE|SWT.BOLD);
		//label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		numTrackslabel.setText(tracks.length() + " tracks");

		addToQueueLabel.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				addToQueueLabel.setForeground(magenta);

			}
		});
		addToQueueLabel.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				addToQueueLabel.setForeground(red);
			}
		});
		addToQueueLabel.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				for(int i = 0; i < tracks.length(); i++)
				{
					JSONObject currentTrack = (JSONObject) tracks.get(i);
					UIQueue.addToQueue(currentTrack);
				}
			}
		});			

	}

	private static void addResizeMultilineEvent(final ExpandBar bar, final ExpandItem item, final Composite comp)
	{
		bar.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				org.eclipse.swt.graphics.Point size = comp.computeSize(bar.getClientArea().width - 8,SWT.DEFAULT);
				item.setHeight(totalHeight + size.y);
			}
		});
	}
	private static void expandItemInit(String titleText,ExpandBar bar,ExpandItem item, Composite comp, int col)
	{

		item.setText(titleText);
		addResizeMultilineEvent(bar, item, comp);

		//comp.setBackground(device.getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout (col, false);
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 3;
		layout.verticalSpacing = 3;
		comp.setLayout(layout);	


	}
	private static void addItemToComposite(String labelText, String labelData, final Composite comp)
	{

		Label label = new Label (comp, SWT.NONE);
		label.setText(labelText);

		label = new Label (comp, SWT.RESIZE| SWT.WRAP);
		label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		label.setForeground(red);
		label.setText(labelData);

	}
	private static void addTrackToComposite(final JSONObject track, final Composite comp)
	{

		Label label = new Label (comp, SWT.RESIZE| SWT.WRAP);
		label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		label.setText(track.getString("title"));

		final Label addToQueueLabel = new Label (comp, SWT.NONE|SWT.BOLD);
		addToQueueLabel.setForeground(red);
		addToQueueLabel.setText("Add to Queue");

		addToQueueLabel.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				addToQueueLabel.setForeground(magenta);

			}
		});
		addToQueueLabel.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				addToQueueLabel.setForeground(red);
				//urllabel.setText(link);
			}
		});
		addToQueueLabel.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				UIQueue.addToQueue(track);
			}
		});

		final Label trackInfoLabel = new Label (comp, SWT.NONE);
		trackInfoLabel.setForeground(red);
		trackInfoLabel.setText("Info");

		trackInfoLabel.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				trackInfoLabel.setForeground(magenta);

			}
		});
		trackInfoLabel.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				trackInfoLabel.setForeground(red);
				//urllabel.setText(link);
			}
		});
		trackInfoLabel.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				frontend.TrackInfo.openTrackInfoBox(track);
			}
		});
	}
	private static void addURLToComposite(String labelText, String urlString, final Composite comp)
	{

		Label urlLabelText = new Label (comp, SWT.NONE );
		urlLabelText.setText(labelText);
		// With this in addition, it will wrap

		final Label urlLabelData = new Label (comp, SWT.RESIZE| SWT.WRAP);
		urlLabelData.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
		urlLabelData.setForeground(red);
		final String link = urlString;
		urlLabelData.setText(link);


		urlLabelData.addListener(SWT.MouseEnter, new Listener() {
			public void handleEvent(Event e) {
				urlLabelData.setForeground(magenta);

			}
		});
		urlLabelData.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				urlLabelData.setForeground(red);
				//urllabel.setText(link);
			}
		});
		urlLabelData.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				Program.launch(link);
			}
		});

	}

}
