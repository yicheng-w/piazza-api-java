package piazza;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class APiazzaSession implements PiazzaSession {
	
	final String piazzaLogic = "https://piazza.com/logic/api";
	final String piazzaMain = "https://piazza.com/main/api";
	
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	
	private boolean loggedIn = false;
	
	@Override
	public void login(String email, String password) throws ClientProtocolException, IOException, LoginFailedException {
		// TODO Auto-generated method stub
		String loginData = new JSONObject()
				.put("method", "user.login")
				.put("params", new JSONObject()
					.put("email", email)
					.put("pass", password)).toString();
		
		System.out.println(loginData);
		
		HttpPost login = new HttpPost(piazzaLogic);
		
		login.setEntity(new StringEntity(loginData));
		login.setHeader("Accept", "application/json");
		login.setHeader("Content-type", "application/json");
		
		CloseableHttpResponse resp = httpClient.execute(login);
		
		if (resp.getStatusLine().getStatusCode() != 200) {
			throw new LoginFailedException("Incorrect login credentials.");
		}
		
		System.out.println(EntityUtils.toString(resp.getEntity()));
		
		loggedIn = true;
	}
	
	public Map<String, Object> getResp(String data, String APIEndpt) throws NotLoggedInException, ClientProtocolException, IOException {
		if (!loggedIn) {
			throw new NotLoggedInException("You have not logged in");
		}
		
		HttpPost request = new HttpPost(APIEndpt);
		request.setEntity(new StringEntity(data));
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		
		CloseableHttpResponse resp = httpClient.execute(request);
		
		if (resp.getStatusLine().getStatusCode() != 200)
			return null;
		
//		System.out.println(EntityUtils.toString(resp.getEntity()));
		return new JSONObject(EntityUtils.toString(resp.getEntity())).toMap();
	}
	
	public Map<String, Object> piazzaAPICall(String method, JSONObject params, String APIEndpt) throws ClientProtocolException, NotLoggedInException, IOException {
		String requestData = new JSONObject()
				.put("method", method)
				.put("params", params).toString();
		
		return this.getResp(requestData, APIEndpt);
	}

	
//	@Override
//	public PiazzaClass getClass(String classID) throws NotLoggedInException, UnsupportedEncodingException {
//		// TODO Auto-generated method stub
//		if (!loggedIn) {
//			throw new NotLoggedInException("You have not logged in");
//		}
//		String data = new JSONObject()
//				.put("method", "content.get")
//				.put("params", new JSONObject()
//						.put("cid", classID).toString()).toString();
//		
//		HttpPost getClass = new HttpPost(piazzaLogic);
//		
//		getClass.setEntity(new StringEntity(data));
//		getClass.setHeader("Accept", "application/json");
//		getClass.setHeader("Content-type", "application/json");
//		
//		CloseableHttpResponse resp = httpClient.execute(getClass);
//		
//		if (resp.getStatusLine().getStatusCode() != 200) {
//			return null;
//		}
//		
//		return new PiazzaClass();
//	}
}
