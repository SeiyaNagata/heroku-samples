package herokuSamples.util;

import org.h2.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Database {
	private static boolean initialized = false;

	private static boolean isH2Mem(String url) {
		return url.startsWith("jdbc:h2:mem:");
	}

	private static boolean isPostgres(String url) {
		return url.startsWith("postgres://");
	}

	private static void initialize(Connection con) throws SQLException {
		try {
			Statement stmt = con.createStatement();
			String sql = FileUtils.readFileAsString(new File("initialize.sql"), "utf-8");
			for (String s : sql.split(";")) {
				stmt.executeUpdate(s);
			}
			con.commit();
			initialized = true;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (isH2Mem(databaseUrl)) {
			Driver.load();
			Connection con = DriverManager.getConnection(databaseUrl);
			if (!initialized) {
				initialize(con);
			}
			return con;
		} else if (isPostgres(databaseUrl)) {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
			try {
				URI uri = new URI(databaseUrl);
				String host = uri.getHost();
				int port = uri.getPort();
				if (port > 0) {
					host += ":" + port;
				}
				String db = uri.getPath();
				String username = uri.getUserInfo();
				String password = null;
				int idx = username.indexOf(":");
				if (idx != -1) {
					password = username.substring(idx + 1);
					username = username.substring(0, idx);
				}
				Connection con = DriverManager.getConnection(
					"jdbc:postgresql://" + host + db, username, password);
				return con;
			} catch (URISyntaxException e) {
				throw new IllegalStateException(e);
			}
		} else {
			throw new IllegalStateException("Illegal DATABASE_URL: " + databaseUrl);
		}
	}
}