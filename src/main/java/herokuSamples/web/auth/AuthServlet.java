package herokuSamples.web.auth;

import java.util.Arrays;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;

@WebServlet(name="AuthServlet", urlPatterns={"/auth"})
public class AuthServlet extends HttpServlet {

	// ロガー
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static Map<String, Object> initParams() {
		String auth0clientId = System.getenv("AUTH0_CLIENT_ID");
		String auth0domain   = System.getenv("AUTH0_DOMAIN");
		String auth0callback = System.getenv("AUTH0_CALLBACK_URL");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "ソーシャルログイン サンプル");
		params.put("auth0clientId", auth0clientId);
		params.put("auth0domain", auth0domain);
		params.put("auth0callback", auth0callback);

		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		params.put("url", "http://" + req.getHeader("host") + "/pdfSample.html");

		String state = UUID.randomUUID().toString();
		req.getSession().setAttribute("state", state);
		params.put("state", state);

		String userInfo = (String)req.getSession().getAttribute("auth0_user");
		if (userInfo != null) {
			UserInfo user = UserInfo.fromJson(userInfo);
			logger.info("Auth0 login: " + user.getName() + ", " + user.getEmail());
			params.put("user", user);
		}
		TemplateEngine.merge(res, "auth/auth.html", params);
	}

}
	