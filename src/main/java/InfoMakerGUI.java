//import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.io.*;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * GUI to collect content information for use across Dove platform.
 */
@SuppressWarnings("serial")
public class InfoMakerGUI extends JFrame {//add extends InfoMaker

  final int WINDOW_WIDTH = 420;
  final int WINDOW_HEIGHT = 360;
  private JPanel panel, prev;
  private JFrame preview;
  private JLabel lMessage, lName, lOrigin, lDesc, lThumb, lThumbStats, lOutput;
  private JTextField tName, tOrigin, tThumb,tThumbStats, tOutput;
  private JTextArea tDesc;
  private JCheckBox cFolder, cVid, cAud, cMus, cDoc, cPic, cOther;
  private JButton bCreate, bPreview, bChoose, bCompress, bClear, bOutput;
  private JMenuBar menuBar;
  private JMenu fileMenu, actionMenu,helpMenu;
  private JMenuItem iEdit, iImport, iSave, iSaveAs, iExit, iCreate, iPreview, 
      iClear, iThumb, iOutput, iHelp, iAbout;
  private Dimension space = new Dimension(5, 0);
  private String outputDest;
  private Properties defaults;
  private boolean isConfigLoaded;
  private File infoCfg;
  //TODO fix resize texfields
  
  /**
   * General Constructor initializes GUI, fetches default values from program
   * directory in user home directory. 
   */
  public InfoMakerGUI(){
    this(new File(System.getProperty("user.home")
        + File.separator+".dove"+File.separator + "info.cfg"));
  }
  
  /**
   * Selected Constructor initializes GUI, fetches default values from given 
   *     configuration file.
   * @param customInfo file 
   */
  public InfoMakerGUI(File customInfo){
    infoCfg = customInfo;
    setTitle("Info Maker");
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    buildPanel();
    add(panel);
    buildMenubar();
    isConfigLoaded = loadDefaults();
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
  
  /**
   * Loads default entry values from default file: info.cfg
   */
  private boolean loadDefaults(){
    return loadDefaults(infoCfg);
  }
  
  /**
   * Loads default entry values from given info.cfg
   */
  private boolean loadDefaults(File infoFile){
    if(infoFile.exists() && infoFile.canRead()){
      defaults = new Properties();
      try{
        defaults.load(new FileInputStream(infoFile));
        tOrigin.setText(defaults.getProperty("origin"));
        cFolder.setSelected(Boolean.parseBoolean(defaults.getProperty("makeFolder", "false")));
        tOutput.setText(defaults.getProperty("outputPath"));
        outputDest=defaults.getProperty("outputPath");
        tDesc.setText(defaults.getProperty("description"));
        return true;
      }catch(IOException io){
        System.out.println("[InfoMaker] Configuration file - Not Found!");
        io.printStackTrace();
        return false;
      }
    }else{
      //File not valid, just return false
      return false;
      
    }
  }
  
  /**
   * Saves the currently entered data to info.cfg file.
   */
  private boolean saveDefaults(){
    return saveDefaults(new File(System.getProperty("user.home")
          + File.separator+".dove"+File.separator + "info.cfg"));
  }
  
  /**
   * Saves the currently entered data to a given file.
   */
  private boolean saveDefaults(File config){
    try{
      //defaults.load(new FileInputStream("info.cfg"));
      FileOutputStream output = new FileOutputStream(config);
      defaults.setProperty("origin", tOrigin.getText() );
      defaults.setProperty("outputPath", tOutput.getText());
      defaults.setProperty("makeFolder", Boolean.toString(cFolder.isSelected()) );
      defaults.setProperty("description", tDesc.getText());
      defaults.store(output, null );
      System.out.println("[InfoMaker] Configuration file completed.");
      return true;
    }catch(IOException io){
      System.out.println("[InfoMaker] info.cfg - Not Found!");
      io.printStackTrace();
      return false;
    }
  }
  
  /**
   * Draws the main input panel. Collects information to be content data.
   */
  private void buildPanel(){
    lMessage = new JLabel("Provide the following information about the content.");
    lName = new JLabel("Name:", JLabel.TRAILING);
    lOrigin = new JLabel("Origin:", JLabel.TRAILING);
    lDesc = new JLabel("Description:", JLabel.TRAILING);
    lThumb = new JLabel("Thumbnail:", JLabel.TRAILING);
    lThumbStats = new JLabel("Thumbnail Info:", JLabel.TRAILING);
    lOutput = new JLabel("Output Path:", JLabel.TRAILING);
        
    tName = new JTextField(10);
    tName.setActionCommand(tName.getText() );
    tName.setToolTipText("Hit Enter to apply changes.");
    tName.addActionListener(new MakeFolderListener() );
    tName.addFocusListener(new NameFocusListener() );
    tOrigin = new JTextField(10);
    tDesc = new JTextArea(5,10);
    tThumb = new JTextField(10);
    tThumb.setEditable(false);
    tThumbStats = new JTextField(10);
    tThumbStats.setText("N/A");
    tThumbStats.setEditable(false);
    JScrollPane scroll = new JScrollPane(tDesc);
    tDesc.setLineWrap(true);
    
    lName.setLabelFor(tName);
    lOrigin.setLabelFor(tOrigin);
    lDesc.setLabelFor(scroll);
    tOutput = new JTextField(10);
    tOutput.setEditable(false);
    
    cFolder = new JCheckBox("Create Folder");
    cFolder.addActionListener(new MakeFolderListener());
    cFolder.setToolTipText("Output files into this folder name.");
    cVid = new JCheckBox("Video");
    cAud = new JCheckBox("Audio");
    cMus = new JCheckBox("Music");
    cDoc = new JCheckBox("Document");
    cPic = new JCheckBox("Picture");
    cOther = new JCheckBox("Other");
    
    bCreate = new JButton("Create Files...");
    bCreate.addActionListener(new CreateButtonListener());
    bPreview  = new JButton("Preview...");
    //bPreview.addActionListener(new MakeFolderListener());
    bPreview.addActionListener(new PreviewButtonListener());
    bChoose = new JButton("...");
    bChoose.addActionListener(new ThumbButtonListener());
    bCompress = new JButton("Resize Image");
    bCompress.addActionListener(new CompressImageListener());
    bCompress.setToolTipText("Resizes file down to the largest resolution nessecary, keeping aspect ratio.");
    bClear = new JButton("Clear");
    bClear.addActionListener(new ClearButtonListener());
    bOutput = new JButton("...");
    bOutput.addActionListener(new OutputLocationListener() );
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5) );
    
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
    Dimension space = new Dimension(5,0);
      
    Box bName = new Box(BoxLayout.LINE_AXIS);
      bName.add(lName);
      bName.add(Box.createRigidArea(space));
      bName.add(tName);
      bName.add(Box.createRigidArea(space));
      bName.add(cFolder);
    Box bOrigin = new Box(BoxLayout.LINE_AXIS);
      bOrigin.add(lOrigin);
      bOrigin.add(Box.createRigidArea(space));
      bOrigin.add(tOrigin);
    Box bDesc = new Box(BoxLayout.LINE_AXIS);
      bDesc.add(lDesc);
      bDesc.add(Box.createRigidArea(space));
      bDesc.add(scroll);
    Box bThumb = new Box(BoxLayout.LINE_AXIS);
      bThumb.add(lThumb);
      bThumb.add(Box.createRigidArea(space));
      bThumb.add(tThumb);
      bThumb.add(Box.createRigidArea(space));
      bThumb.add(bChoose);
    Box bxThumbStats = new Box(BoxLayout.LINE_AXIS);
      bxThumbStats.setAlignmentY(RIGHT_ALIGNMENT);
      //bxThumbStats.add(Box.createHorizontalStrut(0));
      bxThumbStats.add(lThumbStats);
      bxThumbStats.add(Box.createRigidArea(space));
      bxThumbStats.add(tThumbStats);
      bxThumbStats.add(Box.createRigidArea(space));
      bxThumbStats.add(bCompress);
      //bxThumbStats.add(Box.createHorizontalGlue());
    Box bxOutput = new Box(BoxLayout.LINE_AXIS);
      bxOutput.add(lOutput);
      bxOutput.add(Box.createRigidArea(space));
      bxOutput.add(tOutput);
      bxOutput.add(Box.createRigidArea(space));
      bxOutput.add(bOutput);
    box.add(lMessage);	
    box.add(bName);
    box.add(bOrigin);
    box.add(bDesc);
    box.add(bThumb);
    box.add(bxThumbStats);
    box.add(bxOutput);
    panel.add(box);
    
    JPanel tags = new JPanel();
    tags.setLayout(new GridLayout(2,3));
    tags.setBorder(BorderFactory.createTitledBorder("Content Type"));
    tags.add(cVid);
    tags.add(cAud);
    tags.add(cMus);
    tags.add(cDoc);
    tags.add(cPic);
    tags.add(cOther);
    tags.setAlignmentY(CENTER_ALIGNMENT);
    panel.add(tags);
    
    Box bottom = new Box(BoxLayout.LINE_AXIS);
    //bottom.add(tags);
    bottom.add(Box.createHorizontalGlue());
    bottom.add(bPreview);
    bottom.add(Box.createRigidArea(new Dimension(5,0)));
    bottom.add(bClear);
    bottom.add(Box.createRigidArea(new Dimension(5,0)));
    bottom.add(bCreate);
    //bottom.add(Box.createRigidArea(new Dimension(5,0)));
    //
    bottom.add(Box.createHorizontalGlue());
    panel.add(bottom);
  }
  
  /**
   * Clears all entered data from form.
   */
  private void clearPanel(){
    tThumbStats.setText("N/A");
    tName.setText("");
    tOrigin.setText("");
    tDesc.setText("");
    tThumb.setText("");
    tOutput.setText("");
    cVid.setSelected(false);
    cAud.setSelected(false);
    cMus.setSelected(false);
    cDoc.setSelected(false);
    cPic.setSelected(false);
    cOther.setSelected(false);
    updatePanel();
  }
  
  /**
   * Sets menubar at top of main input panel.
   */
  private void buildMenubar(){
    menuBar = new JMenuBar();
    // File Menu
    fileMenu = new JMenu("File");
      iEdit = new JMenuItem("Open/Edit...");
      iEdit.addActionListener(new EditListener());
      iImport = new JMenuItem("Import Defaults...");
      iImport.addActionListener(new ImportListener() );
      iSave = new JMenuItem("Save Defaults");
      iSave.setActionCommand("save");
      iSave.addActionListener(new ExportListener() );
      iSaveAs = new JMenuItem("Save Defaults As...");
      iSaveAs.setActionCommand("saveAs");
      iSaveAs.addActionListener(new ExportListener());
      iExit = new JMenuItem("Exit");
      iExit.addActionListener(new ExitListener() );
      fileMenu.add(iEdit);
      fileMenu.addSeparator();
      fileMenu.add(iImport);
      fileMenu.add(iSave);
      fileMenu.add(iSaveAs);
      fileMenu.add(iExit);
    // Action Menu
    actionMenu = new JMenu("Action");
      iCreate = new JMenuItem("Create Files...");
      iCreate.addActionListener(new CreateButtonListener() );
      iThumb = new JMenuItem("Import Thumbnail Image...");
      iThumb.addActionListener(new ThumbButtonListener() );
      iOutput = new JMenuItem("SetOutputDirectory");
      iOutput.addActionListener(new OutputLocationListener() );
      iPreview = new JMenuItem("Preview Output");
      iPreview.addActionListener(new PreviewButtonListener() );
      iClear = new JMenuItem("Clear All Entries");
      iClear.addActionListener(new ClearButtonListener());
      actionMenu.add(iCreate);
      actionMenu.add(iThumb);
      actionMenu.add(iPreview);
    //Option Menu
    //optionMenu = new JMenu("Option");
      //cFolderMenu = new JCheckBox("Create Folder");
      //cFolderMenu.addActionListener(new MakeFolderListener());
      //optionMenu.add(cFolderMenu);
    // Help Menu
    helpMenu = new JMenu("Help");
      iHelp = new JMenuItem("Help Information");
      iHelp.addActionListener(new HelpListener() );
      iAbout = new JMenuItem("About InfoMaker");
      iAbout.addActionListener(new AboutListener() );
      helpMenu.add(iHelp);
      helpMenu.add(iAbout);
    menuBar.add(fileMenu);
    //menuBar.add(optionMenu);
    menuBar.add(actionMenu);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);
  }
  
  /**
   * Draw side window that show preview of data, from currently entered fields.
   */
  private void buildPreview(){
    preview = new JFrame("Preview");
    preview.setSize(620, 380);
    prev = new JPanel();
    prev.setLayout(new BorderLayout(5,5));
    prev.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    //Menubar
    JMenuBar prevBar = new JMenuBar();
      JMenuItem refresh = new JMenuItem("Refresh");
      refresh.addActionListener(new PreviewRefreshListener() );
      JMenuItem done = new JMenuItem("Exit");
      done.addActionListener(new PreviewDoneListener() );
    prevBar.add(refresh);
    prevBar.add(done);
    preview.setJMenuBar(prevBar);
    //Thumbnail Image
    BufferedImage img = null;
    JLabel thumbIcon;
      try{
        img = ImageIO.read(new File(tThumb.getText()));
      }catch(IOException e){
        // That file is not valid!! show no image
      }
      if(img != null){
        ImageIcon ico;
        if(img.getHeight() > img.getWidth() ){
          ico= new ImageIcon(img.getScaledInstance(-1, 300, Image.SCALE_FAST ));
        }else{
          ico= new ImageIcon(img.getScaledInstance(300, -1, Image.SCALE_FAST ));
        }
        thumbIcon = new JLabel(ico, SwingConstants.CENTER);
      }else{
        thumbIcon = new JLabel("Thumbnail N/A", SwingConstants.CENTER );
        thumbIcon.setSize(300, 300);
        //thumbIcon.setMinimumSize(new Dimension(300,300));
        //thumbIcon.setPreferredSize(new Dimension(300,300));
      }
      thumbIcon.setPreferredSize(new Dimension(300,300));
      thumbIcon.setAlignmentX(CENTER_ALIGNMENT);
    Box bThumb = new Box(BoxLayout.LINE_AXIS);
      bThumb.add(thumbIcon);
      bThumb.setAlignmentX(0.5f);
    prev.add(thumbIcon, BorderLayout.WEST);
    //TextBoxes
    Box bTexts = new Box(BoxLayout.PAGE_AXIS);
    Box bName = new Box(BoxLayout.LINE_AXIS);
      bName.add(new JLabel("Name:"));
      bName.add(Box.createRigidArea(space));
      JTextField tNameLock = new JTextField(tName.getText());
      tNameLock.setAlignmentX(LEFT_ALIGNMENT);
      //tNameLock.setMaximumSize(tNameLock.getPreferredSize());
      tNameLock.setEditable(false);
      bName.add(tNameLock);
    Box bOrigin = new Box(BoxLayout.LINE_AXIS);
      bOrigin.add(new JLabel("Origin:"));
      bOrigin.add(Box.createRigidArea(space));
      JTextField tOriginLock = new JTextField(tOrigin.getText());
      //tOriginLock.setMaximumSize(tOriginLock.getPreferredSize());
      tOriginLock.setEditable(false);
      bOrigin.add(tOriginLock);
    Box bDesc = new Box(BoxLayout.LINE_AXIS);
      bDesc.add(new JLabel("Desc.:"));
      bDesc.add(Box.createRigidArea(space));
      JTextArea tDescLock = new JTextArea(6, 15);
      tDescLock.setText(tDesc.getText());
      tDescLock.setEditable(false);
      tDescLock.setLineWrap(true);
      JScrollPane scroll2 = new JScrollPane(tDescLock);
      bDesc.add(scroll2);
    Box bList = new Box(BoxLayout.LINE_AXIS);
      bList.setBorder(BorderFactory.createTitledBorder("Example Output Files:"));
      JTextArea taList = new JTextArea(4,10);
      taList.setEditable(false);
      taList.setText(
          InfoMaker.showExampleWriteOut(new File(tOutput.getText()) , new File(tThumb.getText()) ) );
      JScrollPane scroll = new JScrollPane(taList);
      bList.add(scroll);
    bTexts.add(bName);
    //bTexts.add(Box.createVerticalGlue());
    bTexts.add(bOrigin);
    //bTexts.add(Box.createVerticalGlue());
    bTexts.add(bDesc);
    //bTexts.add(Box.createVerticalGlue());
    JPanel tags = new JPanel();
    tags.setLayout(new GridLayout(2,3));
    tags.setBorder(BorderFactory.createTitledBorder("Content Type"));
    JCheckBox cVidLock = new JCheckBox(cVid.getText(), cVid.isSelected());
    JCheckBox cAudLock = new JCheckBox(cAud.getText(), cAud.isSelected());
    JCheckBox cMusLock = new JCheckBox(cMus.getText(), cMus.isSelected());
    JCheckBox cDocLock = new JCheckBox(cDoc.getText(), cDoc.isSelected());
    JCheckBox cPicLock = new JCheckBox(cPic.getText(), cPic.isSelected());
    JCheckBox cOtherLock = new JCheckBox(cOther.getText(), cOther.isSelected());
    cVidLock.setEnabled(false);
    cAudLock.setEnabled(false);
    cMusLock.setEnabled(false);
    cDocLock.setEnabled(false);
    cPicLock.setEnabled(false);
    cOtherLock.setEnabled(false);
    tags.add(cVidLock);
    tags.add(cAudLock);
    tags.add(cMusLock);
    tags.add(cDocLock);
    tags.add(cPicLock);
    tags.add(cOtherLock);
    tags.setAlignmentY(CENTER_ALIGNMENT);
    bTexts.add(tags);
    prev.add(bTexts, BorderLayout.CENTER);
    prev.add(bList, BorderLayout.SOUTH);
    preview.add(prev);
    //preview.pack();
  }
  
  /**
   * 
   */
   //TODO update panel implement or delete.
  private void updatePanel(){
    //tThumb.revalidate();
  }
  
  /**
   * Sets JLabel of filesize,  height and width of givel thumnail image.
   * @param thumbfile file 
   */
  private void updateThumbStats(File thumbFile){
    //File thumbFile = thumb.getSelectedFile();
    tThumb.setText(thumbFile.getAbsolutePath());
     BufferedImage img = null;
    //JLabel thumbIcon;
    try{
      panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      img = ImageIO.read(new File(tThumb.getText()));
    }catch(IOException ioe){
      // That file is not valid!! show no image
    }finally{
      panel.setCursor(Cursor.getDefaultCursor());
    }
    String len = Dove.humanReadableByteCount(new File(tThumb.getText()).length(), true);
    String str = "Width x Height: " + img.getWidth()+"x"+img.getHeight();
    tThumbStats.setText(str+",  File Size: "+ len);
  }
  
  /**
   * Adds file separator to given string, unless last index is a file separator
   * @param in given string
   * @returns string with file separator at end
   */
  private String addFileSeparator(String in){
    in += File.separator;
    String compare = File.separator + File.separator;
    if(in.lastIndexOf(compare) > 0){
      return in.substring(0, in.length() -2 );
    }else{
      return in;
    }
  }
  
  /**
   * Updates Output textfield when Name textfield losses focus. 
   */
  private class NameFocusListener implements FocusListener{
    //@Override
    public void focusGained(FocusEvent arg0) {}
    //@Override
    public void focusLost(FocusEvent arg0) {
      if(cFolder.isSelected()){
        //Append to output String 
        tOutput.setText(outputDest + File.separator	+ tName.getText() 
        + File.separator);
      }else{
        //clear from output string
        tOutput.setText(outputDest + File.separator);
      }	
    }
  }
  
  /**
   * Updates Output textfield when Make Folder checkbox is ticked.
   */
  private class MakeFolderListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(cFolder.isSelected()){
        //Append to output String 
        tOutput.setText(outputDest + File.separator	+ tName.getText() 
        + File.separator);
      }else{
        //clear from output string
        tOutput.setText(outputDest + File.separator);
      }
    }
  }
  
  /**
   * Open a file browser to chose output location for content files.
   */
  private class OutputLocationListener implements ActionListener{
    public void actionPerformed(ActionEvent e1) {
      JFileChooser loc = new JFileChooser();
      loc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int n = loc.showSaveDialog(null);
      if(n == JFileChooser.APPROVE_OPTION){
        outputDest = loc.getSelectedFile().getAbsolutePath();
        //outputDest = addFileSeparator(outputDest);
        tOutput.setText(outputDest 	+ (cFolder.isSelected() ? 
                File.separator + tName.getText() : "" ) );
      }else if(n == JFileChooser.CANCEL_OPTION) {
        // Do nothing, the window auto-closes.
      }
    }
  }
  
  /**
   * Runs checks of entered data and if successful creates files/folders.
   */
  private class CreateButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e1) {
      boolean ready = true;
      File path = new File( tOutput.getText() );
      if(tName.getText().isEmpty()){
        JOptionPane.showMessageDialog(panel,"Name field is necessary.",
              "Error", JOptionPane.ERROR_MESSAGE);
        ready = false;
      }if(tOutput.getText().isEmpty()){
        JOptionPane.showMessageDialog(panel,"Output Path is necessary.",
              "Error", JOptionPane.ERROR_MESSAGE);
        ready = false;
      }if(path.exists() || new ContentItem(path).hasData()){
        String header = "Are you sure you want write over the data already at the location:\n"+
          path.getAbsolutePath();
        String[] opts = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(null, header, "Overwrite Files?", 
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0] );
        if(n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION){
          ready = false;
        }else if(n == JOptionPane.YES_OPTION){
          ready = true;
        }
      }if(ready){//write it
        path.mkdir();
        Info result = new Info(tName.getText(), tOrigin.getText(), tDesc.getText(), 
            cVid.isSelected(), cAud.isSelected(), cMus.isSelected(), 
            cDoc.isSelected(), cPic.isSelected(), cOther.isSelected() );
        try{
          if(tThumb.getText().isEmpty()){
            new InfoMaker(result, path);
            JOptionPane.showMessageDialog(panel,"Successfuly created files.",
                  "Done", JOptionPane.INFORMATION_MESSAGE);
          }else{
            new InfoMaker(result, path, new File(tThumb.getText()) );
            JOptionPane.showMessageDialog(panel,"Successfuly created files.",
                  "Done", JOptionPane.INFORMATION_MESSAGE);
          }
        }catch (IOException  e2) {
          e2.printStackTrace();
          JOptionPane.showMessageDialog(panel,"Files not created.\n" +
              "Error: Unable to write to location. No files copied.",
                "Error", JOptionPane.ERROR_MESSAGE);
          System.out.println("Error: Unable to write to location. No files copied.");
          //exceptions - there should be none, write permissions?
        }
      }
    }
  }
  
  /**
   * Builds preview window and displays it. 
   */
  private class PreviewButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(cFolder.isSelected()){
        //Append to output String 
        tOutput.setText(outputDest + File.separator	+ tName.getText() + File.separator);
      }else{
        //clear from output string
        tOutput.setText(outputDest + File.separator);
      }
      try{
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        buildPreview();  
      }finally{
        setCursor(Cursor.getDefaultCursor());
      }
      preview.setVisible(true);
    }
  }
  
  /**
   * Shows file browser to choose thumbnail image. Updates stats.
   */
  private class ThumbButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFileChooser thumb = new JFileChooser();
      int status = thumb.showOpenDialog(null);
      if(status == JFileChooser.APPROVE_OPTION){
        /*File thumbFile = thumb.getSelectedFile();
        tThumb.setText(thumbFile.getAbsolutePath());
         BufferedImage img = null;
        //JLabel thumbIcon;
        try{
          panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          img = ImageIO.read(new File(tThumb.getText()));
        }catch(IOException ioe){
          // That file is not valid!! show no image
        }finally{
          panel.setCursor(Cursor.getDefaultCursor());
        }
        String len = Dove.humanReadableByteCount(new File(tThumb.getText()).length(), true);
        String str = "Width x Height: " + img.getWidth()+"x"+img.getHeight();
        tThumbStats.setText(str+",  File Size: "+ len);
        */
        updateThumbStats(thumb.getSelectedFile());
      }else{
        //Chill.
      }
      tThumb.repaint();
    }
  }
  
  /**
   * Runs compression on thumbnail image. Built- in 
   * compression: "TYPE_4BYTE_ABGR"
   */
  private class CompressImageListener implements ActionListener{
    public void actionPerformed(ActionEvent e){ 
      //TODO when finalized move code to InfoMaker.class
      try{
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BufferedImage img = ImageIO.read( new File(tThumb.getText()));
        int h = 300, w = 300;
        //Computes Aspect Ratio
        if(img.getWidth() * h < img.getHeight() * w) {
              w = img.getWidth()*h/img.getHeight();
          }else{
            h = img.getHeight()* w /img.getWidth();
          }
        BufferedImage scaleBuff = new BufferedImage(w, h, 
            BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = scaleBuff.createGraphics();
        try{
          g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
          g.drawImage(img, 0, 0, w, h, null);
        }finally{ //waits for g.draw image to finish
          g.dispose();
        }
        //ImageIO.write(image, formatName, outputFile);
        File thumb = File.createTempFile("thumb", ".png");
        ImageIO.write(scaleBuff, "png", thumb);
        tThumb.setText(thumb.getAbsolutePath());
        String len = Dove.humanReadableByteCount(
            new File(tThumb.getText()).length(), true);
        String str = scaleBuff.getWidth()+"x"+scaleBuff.getHeight() + 
            "  File Size: "+ len;
        tThumbStats.setText("Width x Height: "+str);
      }catch(IOException io){
        //File not ready.
      }catch(NullPointerException nl){
        //
      }finally{
         panel.setCursor(Cursor.getDefaultCursor());
      }
      
    }
  }
  
  /**
   * Open data from data file for editing. Allow Overwrite later w/ new info.
   */
  private class EditListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFileChooser choice = new JFileChooser(outputDest );
      choice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      choice.setDialogTitle("Chose content folder to edit.");
      int n = choice.showDialog(null, "Open");
      if(n == JFileChooser.APPROVE_OPTION){
        //loadDefaults(choice.getSelectedFile() );
        try{
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          ContentItem load = new ContentItem(choice.getSelectedFile() );
          tName.setText(load.getInfo().getName());
          tOrigin.setText(load.getInfo().getOrigin());
          tDesc.setText(load.getInfo().getDesc());
          outputDest = load.getFile().getParent();
          if(load.hasImage()){
            tThumb.setText(load.getImageFile().getAbsolutePath());
            if(load.getImageFile().length() > 0f){
              updateThumbStats(load.getImageFile());
            }
            //tThumb.setText(load.getFile().getAbsolutePath()+ File.separator+ )
          }
          tOutput.setText(load.getFile().getAbsolutePath());
          cVid.setSelected(load.getInfo().isVideo());
          cAud.setSelected(load.getInfo().isAudio());
          cMus.setSelected(load.getInfo().isMusic());
          cDoc.setSelected(load.getInfo().isDocument());
          cPic.setSelected(load.getInfo().isPictures());
          cOther.setSelected(load.getInfo().isOther());
        }catch (NullPointerException io){
          clearPanel();
          JOptionPane.showMessageDialog(panel,
              "There is no data at this location.",
              "Error", JOptionPane.ERROR_MESSAGE);
        }finally{
          setCursor(Cursor.getDefaultCursor());
        }
      }else{ //if(n == JFileChooser.CANCEL_OPTION) {
      }
    }
  }
  
  /**
   * Opens file chooser and reads a custom defaults file.
   */
  private class ImportListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFileChooser choice = new JFileChooser(infoCfg );
      choice.setFileSelectionMode(JFileChooser.FILES_ONLY);
      choice.setDialogTitle("Chose InfoMaker Config file.");
      int n = choice.showDialog(null, "Set");
      if(n == JFileChooser.APPROVE_OPTION){
        try{
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          loadDefaults(choice.getSelectedFile());
          System.out.println("[InfoMakerGUI] Changed config file to " + 
          choice.getSelectedFile().getAbsolutePath());
        }finally{
          setCursor(Cursor.getDefaultCursor());
        }
      }else{ //if(n == JFileChooser.CANCEL_OPTION) {
      }
      //buildPanel(); // fix calling build panel
    }
  }
/*	private class ImportOtherListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFileChooser choice = new JFileChooser(infoCfg );
      choice.setFileSelectionMode(JFileChooser.FILES_ONLY);
      choice.setDialogTitle("Chose InfoMaker Config file.");
      int n = choice.showDialog(null, "Set");
      if(n == JFileChooser.APPROVE_OPTION){
        loadDefaults(choice.getSelectedFile() );
         System.out.println("[InfoMakerGUI] Changed config file to " + choice.getSelectedFile().getAbsolutePath());
      }else{ //if(n == JFileChooser.CANCEL_OPTION) {
      }
    }
  }
*/
  
  /**
   * Exports the entered data to a custom defaults file.
   */
  private class ExportListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      if(e.getActionCommand().equals("save")){
        String header = "Are you sure you want to save the entered info as defaults to:\n"+
            infoCfg.getAbsolutePath();
        String[] opts = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(null, header, "Save Defaults?", 
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0] );
        if(n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION){
          //Do nothing.
        }else if(n == JOptionPane.YES_OPTION){
          saveDefaults();
        }
      }else if(e.getActionCommand().equals("saveAs")){
        JFileChooser choice = new JFileChooser(infoCfg );
        choice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        choice.setDialogTitle("Save configuration file");
        int n = choice.showDialog(null, "Save");
        if(n == JFileChooser.APPROVE_OPTION){
          saveDefaults(choice.getSelectedFile());
        }else if(n == JFileChooser.CANCEL_OPTION) {
          //Do Nothing.
        }
      }
    }
  }
  
  /**
   * Terminates program.
   */
  private class ExitListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      System.exit(0);
    }
  }
  
  /**
   * Launches Help window.
   */
  private class HelpListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      JFrame help = new JFrame("Help Information");
      help.add(new JLabel("Help on using this application is provided online."));
      help.setSize(200, 120);
      help.setVisible(true);
    }
  }
  
  /**
   * Launches About Window.
   */
  private class AboutListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      //JFrame about = new JFrame("About InfoMaker");
      //about.add(new JLabel("Info"));//TODO add info to about page.
      //about.setSize(160,200);
      //about.setVisible(true);
      String about = "";
      JOptionPane.showMessageDialog(panel, about, "About Dove", JOptionPane.PLAIN_MESSAGE, null);

    }
  }
  
  /**
   * Clear all entered data.
   */
  private class ClearButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      // Erase all fields and clears tags
      clearPanel();
    }
  }
  
  /**
   * Update preview window and redisplays.
   */
  private class PreviewRefreshListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      preview.setVisible(false);
      preview.removeAll(); //TODO preview needs smoother update.
      buildPreview();
      preview.setVisible(true);
    }
  }
  
  /**
   * Closes preview window.
   */
  private class PreviewDoneListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
      preview.dispose();
    }
  }
  
  /**
   * Main method. 
   */
  public static void main(String[] args){
    //config file as first arg; so configs can be loaded be shortcut
    if(args.length != 0 ){
      if(new File(args[0]).canRead()){
        new InfoMakerGUI(new File(args[0]));
      }
    }else{//No params  passed
      new InfoMakerGUI();
    }
  }
}
