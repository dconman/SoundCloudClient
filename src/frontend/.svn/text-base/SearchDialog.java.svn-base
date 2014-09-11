package frontend;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import backend.SoundCloudAPIManager;

/**
 * Creates the Search Shell that performs searches for tracks.
 * 
 * @author Jordan
 */
public class SearchDialog {
	static int currentOffset;
	
	private static void setOffset(int n) {
		currentOffset = n;
	}
	
	private static int getOffset() {
		return currentOffset;
	}
	
	/**
	 * Creates a shell for searching.
	 * Starts out as invisible.
	 * 
	 * @author Jordan
	 */
	public static void createSearchShell(){
		Shell searchShell = new Shell(SoundCloudGUI.mainShell, SWT.TITLE | SWT.RESIZE);
	    Composite compSearch = new Composite(searchShell, SWT.NONE);
		Text txtSearch = new Text(compSearch, SWT.BORDER);
		Button btnGo = new Button(compSearch, SWT.PUSH);
		final ScrolledComposite sc = new ScrolledComposite(searchShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.addListener(SWT.Activate, new Listener() {
		    public void handleEvent(Event e) {
		        sc.setFocus();
		    }
		});
		Composite c = new Composite(sc, SWT.NONE);
	    Button btnPrev = new Button(searchShell, SWT.PUSH);
	    Button btnNext = new Button(searchShell, SWT.PUSH);
		Button btnCancel = new Button(searchShell, SWT.PUSH);
		
		{
			searchShell.setText("Search");
			searchShell.setLayout(new GridLayout(2, false));
		}
		
		{
			compSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			compSearch.setLayout(new RowLayout());
		}
		
		{
			txtSearch.setLayoutData(new RowData(330,SWT.DEFAULT));
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
			}.init(btnGo);
			txtSearch.addTraverseListener(enterListener);
		}
		
		{
			btnGo.setText("Go");
			btnGo.addSelectionListener(new SelectionAdapter(){
				Text searchTextBox;
				Composite resultComposite;
				public SelectionAdapter init(Text a, Composite b){
					searchTextBox=a;
					resultComposite=b;
					return this;
				}
				public void widgetSelected(SelectionEvent arg0) {
					new Thread(){
						String search;
						Composite resultComposite;
						public Thread init(String a, Composite b){
							search=a;
							resultComposite=b;
							return this;
						}
						public void run(){
							try{
								JSONArray results = SoundCloudAPIManager.searchTracks(search, 0, 30);
								setOffset(0);
								Display.getDefault().asyncExec(new Runnable(){
									JSONArray results;
									Composite resultComposite;
									
									public Runnable init(JSONArray a, Composite b){
										results=a;
										resultComposite=b;
										return this;
									}

									@Override
									public void run() {
										for(Control control : resultComposite.getChildren()){
											control.dispose();
										}
										if(results.length()==0)
										{
											new SongComposite(resultComposite, "No results.");
										}
										initializeResults(results, resultComposite);
										sc.setMinSize(resultComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
									}
								}.init(results, resultComposite));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.init(searchTextBox.getText(), resultComposite).start();
				}
			}.init(txtSearch, c));
		}
	    
		{
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			data.horizontalSpan = 2;
			sc.setLayoutData(data);
		}

		{
		    sc.setContent(c);
		    sc.setExpandHorizontal(true);
		    sc.setExpandVertical(true);
		    sc.setMinSize(c.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}

		{
			c.setLayout(new GridLayout(1, true));
		    new SongComposite(c, "No results.");
		}
		
	    {
			btnCancel.setLayoutData(new RowData(60,SWT.DEFAULT));
			btnCancel.setText("Cancel");
			btnCancel.addSelectionListener(new SelectionAdapter(){
				Shell shell;
				public SelectionAdapter init(Shell searchShell){
					shell=searchShell;
					return this;
				}
				public void widgetSelected(SelectionEvent arg0) {
					shell.setVisible(false);
					SoundCloudGUI.menuSearch.setText(SoundCloudGUI.MENU_SEARCH_SHOW);
				}
			}.init(searchShell));
			GridData data = new GridData(SWT.END, SWT.CENTER, false, false, 1, 1);
			data.horizontalSpan = 2;
			btnCancel.setLayoutData(data);
		}
	    
	    {
	    	btnPrev.setLayoutData(new RowData(30,SWT.DEFAULT));
	    	btnPrev.setText("Prev 30");
	    	btnPrev.addSelectionListener(new SelectionAdapter(){
	    		Text theText;
	    		Composite theComp;
	    		public SelectionAdapter init(Text t, Composite c){
	    			theText = t;
	    			theComp = c;
	    			return this;
	    		}
	    		public void widgetSelected(SelectionEvent arg0) {
	    			new Thread(){
	    				String search;
	    				Composite resultComposite;
	    				public Thread init(String a, Composite b){
	    					search=a;
	    					resultComposite=b;
	    					return this;
	    				}
	    				public void run(){
	    					try{
	    						if((getOffset()-30)<0){
	    							setOffset(0);
	    							return;
	    						}else
		    						setOffset(getOffset()-30);
	    						JSONArray results = SoundCloudAPIManager.searchTracks(search, getOffset(), 30);
	    						Display.getDefault().asyncExec(new Runnable(){
	    							JSONArray results;
	    							Composite resultComposite;
	    							
	    							public Runnable init(JSONArray a, Composite b){
	    								results=a;
	    								resultComposite=b;
	    								return this;
	    							}
	    							
    								@Override
   									public void run() {
   										for(Control control : resultComposite.getChildren())
	    									control.dispose();
	    								if(results.length()==0)
	    									new SongComposite(resultComposite, "No results.");
	    								initializeResults(results, resultComposite);
	    								sc.setMinSize(resultComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    							}
	    						}.init(results, resultComposite));
	    					} catch (IOException e) {
	    						e.printStackTrace();
	    					}
	    				}
	    			}.init(theText.getText(), theComp).start();
	    		}
	    	}.init(txtSearch, c));
	    	btnPrev.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
	    }
	    
	    {
	    	btnNext.setLayoutData(new RowData(30,SWT.DEFAULT));
	    	btnNext.setText("Next 30");
	    	btnNext.addSelectionListener(new SelectionAdapter(){
	    		Text theText;
	    		Composite theComp;
	    		public SelectionAdapter init(Text t, Composite c){
	    			theText = t;
	    			theComp = c;
	    			return this;
	    		}
	    		public void widgetSelected(SelectionEvent arg0) {
	    			new Thread(){
	    				String search;
	    				Composite resultComposite;
	    				public Thread init(String a, Composite b){
	    					search=a;
	    					resultComposite=b;
	    					return this;
	    				}
	    				public void run(){
	    					try{
	    						setOffset(getOffset()+30);
	    						JSONArray results = SoundCloudAPIManager.searchTracks(search, getOffset(), 30);
	    						Display.getDefault().asyncExec(new Runnable(){
	    							JSONArray results;
	    							Composite resultComposite;
	    							
	    							public Runnable init(JSONArray a, Composite b){
	    								results=a;
	    								resultComposite=b;
	    								return this;
	    							}
    									@Override
   									public void run() {
   										for(Control control : resultComposite.getChildren())
	    									control.dispose();
	    								if(results.length()==0)
	    									new SongComposite(resultComposite, "No results.");
	    								initializeResults(results, resultComposite);
	    								sc.setMinSize(resultComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    							}
	    						}.init(results, resultComposite));
	    					} catch (IOException e) {
	    						e.printStackTrace();
	    					}
	    				}
	    			}.init(theText.getText(), theComp).start();
	    		}
	    	}.init(txtSearch, c));
	    	btnNext.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
	    }
	    
	    searchShell.setBackground(SoundCloudGUI.mainShell.getBackground());
	    searchShell.setMinimumSize(500, 500);
	    searchShell.setSize(500, 500);
	    searchShell.setVisible(false);
		SoundCloudGUI.searchShell=searchShell;
	  }
	
	private static void initializeResults(JSONArray results, Composite resultComposite) {
		for(int i=0;i<results.length();i++){
			SongComposite temp = new SongComposite(resultComposite, results.getJSONObject(i));
			temp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			
			Menu popupMenu = new Menu(temp);
		    MenuItem addItem = new MenuItem(popupMenu, SWT.NONE);
		    addItem.setText("Add");
		    MenuItem infoItem = new MenuItem(popupMenu, SWT.NONE);
		    infoItem.setText("Info");
		    
		    SelectionAdapter addAdpt =  new SelectionAdapter(){
		    	SongComposite source;
		    	
				public SelectionAdapter init(SongComposite s){
					source = s;
					return this;
				}
				
				public void widgetSelected(SelectionEvent e) {
					Display.getDefault().asyncExec(new Runnable(){

						private JSONObject song;

						@Override
						public void run() {
							UIQueue.addToQueue(song);
						}

						public Runnable init(JSONObject song) {
							this.song = song;
							return this;
						}}.init(source.song));
				}
			}.init(temp);
			addItem.addSelectionListener(addAdpt);
		    
		    SelectionAdapter infoAdpt =  new SelectionAdapter(){
		    	SongComposite source;
		    	
				public SelectionAdapter init(SongComposite s){
					source = s;
					return this;
				}
				
				public void widgetSelected(SelectionEvent e) {
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
			}.init(temp);
			infoItem.addSelectionListener(infoAdpt);
			
		    temp.setMenu(popupMenu);
		    Control[] songCompChildren = temp.getChildren();
		    for(Control cont : songCompChildren)
		    	cont.setMenu(popupMenu);
		}
	}
	
	
}