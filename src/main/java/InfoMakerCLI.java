public InfoMakerCLI{
    /**
   * Main method: interactive input to create content informational files.
   * @param args not used
   */
  public static void main(String[] args) throws IOException{
    // Creates a java readable file: info.dat
    // and a human readable info.txt
    String name, org, desc;
    boolean vid, aud, mus, doc, pic, other;
    Scanner key = new Scanner(System.in);
    
    boolean done = false;
    while(!done){
      System.out.println("Please provide the following information about the " 
        + "content. Press enter after each selection");
      System.out.println("Name? ");
      name = key.nextLine();
      //key.nextLine();
    
      System.out.println("Origin? ");
      org = key.nextLine();
      //key.nextLine();
    
      System.out.println("Description? ");
      desc = key.nextLine();
      //key.nextLine();
    
      System.out.println("Video? ");
      vid = key.nextBoolean();
    
      System.out.println("Audio? ");
      aud = key.nextBoolean();
    
      System.out.println("Music? ");
      mus = key.nextBoolean();
    
      System.out.println("Document? ");
      doc = key.nextBoolean();
    
      System.out.println("Pictures? ");
      pic = key.nextBoolean();
    
      System.out.println("Other? ");
      other = key.nextBoolean();
      
      System.out.println("Is everything correct?");
      done = key.nextBoolean();
    }
    key.close();
    
    System.out.println();
    Info result = new Info(name, org, desc, vid, aud, mus, doc, pic, other);
    File currentDir = new File(System.getProperty("user.dir"));
    
    if(args.length > 0 ){
      File thumb = new File(args[0]);
      if(thumb.isFile()){
        System.out.println("Thumbnail Image Filename: " + args[0]);
        writeOut(result, currentDir,new File(args[0]));
        copyImg(currentDir, new File(args[0]));
      }
    }else{
      writeOut(result, currentDir);
    }
    System.out.println("Data created successfully.");
  }
}