package frontend;


import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import backend.HTTPException;
import backend.SoundCloudAPIManager;

public class FileUploadDialog {
	
	public static void createFileUploadDialog(){
		Shell programShell=new Shell(SoundCloudGUI.mainShell, SWT.TITLE | SWT.APPLICATION_MODAL);
		programShell.setText("Track Upload");
		Composite fileSelectComposite=new Composite(programShell,0);
		Text fileTextBox=new Text(fileSelectComposite,SWT.SINGLE|SWT.BORDER);
		Button fileDialogButton=new Button(fileSelectComposite,SWT.PUSH);
		Composite trackTitleComposite=new Composite(programShell,0);
		Label titleLabel=new Label(trackTitleComposite, 0);
		titleLabel.setText("Title: ");
		Text titleTextBox=new Text(trackTitleComposite,SWT.SINGLE|SWT.BORDER);
		Button privateButton=new Button(trackTitleComposite, SWT.CHECK);
		Composite buttonComposite=new Composite(programShell,0);
		Button uploadButton=new Button(buttonComposite, SWT.PUSH);
		Button cancelButton=new Button(buttonComposite,SWT.PUSH);
		ProgressBar uploadBar=new ProgressBar(programShell,SWT.SMOOTH);
		
		{
			RowData layout=new RowData();
			layout.width=100;
			cancelButton.setLayoutData(layout);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionAdapter(){
				Shell shell;
				public SelectionAdapter init(Shell a){
					shell=a;
					return this;
				}
				public void widgetSelected(SelectionEvent arg0) {
					shell.close();
				}
			}.init(programShell));
		}
		{
			RowData layout=new RowData();
			layout.height=35;
			uploadBar.setLayoutData(layout);
		}
		{
			buttonComposite.setLayout(new RowLayout());
		}
		{
			trackTitleComposite.setLayout(new RowLayout());
		}
		{
			uploadButton.setText("Upload");
			RowData layout=new RowData();
			layout.width=100;
			uploadButton.setLayoutData(layout);
			uploadButton.addSelectionListener(new SelectionAdapter(){
				Button uploadButton;
				Button privateButton;
				Text titleText;
				Text fileText;
				Shell parent;
				ProgressBar bar;
				
				public SelectionAdapter init(Button a,Button b, Text c, Text d,Shell e,ProgressBar f){
					uploadButton=a;
					privateButton=b;
					titleText=c;
					fileText=d;
					parent=e;
					bar=f;
					return this;
				}
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					uploadButton.setEnabled(false);
					boolean isPrivate=privateButton.getSelection();
					String trackTitle=titleText.getText();
					File trackFile=new File(fileText.getText());
					if(!trackFile.exists()){
						MessageBox error=new MessageBox(parent, SWT.ICON_ERROR|SWT.OK);
						error.setMessage("File does not exist");
						error.open();
						uploadButton.setEnabled(true);
						return;
					}
					if(trackTitle.isEmpty()){
						MessageBox error=new MessageBox(parent, SWT.ICON_ERROR|SWT.OK);
						error.setMessage("Enter a Track Title");
						error.open();
						uploadButton.setEnabled(true);
						return;
					}
					new Thread(){
						File file;
						String title;
						boolean isPrivate;
						ProgressBar bar;
						Shell parent;
						public Thread init(File a, String b, boolean c, ProgressBar d, Shell e){
							file=a;
							title=b;
							isPrivate=c;
							bar=d;
							parent=e;
							return this;
						}
						public void run(){
							try {
								SoundCloudAPIManager.uploadTrack(title, !isPrivate, file, bar);
							} catch (HTTPException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Display.getDefault().asyncExec(new Runnable(){
								Shell parent;
								public Runnable init(Shell a){
									parent=a;
									return this;
								}
								@Override
								public void run() {
									MessageBox error=new MessageBox(parent, SWT.ICON_INFORMATION|SWT.OK);
									error.setMessage("Upload Complete.");
									error.open();
									parent.close();
									
								}
							}.init(parent));
							
						}
					}.init(trackFile, trackTitle, isPrivate, bar,parent).start();
					
				}
			}.init(uploadButton,privateButton,titleTextBox,fileTextBox,programShell,uploadBar));
		}
		{
			privateButton.setText("Track is Private");
		}
		{
			RowData layout=new RowData();
			layout.width=300;
			fileTextBox.setLayoutData(layout);
			fileTextBox.setEditable(false);
		}
		{
			RowData layout=new RowData();
			layout.width=200;
			titleTextBox.setLayoutData(layout);
		}
		{
			RowLayout layout = new RowLayout();
			layout.type=SWT.VERTICAL;
			layout.fill=true;
			programShell.setLayout(layout);
		}
		{
			fileSelectComposite.setLayout(new RowLayout());
		}
		{
			fileDialogButton.setText("Browse...");
			fileDialogButton.addSelectionListener(new SelectionAdapter(){
				Shell shell;
				Text textBox;
				
				public SelectionAdapter init(Shell i, Text b){
					shell=i;
					textBox=b;
					return this;
				}
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					FileDialog fileSelecter=new FileDialog(shell,SWT.OPEN);
					fileSelecter.setFilterExtensions(new String[]{"*.mp3"});
					String file=fileSelecter.open();
					if(file!=null){
						textBox.setText(file);
					}
					
				}
			}.init(programShell,fileTextBox));
		}
		programShell.pack();
		programShell.open();
		
	}

}
