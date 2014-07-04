package herokuSamples.web.batch;

import herokuSamples.util.TemplateEngine;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name="BatchServlet", urlPatterns={"/batch"})
public class BatchServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "バッチ サンプル");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		TemplateEngine.merge(res, "batch/batch.html", params);
	}
}

