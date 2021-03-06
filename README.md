# README #

Project Dove is a java-powered kiosk to load files onto flash drive, or mobile device over wifi.

## TODO List
```
Notation guide: Leave a space.
  none not started
  .. wrapped line
  XX canceled idea, impossibility
  -- completed
  >> in Progress
  ?? make decision, or needs research
 
    DoveCLI
    -- InfoMakerCLI
    -- drive Skipper class
    -- thumbnail only button
    -- file list on GUI
    XX 'df -h' to get partition size estimates? --> df only reads mounted fs, 
    -- config and info config setters -> Wizards
    -- tui and gui for config.cfg file
    -- look for config files in home directory, System.getProperty("user.home");
    -- move dove.txt info into header of html file for fewer files.
    -- how can program ask for root/admin->> 
    wrap loaders in shell script that calls gksudo
    -- search-no popup, takeover center stage
    -- ContentItem arraylist of filenames
    in full kiosk preload thumbs for speed over memory, eventually
    -- splash page on DoveGUI with loading bar, need RS
    >> code to listen for storage devices, esp. removed before unmount
    -- list page needs undo button
    -- thumb compression-better
    -- Infomaker editing, better/smoother Change Listener
      .. called on every change, to keep everything updated?
    ConfigWizard need explanations in main window
    -- fix tags search
    -- thumbnails only button
    XX restrict sort type to after type is chosen only, when clear go default
    -- InfomakergGui overwrites? [it did, fixed]
    XX add sort best type
    -- Align thumbnails
    >> Copy Progress Bar
    help/about panels
    DoveKiosk breadcrumbs at top
    -- bash code-better make class->> nope just use standard better
    Documentation and information
    prelim website
    XX ask Reddit for project name
    check to see if file sizes can fit on filesystem and disable aprop. content
    -- add config to set name of Dove folder on dest. drive
    -- suggest content origin = folder name
    add License or copyright information, if present
    XX Fix wildcard input statements
    -- limit line width to 80 char
    logically order methods/members,
    order overloaded methods, never split
    -- convert tabs to two spaces indents
    break lines before symbols
    Flatten all toString to true one-liners
    add logs ability and log levels: use SLF4J with Log4J
    add receipts, give time and date of transaction history in dove folder
    add flavor and legal payload from content origin w/ Folder creation
    >> change build type to maven, and flesh out
    add command-line option parse
    ask to copy same content set to different device
    login portal to advanced mode:
    .. ability to change settings from inside program and restart
    line breaks and other simple formatting in content description
    add JUnit testing
```
###Current Version: 0.0.8

###Bookmarks
* [Get JProgressBars update](http://stackoverflow.com/questions/13574461/need-to-have-jprogress-bar-to-measure-progress-when-copying-directories-and-
* [Copy Estimates](http://stackoverflow.com/questions/1152208/computing-estimated-times-of-file-copies-movements)
* [File Size Limits](http://stackoverflow.com/questions/21926721/bash-how-to-find-the-max-supported-file-size-of-a-filesystem)
 	

### Online IDEs
* [http://www.compilejava.net/](http://www.compilejava.net/)
* [https://ideone.com/](https://ideone.com/)
* [https://c9.io/](https://c9.io/) Good ide, compiles, cant run.
* [https://codenvy.com/](https://codenvy.com/) Great ide.
** [Tutorials](http://docs.codenvy.com/user/tutorials/)

### Code Help
* [Google Code Standards](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html)
* [JavaDoc](http://www.oracle.com/technetwork/articles/java/index-137868.html)
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)


### How do I get set up? ###
**Installation**
This README would normally document whatever steps are necessary to get your application up and running.

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Justin A. Williams; justinawcs(at)gmail.com
* Other community or team contact