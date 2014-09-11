package frontend;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.json.JSONObject;

/**
 * SongComposite extends Composite and is used for creating song objects that
 * can be used to populate other Composites like the Queue or Search queue.
 * 
 * @author Jordan
 */
public class SongComposite extends Composite{
	public JSONObject song;
	Label lblInfo;
	
	/**
	 * Constructor used for displaying a song and turning it into a Composite.
	 * 
	 * @author Jordan
	 * @param parent The parent Composite that this SongComposite will be added to.
	 * @param style An int, the style of the Composite. Use SWT.*
	 * @param s The JSONObject (the song) that will be turned into a Composite
	 */
	public SongComposite(Composite parent, JSONObject s) {
		super(parent, SWT.NONE);
		song = s;
		
		MouseListener doubleClick = new MouseListener(){
			SongComposite source;
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				Display.getDefault().asyncExec(new Runnable(){

					private JSONObject song;

					@Override
					public void run() {
						TrackInfo.openTrackInfoBox(song);
					}

					public Runnable init(JSONObject song) {
						this.song = song;
						return this;
					}}.init(source.song));
			}
			
			public MouseListener init(SongComposite s){
				source = s;
				return this;
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		}.init(this);

		RowLayout rowLayout = new RowLayout();
		this.setLayout(rowLayout);
		Label lblArt = new Label(this, SWT.NONE);
		lblInfo = new Label(this, SWT.NONE);

		{
			lblArt.addMouseListener(doubleClick);
			SoundCloudGUI.getImage(lblArt, song.optString("artwork_url",song.getJSONObject("user").optString("avatar_url")), 100, 100, false);
			lblArt.pack();
			lblArt.setLayoutData(new RowData(100, 100));
		}

		{
			lblInfo.addMouseListener(doubleClick);
			lblInfo.setText(song.getString("title")+"\n"+song.getJSONObject("user").getString("username"));
			
			//Format song duration into a readable format
			int dur = song.getInt("duration");
			int sec = dur/1000 %60;
			int min = dur/1000 /60%60;
			int hr  = dur/1000 /60/60;

			lblInfo.setText(lblInfo.getText()+"\n");
			if(hr>1)	lblInfo.setText(lblInfo.getText()+hr+":");
			if(min<10)	lblInfo.setText(lblInfo.getText()+"0"+min);
			else		lblInfo.setText(lblInfo.getText()+min);
			if(sec<10)	lblInfo.setText(lblInfo.getText()+":0"+sec);
			else		lblInfo.setText(lblInfo.getText()+":"+sec);
		}		

		
		this.addMouseListener(doubleClick);
		this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.setBackground(parent.getBackground());
	}

	/**
	 * Constructor used for displaying a message and turning it into a Composite.
	 * 
	 * @author Jordan
	 * @param parent The parent Composite that this SongComposite will be added to.
	 * @param style An int, the style of the Composite. Use SWT.*
	 * @param message The String message to be displayed by the composite
	 */
	public SongComposite(Composite parent, String message) {
		super(parent, SWT.NONE);
		
		Label infoLbl = new Label(this, SWT.BEGINNING);
		infoLbl.setText(message);
		infoLbl.pack();        
	}
	
	public void setColor(Color c)
	{
		this.setBackground(c);
		lblInfo.setBackground(c);
	}
}