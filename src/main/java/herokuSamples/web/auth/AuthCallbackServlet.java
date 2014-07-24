package herokuSamples.web.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@WebServlet(name="AuthCallbackServlet", urlPatterns={"/auth/callback"})
public class AuthCallbackServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String code = req.getParameter("code");
		String state = req.getParameter("state");
		String state2 = (String)req.getSession().getAttribute("state");
		if (code == null || state == null || !state.equals(state2)) {
			res.setStatus(401);
			res.getWriter().print("Bad Request");
		} else {
			AuthResponse ar = authenticate(req);
			req.getSession().setAttribute("auth0_access_token", ar.access_token);
			req.getSession().setAttribute("auth0_user", ar.getUserInfoAsJson());
			res.sendRedirect("/auth");
		}
	}

	private AuthResponse authenticate(HttpServletRequest req) throws IOException {
		String auth0clientId = System.getenv("AUTH0_CLIENT_ID");
		String auth0domain   = System.getenv("AUTH0_DOMAIN");
		String auth0secret   = System.getenv("AUTH0_CLIENT_SECRET");

		// Parse request to fetch authorization code
		String authorizationCode = req.getParameter("code");
		try {
			URI accessTokenURI = new URI("https://" + auth0domain + "/oauth/token");

			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(accessTokenURI);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("client_id", auth0clientId));
			nameValuePairs.add(new BasicNameValuePair("client_secret", auth0secret));

			nameValuePairs.add(new BasicNameValuePair("redirect_uri", req.getRequestURL().toString()));
			nameValuePairs.add(new BasicNameValuePair("code", authorizationCode));

			nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
System.out.println("str: " + str);
			return new Gson().fromJson(str, AuthResponse.class);
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	public static class AuthResponse {
		public String access_token;
		public String id_token;
		public String token_type;

		public String getUserInfoAsJson() {
			String[] split = id_token.split("\\.");
			if (split.length != 3) {
				throw new IllegalStateException(id_token);
			}
			try {
				byte[] data = Base64.decodeBase64(split[1]);
				return new String(data, "utf-8");
			} catch (IOException e) {
				//not occure
				throw new IllegalStateException(e);
			}
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("access_token: ").append(access_token).append("\n")
				.append("token_type: ").append(token_type).append("\n")
				.append("id_token: ").append(id_token).append("\n");
			return buf.toString();
		}
	}
}
