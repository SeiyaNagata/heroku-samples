package herokuSamples.web.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;

@WebServlet(name="IndexServlet", urlPatterns={"/"})
public class IndexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("message", "World!");
		params.put("title", "Heroku サンプル");
		TemplateEngine.merge(res, "index/index.html", params);
	}
}
