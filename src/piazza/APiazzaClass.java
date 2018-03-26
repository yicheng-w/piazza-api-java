package piazza;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class APiazzaClass implements PiazzaClass{
	
	final String piazzaLogic = "https://piazza.com/logic/api";
	final String piazzaMain = "https://piazza.com/main/api";

	protected PiazzaSession mySession = null;
	protected String cid;
	
	public APiazzaClass(String email, String password, String classID) throws ClientProtocolException, IOException, LoginFailedException {
		this.mySession = new APiazzaSession();
		this.mySession.login(email, password);
		this.cid = classID;
	}
	
	public List<Map<String, Object>> getFeed(int limit, int offset) throws ClientProtocolException, NotLoggedInException, IOException {
		JSONObject data = new JSONObject()
					.put("limit", limit)
					.put("offset", offset)
					.put("sort", "updated")
					.put("nid", this.cid);
		Map<String, Object> resp = this.mySession.piazzaAPICall("network.get_my_feed", data, piazzaLogic);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> feed = (List<Map<String, Object>>)((Map<String, Object>)this.getResults(resp)).get("feed");
		
//		System.out.println(feed.get(0).keySet());

		return feed;
	}
	
	public Map<String, Object> getUser(String uid) throws ClientProtocolException, NotLoggedInException, IOException {
		JSONObject data = new JSONObject().put("ids", new String[]{uid})
				.put("nid", this.cid);
		Map<String, Object> resp = this.mySession.piazzaAPICall("network.get_users", data, piazzaLogic);
		//System.out.println(resp.toString());
		@SuppressWarnings("unchecked")
		Map<String, Object> user = ((List<Map<String, Object>>)this.getResults(resp)).get(0);
		return user;
	}
	
	public Map<String, Object> getPost(String cid) throws ClientProtocolException, NotLoggedInException, IOException {
		JSONObject data = new JSONObject().put("cid", cid);
		Map<String, Object> resp = this.mySession.piazzaAPICall("content.get", data, piazzaLogic);
    	@SuppressWarnings("unchecked")
		Map<String, Object> post = (Map<String, Object>)this.getResults(resp);
		return post;
	}
	
	public List<Map<String, Object>> getAllPosts() throws ClientProtocolException, NotLoggedInException, IOException {
		List<Map<String, Object>> feed = this.getFeed(999999, 0);
		List<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : feed) {
			posts.add(this.getPost((String)item.get("id")));
		}
		return posts;
	}
	
	public String getAuthorId(Map<String, Object> post) { // get author id of the post
    	@SuppressWarnings("unchecked")
		List<Map<String, Object>> hist = (List<Map<String, Object>>) post.get("change_log");
		String authorId = "";
		for (Map<String, Object> update : hist) {
			if (update.get("type").equals("create")) {
				authorId = (String) update.get("uid");
				break;
			}
		}
		return authorId;
	}
	
	public String getUserName(String uid) throws ClientProtocolException, NotLoggedInException, IOException {
		return (String) this.getUser(uid).get("name");
	}
	
	public String getUserEmail(String uid) throws ClientProtocolException, NotLoggedInException, IOException {
		return (String) this.getUser(uid).get("email");
	}
	
	public boolean createPost(String title, String content) {
		// TODO fill-in
		return true;
	}
	
	public boolean createFollowup(String postToReply, String content, boolean anonymous) {
		// TODO fill-in
		return true;
	}
	
	private Object getResults(Map<String, Object> resp) {
		if (resp.get("error") != null) {
			return null;
		}
		return resp.get("result");
	}
}
