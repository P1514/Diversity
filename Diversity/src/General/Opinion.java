package General;

import java.util.ArrayList;
import java.sql.Date;
import java.util.concurrent.ConcurrentHashMap;

//Opinion Individual Object
public class Opinion {

	private Post main;
	private int author_id; // String
	private String author_id2;
	private ArrayList<Post> comments = new ArrayList<Post>();
	private String URI = "";
	private double reach = 0;
	private double polarity = 0;
	private double total_inf = 0;
	private Date timestamp;
	private String pss;
	private Integer product;

	public Opinion(Post _main, String _pss, int _product) {
		this.main = _main;
		this.author_id = main.getUID();
		timestamp = main.getTime();
		pss = _pss;
		product = _product;

	}
	
	public Opinion(Post _main, String _pss, int _product, String _URI) {
		this.main = _main;
		this.author_id2 = main.getUID(false);
		timestamp = main.getTime();
		pss = _pss;
		product = _product;
		URI=_URI;

	}

	public void evalReach(double avgPost, double avgLikes, double avgViews) {
		this.reach = Settings.pWcomments * (ncomments() / avgPost) + Settings.pWlikes * (nlikes() / (avgLikes != 0 ? avgLikes : 1))
				+ Settings.pWviews * (nviews() / (avgViews != 0 ? avgViews : 1));
	}

	public void evalPolarity(ConcurrentHashMap<Integer, Author> authordb) {
		total_inf = authordb.get(author_id).getInfluence();
		polarity = total_inf * main.getPolarity();

		comments.forEach((v) -> {
			total_inf += authordb.get(v.getUID()).getInfluence();
			polarity += v.getPolarity() * authordb.get(v.getUID()).getInfluence();

		});

		polarity = polarity / total_inf;
	}
	
	public void evalPolarity2(ConcurrentHashMap<String, Author> authordb) {
		total_inf = authordb.get(author_id2).getInfluence();
		polarity = total_inf * main.getPolarity();

		comments.forEach((v) -> {
			total_inf += authordb.get(v.getUID(false)).getInfluence();
			polarity += v.getPolarity() * authordb.get(v.getUID(false)).getInfluence();

		});

		polarity = polarity / total_inf;
	}

	public void addcomment(Post _comment) {
		comments.add(_comment);
	}

	public double getPolarity() {
		return polarity;
	}

	public double getReach() {
		return reach;
	}

	public int getUID() {
		return author_id;
	}
	
	public String getUID(boolean a) {
		return author_id2;
	}

	public double getTotalInf() {
		return total_inf;
	}

	public java.sql.Date getTime() {
		return timestamp;
	}

	public String getPSS() {
		return pss;
	}
	
	public Integer getProduct(){
		return product;
	}

	public int ncomments() {

		return (comments.isEmpty() ? 0 : comments.size());
	}

	public int newcomments() {
		int newcomm = 0;

		for (Post i : comments) {
			if (i.getTime() != null)
				newcomm++;
		}

		return newcomm;

	}

	public int nlikes() {
		int num = 0;
		for (Post i : comments) {
			num += i.getLikes();
		}
		return num;
	}

	public int newlikes() {

		int newlike = 0;

		for (Post i : comments) {
			if (i.getTime() != null)
				newlike+=i.getLikes();
		}

		return newlike;

	}

	public int nviews() {
		int num = 0;
		for (Post i : comments) {
			num += i.getViews();
		}
		return num;
	}

	public int newviews() {

		int newview = 0;

		for (Post i : comments) {
			if (i.getTime() != null)
				newview+=i.getViews();
		}

		return newview;
	}

	public ArrayList<Post> getPosts() {
		if (!comments.contains(this.main))
			comments.add(this.main);
		return comments;
	}
}