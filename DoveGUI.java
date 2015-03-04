import java.awt.event.*;
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
//import java.awt.Color;
//import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.io.File;
//import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
//import javax.swing.BoxLayout;
//import java.io.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;
import java.io.IOException;

/**
 * @author jaw
 *
 */
@SuppressWarnings("serial")
public class DoveGUI extends JFrame{
	//TODO Load Dove differently to allowing tracking of loading at startup
	private Dove main;
	final int WINDOW_WIDTH = 800;
	final int WINDOW_HEIGHT = 600;
	private CardLayout cards = new CardLayout();
	private JWindow splash;
	private JPanel panel, cardPanel, topBar, botBar; // only main panels
	//card panels-select, content, [eject/load]-drive, list, copy, info, search
	private JPanel selectCard, contentCard, ejectCard, listCard, 
					copyCard, infoCard, searchCard;
	private JPanel center, stage; //sub Panels - selectCard
	private JCheckBox cVid, cAud, cMus, cDoc, cPic, cOther, cThumbsOnly;
	private JRadioButton bAZ, bZA, bNew, bOld, bSmall, bLarge; //bTagsANY, bTagsALL;
	private JButton bSearch, bClearSearch, /*bFindTags,*/ bClearTags, bClearAll;
	private JButton bHelp, bSeeList, bCopy, bRefreshDrives;
	private JButton bFirst, bPrev, bNext, bLast, bClear, bPager;
	private JButton bBack, bAdd, bRemoveDevice;
	private JLabel lStatus, lWarn, lData;
	private JTextField tSearch;
	private JProgressBar beforeBar, afterBar, copyBar, loadBar;
	private JComboBox<String> driveList;
	private Box sidebar, nav, option, bxList;
	private Dimension hSpace = new Dimension(10, 0);
	private Dimension vSpace = new Dimension(0, 5);
	private String search, status, pager;
	private boolean isSearch, isTags, isThumbsOnly;
	private enum SortType {AZ, ZA, NEW, OLD, SMALL, LARGE};
	private final String[] SORT_NAME = {"Alphabetically", "Alphabetically Reversed", "by Newest First",
			"by Oldest First", "by Smallest First", "by Largest First"}; // Keep synced w/ SortType sort
	private final SortType DEFAULT_SORT = SortType.NEW; //Default
	private SortType sort = DEFAULT_SORT; 
	private final int BAR_MAX = 10000, PAGE_SIZE = 6;
	private ArrayList<ContentItem> list = new ArrayList<ContentItem>(), undoList;
	private long listTotalSize = 0, undoSize; 
	private int bookmark = 0;
	private final DecimalFormat form = new DecimalFormat("#0.00");
	private final DecimalFormat perc = new DecimalFormat("#0.00%");
	
	public DoveGUI(){
		//start splash
		startSplash();
		main = new Dove();
		main.getDevices().addDevice("/tmp/ramdisk/","Ramdisk","16 MB", true);
		setTitle("Dove");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		panel.setLayout(new BorderLayout() );
		cardPanel = new JPanel();
		cardPanel.setLayout(cards);
		main.getSource().sortDate();
		main.getSource().reverse();// Default, newest first
		selectCard = new JPanel();
		selectCard.setLayout(new BoxLayout(selectCard, BoxLayout.LINE_AXIS));
		makeSidebar();
		makeTopBar();
		makeBottomBar();
		makeMain(0);
		cardPanel.add(selectCard, "select");
		cards.first(cardPanel);
		panel.add(cardPanel, BorderLayout.CENTER);
		add(panel);
		setLocationRelativeTo(null);
		//pack();
		splash.setVisible(false);
		setVisible(true);
		splash.dispose();
	}
	public void startSplash(){
		splash = new JWindow();
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
		panel.setPreferredSize(new Dimension(250, 60));
		JLabel text = new JLabel("Loading Project Dove...");
		text.setFont(new Font("sansserif", Font.BOLD, 20));
		loadBar = new JProgressBar();
		text.setForeground(loadBar.getForeground().darker());
		loadBar.setPreferredSize(new Dimension(150, 30));
		loadBar.setIndeterminate(true);
		panel.add(text, BorderLayout.CENTER);
		panel.add(loadBar, BorderLayout.SOUTH);
		splash.setContentPane(panel);
		splash.pack();
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
		//Load loader = new Load();
		//loader.doInBackground();
	}
/*	//Disabled content item counter for loading, non functional
 * 	private class Load extends SwingWorker<Void, Void>{
		@Override
		public Void doInBackground(){
			boolean done = false;
			while(!done ){
				if(checkLoadingProgress()){
					if(loadBar.getValue() == (loadBar.getMaximum())){
						done = true;
					}
				}
			}
			return null;
		}
	}	
	public boolean checkLoadingProgress(){
		Integer max = null, count = null;
		boolean flag = false;
		try{
			Thread.sleep(600);
			max = main.getSource().getMaxItems();
			count = main.getSource().getItemCount();
			loadBar.setMaximum(max);
			loadBar.setValue(count);
			loadBar.setIndeterminate(false);
			flag = true;
			System.out.println(max +" "+ count +" "+ flag);
		}catch(NullPointerException n){
			System.out.print(".");
		}catch(InterruptedException i){
			i.printStackTrace();
		}
		return flag;
	}
 */
	
	public void makeSidebar(){
		//TODO simplify tags search: tags any/all, add has thumbnail 
		sidebar = new Box(BoxLayout.PAGE_AXIS);
		sidebar.setBorder(BorderFactory.createEmptyBorder(0,10,0,10) );
		//lSearch = new JLabel("Search:");
		//lSearch.setAlignmentY(LEFT_ALIGNMENT);
		/*tSearch = new JTextField("Search");
		tSearch.setMaximumSize(new Dimension(130, 30));
		tSearch.addFocusListener(new FocusListener() {
		    @Override
		    public void focusGained(FocusEvent e) {
		        tSearch.setText(null); // Empty the text field when it receives focus
		    }
		    @Override
		    public void focusLost(FocusEvent e) {
		    	tSearch.setText("Search");
		    }
		});
		tSearch.addActionListener(new SearchTextFieldListener() );*/
		bSearch = new JButton("Search");// or icon of magnifying glass
		bSearch.addActionListener(new SearchButtonListener() );
		bClearSearch = new JButton("Clear");
		bClearSearch.setToolTipText("Clear Search");
		bClearSearch.addActionListener(new SearchClearButtonListener() );
		bClearSearch.setMargin(new Insets(2,0,2,0));
		Box bxSearch = new Box(BoxLayout.LINE_AXIS);
			//bxSearch.add(Box.createVerticalGlue());
			bxSearch.add(bSearch);
			bxSearch.add(bClearSearch);
			//bxSearch.add(bSearchGo);
			bxSearch.setAlignmentX(0.0f);
			//bxSearch.add(Box.createVerticalGlue());
		bAZ = new JRadioButton("A-Z");
		bZA = new JRadioButton("Z-A");
		bNew = new JRadioButton("Newest", true); // Default
		bOld = new JRadioButton("Oldest");
		bSmall = new JRadioButton("Smallest");
		bLarge = new JRadioButton("Largest");
		ButtonGroup group = new ButtonGroup();
		group.add(bAZ);
		group.add(bZA);
		group.add(bNew);
		group.add(bOld);
		group.add(bSmall);
		group.add(bLarge);
		bAZ.addActionListener(new AZButtonListener() );
		bZA.addActionListener(new ZAButtonListener() );
		bNew.addActionListener(new NewButtonListener() );
		bOld.addActionListener(new OldButtonListener() );
		bSmall.addActionListener(new SmallButtonListener() );
		bLarge.addActionListener(new LargeButtonListener() );
		Box bxSort = new Box(BoxLayout.PAGE_AXIS);
			bxSort.setBorder(BorderFactory.createTitledBorder("Sorting:") );
			bxSort.add(bAZ);
			bxSort.add(bZA);
			bxSort.add(bNew);
			bxSort.add(bOld);
			bxSort.add(bSmall);
			bxSort.add(bLarge);
		cVid = new JCheckBox("Video");
		cAud = new JCheckBox("Audio");
		cMus = new JCheckBox("Music");
		cDoc = new JCheckBox("Document");
		cPic = new JCheckBox("Picture");
		cOther = new JCheckBox("Other"); 
		cVid.addActionListener(new FindTagsButtonListener());
		cAud.addActionListener(new FindTagsButtonListener());
		cMus.addActionListener(new FindTagsButtonListener());
		cDoc.addActionListener(new FindTagsButtonListener());
		cPic.addActionListener(new FindTagsButtonListener());
		cOther.addActionListener(new FindTagsButtonListener());
		//bTagsANY = new JRadioButton("Any", true);
		//bTagsALL = new JRadioButton("All");
		//ButtonGroup tagGroup = new ButtonGroup();
		//tagGroup.add(bTagsANY);
		//tagGroup.add(bTagsALL);
		//bFindTags = new JButton("Find");
		//bFindTags.addActionListener(new FindTagsButtonListener() );
		bClearTags = new JButton("Clear");
		bClearTags.setToolTipText("Clear Type");
		bClearTags.addActionListener(new ClearTagsButtonListener() );
		//bClearTags.setMargin(new Insets(2,0,2,0));
		//bTagsANY.setAlignmentX(Component.CENTER_ALIGNMENT);
		//bTagsALL.setAlignmentX(CENTER_ALIGNMENT);
		//bTagsANY.addActionListener(new AnyButtonListener() );
		//bTagsALL.addActionListener(new AllButtonListener() );
		Box bxTags = new Box(BoxLayout.PAGE_AXIS);
			bxTags.setBorder(BorderFactory.createTitledBorder("Type:") );
			bxTags.add(cVid);
			bxTags.add(cAud);
			bxTags.add(cMus);
			bxTags.add(cDoc);
			bxTags.add(cPic);
			bxTags.add(cOther);
			/*Box bxTagsButtons = new Box(BoxLayout.LINE_AXIS);
				bxTagsButtons.add(bTagsANY);
				//bxTagsButtons.add(Box.createRigidArea(hSpace));
				bxTagsButtons.add(Box.createHorizontalGlue() );
				bxTagsButtons.add(bTagsALL);
			bxTags.add(bxTagsButtons);*/
			//Box bxTagsRadio = new Box(BoxLayout.LINE_AXIS);
			//	bxTagsRadio.add(bTagsANY);
			//	bxTagsRadio.add(bTagsALL);// Replace with More Options... Popup
			//	bxTagsRadio.setAlignmentX(0.0f);
			Box bxFind = new Box(BoxLayout.LINE_AXIS);
				//bxFind.add(bFindTags);
				bxFind.add(bClearTags);
				bxFind.setAlignmentX(0.0f);
			//bxTags.add(bxTagsRadio);
			bxTags.add(bxFind);
		bClearAll = new JButton("Revert");
		bClearAll.addActionListener(new ClearAllButtonListener());
		cThumbsOnly = new JCheckBox("Thumbs Only");
		cThumbsOnly.setToolTipText("Show only items with thumbnail images.");
		cThumbsOnly.addActionListener(new ThumbsOnlyButtonListener());
		//cFileNameSearch = new JCheckBox("Search Filenames");
		Box bxAdv = new Box(BoxLayout.PAGE_AXIS);
			//bxAdv.setBorder(BorderFactory.createTitledBorder("Advanced:") );
			bxAdv.add(cThumbsOnly);
			//bxAdv.add(cFileNameSearch);// read by config file instead
		sidebar.add(Box.createVerticalGlue() );
		sidebar.add(bxSearch);
		//sidebar.add(Box.createRigidArea(vSpace) );		
		sidebar.add(bxSort);
		//sidebar.add(Box.createRigidArea(vSpace) );
		sidebar.add(bxTags);
		sidebar.add(bxAdv);
		sidebar.add(Box.createVerticalGlue() );
		//sidebar.add(bClearAll);
		selectCard.add(sidebar);
	}
	
	public void makeTopBar(){
		//Information Current Status & help
		//# items found, search term, sorted by, of type __
		topBar = new JPanel();
		topBar.setLayout(new BorderLayout(10,10) );
		JLabel greet = new JLabel("<html><h2>Welcome!</h2></html>");
		//greet.setFont(new Font(20)); // or just increase font size to larger
		lStatus = new JLabel();
		//lStatus.setAlignmentY(0.5f);
		updateStatus();
		bHelp = new JButton("Help...");
		bHelp.addActionListener(new HelpButtonListener());
		topBar.add(greet, BorderLayout.WEST);
		topBar.add(lStatus,BorderLayout.CENTER);
		topBar.add(bHelp, BorderLayout.EAST);
		panel.add(topBar, BorderLayout.NORTH);
	}
	
	public void makeBottomBar(){
		//N-set Buttons: See Manifest/List, Ready Transfer, Eject Drive
		//S-set-1 Labels: Text info, Selected Size, Free Space Est. Used Space
		//S-set 2 Label: Drive Name, Progress Bar Remaining Space/Free Space 
		botBar = new JPanel();
		botBar.setLayout(new BorderLayout());
		bSeeList = new JButton("See List");
		bSeeList.addActionListener(new ListButtonListener() );
		bCopy = new JButton("Copy List");
		bCopy.addActionListener(new CopyButtonListener() );
		driveList = new JComboBox<String>();
		updateDriveList();
		driveList.addActionListener(new DriveListListener() );
		driveList.setPreferredSize(new Dimension(50, 0) );
		
		lWarn = new JLabel("");
		bRefreshDrives = new JButton("Refresh");
		bRefreshDrives.setToolTipText("Refresh Drive Listing");
		bRefreshDrives.setMargin(new Insets(2,0,2,0));
		bRefreshDrives.addActionListener(new DriveRefreshListener() );
/*		bRefreshDrives.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(main.getDevices().isMounted() ){//unmount on any change
					main.getDevices().unmount();
					updateBars();
				}
				try{
					main.getDevices().refresh();
				}catch(IOException io){
					io.printStackTrace();
				}
				updateDriveList();
			}
		});*/
		//bEject = new JButton("Eject Drive");
		//bEject.addActionListener(new EjectButtonListener() );
		//bDriveInfo = new JButton("Drive Info...");
		//bDriveInfo.addActionListener(new DriveInfoButtonListener() );
		
		Box hz = new Box(BoxLayout.LINE_AXIS);
			hz.add(Box.createRigidArea(hSpace));
			//hz.add(bDriveInfo);
			//hz.add(Box.createRigidArea(hSpace));
			//hz.add(bEject);
			//hz.add(Box.createGlue());
			hz.add(bRefreshDrives);
			//hz.add(Box.createRigidArea(hSpace));
			hz.add(driveList);
			hz.add(Box.createRigidArea(hSpace));
			hz.add(lWarn);
			//hz.add(tSearch);
			hz.add(Box.createGlue());
			hz.add(bSeeList);
			hz.add(Box.createRigidArea(hSpace));
			hz.add(bCopy);
			hz.add(Box.createRigidArea(hSpace));
			//hz.setAlignmentX(5f);
		beforeBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, BAR_MAX);
		afterBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, BAR_MAX);
		updateBars();
		Box vt = new Box(BoxLayout.PAGE_AXIS);
			vt.setBorder(BorderFactory.createTitledBorder("Drive Capacity Used") );
			vt.add(beforeBar);
			vt.add(afterBar);
		botBar.add(Box.createRigidArea(vSpace), BorderLayout.NORTH);
		botBar.add(hz,BorderLayout.CENTER);
		botBar.add(vt, BorderLayout.SOUTH);
		panel.add(botBar, BorderLayout.SOUTH);
	}
	
	public void makeMain(int start){
		// Big Grid 2x3: Locked Size
		// Grid 1x4: NavButtons Below Prev, Next, First, Last 
		center = new JPanel();
		center.setLayout(new BorderLayout(5,5));
		//stage = new JPanel();
		//stage.setLayout(new GridLayout(2,3,10,10));
		updateGrid(start);
		nav = new Box(BoxLayout.LINE_AXIS);
			bFirst = new JButton("First Page");
			bFirst.setActionCommand("first");
			bFirst.addActionListener(new NavButtonListener() );
			bPrev = new JButton("Previous Page");
			bPrev.setActionCommand("prev");
			bPrev.addActionListener(new NavButtonListener());
			bPager = new JButton("pager");
			updatePager();
			//lPager = new JLabel(bPager.getText() );
			bNext = new JButton("Next Page");
			bNext.setActionCommand("next");
			bNext.addActionListener(new NavButtonListener() );
			bLast = new JButton("Last Page");
			bLast.setActionCommand("last");
			bLast.addActionListener(new NavButtonListener() );
			bClear = new JButton("Clear");
			bClear.setActionCommand("clear");
			bClear.addActionListener(new NavButtonListener() );
			nav.add(Box.createGlue());
			nav.add(bFirst);
			nav.add(bPrev);
			nav.add(bPager);
			//nav.add(lPager);
			nav.add(bNext);
			nav.add(bLast);
			//nav.add(bClear);
			nav.add(Box.createGlue());
		center.add(stage, BorderLayout.CENTER);
		center.add(nav, BorderLayout.SOUTH);
		center.setPreferredSize(new Dimension(0, 0));
		// ^^This works for some reason, better than anything else.
		updateNav();
		selectCard.add(center);
	}
	
	public void makeContent(ContentItem peek, int bookmarkIndex){
		//Shows selected item info and choice to add to list
		contentCard = new JPanel();
		contentCard.setLayout(new BorderLayout(10,10) );
		contentCard.setBorder(BorderFactory.createEmptyBorder(0,10,10,10) );
		JLabel lName = new JLabel("<html><center><h2>"+peek.getInfo().getName()+
				"</h2></center></html>", SwingConstants.CENTER);
		lName.setBorder(BorderFactory.createEtchedBorder());
		Image img;
			try{
				img = peek.getImageScaledFast(300, 300);
			}catch(Exception e){
				img = null;
				//e.printStackTrace();
			}
		JLabel thumb;
			if(img != null){
				ImageIcon ico = new ImageIcon(img);
				thumb = new JLabel(ico, SwingConstants.CENTER );
				thumb.setHorizontalAlignment(SwingConstants.CENTER);
			}else{
				thumb = new JLabel("Thumbnail N/A", SwingConstants.CENTER );
				thumb.setSize(300, 300);
				thumb.setMinimumSize(new Dimension(300,300));
				thumb.setPreferredSize(new Dimension(300,300));
			}
		String d = Dove.humanReadableByteCount(peek.getSize(),false);
		String per = (main.getDevices().isMounted() ? 
				" or "+perc.format((double)peek.getSize() /(double) main.getDevices().
						getMountedDrive().getTotalSpace() ) +
				" of drive capacity." : "" );
		//JLabel lSize = new JLabel("Size: "+ d + per, SwingConstants.RIGHT);
		/*Box bxSize = new Box(BoxLayout.LINE_AXIS);
			bxSize.add(Box.createRigidArea(hSpace));
			bxSize.add(Box.createHorizontalGlue());
			bxSize.add(lSize);*/
		/*Box bxThumb = new Box(BoxLayout.LINE_AXIS);
			bxThumb.add(thumb);
			bxThumb.setAlignmentX(0.5f);*/
		Box bxThumb2 = new Box(BoxLayout.LINE_AXIS);
			bxThumb2.add(Box.createHorizontalGlue());
			//bxThumb.add(lName);
			bxThumb2.add(thumb);
			//bxThumb.add(bxSize);
			bxThumb2.add(Box.createHorizontalGlue());
			//bxThumb2.setAlignmentX(0.5f);
		Box info = new Box(BoxLayout.PAGE_AXIS);
			String h1 = "<html><body style='width:310px'><table> ";
			//h1 += "<tr><td valign='baseline'>Name:</td> <td>"+peek.getInfo().getName()+"</td></tr>";
			h1 += "<tr><td valign='baseline'>Size:</td> <td>"+ d + per+"</td></tr>";
			h1 += "<tr><td valign='baseline'>Origin:</td> <td>"+peek.getInfo().getOrigin()+"</td></tr>";
			h1 += "<tr><td valign='baseline'>Description:</td> <td>"+peek.getInfo().getDesc()+"</td></tr>";
			h1 += "<tr><td valign='baseline'>Date:</td> <td>"+peek.getInfo().getDate().toString()+"</td></tr>";
			//System.out.println( (double)peek.getSize() /(double) main.getDevices().getMountedDrive().getTotalSpace() );
			//h1 += "<tr><td>Size:</td> <td>"+ d + per +"</td></tr>";
			h1 += "<tr><td>Media Type:</td> <td>"+ peek.getInfo().getTagsString() +"</td></tr>";
			h1 += "<tr><td valign='baseline'>File List:</td> <td>";
				for(String names : peek.getNames() ){
					h1 += names + "<br />";
				}
			h1 += "</table></body></html>";
			lData = new JLabel(h1);
			JScrollPane scroll = new JScrollPane(lData);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setBorder(BorderFactory.createEmptyBorder(5,0,5,0) );
			info.add(Box.createVerticalGlue());
			info.add(scroll);
			info.add(Box.createVerticalGlue());
		Box middle = new Box(BoxLayout.LINE_AXIS);
			middle.add(Box.createHorizontalGlue());
			middle.add(bxThumb2);
			middle.add(Box.createRigidArea(new Dimension(20,0)));
			middle.add(info);
			middle.add(Box.createHorizontalGlue());
		option = new Box(BoxLayout.LINE_AXIS);
			bBack = new JButton("Go Back");
			bBack.setActionCommand("content");
			bBack.addActionListener(new BackButtonListener() );
			bAdd = new JButton("Add to List");
			bAdd.setActionCommand(String.valueOf(bookmarkIndex));//return 
			bAdd.addActionListener(new AddButtonListener());
			option.add(Box.createHorizontalGlue());
			option.add(bBack);
			option.add(Box.createRigidArea(hSpace));
			option.add(Box.createRigidArea(hSpace));
			option.add(bAdd);
			option.add(Box.createHorizontalGlue());
		contentCard.add(lName, BorderLayout.NORTH);
		//contentCard.add(bxThumb, BorderLayout.WEST);
		contentCard.add(middle, BorderLayout.CENTER);
		contentCard.add(option, BorderLayout.SOUTH);
	}
	
	public void makeInfo(){
		//show drive details: 
		infoCard = new JPanel();
		infoCard.setLayout(new BorderLayout());
	}
	
	public void makeEject(){
		//insta stop device, show eject now button, choose diff drive by combo box
		ejectCard = new JPanel();
		ejectCard.setLayout(new BorderLayout());
	}
	
	public void makeList(){
		listCard = new JPanel();
		listCard.setLayout(new BorderLayout(10,10));
		listCard.setBorder(BorderFactory.createEmptyBorder(20,30,20,30) );
		String h= "<html>Showing the list of items that will be "+
				"transfered to your storage device.";
		if(!main.getDevices().isMounted() ){
			h+="<br />Please mount a drive from the Drop Down box Below," +
					" to see available space.";
		}else if(listTotalSize >= main.getDevices().getMountedDrive() .getFreeSpace()){
			long over = listTotalSize - main.getDevices().getMountedDrive().getFreeSpace();
			String overS = Dove.humanReadableByteCount(over, false);
			double overPer = (double)over / (double)listTotalSize;
			h+="<br />The list is over capacity. Remove at least " + overS + 
					" or " + form.format(overPer*100) +
					"% of data from the list.";
		}else{
			h+="<br />There is room on your device to fit this list of data.";
		}
		lStatus.setText(h);
		//undoList = list;
		int count = list.size();	
		JButton[] del = new JButton[count];
		JButton[] item = new JButton[count];
		Box[] bx = new Box[count];
		bxList = new Box(BoxLayout.PAGE_AXIS);
		JScrollPane scroll = new JScrollPane(bxList);
		if(count == 0){
			bx = new Box[1];
			//del = new JButton[1];
			item = new JButton[1];
			item[0] = new JButton("<html>The list is empty.<br/></html>");
			item[0].setEnabled(true);
			item[0].setMargin(new Insets(4,0,4,0));
			item[0].setActionCommand("list");
			item[0].addActionListener(new BackButtonListener() );
			bx[0] = new Box(BoxLayout.LINE_AXIS);
				bx[0].setBorder(BorderFactory.createRaisedBevelBorder() );
				bx[0].add(item[0]);
			bxList.add(bx[0]);
		}else{
		for(int i=0; i<count; i++){
			bx[i] = new Box(BoxLayout.LINE_AXIS);
				Double per = (double)(list.get(i).getSize()) / (double)(listTotalSize);
				String text = "<html>"+ list.get(i).getInfo().getName() +" : "+
						Dove.humanReadableByteCount(list.get(i).getSize(), false) +
						"<br/>"+ form.format(per*100) + "% of List";
				//System.out.println(list.get(i).getInfo().getName() +"\t"+
				//		list.get(i).hasImage());
				ImageIcon ico;
				try{
					ico = new ImageIcon(list.get(i).getImageScaledFast(48, 48));
					item[i] = new JButton(text, ico);
				}catch(NullPointerException e){
					item[i] = new JButton(text);
					item[i].setMargin(new Insets(11,66,11,0));
				}
				item[i].setHorizontalAlignment(SwingConstants.LEFT);
				item[i].setVerticalTextPosition(SwingConstants.CENTER);
				item[i].setHorizontalTextPosition(SwingConstants.RIGHT);
				item[i].addActionListener(new ItemButtonListener() );
				item[i].setActionCommand(String.valueOf(i));
				del[i] = new JButton("Remove");
				del[i].setMargin(new Insets(19,3,19,3)); //corresponds to 48h icon
				del[i].setActionCommand(String.valueOf(i));
				del[i].addActionListener(new RemoveButtonListener());
				bx[i].add(item[i]);
				bx[i].add(Box.createRigidArea(new Dimension(5, 0)));
				bx[i].add(del[i]);
			bxList.add(bx[i]);
			bxList.add(Box.createRigidArea(vSpace));
		} }
		JButton undo = new JButton("Undo");
		undo.addActionListener(new UndoButtonListener());
		option = new Box(BoxLayout.LINE_AXIS);
			bBack = new JButton("Go Back");
			bBack.setActionCommand("list");
			bBack.addActionListener(new BackButtonListener() );
			//undo.addActionListener(new UndoButtonListener() );
			JButton RmDup = new JButton("Remove Duplicate Items");
			RmDup.addActionListener(new RemoveDuplicateButtonListener() );
			JButton copy  = new JButton("Copy List");
			copy.addActionListener(new CopyButtonListener() );
			option.add(Box.createHorizontalGlue());
			option.add(bBack);
			option.add(Box.createRigidArea(hSpace));
			option.add(undo);
			option.add(Box.createRigidArea(hSpace) );
			option.add(RmDup);
			option.add(Box.createRigidArea(hSpace));
			option.add(copy);
			option.add(Box.createHorizontalGlue());
		listCard.add(scroll, BorderLayout.CENTER);
		listCard.add(option, BorderLayout.SOUTH);
	}
	
	public void makeCopy(){
		copyCard = new JPanel();
		copyCard.setLayout(new BorderLayout(05,10) );
		copyCard.setBorder(BorderFactory.createEmptyBorder(20,30,20,30) );
		lStatus.setText("Correct any issues listed below, then copy files to your device.");
		// Preconditions list occupied, list size okay, drive mounted, Dove heierachy setup
		Box bxCopy = new Box(BoxLayout.PAGE_AXIS);
			JButton listOcc = new JButton();
			String h = "<html>";
			String h2 = "<br/></html>";
			int flag = 0;
				if(list.size() > 0 ){
					listOcc.setText(h+"List contains items."+h2);
					listOcc.setEnabled(false);
				}else{
					listOcc.setText(h+"Items must be added to the list to start copying."+h2);
					listOcc.setEnabled(true);
					listOcc.setActionCommand("copy");
					listOcc.addActionListener(new BackButtonListener() );
					flag++;
				}
			JButton listSpace = new JButton();
				if(main.getDevices().isMounted() && listTotalSize >= 
				main.getDevices().getMountedDrive().getFreeSpace() ){
					listSpace.setText(h+"List is too large to be copied to " +
							"device. Remove some items."+h2);
					listSpace.setEnabled(true);
					listSpace.addActionListener(new ListButtonListener() );
					flag++;
				}else{
					listSpace.setText(h+"List contents will fit on the device."
							+h2);
					listSpace.setEnabled(false);
				}
			JButton drvMounted = new JButton();
				if(main.getDevices().isMounted() ){
					drvMounted.setText(h+"Drive properly mounted."+h2);
					drvMounted.setEnabled(false);
				}else{
					drvMounted.setText(h+"Please mount a drive from the Drop-" +
							"Down List below."+h2);
					drvMounted.setEnabled(true);// no button action
					flag++;
				}
			//Removed code to check for properly setup device/drive. 
			// Program will drop folders on Dove/ if there and create one 
			// if not with no interaction in the interests of speed
			/*JButton drvDoveSet = new JButton();
				if(main.getDevices().isMounted() && 
						main.getDevices().getMountedDrive().isSetup() ){
					drvDoveSet.setText(h+"Drive set-up to recieve files."+h2);
					drvDoveSet.setEnabled(false);
				}else if(main.getDevices().isMounted() == false){
					drvDoveSet.setVisible(false);
				}else{
					drvDoveSet.setText(h+"Click to set-up drive to recieve " +
							"files."+h2);
					drvDoveSet.setEnabled(true);
					drvDoveSet.addActionListener(new DoveButtonListener() );
					flag++;
				}*/
			JButton[] arr = { listOcc, listSpace, drvMounted };
			//Box[] fill = new Box[arr.length];
			for(int i=0; i<arr.length; i++){
				//fill[i] = new Box(BoxLayout.LINE_AXIS);
				//arr[i].setMargin(new Insets(4,0,4,0));
				//fill[i].add(arr[i]);
				//fill[i].add(Box.createHorizontalGlue());
				bxCopy.add(arr[i]);
				bxCopy.add(Box.createRigidArea(new Dimension(0,10)) );
			}
			JButton copyGo  = new JButton(h+"<center>Transfer Files<br/>Now!</center>");
				copyGo.setHorizontalTextPosition(SwingConstants.CENTER);
				copyGo.setHorizontalAlignment(SwingConstants.CENTER);
				copyGo.setMargin(new Insets(20,0,20,0));//taller button
				copyGo.addActionListener(new FinalCopyButtonListener() );
				copyGo.setEnabled(flag == 0);
			Box bxBar = new Box(BoxLayout.PAGE_AXIS);
				bxBar.setBorder(BorderFactory.createTitledBorder("Copy Progress"));
				copyBar = new JProgressBar(SwingConstants.HORIZONTAL);
					//copyBar.setIndeterminate(true);
					//copyBar.setMaximum((int)listTotalSize);
					copyBar.setMaximum(100);
					copyBar.setString("Ready...");
					copyBar.setStringPainted(true);
				JLabel filesRemaining = new JLabel("Files remaining: ");
				bxBar.add(copyBar);
				bxBar.add(Box.createRigidArea(vSpace)); 
				bxBar.add(filesRemaining);
			//JLabel success = new JLabel("<html><h2>Files copied successfully.</h2>");
			//	success.setEnabled(false);
			bRemoveDevice = new JButton("Unmount device to safely remove it.");
				bRemoveDevice.addActionListener(new RemoveDeviceButtonListener() );
				bRemoveDevice.setEnabled(false);
			bxCopy.add(Box.createRigidArea(new Dimension(0,50)) );
			bxCopy.add(copyGo);
			//TODO fix eject drive button
			bxCopy.add(Box.createRigidArea(new Dimension(0,10)) );
			bxCopy.add(bxBar);
			bxCopy.add(Box.createRigidArea(new Dimension(0,10)) );
			bxCopy.add(bRemoveDevice);
			//bxCopy.add(listOcc);
			//bxCopy.add(listSpace);
			//bxCopy.add(drvMounted);
			//bxCopy.add(drvDoveSet);
		option = new Box(BoxLayout.LINE_AXIS);
			bBack = new JButton("Go Back");
			bBack.setActionCommand("copy");
			bBack.addActionListener(new BackButtonListener() );
			//JButton copy  = new JButton("Copy Now");
			//copy.addActionListener(new CopyButtonListener() );
			option.add(Box.createHorizontalGlue());
			option.add(bBack);
			option.add(Box.createRigidArea(new Dimension(20,0)));
			//option.add(copy);
			option.add(Box.createHorizontalGlue());
		copyCard.add(bxCopy, BorderLayout.CENTER);
		copyCard.add(option, BorderLayout.SOUTH);	
	}
	
	public void makeSearch(){
		searchCard = new JPanel();
		searchCard.setLayout(new BorderLayout(10,10));
		searchCard.setBorder(BorderFactory.createEmptyBorder(20,30,20,30) );
		Box bxSearch = new Box(BoxLayout.LINE_AXIS);
			bxSearch.setBorder(BorderFactory.createEmptyBorder(160, 0, 160, 0));
			JLabel lSearch = new JLabel("Search"); 
			tSearch = new JTextField(20);
			Font largerFont = new Font(tSearch.getFont().getFontName(), 
					tSearch.getFont().getStyle(), 28);
			tSearch.setFont(largerFont);
			tSearch.addActionListener(new SearchTextFieldListener());
			JButton bGoSearch = new JButton("Go!");
			bGoSearch.addActionListener(new SearchTextFieldListener());
			bxSearch.add(Box.createHorizontalGlue());
			bxSearch.add(lSearch);
			bxSearch.add(Box.createRigidArea(hSpace));
			bxSearch.add(tSearch);
			bxSearch.add(Box.createRigidArea(hSpace));
			bxSearch.add(bGoSearch);
			bxSearch.add(Box.createHorizontalGlue());
		option = new Box(BoxLayout.LINE_AXIS);
			bBack = new JButton("Go Back");
			bBack.setActionCommand("search");
			bBack.addActionListener(new BackButtonListener() );
			option.add(Box.createHorizontalGlue());
			option.add(bBack);
			option.add(Box.createRigidArea(new Dimension(10,0)));
			//option.add(bGoSearch);
			option.add(Box.createHorizontalGlue());
		searchCard.add(bxSearch, BorderLayout.CENTER);
		searchCard.add(option, BorderLayout.SOUTH);
		tSearch.grabFocus();
		tSearch.requestFocusInWindow();
		//searchCard.add(stuff);
	}
	
	private void updateGrid(int start){
		try{
			center.invalidate();
			center.remove(stage);
			//center.repaint();
			//stage.removeAll();
			//stage = null;
		}catch(NullPointerException e){
			// just skip
			//System.out.println("Not loaded..");
		}
		stage = new JPanel();
		stage.invalidate();
		stage.setLayout(new GridLayout(2,3,10,10));
		stage.setBorder(BorderFactory.createEmptyBorder(5,0,0,10) );
		//Num of grid items is PAGE_SIZE
		bookmark = start;
		//int count = main.getSource().getLength() < PAGE_SIZE ? main.getSource().getLength() : PAGE_SIZE;
		int count = main.getSource().getLength();
		//System.out.println(start +" "+ bookmark +" "+ count);
		for(int i=start; i < (start+PAGE_SIZE) ;i++){
			//System.out.print(i + "-" +count);
			if(i<count){
				String name = main.getSource().getInfoAt(i).getName();
				//System.out.println("  " + name);
				ImageIcon icon;
				try{
					icon = new ImageIcon(main.getSource().getItemAt(i).getImageScaledFast(160,160));
				}catch(NullPointerException e){
					icon = null;
				}
				JButton num;
				if(icon == null){
					num = new JButton(name);
				}else{
					num = new JButton(name, icon);
				}
				num.setToolTipText(name);
				num.setVerticalAlignment(SwingConstants.CENTER);
				num.setVerticalTextPosition(SwingConstants.BOTTOM);
				num.setHorizontalTextPosition(SwingConstants.CENTER);
				num.addActionListener(new ItemButtonListener() );
				num.setActionCommand(String.valueOf(i));
				stage.add(num);
			}else{
				//System.out.println("  N/A");
				JButton num = new JButton("N/A");
				num.setEnabled(false);
				stage.add(num);
			}
		}
		stage.revalidate();
		stage.setVisible(false);
		stage.setVisible(true);
		//setPagerText();
		center.add(stage, BorderLayout.CENTER);
		stage.revalidate();
		stage.repaint();
		//stage.repaint();
		center.revalidate();
		center.repaint();
		center.setVisible(false);
		center.setVisible(true);
		//center.repaint();
		//updatePager();
	}
	
	private void updatePager(){
		//Idea: Change to DropDownList for non-kiosk uses.
		//pageList;
		//bPager = null;
		nav.repaint();
		//lPager = new JLabel("Stuff");
		int page = (bookmark / PAGE_SIZE)+1;
		int pageMax = ((main.getSource().getLength() - 1) / PAGE_SIZE ) +1  ;
		pager = "Page " + page +" of "+ pageMax;
		//pager = (bookmark+1) +" : "+Integer.valueOf(bookmark+PAGE_SIZE).toString(); 
				//+" of " + main.getSource().getLength();
		//System.out.println("[DoveGUI.updatePager] Pager: "+ pager);
		bPager.setText(pager);
		bPager.setEnabled(false);
		//bPager = new JButton("( -_- )" /*temp*/ );
		//bPager = new JButton(temp );
		//bPager.validate();
		//bPager.revalidate();
		//bPager.repaint();
		//try{
		//	nav.revalidate();
		//}catch(NullPointerException e){}
		
		//System.out.println("Updating Button: "+temp);
		//System.out.println(bPager.getText());
		//lPager.setText(bPager.getText());
	}
	private void updateNav(){
		if(bookmark+PAGE_SIZE >= main.getSource().getLength() ){
			bNext.setEnabled(false);
			bLast.setEnabled(false);
		}else{
			bNext.setEnabled(true);
			bLast.setEnabled(true);
		}
		if(bookmark < PAGE_SIZE){
			bPrev.setEnabled(false);
			bFirst.setEnabled(false);
		}else{
			bPrev.setEnabled(true);
			bFirst.setEnabled(true);
		}
		updatePager();
	}
	
	private void updateStatus(){
		int i = main.getSource().getLength();
		int higher = i<bookmark+PAGE_SIZE ? i : bookmark + PAGE_SIZE;
		int lower = higher==0 ? 0 : bookmark+1;
		String stat = "Showing "+ lower +" through "+ higher +" of  "+ i + 
				" resulting item" + (i==1 ? ", " : "s, ") ;
		stat += "sorted " + SORT_NAME[sort.ordinal()] + ".";
		if(isSearch){
			stat += "<br>" + "Currently Searching for \"" + search + "\". ";
		}
		if(isTags){
			stat += "<br>" + getTags();
		}
		status = "<html> "+ stat + "</html>" ;
		lStatus.setText(status);
		//setPagerText();
	}
	private void updateBars(){
			double percentRemBefore, percentRemAfter;
			if(main.getDevices().isMounted() ){
				percentRemBefore = main.getDevices().getMountedDrive().getPercentRem();
				percentRemAfter = main.getDevices().getMountedDrive().getPercentRem(listTotalSize);
				double beforeVal = BAR_MAX - percentRemBefore * (BAR_MAX/100);
				//beforeBar.setForeground(new Color(96, 128, 64));
				//System.out.println(beforeBar.getForeground());
				beforeBar.setValue(Double.valueOf(beforeVal).intValue() );
				beforeBar.setString("Now: " + form.format(beforeVal/100) + "%" );
				beforeBar.setToolTipText("The current amout of space currently used on the drive.");
				beforeBar.setStringPainted(true);
				double afterVal = BAR_MAX - percentRemAfter * (BAR_MAX/100);  
				afterBar.setValue(Double.valueOf(afterVal).intValue());
				afterBar.setString("After: "+  form.format(afterVal/100) + "%" );
				afterBar.setToolTipText("The value amount of space that will be used by the drive after the file transfer.");
				afterBar.setStringPainted(true);
				if(percentRemAfter < 00.0d){
					afterBar.setForeground(new Color(5, 5, 5));
				}else if(percentRemAfter < 10.0d){
					afterBar.setForeground(new Color(255, 0, 0));
				}else if(percentRemAfter < 20.0d){
					afterBar.setForeground(new Color(255, 128, 0));
				}else{//normal
					afterBar.setForeground(beforeBar.getForeground());
				}
			}else{//main.getDevices().isMounted == false
				//percentRemBefore = 0;
				//percentRemAfter = 0;
				//double beforeVal = BAR_MAX - percentRemBefore * (BAR_MAX/100);
				beforeBar.setValue(0);
				beforeBar.setString("N/A");
				beforeBar.setStringPainted(true);
				//double afterVal = BAR_MAX - percentRemAfter * (BAR_MAX/100);  
				afterBar.setValue(0);
				afterBar.setString("N/A");
				afterBar.setStringPainted(true);
			}
	/*		double beforeVal = BAR_MAX - percentRemBefore * (BAR_MAX/100);
			beforeBar.setValue(Double.valueOf(beforeVal).intValue() );
			beforeBar.setString("Before: " + Double.toString(beforeVal/100).substring(0, 5) + "%" );
			beforeBar.setStringPainted(true);
			double afterVal = BAR_MAX - percentRemAfter * (BAR_MAX/100);  
			afterBar.setValue(Double.valueOf(afterVal).intValue());
			afterBar.setString("After: "+  Double.toString(afterVal/100).substring(0, 5) + "%" );
			afterBar.setStringPainted(true);
	*/
		}
	private void updateDriveList(){
		String[] arr = main.getDevices().getInfoArray();
		String[] done = new String[arr.length +1];
		driveList.removeAllItems();
		done[0] = ("Select Drive to Mount");//position 0
		for(int i=1; i<done.length; i++){
			done[i] = arr[i-1];
		}
		driveList.setModel(new DefaultComboBoxModel<String>(done));
		driveList.validate();
		//driveList = new JComboBox<String>(arr);
		//driveList.addActionListener(new DriveListListener() );
		//driveList.setPreferredSize(new Dimension(50, 0) );
	}
	private String getTags(){
		String hold = "Searching for " + /*(bTagsANY.isSelected() ? "any":"all") */ "any"+ 
				" media of type: ";
		hold += cVid.isSelected() ? "Video, " : "";
		hold += cAud.isSelected() ? "Audio, " : "" ;
		hold += cMus.isSelected() ? "Music, " : "" ;
		hold += cDoc.isSelected() ? "Document, " : "" ;
		hold += cPic.isSelected() ? "Pictures, " : "" ;
		hold += cOther.isSelected() ? "Other, " : "" ;
		String temp = hold.substring(0, hold.length()-2 ) + ".";
		return temp;
	}
	
	private void sortLast(){
		//System.out.println("Sort ordinal - " +sort.ordinal() );
		switch(sort.ordinal()){
		case 0: //AZ
			main.getSource().sortAZ();
			break;
		case 1: //ZA
			main.getSource().sortAZ();
			main.getSource().reverse();
			break;
		case 2: //Newest
			main.getSource().sortDate();
			main.getSource().reverse();
			break;
		case 3: //Oldest
			main.getSource().sortDate();
			break;
		case 4: //Smallest
			main.getSource().sortSize();
			break;
		case 5: //Largest
			main.getSource().sortSize();
			main.getSource().reverse();
			break;
		default:
			System.out.println("Bad Sort: "+sort + sort.ordinal());
			main.getSource().sortDate();
			main.getSource().reverse();
		}
	}
	private void filter(){
		main.getSource().refresh();
		if(isThumbsOnly){
			main.getSource().onlyThumbs();
		}else{
		}
		if(isSearch){
			main.getSource().search(search);
		}else{
		}
		if(isTags){
			main.getSource().tags(cVid.isSelected(), cAud.isSelected(),
					cMus.isSelected(), cDoc.isSelected(), 
					cPic.isSelected(), cOther.isSelected());
		}else{
		}
		sortLast();
	}
	//moved to filter()
/*	private void reloadSearch(){
		//main.getSource().refresh();
		if(isSearch == true){
			if(search != "" || search != null){
				main.getSource().search(search);
			}
		}
	}
	private void reloadTags(){
		//
		if(isTags == true){
			//if(bTagsANY.isSelected()){
				main.getSource().tagsAny(cVid.isSelected(), cAud.isSelected(),
					cMus.isSelected(), cDoc.isSelected(), 
					cPic.isSelected(), cOther.isSelected() );
			//}else if (bTagsALL.isSelected()){
			//	main.getSource().tagsAll(cVid.isSelected(), cAud.isSelected(),
			//			cMus.isSelected(), cDoc.isSelected(), 
			//			cPic.isSelected(), cOther.isSelected() );
			//}else{
			//	System.out.println("Code is all screwed up!");
			//}
		}
	}*/
	private class SearchTextFieldListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			isSearch = true;
			String stSearch = tSearch.getText();
			if(stSearch == null || stSearch.matches("") ){
				//System.out.println("Null Search");
				isSearch = false;
			}else{
				main.getSource().refresh();
				main.getSource().search(stSearch);//allows for config set
				search = stSearch;
				filter();
				updateStatus();
				updateGrid(0);
				updateNav();
			}
			cardPanel.remove(searchCard);
			cards.first(cardPanel);
		}
	}
	private class SearchButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			makeSearch();
			cardPanel.add(searchCard, "search");
			cards.show(cardPanel, "search");
			tSearch.grabFocus();
			tSearch.requestFocusInWindow();
			/*isSearch = true;
			String stSearch = JOptionPane.showInputDialog("Search:");
			//System.out.println(">"+stSearch+"<");
			if(stSearch == null || stSearch.matches("") ){
				//System.out.println("Null Search");
				isSearch = false;
			}else{
				main.getSource().refresh();
				main.getSource().search(stSearch);//allows for config set
				search = stSearch;
				sortLast();
				updateStatus();
				reloadTags();
				updateGrid(0);
				updateNav();
			}*/
		}
	}
	
	private class SearchClearButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//tSearch.setText(null);
			isSearch = false;
			search = null;
			main.getSource().refresh();
			filter();
			updateStatus();
			updateGrid(0);
			updateNav();
			
		}
	}
	private class ThumbsOnlyButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(cThumbsOnly.isSelected()){//Show Thumbs Only = true
				isThumbsOnly = true;
				main.getSource().setAllowNoThumb(false);
			}else{
				isThumbsOnly = false;
				main.getSource().setAllowNoThumb(true);
			}
			filter();
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class AZButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortAZ();
			sort = SortType.AZ;
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class ZAButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortAZ();
			main.getSource().reverse();
			sort = SortType.ZA;
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class NewButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortDate();
			main.getSource().reverse();
			sort = SortType.NEW;
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class OldButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortDate();
			sort = SortType.OLD;
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class SmallButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortSize();
			sort = SortType.SMALL;
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class LargeButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().sortSize();
			main.getSource().reverse();
			sort = SortType.LARGE;
			updateStatus();
			updateGrid(0);
			updatePager();
			updateNav();
		}
	}

/*	private class AnyButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().tagsAny(cVid.isSelected(), cAud.isSelected(),
					cMus.isSelected(), cDoc.isSelected(), 
					cPic.isSelected(), cOther.isSelected() );
			updateStatus();
			//reload
		}
	}*/
	//TODO fix
	private class FindTagsButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().refresh();
			if(((JCheckBox)e.getSource()).isSelected() ){
				//item was selected
				isTags = true;
				main.getSource().tags(cVid.isSelected(), cAud.isSelected(),
					cMus.isSelected(), cDoc.isSelected(), cPic.isSelected(), 
					cOther.isSelected() );
			}else{//item was deselected
				if(cVid.isSelected() || cAud.isSelected() || cMus.isSelected() ||
						cDoc.isSelected() || cPic.isSelected() || cOther.isSelected()){
					// at least one item is still selected
					main.getSource().tags(cVid.isSelected(), cAud.isSelected(),
						cMus.isSelected(), cDoc.isSelected(), cPic.isSelected(), 
						cOther.isSelected() );
				}else{//no items selected
					main.getSource().refresh();
					isTags = false;
				}
			}
/*			if(cVid.isSelected() || cAud.isSelected() || cMus.isSelected() ||
				cDoc.isSelected() || cPic.isSelected() || cOther.isSelected()){
				if(bTagsANY.isSelected()){
					main.getSource().tagsAny(cVid.isSelected(), cAud.isSelected(),
						cMus.isSelected(), cDoc.isSelected(), 
						cPic.isSelected(), cOther.isSelected() );
				}else if (bTagsALL.isSelected()){
					main.getSource().tagsAll(cVid.isSelected(), cAud.isSelected(),
							cMus.isSelected(), cDoc.isSelected(), 
							cPic.isSelected(), cOther.isSelected() );
				}else{
					System.out.println("Code is all screwed up!");
				}
			}
			if(bTagsANY.isSelected()){
				main.getSource().tagsAny(cVid.isSelected(), cAud.isSelected(),
					cMus.isSelected(), cDoc.isSelected(), 
					cPic.isSelected(), cOther.isSelected() );
			}else if (bTagsALL.isSelected()){
				main.getSource().tagsAll(cVid.isSelected(), cAud.isSelected(),
						cMus.isSelected(), cDoc.isSelected(), 
						cPic.isSelected(), cOther.isSelected() );
			}else{
				System.out.println("Code is all screwed up!");
			}
*/
			//isTags = true;
			filter();
			sortLast();
			updateStatus();
			updateGrid(0); 
			updateNav();
		}
	}
	private class ClearTagsButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			cVid.setSelected(false);
			cAud.setSelected(false);
			cMus.setSelected(false);
			cDoc.setSelected(false);
			cPic.setSelected(false);
			cOther.setSelected(false);
			isTags = false;
			main.getSource().refresh();
			filter();
			sortLast();
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	
	private class ClearAllButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getSource().refresh();
			isSearch = false;
			isTags = false;
			sortLast();
			updateStatus();
			updateGrid(0);
			updateNav();
		}
	}
	private class HelpButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//TODO display help panel. ??? web call
			JPanel help = new JPanel();
			help.setVisible(true);
		}
	}
	
	private class DriveRefreshListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
				main.getDevices().refresh();
			}catch(IOException io){
				io.printStackTrace();
			}
			updateDriveList();
		}
	}
	
	private class DriveListListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			lWarn.setText("");
			int index = driveList.getSelectedIndex();
			if(main.getDevices().isMounted() ){//unmount on any change
				main.getDevices().unmount();
				updateBars();
			}
			if(index == 0 || index == -1){//first item selected, refresh list
				try{
					main.getDevices().refresh();
				}catch(IOException io){
					io.printStackTrace();
				}
				updateDriveList();
			}else if(index > 0){
				if( main.getDevices().mount(index - 1) ){
					//lWarn.setText("");
					updateBars();
				}else{
					lWarn.setText("Drive not Mounted, cannot be used.");
					updateBars();
					//add warning that drive did not mount, cannot be used.
				}
			}
			//TODO add refresh page, get card name and redraw card
		}
	}
	
	private class ItemButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//System.out.println(e.getActionCommand());
			int num = Integer.parseInt(e.getActionCommand() );
			//System.out.println(num + ":" + main.getSource().getItemAt(num).toString());
			makeContent(main.getSource().getItemAt(num), num);
			cardPanel.add(contentCard, "content");
			cards.show(cardPanel, "content");
			//center.remove(stage);
			//center.remove(nav);
			//sidebar.setVisible(false);
			//center.add(contentCard, BorderLayout.CENTER);
			//center.add(option, BorderLayout.SOUTH);
			//center.revalidate();
			updateBars();
		}
	}
	private class ListButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			undoList = new ArrayList<ContentItem>(list) ;
			undoSize = new Long(listTotalSize);
			//System.out.println(undoList.toString());
			makeList();
			if(main.getDevices().isMounted()){
				main.getDevices().getMountedDrive().refresh();
				updateBars();
			}
			cardPanel.add(listCard, "list");
			cards.show(cardPanel, "list");
		}
	}
	
	private class CopyButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			makeCopy();
			if(main.getDevices().isMounted()){
				main.getDevices().getMountedDrive().refresh();
				updateBars();
			}
			cardPanel.add(copyCard, "copy");
			cards.show(cardPanel, "copy");
		}
	}
	private class FinalCopyButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//((JButton)e.getSource()).setText("Now copying...");
			((JButton)e.getSource()).setEnabled(false);
			copyBar.setIndeterminate(false);
			copyBar.setString("0%");
			copyBar.setValue(0);
			Task copy = new Task();
			copy.addPropertyChangeListener(new ProgressChangeListener());
			copy.doInBackground();
			list.clear();
			listTotalSize = 0;
			main.getDevices().getMountedDrive().refresh();
			updateBars();
			
		}
	}
	private class Task extends SwingWorker<Void, Void>{
		@Override
		public Void doInBackground(){
			long bytesCopied = 0;
			setProgress(0);
			System.out.println("Starting copy.");
			firePropertyChange("progress", getProgress() ,0);
			setProgress(0);
			copyBar.update(copyBar.getGraphics());
			for(int i=0; i<list.size(); i++){
				try{
					if(!main.getDevices().getMountedDrive().isSetup()){
						main.getDevices().getMountedDrive().setupDrive();
					}
					Thread.sleep(1000);
					main.copy(list.get(i) );
					bytesCopied += main.getBytesCopied();
					Double temp = (bytesCopied / (double)listTotalSize);
					int temp2 = (int) (100*bytesCopied / listTotalSize);
					System.out.println(bytesCopied +" of "+ listTotalSize
							+" = "+ temp*100d +"%\t"+ temp2);
					firePropertyChange("progress", getProgress() ,temp2);
					setProgress(temp2);
					//setProgress(i* STEP);
					//copyBar.repaint();
					copyBar.update(copyBar.getGraphics());
				}catch(IOException io){
					System.out.println("PANIC - Copy failed.");
				}catch(InterruptedException e){
					System.out.println("Thread refused to sleep...");
				}
			}
			copyBar.setString("Finished.");
			System.out.println("Done. Copied: "+ bytesCopied +" ListSize: "+ listTotalSize );  
			bRemoveDevice.setEnabled(true);
			return null;
		}
	}
	private class ProgressChangeListener implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			int progress = (Integer) evt.getNewValue();
			copyBar.setValue(progress);
			copyBar.setString(progress+"%");
			//copyBar.revalidate();
	            //System.out.println(progress +" "+copyBar.getMaximum());
	            //taskOutput.append(String.format(
	            //        "Completed %d%% of task.\n", task.getProgress()));
		}
	}
	
	private class DoveButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getDevices().getMountedDrive().setupDrive();
			//Update list window.
		}
	}
	private class NavButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String nav = e.getActionCommand();
			if(nav.contentEquals("first")){
				updateGrid(0);
				//System.out.println("Go first.");
			}else if(nav.contentEquals("prev")){
				if (bookmark >= (PAGE_SIZE - 1)){
					bookmark -=PAGE_SIZE;
					updateGrid(bookmark);
					//updatePager();
					//System.out.println("Go prev.");
				}else{
					System.out.println("Bad Code: Can't go back.");
				}
			}else if(nav.contentEquals("next")){
				if(main.getSource().getLength() > (bookmark+PAGE_SIZE) ){
					//System.out.println(bookmark+PAGE_SIZE);
					updateGrid(bookmark+PAGE_SIZE);
					//updatePager();
					//System.out.println("Go next.");
				}else{
					System.out.println("Bad Code: Can't go forward.");
				}
			}else if(nav.contentEquals("last") ){
				int hold = main.getSource().getLength() / PAGE_SIZE;
				hold = (main.getSource().getLength() % PAGE_SIZE == 0 ? hold-1: hold);
				bookmark = hold * PAGE_SIZE;
				updateGrid(bookmark);
				//System.out.println("Go last.");
			}else if(nav.contentEquals("clear")){
				center.remove(stage);
				stage = null;
				updateGrid(bookmark);
				//center.repaint();
				center.revalidate();
				//stage.repaint();
				stage.revalidate();
			}else{
				System.out.println("Coding FAIL!!");
			}
			updateStatus();
			updateNav();
			updatePager();
		}
	}
	
	private class BackButtonListener implements ActionListener{
		//TODO NOTE: track objects in cardpanel
		public void actionPerformed(ActionEvent e){
			String s = e.getActionCommand();
			if(s.matches("content")){
				cardPanel.remove(contentCard);
			}else if(s.matches("list")){
				cardPanel.remove(listCard);
			}else if(s.matches("copy")){
				cardPanel.remove(copyCard);
			}else if(s.matches("search")){
				cardPanel.remove(searchCard);
			}
			cards.first(cardPanel);
			updateGrid(bookmark);
			updateStatus();
			updateBars();
		}
	}
	private class AddButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int opt = Integer.parseInt(e.getActionCommand());
			list.add(main.getSource().getItemAt(opt)) ;
			//System.out.println(list.toString());
			listTotalSize += main.getSource().getItemAt(opt).getSize();
			updateBars();
			cardPanel.remove(contentCard);
			cards.first(cardPanel);
		}
	}
	
	private class RemoveButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			((JComponent) e.getSource()).getParent().setVisible(false);//abra-cadabra
			int item = Integer.parseInt(e.getActionCommand());
			listTotalSize -= list.get(item).getSize();
			list.remove(item);
			updateBars();
			listCard.removeAll();
			listCard=null;
			makeList();
			listCard.revalidate();
			cardPanel.revalidate();
			cardPanel.add(listCard, "list");
			cards.show(cardPanel, "list");
		}
	}
	private class UndoButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//System.out.println(undoList.toString());
			list = undoList;
			listTotalSize = undoSize;
			//System.out.println(list.toString());
			updateBars();
			listCard.removeAll();
			listCard = null;
			makeList();
			undoList = new ArrayList<ContentItem>(list) ;
			undoSize = new Long(listTotalSize);
			listCard.revalidate();
			cardPanel.revalidate();
			cardPanel.add(listCard, "list");
			cards.show(cardPanel, "list");
		}
	}
	private class RemoveDuplicateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			for(int i=0; i<list.size(); i++){
				int index = list.lastIndexOf(list.get(i) );
				while(index > i){
					listTotalSize -= list.get(index).getSize();
					list.remove(index);
					index = list.lastIndexOf(list.get(i) );// IDEA use a while loop!
				}
			}
			updateBars();
			listCard.removeAll();
			listCard = null;
			makeList();
			listCard.revalidate();
			cardPanel.revalidate();
			cardPanel.add(listCard, "list");
			cards.show(cardPanel, "list");
		}
	}
	private class RemoveDeviceButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			main.getDevices().unmount();
			list.clear();
			listTotalSize = 0;
			updateBars();
			driveList.setSelectedIndex(0);
			
		}
	}
	public static void main(String[] args) {
		new DoveGUI();
	} 
}
