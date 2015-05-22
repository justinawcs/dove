import java.util.Properties;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import java.util.StringTokenizer;

public class ConfigWizard {
	public static void main(String[] args){
		boolean textMode = false;
		if(args.length > 0 ){
			//TODO replace with stringtokenizer search
			textMode = (args[0].equalsIgnoreCase("-t") ||
				args[0].equalsIgnoreCase("--text") ? 
				true : false );
		}
		if(GraphicsEnvironment.isHeadless() || textMode){
			textInterface();
		}else{// has GUI
			graphicInterface();
			
		}
	}
	
	public static void graphicInterface(){
		//and additional explanations in main panel 
		String mountLoc ="", contentLoc ="", folderName="";
		boolean grepEx=true, noThumbs=true, searchFileNames=true;
		//Load config.cfg properties
		Properties configProps = new Properties();
		File config = new File(System.getProperty("user.home")
				+File.separator+".dove"+File.separator +"config.cfg");
		boolean hasConfig = config.exists() && config.canRead();
		//String cfgMountLoc ="", cfgContentLoc ="";
		//boolean cfgGrepEx =false, cfgNoThumbs =false, cfgSearchFiles=false;
		if(hasConfig){
			try{
				configProps.load(new FileInputStream(config ));
			}catch(IOException io){
				//System.out.println("[Dove] config.cfg - Not Found!");
			}
			mountLoc = configProps.getProperty("mountLocation");
			contentLoc = configProps.getProperty("contentLocation");
			folderName = configProps.getProperty("folderName");
			grepEx = Boolean.parseBoolean(configProps.getProperty("grepExcludes") );
			noThumbs = Boolean.parseBoolean(configProps.getProperty("allowNoThumbContent") );
			searchFileNames = Boolean.parseBoolean(configProps.getProperty("searchFileNames") );
			System.out.println("[ConfigWizard] Config file successfully loaded: "+ 
			config.getAbsolutePath().toString());
		}
		boolean done = false;
		//Start info Dialog
		//TODO change from JOption to jframe, 
		String header = "This program will ask for the infomation " +
						"needed to confiure Dove for use.\n";
		if(hasConfig){
				header += "\nPrevious config file loaded: " + 
						config.getAbsolutePath().toString() + 
						"\n    Mount Location: " + mountLoc +
						"\n    Content Location: " + contentLoc +
						"\n    Folder Name: " + folderName +
						"\n    GrepExcludes: " + grepEx +
						"\n    NoThumbs: " + noThumbs +
						"\n    SearchFileNames: " + searchFileNames ;
		}
		String[] opts = {"Continue","Help", "Exit"};
		int n = JOptionPane.showOptionDialog(null, header, "Dove Configuration", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0] );
		if(n == JOptionPane.CANCEL_OPTION || n == JOptionPane.CLOSED_OPTION){
			done = true; //Bypass loop, Program exit.
		}else if(n == JOptionPane.NO_OPTION){
			//OPEN INFORMATIONAL WINDOW
			String help = "Help information!";
			int WINDOW_WIDTH = 400, WINDOW_HEIGHT = 400;
			JFrame frame = new JFrame();
			JPanel page = new JPanel();
			frame.setTitle("Config Help");
			frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			JLabel h = new JLabel(help);
			h.setBorder(BorderFactory.
					createEmptyBorder(10, 10, 10, 10));
			page.add(h);
			frame.add(page);
			frame.pack();
			//frame.setLocationRelativeTo(null);
			
			//JOptionPane.showMessageDialog(null, help, "Config Help",
			//		JOptionPane.INFORMATION_MESSAGE);
		}
		while(!done){
			//MountLocation Dialog
			JFileChooser choice = new JFileChooser(mountLoc );
			choice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			choice.setDialogTitle("Location to Mount Devices.");
			choice.setMultiSelectionEnabled(false);
			n = choice.showDialog(null, "Set");
			if(n == JFileChooser.APPROVE_OPTION){
				mountLoc = choice.getSelectedFile().getAbsolutePath();
				 System.out.println("[ConfigWizard] MountLocation: " + mountLoc);
			}else if(n == JFileChooser.CANCEL_OPTION) {
				done = true;
				break;
			}
			
			//ContentLocation Dialog
			choice = new JFileChooser(contentLoc );
			choice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			choice.setDialogTitle("Location of Content Folders.");
			n = choice.showDialog(null, "Set");
			if(n == JFileChooser.APPROVE_OPTION){
				contentLoc = choice.getSelectedFile().getAbsolutePath();
				System.out.println("[ConfigWizard] ContentLocation: " + contentLoc);
			}else if(n == JFileChooser.CANCEL_OPTION) {
				done = true;
				break;
			}
			
			//FolderName Dialog
			header = "Please enter the name that the folder will use on drives.";
			String name = "Dove"; //hard Default;
			try{
				 name = JOptionPane.showInputDialog(null, header, "Folder Name", 
				JOptionPane.QUESTION_MESSAGE, null, null, "Dove").toString();
			}catch(Exception e){
				//e.printStackTrace();
				done = true;
				break;
			}
			folderName = name;
			System.out.println("[ConfigWizard] FolderName: " + folderName);
			
			//GrepExcludes Dialog
			header = "Will the drives listed in 'skippedDrives' be skipped?\nDefault is yes.";
			//Object[] options = {"Yes", "No"};
			//JOptionPane optionPane = new JOptionPane(
			//	    header, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
			n = JOptionPane.showConfirmDialog(null, header, "GrepExcludes", 
					 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(n == JOptionPane.YES_OPTION){
				grepEx = true;
			}else if (n == JOptionPane.NO_OPTION){
				grepEx = false;
			}else if (n == JOptionPane.CLOSED_OPTION){
				done = true;
				break;
			}
			System.out.println("[ConfigWizard] GrepEx: " + grepEx);
			
			//allowNoThumbnails Dialog
			header = "Would you like to display content items that do NOT have thumbnail images?";
				//Object[] options = {"Yes", "No"};
				//JOptionPane optionPane = new JOptionPane(
				//	    header, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
			n = JOptionPane.showConfirmDialog(null, header, "AllowNoThumbContent", 
					 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(n == JOptionPane.YES_OPTION){
				noThumbs = true;
			}else if (n == JOptionPane.NO_OPTION){
				noThumbs = false;
			}else if (n == JOptionPane.CLOSED_OPTION){
				done = true;
				break;
			}
			System.out.println("[ConfigWizard] AllowNoThumbnailContent: " + noThumbs);
			
			//SearchFileNames Dialog
			header = "Would you like to search through content items using filenames?\n" +
					"This takes more time but improves searches.";
				//Object[] options = {"Yes", "No"};
				//JOptionPane optionPane = new JOptionPane(
				//	    header, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
			n = JOptionPane.showConfirmDialog(null, header, "SearchFileNames", 
					 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(n == JOptionPane.YES_OPTION){
				searchFileNames = true;
			}else if (n == JOptionPane.NO_OPTION){
				searchFileNames = false;
			}else if (n == JOptionPane.CLOSED_OPTION){
				done = true;
				break;
			}
			System.out.println("[ConfigWizard] SearchFileNames: " + searchFileNames);
			
			//Confirm Dialog
			header = "Please confirm the entered data. If correct, then choose " +
					"to write data to file or make changes.";
			header += "\nMountLocation: " + mountLoc
					+ "\nContentLocation: " + contentLoc
					+ "\nFolderName: " + folderName
					+ "\nGrepEx: " + grepEx
					+ "\nAllowNoThumbnailContent: " + noThumbs
					+ "\nSearchFileNames: " + searchFileNames;
			Object[] options = {"Save to File", "Edit", "Exit"};
			n = JOptionPane.showOptionDialog(null, header, "Confirm Data",
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[2]);
			if(n == JOptionPane.YES_OPTION){
				done = true;
				//write to file
				try{
					FileOutputStream output = new FileOutputStream(config);
					configProps.setProperty("mountLocation", mountLoc);
					configProps.setProperty("contentLocation", contentLoc);
					configProps.setProperty("folderName", folderName);
					configProps.setProperty("grepExcludes", String.valueOf(grepEx) );
					configProps.setProperty("allowNoThumbContent", String.valueOf(noThumbs) );
					configProps.setProperty("searchFileNames", String.valueOf(searchFileNames) );
					configProps.store(output, null );
					System.out.println("Configuration file completed.");
					output.close();
					JOptionPane.showMessageDialog(null, "Configuration file completed.\nFile written to "+
							config.toString()+"\nProgram will now exit.");
				}catch(IOException io){
					io.printStackTrace();
				}
			}else if (n == JOptionPane.NO_OPTION){
				// loop around
			}else if (n == JOptionPane.CLOSED_OPTION || n== JOptionPane.CANCEL_OPTION){
				done = true;
				System.out.println("Program terminated.");
				System.exit(0);
				//break;
			}else{}
			//System.out.println("[ConfigWizard] SearchFileNames: " + searchFileNames);
		}
		System.out.println("Program terminated.");
		System.exit(0);
	}
	
	public static void textInterface(){
		String mountLoc = "", contentLoc = "", folderName = "";
		boolean grepEx =false, noThumbs= false, searchFileNames= false;
		/* #Sat Nov 30 12:08:33 EST 2013
		mountLocation=/media/Dove
		contentLocation=/home/jaw/bin/Dove/content2
		folderName=Dove    //added later
		grepExcludes=true
		allowNoThumbContent=true
		searchFileNames=true */ 
		//check for existing config.cfg, pull data if available, ask if correct, terminate
		//no config, ask questions,
		Properties configProps = new Properties();
		File config = new File(System.getProperty("user.home")
				+File.separator+".dove"+File.separator +"config.cfg");
		boolean hasConfig = config.exists() && config.canRead();
		//
		String cfgMountLoc ="", cfgContentLoc ="", cfgFolderName ="";
		boolean cfgGrepEx =false, cfgNoThumbs =false, cfgSearchFiles=false;
		if(hasConfig){
			try{
				configProps.load(new FileInputStream(config));
			}catch(IOException io){
				//System.out.println("[Dove] config.cfg - Not Found!");
			}
			cfgMountLoc = configProps.getProperty("mountLocation");
			cfgContentLoc = configProps.getProperty("contentLocation");
			cfgFolderName = configProps.getProperty("folderName");
			cfgGrepEx = Boolean.parseBoolean(configProps.getProperty("grepExcludes") );
			cfgNoThumbs = Boolean.parseBoolean(configProps.getProperty("allowNoThumbContent") );
			cfgSearchFiles = Boolean.parseBoolean(configProps.getProperty("searchFileNames") );
			System.out.println("[ConfigWizard] Config file successfully loaded: "+ config.getAbsolutePath().toString());
		}
		boolean exit = false;
		//String input;
		Scanner key = new Scanner(System.in);
		while(!exit){
			//TODO needs inner loop to check all entries
			System.out.print("Dove Configuration Wizard\n" +
					"Please enter location to mount devices. ex. /media/Dove\n" +
					(hasConfig ? "From config file: \t" + cfgMountLoc :"" ) +"\n> ");
			mountLoc = key.nextLine();
			
			System.out.print("Please enter location where content folders will be. " +
					"ex. /home/user/Content\n" +
					(hasConfig ? "From config file: \t" + cfgContentLoc : "") +"\n> " );
			contentLoc = key.nextLine();
			
			System.out.print("Please enter the name that the folder will use on drives. " +
					"ex. Vacation or SeaLab\n" +
					(hasConfig ? "From config file: \t" + cfgFolderName : "" )+ "\n> ");
			folderName = key.nextLine();
			
			System.out.print("Please enter [true|false] if drives listed in config will not be used.\n"+
					(hasConfig ? "From config file: \t" + cfgGrepEx : "") +"\n> ");
			grepEx = key.nextBoolean();
			
			System.out.print("Please enter [true|false] if content with no thumbnail images will be allowed.\n"+
					(hasConfig ? "From config file: \t" + cfgNoThumbs : "") +"\n> ");
			noThumbs = key.nextBoolean();
			
			System.out.print("Please enter [true|false] if filenames will be searched.\n"+
					(hasConfig ? "From config file: \t" + cfgSearchFiles : "") +"\n> ");
			searchFileNames = key.nextBoolean();
			
			System.out.println("Mount Location: " + mountLoc +
							"\nContent Location: " + contentLoc +
							"\nFolder Name: " + folderName +
							"\nGrepExcludes: " + grepEx +
							"\nNoThumbs: " + noThumbs +
							"\nSearchFileNames: " + searchFileNames );	
			System.out.print("Is the information above correct? [true|false]\n> ");
			exit = key.nextBoolean();
			
		}
		try{
			FileOutputStream output = new FileOutputStream(config);
			configProps.setProperty("mountLocation", mountLoc);
			configProps.setProperty("contentLocation", contentLoc);
			configProps.setProperty("folderName", folderName);
			configProps.setProperty("grepExcludes", String.valueOf(grepEx) );
			configProps.setProperty("allowNoThumbContent", String.valueOf(noThumbs) );
			configProps.setProperty("searchFileNames", String.valueOf(searchFileNames) );
			configProps.store(output,null );
			System.out.println("Configuration file completed: " + config.getAbsolutePath() );
			output.close();
		}catch(IOException io){
			io.printStackTrace();
		}
		key.close();
	}
}
