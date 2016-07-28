package importDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import backend.Settings;

//Opinion Individual Object
public class Opinion {

	private Post main;
	private int author_id; // String
	private ArrayList<Post> comments = new ArrayList<Post>();
	private double reach = 0;
	private double polarity = 0;
	private Settings dbc = new Settings();
	private double total_inf = 0;
	private Date timestamp;
	private int tag;

	public Opinion(Post _main, int _tag) {
		this.main = _main;
		this.author_id = main.getUID();
		timestamp = main.getTime();
		tag = _tag;

	}

	public void evalReach(double avgPost, double avgLikes, double avgViews) {
		this.reach = dbc.pWcomments * (ncomments() / avgPost) + dbc.pWlikes * (nlikes() / avgLikes)
				+ dbc.pWviews * (nviews() / avgViews);
	}

	public void evalPolarity(HashMap<Integer, Author> authordb) {
		total_inf = authordb.get(author_id).getInfluence();
		polarity = total_inf * main.getPolarity();

		comments.forEach((v) -> {
			total_inf += authordb.get(v.getUID()).getInfluence();
			polarity += v.getPolarity() * authordb.get(v.getUID()).getInfluence();

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

	public double getTotalInf() {
		return total_inf;
	}

	public java.util.Date getTime() {
		return timestamp;
	}

	public int getTag() {
		return tag;
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
