import java.io.Serializable;
import java.util.Date;

public class Info implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String origin;
	private String desc;
	private Date born;
	private boolean vid, aud, mus, doc, pic, other;
	
	//Explicit date
	public Info(String nm, String or, String ds, Date dt, boolean v, 
				boolean a, boolean m, boolean d, boolean p, boolean o){
		name = nm;
		origin = or;
		desc = ds;
		born = dt;
		vid = v;
		aud = a;
		mus = m;
		doc = d;
		pic = p;
		other = o;

	}//Date = now
	public Info(String nm, String or, String ds, boolean v, 
			boolean a, boolean m, boolean d, boolean p, boolean o){
	name = nm;
	origin = or;
	desc = ds;
	born = new Date();
	vid = v;
	aud = a;
	mus = m;
	doc = d;
	pic = p;
	other = o;
	}
	
	public String getName() {
		return name;
	}
	public String getOrigin() {
		return origin;
	}
	public String getDesc() {
		return desc;
	}
	public Date getDate() {
		return born;
	}
	public boolean isVideo() {
		return vid;
	}
	public boolean isAudio() {
		return aud;
	}
	public boolean isMusic() {
		return mus;
	}
	public boolean isDocument() {
		return doc;
	}
	public boolean isPictures() {
		return pic;
	}
	public boolean isOther() {
		return other;
	}
	public boolean[] getTags(){
		boolean[] temp = new boolean[] {vid, aud, mus, doc, pic, other}; 
		return temp;
	}
	public int getTagSum(){
		int i=0;
		for(boolean b:getTags()){
			i += b ? 1 : 0; 
		}
		return i;
	}
	
	public String getTagsString(){
		if(getTagSum()==0){
			return "None.";
		}
		String hold = "";
		hold += vid ? "Video, " : "";
		hold += aud ? "Audio, " : "" ;
		hold += mus ? "Music, " : "" ;
		hold += doc ? "Document, " : "" ;
		hold += pic ? "Pictures, " : "" ;
		hold += other ? "Other, " : "" ;
		String temp = hold.substring(0, hold.length()-2 ) + ".";
		return temp;
	}
	
	public String toString(){
		return "Name: " + name + "\n" +
				"Origin: " + origin + "\n" + 
				"Description: " + desc + "\n" + 
				"Date: " + born.toString() + "\n" +
				"Video: " + vid + "\n" +
				"Audio: " + aud + "\n" +
				"Music: " + mus + "\n" +
				"Document: " + doc + "\n" +
				"Pictures: " + pic + "\n" +
				"Other: " + other;
	}
	public String toHtml(){
		String h1 = "<!--\n" + toString() + "\n-->\n";  
		h1 += "<html><body style='width:350px' ><table> ";
		h1 += "<tr><td halign='center' colspan='2'><img alt='Thumbnail: N/A' />";
		h1 += "<tr><td valign='baseline'>Name:</td> <td>"+ name +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Origin:</td> <td>"+ origin +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Description:</td> <td>"+ desc +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Date:</td> <td>"+ born.toString() +"</td></tr>";
		h1 += "<tr><td>Media Type:</td> <td>"+ getTagsString() +"</td></tr>";
		h1 += "</table></body></html>";
		return h1;
	}
	public String toHtml(String thumbExt){
		String h1 = "<!--\n" + toString() + "\n-->\n";  
		h1 += "<html><body style='width:350px' ><table> ";
		h1 += "<tr><td halign='center' colspan='2'><img src='thumb." + thumbExt +" ' />";
		h1 += "<tr><td valign='baseline'>Name:</td> <td>"+ name +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Origin:</td> <td>"+ origin +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Description:</td> <td>"+ desc +"</td></tr>";
		h1 += "<tr><td valign='baseline'>Date:</td> <td>"+ born.toString() +"</td></tr>";
		h1 += "<tr><td>Media Type:</td> <td>"+ getTagsString() +"</td></tr>";
		h1 += "</table></body></html>";
		return h1;
	}
	
}
