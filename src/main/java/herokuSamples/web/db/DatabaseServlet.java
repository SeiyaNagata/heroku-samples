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

@WebServlet(name="DatabaseServlet", urlPatterns={"/db"})
public class DatabaseServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String name = req.getParameter("name");
			String message = name == null ? "挨拶する人を選択してください。" : getString(name);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("title", "データベース接続");
			params.put("message", message);
			if (name != null) {
				params.put(name, name);
			}
			TemplateEngine.merge(res, "db/database.html", params);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private static String getString(String name) throws SQLException {
		Connection con = Database.getConnection();
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT message FROM hello_test WHERE name = ?");
			try {
				stmt.setString(1, name);
				ResultSet rs = stmt.executeQuery();
				try {
					if (rs.next()) {
						return rs.getString(1);
					} else {
						return "!#D$D%('3')!fdj";
					}
				} finally {
					rs.close();
				}
			} finally {
				stmt.close();
			}
		} finally {
			con.close();
		}
	}
}
