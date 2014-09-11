package frontend;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONObject;

import backend.Mp3Player;


public class UIQueue {

	public static Shell queueShell;
	private static SongComposite[] currentQueue = {};
	private static Composite c;
	public static int selected = -1;
	public static ScrolledComposite sc;
	public static Runnable refresher = new Runnable(){
		@Override
		public void run() {
			UIQueue.refresh();
		}

	};


	public static void populateQueueShell(){
		Shell mainShell = SoundCloudGUI.mainShell;
		SoundCloudGUI.queueShell = new Shell(mainShell, SWT.TITLE | SWT.BORDER | SWT.RESIZE);
		queueShell = SoundCloudGUI.queueShell;

		queueShell.setText("Queue");
		queueShell.setMinimumSize(200, 300);
		queueShell.setSize(mainShell.getSize().x/2 + 70, mainShell.getSize().y*2);
		queueShell.setLocation(mainShell.getSize().x + mainShell.getLocation().x+10, mainShell.getLocation().y);
		queueShell.setLayout(new GridLayout());

		sc = new ScrolledComposite(queueShell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				sc.setFocus();
			}
		});
		{
			sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

			c = new Composite(sc, SWT.NONE);
			c.setLayout(new GridLayout(1, true));
			sc.setContent(c);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(c.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		Composite queueButtons = new Composite(queueShell, 0);
		{
			GridData gridData = new GridData();
			gridData.verticalAlignment = GridData.END;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			queueButtons.setLayoutData(gridData);
			queueButtons.setLayout(new GridLayout(4, true));
		}
		Button moveUp = new Button(queueButtons, SWT.PUSH | SWT.WRAP);
		{
			GridData gridData = new GridData();
			gridData.heightHint = 30;
			gridData.widthHint = 80;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
			moveUp.setText("Up");
			moveUp.setLayoutData(gridData);
		}

		moveUp.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(selected == 0 || selected == -1)
					return;
				Mp3Player.swapUp(selected);
				selected--;
				Display.getDefault().asyncExec(refresher);
			}
		});

		Button moveDown = new Button(queueButtons, SWT.PUSH | SWT.WRAP);
		{
			GridData gridData = new GridData();
			gridData.heightHint = 30;
			gridData.widthHint = 80;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
			moveDown.setText("Down");
			moveDown.setLayoutData(gridData);
		}

		moveDown.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(selected == currentQueue.length - 1 || selected == -1)
					return;
				Mp3Player.swapUp(selected+1);
				selected++;
				Display.getDefault().asyncExec(refresher);
			}
		});

		Button moveToTop = new Button(queueButtons, SWT.PUSH | SWT.WRAP);
		{
			GridData gridData = new GridData();
			gridData.heightHint = 30;
			gridData.widthHint = 80;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
			moveToTop.setText("Play Now");
			moveToTop.setLayoutData(gridData);
		}

		moveToTop.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(selected == -1)
					return;
				Mp3Player.playSong(Mp3Player.getQueue()[UIQueue.selected]);
				Mp3Player.removeSong(selected+1);
				selected = 0;
				Display.getDefault().asyncExec(refresher);
			}
		});

		Button remove = new Button(queueButtons, SWT.PUSH | SWT.WRAP);
		{
			GridData gridData = new GridData();
			gridData.heightHint = 30;
			gridData.widthHint = 80;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
			remove.setText("Remove");
			remove.setLayoutData(gridData);
		}

		remove.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(selected == -1)
					return;
				Mp3Player.removeSong(selected);
				if(selected == currentQueue.length-1)
					selected--;
				Display.getDefault().asyncExec(refresher);
			}
		});

		Mp3Player.addOnSongEnd(new Runnable() {

			@Override
			public void run() {
				if(currentQueue.length == 0)
					selected = -1;
				else
					selected-=1;
				Display.getDefault().asyncExec(refresher);
			}});

		refresh();
		queueShell.setVisible(false);

	}


	public static void addToQueue(JSONObject song)
	{
		Mp3Player.addSong(song);
		refresh();
	}

	public static void moveUp(int index)
	{
		Mp3Player.swapUp(index);
		selected--;
		refresh();
	}

	public static void moveDown(int index)
	{
		Mp3Player.swapUp(index-1);
		selected++;
		refresh();
	}

	public static void moveToTop(JSONObject song)
	{
		Mp3Player.playSong(song);
		selected = 0;
		refresh();
	}

	public static void remove(int index)
	{
		Mp3Player.removeSong(index);
		if(currentQueue.length == 0)
			selected = -1;
		refresh();
	}

	public static void refresh()
	{
		JSONObject[] backendQueue = Mp3Player.getQueue();

		if(currentQueue.length > 0)
			for(int i = 0; i < currentQueue.length; i++)
			{
				currentQueue[i].dispose();
			}
		currentQueue = new SongComposite[backendQueue.length];
		for(int i = 0; i < backendQueue.length; i++)
		{
			currentQueue[i] = new SongComposite(c, backendQueue[i]);
			currentQueue[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			Listener mouse=new Listener() {
				int i;
				public void handleEvent(Event e) {
					selected = i;
					for(int k = 0; k < currentQueue.length; k++)
					{
						if(k == i)
						{
							currentQueue[i].setColor(new Color(Display.getDefault(), 225, 190, 190));
						}
						else
						{
							currentQueue[k].setColor(SoundCloudGUI.mainShell.getBackground());
						}
					}

				}
				public Listener init(int a){
					i=a;
					return this;
				}
			}.init(i);	
			for(Control children:currentQueue[i].getChildren()){
				children.addListener(SWT.MouseUp, mouse);
			}
			currentQueue[i].addListener(SWT.MouseUp, mouse);
		}
		if(selected >= 0)
		{
			currentQueue[selected].setColor(new Color(Display.getDefault(), 225, 190, 190));
		}
		sc.setContent(c);
		sc.setMinSize(c.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}



}
