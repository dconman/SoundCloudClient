package frontend;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import maryb.player.PlayerState;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONException;
import org.json.JSONObject;

import backend.HTTPException;
import backend.Mp3Player;
import backend.SoundCloudAPIManager;


public class SoundCloudGUI {

	public static Shell mainShell;
	static Shell searchShell;
	static Shell queueShell;
	private static Label popUp;
	static MenuItem menuFileUpload;
	static final String MENU_FILE_UPLOAD_TEXT = "&Upload a Track\tCtrl+U";
	static MenuItem menuLogin;
	static final String MENU_LOGIN_TEXT = "&Log in\tCtrl+L";
	static final String MENU_LOGOUT_TEXT = "&Log out\tCtrl+L";
	static MenuItem menuSearch;
	static final String MENU_SEARCH_SHOW = "Show &Search\tCtrl+S";
	static final String MENU_SEARCH_HIDE = "Hide &Search\tCtrl+S";
	static MenuItem menuQueue;
	static final String MENU_QUEUE_SHOW = "Show &Queue\tCtrl+Q";
	static final String MENU_QUEUE_HIDE = "Hide &Queue\tCtrl+Q";
	static MenuItem menuTrackInfo;
	static final String MENU_TRACK_INFO_TEXT = "Get Current Track &Info\tCtrl+I";
	static MenuItem menuUserInfo;
	static final String MENU_USER_INFO_TEXT = "UserInfoBox\t"/*Ctrl+U"*/;
	static MenuItem menuPlayPause;
	static final String MENU_PLAY_PAUSE_TEXT = "&Play/Pause\tCtrl+P";
	static MenuItem menuNext;
	static final String MENU_NEXT_TEXT = "&Next Track\tCtrl+N";
	static MenuItem menuCloseBoxes;
	static final String MENU_CLOSE_BOXES_TEXT = "Close Info &Boxes\tCtrl+B";
	static MenuItem menuHideAll;
	static final String MENU_HIDE_ALL_TEXT = "&Hide All Other Windows\tCtrl+H";
	static MenuItem menuExit;
	static final String MENU_EXIT_TEXT = "E&xit Application\tCtrl+X";
	private static Timer seekTimer = null;
	static Image currentImage = null;
	static Image play = null;
	static Image pause = null;
	static Image next = null;

	static ScrolledComposite queueList;
	public static List<Shell> InfoBoxes = Collections.synchronizedList(new ArrayList<Shell>());;

	public static void startGUI() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		Display display = new Display ();
		Display.setAppName("SoundCloud");
		mainShell = new Shell(Display.getCurrent(), SWT.CLOSE | SWT.MIN);
		mainShell.setText("SoundCloud Player");
		currentImage = resize(new Image(Display.getCurrent(),SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/logo_orange_64x64.png")), 100, 100, true);
		play = new Image(Display.getCurrent(), SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/play_50x50.png"));
		pause = new Image(Display.getCurrent(), SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/pause_50x50.png"));
		next = new Image(Display.getCurrent(), SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/next_50x50.png"));
		mainShell.setImage(new Image(Display.getCurrent(),SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/logo_orange_64x64.png")));

		mainShell.setSize(600, 210);
		mainShell.setLocation((gd.getDisplayMode().getWidth()-mainShell.getSize().x)/2, gd.getDisplayMode().getHeight()/12);
		populateMainShell();
		SearchDialog.createSearchShell();
		UIQueue.populateQueueShell();

		mainShell.open ();



		while (!mainShell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		seekTimer.cancel();
		seekTimer = null;

		for(int i = 0; i < 100; i++) {
			Mp3Player.removeOnBuffer(i);
			Mp3Player.removeOnPlay(i);
			Mp3Player.removeOnPause(i);
			Mp3Player.removeOnSongEnd(i);
		}
		try {
			Mp3Player.stopSync();
		} catch (InterruptedException e) {

		}
		SoundCloudAPIManager.logout();

		display.close ();
	}

	/**
	 * Populates the Main Shell
	 * 
	 * @author Dawson
	 * 
	 */
	private static void populateMainShell() {
		mainShell.setLayout(new GridLayout(2, false));

		Listener getSongInfo =  new Listener(){

			@Override
			public void handleEvent(Event event) {

				new Thread(){
					public void run(){
						JSONObject currentSong = Mp3Player.getCurrentlyPlaying();
						Display.getDefault().asyncExec(new Runnable(){

							private JSONObject currentSong;

							@Override
							public void run() {
								if (currentSong != null)
									TrackInfo.openTrackInfoBox(currentSong);

							}

							public Runnable init(JSONObject currentSong) {
								this.currentSong = currentSong;
								return this;
							}}.init(currentSong));
					}
				}.start();

			}};

			// XXX MenuBar
			Menu bar = new Menu(mainShell, SWT.BAR);

			mainShell.setMenuBar(bar);

			MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
			{
				fileItem.setText("&File");

				Menu submenu = new Menu(mainShell, SWT.DROP_DOWN);
				fileItem.setMenu(submenu);
				menuLogin = new MenuItem(submenu, SWT.PUSH);
				menuLogin.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event e) {
						if (SoundCloudAPIManager.isLoggedIn()) {
							logout();
						} else {
							logIn();
						}
					}
				});
				menuLogin.setText(MENU_LOGIN_TEXT);
				menuLogin.setAccelerator(SWT.MOD1 + 'L');

				// This is just for testing purposes until a right click listener is
				// made. WILL REMOVE
				menuTrackInfo = new MenuItem(submenu, SWT.PUSH);

				menuTrackInfo.addListener(SWT.Selection, getSongInfo);
				menuTrackInfo.setText(MENU_TRACK_INFO_TEXT);
				menuTrackInfo.setAccelerator(SWT.MOD1 + 'I');
				

				menuFileUpload = new MenuItem(submenu, SWT.PUSH);
				menuFileUpload.setText(MENU_FILE_UPLOAD_TEXT);
				menuFileUpload.setAccelerator(SWT.MOD1 + 'U');
				menuFileUpload.setEnabled(false);
				menuFileUpload.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event e) {
						if (!SoundCloudAPIManager.isLoggedIn()) return;

						FileUploadDialog.createFileUploadDialog();
					}
				});

			}

			MenuItem playerItem = new MenuItem(bar, SWT.CASCADE);
			{
				playerItem.setText("&Player");

				Menu submenu = new Menu(mainShell, SWT.DROP_DOWN);
				playerItem.setMenu(submenu);
				menuPlayPause = new MenuItem(submenu, SWT.PUSH);
				menuPlayPause.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event e) {

						new Thread() {
							public void run() {
								if (Mp3Player.getState() != PlayerState.PLAYING) {
									Mp3Player.play();
								} else {
									Mp3Player.pause();
								}
							}
						}.start();

					}
				});
				menuPlayPause.setText(MENU_PLAY_PAUSE_TEXT);
				menuPlayPause.setAccelerator(SWT.MOD1 + 'P');

				menuNext = new MenuItem(submenu, SWT.PUSH);
				menuNext.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event e) {
						new Thread() {
							public void run() {
								Mp3Player.playNext();
							}
						}.start();
					}
				});
				menuNext.setText(MENU_NEXT_TEXT);
				menuNext.setAccelerator(SWT.MOD1 + 'N');

			}

			MenuItem windowItem = new MenuItem(bar, SWT.CASCADE);

			windowItem.setText("&Windows");

			Menu submenu = new Menu(mainShell, SWT.DROP_DOWN);
			windowItem.setMenu(submenu);
			menuSearch = new MenuItem(submenu, SWT.PUSH);
			menuSearch.addListener(SWT.Selection, new Listener() {
				private MenuItem menuSearch;

				@Override
				public void handleEvent(Event e) {
					if (!searchShell.getVisible()) {
						searchShell.setVisible(true);
						searchShell.setFocus();
						menuSearch.setText(MENU_SEARCH_HIDE);

					} else {
						searchShell.setVisible(false);
						menuSearch.setText(MENU_SEARCH_SHOW);
					}

				}

				public Listener init(MenuItem menuSearch) {
					this.menuSearch = menuSearch;
					return this;
				}
			}.init(menuSearch));

			menuSearch.setText(MENU_SEARCH_SHOW);
			menuSearch.setAccelerator(SWT.MOD1 + 'S');

			menuQueue = new MenuItem(submenu, SWT.PUSH);
			menuQueue.addListener(SWT.Selection, new Listener() {
				private MenuItem menuQueue;

				@Override
				public void handleEvent(Event e) {
					if (!queueShell.getVisible()) {
						queueShell.setVisible(true);
						UIQueue.refresh();
						queueShell.setFocus();
						menuQueue.setText(MENU_QUEUE_HIDE);

					} else {
						queueShell.setVisible(false);
						menuQueue.setText(MENU_QUEUE_SHOW);
					}

				}

				public Listener init(MenuItem menuQueue) {
					this.menuQueue = menuQueue;
					return this;
				}
			}.init(menuQueue));
			menuQueue.setText(MENU_QUEUE_SHOW);
			menuQueue.setAccelerator(SWT.MOD1 + 'Q');

			menuCloseBoxes = new MenuItem(submenu, SWT.PUSH);
			menuCloseBoxes.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event e) {
					clearInfoBoxes();
				}

			});
			menuCloseBoxes.setText(MENU_CLOSE_BOXES_TEXT);
			menuCloseBoxes.setAccelerator(SWT.MOD1 + 'B');
			new MenuItem(submenu, SWT.SEPARATOR);

			menuHideAll = new MenuItem(submenu, SWT.PUSH);
			menuHideAll.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event e) {
					searchShell.setVisible(false);
					menuSearch.setText(MENU_SEARCH_SHOW);
					queueShell.setVisible(false);
					menuQueue.setText(MENU_QUEUE_SHOW);
					clearInfoBoxes();
				}

			});
			menuHideAll.setText(MENU_HIDE_ALL_TEXT);
			menuHideAll.setAccelerator(SWT.MOD1 + 'H');

			menuExit = new MenuItem(submenu, SWT.PUSH);
			menuExit.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event e) {
					mainShell.close();

				}

			});
			menuExit.setText(MENU_EXIT_TEXT);
			menuExit.setAccelerator(SWT.MOD1 + 'X');

			// XXX Box For Current Song


			Composite currentSongBox = new Composite(mainShell, 0);
			{
				GridData gridData = new GridData();
				gridData.grabExcessVerticalSpace = true;
				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;

				currentSongBox.setLayoutData(gridData);
				currentSongBox.setLayout(new GridLayout(4, false));
				currentSongBox.setToolTipText("Double Click for more Information");
				currentSongBox.addListener(SWT.MouseDoubleClick,getSongInfo);
			}

			Label lblAlbumArt = new Label(currentSongBox, 0);
			{
				lblAlbumArt.setImage(resize(currentImage, 64, 64, false));
				lblAlbumArt.pack();
				GridData gridData = new GridData();
				gridData.heightHint = 64;
				gridData.widthHint = 64;
				lblAlbumArt.setLayoutData(gridData);

				Shell popUpShell = new Shell(mainShell, 0);
				popUp = new Label(popUpShell, 0);
				popUp.setImage(currentImage);
				popUp.pack();
				popUpShell.setVisible(false);
				popUpShell.pack();
				lblAlbumArt.addMouseTrackListener(new MouseTrackListener() {

					private Shell popUpShell;

					@Override
					public void mouseEnter(MouseEvent e) {

					}

					public MouseTrackListener init(Shell popUpShell) {
						this.popUpShell = popUpShell;
						return this;
					}

					@Override
					public void mouseExit(MouseEvent e) {

					}

					@Override
					public void mouseHover(MouseEvent e) {
						Label source = (Label) e.getSource();
						popUpShell.setLocation(source.toDisplay(0, 0));
						popUpShell.open();
						popUpShell.setVisible(true);
						popUp.addMouseTrackListener(new MouseTrackListener() {

							@Override
							public void mouseEnter(MouseEvent e) {

							}

							@Override
							public void mouseExit(MouseEvent e) {
								Label source = (Label) e.getSource();
								source.getShell().setVisible(false);
							}

							@Override
							public void mouseHover(MouseEvent e) {

							}
						});

					}
				}.init(popUpShell));
				lblAlbumArt.setToolTipText("Double Click for more Information");
				lblAlbumArt.addListener(SWT.MouseDoubleClick,getSongInfo);

			}
			Composite songInfoBox = new Composite(currentSongBox, 0);
			{
				GridData gridData = new GridData();

				gridData.horizontalAlignment = GridData.FILL;
				songInfoBox.setLayoutData(gridData);
				songInfoBox.setLayout(new RowLayout(SWT.VERTICAL));
				songInfoBox.setToolTipText("Double Click for more Information");
				songInfoBox.addListener(SWT.MouseDoubleClick,getSongInfo);
			}
			Label lblSongTitle = new Label(songInfoBox, 0);
			{
				FontData[] fD = lblSongTitle.getFont().getFontData();
				fD[0].setHeight(fD[0].getHeight() + 6);
				lblSongTitle.setFont(new Font(Display.getCurrent(), fD[0]));
				lblSongTitle
				.setText("                                                                                    ");
				lblSongTitle.setToolTipText("Double Click for more Information");
				lblSongTitle.addListener(SWT.MouseDoubleClick,getSongInfo);

			}

			Label lblSongArtist = new Label(songInfoBox, 0);
			lblSongArtist.setToolTipText("Double Click for more Information");

			lblSongArtist.addListener(SWT.MouseDoubleClick,new Listener(){

				@Override
				public void handleEvent(Event event) {
					new Thread(){
						public void run(){

							Display.getDefault().asyncExec(new Runnable(){

								private long id;

								public Runnable init(long long1) {
									this.id = long1;
									return this;
								}

								@Override
								public void run() {
									UserInfoBox.openInfoBox(id);

								}}.init(Mp3Player.getCurrentlyPlaying().getLong("user_id")));
						}

					}.start();

				}});

			// XXX Buttons to open Windows and Volume
			Composite windowButtons = new Composite(mainShell, 0);
			{
				GridData gridData = new GridData();
				gridData.verticalSpan = 2;
				windowButtons.setLayoutData(gridData);
				windowButtons.setLayout(new RowLayout(SWT.VERTICAL));
			}
			Button btnSearch = new Button(windowButtons, SWT.PUSH | SWT.WRAP);
			{
				btnSearch.setText("Search\nTracks");
				btnSearch.addListener(SWT.Selection, new Listener() {
					private MenuItem menuSearch;

					@Override
					public void handleEvent(Event e) {
						if (!searchShell.getVisible()) {
							searchShell.setVisible(true);
							searchShell.setFocus();
							menuSearch.setText(MENU_SEARCH_HIDE);

						} else {
							searchShell.setVisible(false);
							menuSearch.setText(MENU_SEARCH_SHOW);
						}

					}

					public Listener init(MenuItem menuSearch) {
						this.menuSearch = menuSearch;
						return this;
					}
				}.init(menuSearch));
				btnSearch.setLayoutData(new RowData(64, 64));

			}
			Button btnQueue = new Button(windowButtons, SWT.PUSH | SWT.WRAP);
			{

				btnQueue.setText("Current\nQueue");
				btnQueue.setSize(64, 64);
				btnQueue.addListener(SWT.Selection, new Listener() {
					private MenuItem menuQueue;

					@Override
					public void handleEvent(Event e) {
						if (!queueShell.getVisible()) {
							queueShell.setVisible(true);
							UIQueue.refresh();
							queueShell.setFocus();
							menuQueue.setText(MENU_QUEUE_HIDE);

						} else {
							queueShell.setVisible(false);
							menuQueue.setText(MENU_QUEUE_SHOW);
						}

					}

					public Listener init(MenuItem menuQueue) {
						this.menuQueue = menuQueue;
						return this;
					}
				}.init(menuQueue));
				btnQueue.setLayoutData(new RowData(64, 64));

			}

			// XXX Controls
			Composite controlBox = new Composite(mainShell, 0);
			{
				GridData gridData = new GridData();
				gridData.grabExcessVerticalSpace = true;
				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				controlBox.setLayoutData(gridData);
				controlBox.setLayout(new FormLayout());

			}

			Button btnPlay = new Button(controlBox, SWT.PUSH);
			{
				FormData formData = new FormData(50, 50);
				formData.top = new FormAttachment(0, 0);
				btnPlay.setLayoutData(formData);
				btnPlay.setImage(play);
				btnPlay.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						new Thread() {
							public void run() {
								if (Mp3Player.getState() == PlayerState.PLAYING) {
									Mp3Player.pause();
								} else {
									if (Mp3Player.getQueue().length != 0) {
										Mp3Player.play();
									}
								}
							}
						}.start();

					}

				});
			}
			Button btnNext = new Button(controlBox, SWT.PUSH);
			Scale scaleSeek = new Scale(controlBox, SWT.HORIZONTAL);
			{
				FormData formData = new FormData(50, 50);
				formData.top = new FormAttachment(0, 0);
				formData.left = new FormAttachment(btnPlay, 10);
				btnNext.setLayoutData(formData);
				btnNext.setImage(next);
				btnNext.addSelectionListener(new SelectionAdapter() {

					private Scale scaleSeek;
					private Button btnPlay;
					private Label lblSongTitle;
					private Label lblSongArtist;
					private Label lblAlbumArt;

					@Override
					public void widgetSelected(SelectionEvent e) {
						new Thread() {
							public void run() {
								if (Mp3Player.getQueue().length > 1) {
									Mp3Player.playNext();
								} else if (Mp3Player.getQueue().length == 1) {

									Mp3Player.seek(Mp3Player.getCurrentlyPlaying().getLong("duration"));

								} else {
									currentImage = resize(new Image(Display.getDefault(),
											SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/logo_orange_64x64.png")),
											100, 100, true);
									final Image waveform = null;
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											if (lblAlbumArt.getImage() != null)
												lblAlbumArt.getImage().dispose();
											lblAlbumArt.setImage(resize(currentImage, 64, 64, false));
											popUp.setImage(currentImage);
											lblSongTitle
											.setText("                                                 ");
											lblSongTitle.pack();
											lblSongArtist
											.setText("                                                 ");
											lblSongArtist.pack();
											scaleSeek.setMaximum(0);
											scaleSeek.setBackgroundImage(waveform);
											btnPlay.setImage(play);
										}
									});
								}
							}
						}.start();

					}

					public SelectionListener init(Scale scaleSeek, Button btnPlay, Label lblSongTitle,
							Label lblSongArtist, Label lblAlbumArt) {
						this.scaleSeek = scaleSeek;
						this.btnPlay = btnPlay;
						this.lblSongTitle = lblSongTitle;
						this.lblSongArtist = lblSongArtist;
						this.lblAlbumArt = lblAlbumArt;
						return this;
					}

				}.init(scaleSeek, btnPlay, lblSongTitle, lblSongArtist, lblAlbumArt));
			}

			{
				scaleSeek.setMaximum(100);
				FormData formData = new FormData(385, 40);
				formData.top = new FormAttachment(0, 5);
				formData.left = new FormAttachment(btnNext, 10, 20);
				scaleSeek.setLayoutData(formData);
				SelectionAdapter seekControl = new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
					 Scale source = (Scale) e.getSource();
						new Thread() {
							private int i;

							public void run() {
								Mp3Player.seek(i);
							}

							public Thread init(int i) {
								this.i = i;
								return this;
							}
						}.init(source.getSelection() * 1000000).start();

					}

				};
				scaleSeek.addSelectionListener(seekControl);
				seekTimer = new Timer();
				seekTimer.scheduleAtFixedRate(new TimerTask() {

					private Scale scaleSeek;
					private SelectionAdapter seekControl;

					@Override
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {

							private Scale scaleSeek;
							private long position;
							private SelectionAdapter seekControl;

							public Runnable init(Scale scaleSeek, SelectionAdapter seekControl,
									long position) {
								this.scaleSeek = scaleSeek;
								this.seekControl = seekControl;
								this.position = position;
								return this;
							}

							@Override
							public void run() {
								scaleSeek.removeSelectionListener(seekControl);
								scaleSeek.setSelection((int) (position / 1000000));
								scaleSeek.addSelectionListener(seekControl);

							}

						}.init(scaleSeek, seekControl, Mp3Player.getPosition()));

					}

					public TimerTask init(Scale scaleSeek, SelectionAdapter seekControl) {
						this.scaleSeek = scaleSeek;
						this.seekControl = seekControl;
						return this;
					}
				}.init(scaleSeek, seekControl), 1000, 500);

				scaleSeek.addMouseListener(new MouseListener() {

					private SelectionAdapter seekControl;

					@Override
					public void mouseDoubleClick(MouseEvent e) {

					}

					public MouseListener init(SelectionAdapter seekControl) {
						this.seekControl = seekControl;
						return this;
					}

					@Override
					public void mouseDown(MouseEvent e) {
						seekTimer.cancel();

					}

					@Override
					public void mouseUp(MouseEvent e) {
						seekTimer = new Timer();
						seekTimer.scheduleAtFixedRate(new TimerTask() {

							private Scale scaleSeek;
							private SelectionAdapter seekControl;

							@Override
							public void run() {
								Display.getDefault().asyncExec(new Runnable() {

									private Scale scaleSeek;
									private long position;
									private SelectionAdapter seekControl;

									public Runnable init(Scale scaleSeek, SelectionAdapter seekControl,
											long position) {
										this.scaleSeek = scaleSeek;
										this.seekControl = seekControl;
										this.position = position;
										return this;
									}

									@Override
									public void run() {
										scaleSeek.removeSelectionListener(seekControl);
										scaleSeek.setSelection((int) (position / 1000000));
										scaleSeek.addSelectionListener(seekControl);

									}

								}.init(scaleSeek, seekControl, Mp3Player.getPosition()));

							}

							public TimerTask init(Scale scaleSeek, SelectionAdapter seekControl) {
								this.scaleSeek = scaleSeek;
								this.seekControl = seekControl;
								return this;
							}
						}.init((Scale) e.getSource(), seekControl), 1000, 500);

					}
				}.init(seekControl));
			}

			// XXX Listeners
			Mp3Player.addOnPlay(new Runnable() {

				private Scale scaleSeek;
				private Label lblSongArtist;
				private Label lblSongTitle;
				private Label lblAlbumArt;
				private Button btnPlay;

				@Override
				public void run() {

					Display.getDefault().asyncExec(new Runnable() {

						private Scale scaleSeek;
						private JSONObject currentSong;
						private Label lblAlbumArt;
						private Label lblSongTitle;
						private Label lblSongArtist;
						private Button btnPlay;

						@Override
						public void run() {

							currentImage.dispose();
							currentImage = resize(new Image(Display.getDefault(),
									SoundCloudGUI.class.getClassLoader().getResourceAsStream("frontend/images/logo_orange_64x64.png")), 100, 100,
									true);

							if (scaleSeek.getBackgroundImage() != null)
								scaleSeek.getBackgroundImage().dispose();
							lblAlbumArt.getImage().dispose();
							lblAlbumArt.setImage(resize(currentImage, 64, 64, false));
							popUp.setImage(currentImage);
							scaleSeek.setBackgroundImage(null);
							if (currentSong != null) {
								String title = "";
								try {
									title = currentSong.getString("title");
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								if (title.length() > 48) {
									lblSongTitle.setText(title.substring(0, 45) + "...");
								} else {
									lblSongTitle.setText(title);
								}
								lblSongTitle.pack();
								try {
									lblSongArtist.setText(currentSong.getJSONObject("user").getString(
											"username"));
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								lblSongArtist.pack();
								try {
									scaleSeek.setMaximum(currentSong.getInt("duration") / 1000);
								} catch (JSONException e1) {
									scaleSeek.setMaximum(100);
								}

								btnPlay.setImage(pause);

								new Thread() {

									private JSONObject currentSong;
									private Label lblAlbumArt;
									private Scale scaleSeek;
									private Image currentImage;

									@Override
									public void run() {
										ImageData waveform = null;
										ImageData art = null;
										try {
											waveform = new ImageData(new URL(currentSong
													.getString("waveform_url")).openStream());
										} catch (IOException e) {
											e.printStackTrace();
										} catch (JSONException e) {
											e.printStackTrace();
										}
										try {
											art = new ImageData(new URL(currentSong
													.optString("artwork_url",currentSong.getJSONObject("user")
															.optString("avatar_url"))).openStream());
										} catch (IOException e) {
											e.printStackTrace();
										} catch (JSONException e) {
											art = null;
										}
										Display.getDefault().asyncExec(new Runnable() {

											private Label lblAlbumArt;
											private Scale scaleSeek;
											private Image currentImage;
											private ImageData art;
											private ImageData waveform;

											@Override
											public void run() {
												if (waveform != null) {
													scaleSeek.setBackgroundImage(new Image(Display
															.getDefault(), waveform.scaledTo(385, 40)));
												}
												if (art != null) {
													currentImage = new Image(Display.getDefault(), art);
													Image artwork = new Image(Display.getDefault(), art
															.scaledTo(64, 64));
													lblAlbumArt.setImage(artwork);
													popUp.setImage(currentImage);

												}

											}

											public Runnable init(Label lblAlbumArt, Scale scaleSeek,
													Image currentImage, ImageData art,
													ImageData waveform) {
												this.lblAlbumArt = lblAlbumArt;
												this.scaleSeek = scaleSeek;
												this.currentImage = currentImage;
												this.art = art;
												this.waveform = waveform;
												return this;
											}
										}.init(lblAlbumArt, scaleSeek, currentImage, art, waveform));

									}

									public Thread init(JSONObject currentSong, Label lblAlbumArt,
											Scale scaleSeek, Image currentImage) {
										this.currentSong = currentSong;
										this.lblAlbumArt = lblAlbumArt;
										this.scaleSeek = scaleSeek;
										this.currentImage = currentImage;
										return this;
									}

								}.init(currentSong, lblAlbumArt, scaleSeek, currentImage).start();
							}

						}

						public Runnable init(Scale scaleSeek, Button btnPlay, Label lblAlbumArt,
								Label lblSongTitle, Label lblSongArtist, JSONObject currentSong) {

							this.scaleSeek = scaleSeek;
							this.btnPlay = btnPlay;
							this.lblAlbumArt = lblAlbumArt;
							this.lblSongTitle = lblSongTitle;
							this.lblSongArtist = lblSongArtist;
							this.currentSong = currentSong;
							return this;
						}
					}.init(scaleSeek, btnPlay, lblAlbumArt, lblSongTitle, lblSongArtist,
							Mp3Player.getCurrentlyPlaying()));

				}

				public Runnable init(Scale scaleSeek, Label lblAlbumArt, Label lblSongTitle,
						Label lblSongArtist, Button btnPlay) {

					this.scaleSeek = scaleSeek;
					this.lblAlbumArt = lblAlbumArt;
					this.lblSongTitle = lblSongTitle;
					this.lblSongArtist = lblSongArtist;
					this.btnPlay = btnPlay;
					return this;
				}
			}.init(scaleSeek, lblAlbumArt, lblSongTitle, lblSongArtist, btnPlay));
			Mp3Player.addOnPause(new Runnable() {

				private Button btnPlay;

				@Override
				public void run() {

					Display.getDefault().asyncExec(new Runnable() {

						private Button btnPlay;

						@Override
						public void run() {

							btnPlay.setImage(play);

						}

						public Runnable init(Button btnPlay) {

							this.btnPlay = btnPlay;

							return this;
						}
					}.init(btnPlay));

				}

				public Runnable init(Button btnPlay) {

					this.btnPlay = btnPlay;
					return this;
				}
			}.init(btnPlay));

	}

	/**
	 * Creates and shows login shell.
	 * 
	 * @author Philip
	 */
	private static void logIn() {
		final Shell shell = new Shell(mainShell, SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("Login");
		shell.setSize(300, 150);

		shell.setLayout(new GridLayout(2, false));

		Label lblUsername = new Label(shell, SWT.NONE);
		lblUsername.setText("Username");

		final Text txtUsername = new Text(shell, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label lblPassword = new Label(shell, SWT.NONE);
		lblPassword.setText("Password");

		final Text txtPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btnCancel = new Button(shell, SWT.PUSH);
		btnCancel.setText("Cancel");
		btnCancel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button btnLogin = new Button(shell, SWT.PUSH);
		btnLogin.setText("Login");
		btnLogin.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					String username;
					String password;

					public Thread init(String username, String password) {
						this.username = username;
						this.password = password;
						return this;
					}

					public void run() {
						try {
							if (SoundCloudAPIManager.login(username, password) == false) {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										Shell shellMsg = new Shell(shell, SWT.TITLE
												| SWT.PRIMARY_MODAL);
										shellMsg.setText("Login Failed!");

										MessageBox msg404 = new MessageBox(shellMsg);
										msg404.setMessage("You must enter a Username or Password.");
										msg404.open();
									}
								});
								return;
							} else {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										menuLogin.setText(MENU_LOGOUT_TEXT);
										shell.close();
										menuFileUpload.setEnabled(true);
									}
								});
								System.out.println("Logged In");

							}
						} catch (HTTPException e) {
							if (e.getHTTPcode() == 401) {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										Shell shellMsg = new Shell(shell, SWT.TITLE
												| SWT.PRIMARY_MODAL);
										shellMsg.setText("Login Failed!");

										MessageBox msg404 = new MessageBox(shellMsg);
										msg404.setMessage("Incorrect Username and/or Password");
										msg404.open();

									}
								});
							}
							return;
						} catch (IOException e) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									Shell shellMsg = new Shell(shell, SWT.TITLE | SWT.PRIMARY_MODAL);
									shellMsg.setText("Unexpected Error!");

									MessageBox msg404 = new MessageBox(shellMsg);
									msg404.setMessage("Unexpected Login Failure");
									msg404.open();
								}
							});
							return;
						}
					}
				}.init(txtUsername.getText(), txtPassword.getText()).start();
			}
		});

		TraverseListener enterListener = new TraverseListener() {
			private Button btnLogin;

			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					btnLogin.notifyListeners(SWT.Selection, new Event());
				}
			}

			public TraverseListener init(Button btnLogin) {
				this.btnLogin = btnLogin;
				return this;
			}
		}.init(btnLogin);

		txtUsername.addTraverseListener(enterListener);
		txtPassword.addTraverseListener(enterListener);
		shell.open();
	}

	/**
	 * Creates an shows shell for logging out.
	 * 
	 * @author Philip
	 */
	private static void logout() {
		Shell shellMsg = new Shell(mainShell, SWT.TITLE | SWT.APPLICATION_MODAL);
		shellMsg.setText("Logout");

		MessageBox msgLogout = new MessageBox(shellMsg, SWT.NO | SWT.YES);
		msgLogout.setMessage("Are you sure you want to logout?");
		int choice = msgLogout.open();
		if (choice == SWT.YES) {
			new Thread() {
				@Override
				public void run() {
					SoundCloudAPIManager.logout();
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							menuLogin.setText(MENU_LOGIN_TEXT);
							menuFileUpload.setEnabled(false);
						}
					});
					System.out.println("Logged Out");

				}
			}.start();
		}
		shellMsg.close();
	}
	/**
	 * Closes all open track info boxes
	 */
	private static void clearInfoBoxes() {
		while (InfoBoxes.size() > 0) {
			InfoBoxes.get(0).close();
		}
	}

	public static Image resize(Image image, int width, int height, boolean disposeOriginal) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);

		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width,
				height);
		gc.dispose();
		if (disposeOriginal) image.dispose(); // don't forget about me!
		return scaled;
	}

	public static void getImage(Label lblArt, String url, int width, int height,
			boolean disposeOriginal) {
		new Thread() {

			private Label lblArt;
			private String url;
			private int width;
			private int height;
			private boolean disposeOriginal;

			@Override
			public void run() {
				ImageData imageData = null;
				int attempts = 3;
				while(imageData == null && attempts > 0){
					try {
						imageData = new ImageData(new URL(url).openStream()).scaledTo(width, height);
					} catch (IOException e) {
						imageData = null;
					} catch (SWTException e) {

						imageData = null;
					}
					attempts--;
				}





				Display.getDefault().asyncExec(new Runnable() {

					private Label lblArt;
					private ImageData imageData;
					private boolean disposeOriginal;

					public Runnable init(Label lblArt, ImageData imageData, boolean disposeOriginal) {
						this.lblArt = lblArt; // this line sets this.lblArt
						// equal to the value stored in
						// the lblArt variable
						this.imageData = imageData;
						this.disposeOriginal = disposeOriginal;
						return this;
					}

					@Override
					public void run() {
						if (imageData != null&&!lblArt.isDisposed()) {
							if (disposeOriginal && !lblArt.getBackgroundImage().equals(null))
								lblArt.getBackgroundImage().dispose();

							lblArt.setImage(new Image(Display.getDefault(), imageData));
						}

					}
				}.init(lblArt, imageData, disposeOriginal));
			}

			public Thread init(Label lblArt, String url, int width, int height,
					boolean disposeOriginal) {
				this.lblArt = lblArt;
				this.url = url;
				this.width = width;
				this.height = height;
				this.disposeOriginal = disposeOriginal;
				return this;
			}
		}.init(lblArt, url, width, height, disposeOriginal).start();
	}
}