package herokuSamples.web.session;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;

@WebServlet(name="SessionServlet", urlPatterns={"/session"})
public class SessionServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "セッション管理");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String value = (String)req.getSession().getAttribute("sessionSample");
		Map<String, Object> params = initParams();
		if (value != null) {
			params.put("value", value);
		}
		TemplateEngine.merge(res, "session/session.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String value = req.getParameter("value");
		if (value == null) {
			value = "";
		}
		req.getSession().setAttribute("sessionSample", value);
		res.sendRedirect("/session");
	}

}
