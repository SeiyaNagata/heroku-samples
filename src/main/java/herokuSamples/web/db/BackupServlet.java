package herokuSamples.web.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.Database;
import herokuSamples.util.TemplateEngine;

@WebServlet(name="BackupServlet", urlPatterns={"/batch"})
public class BackupServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "バッチ処理 サンプル");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		TemplateEngine.merge(res, "batch/batch.html", params);
	}
}
