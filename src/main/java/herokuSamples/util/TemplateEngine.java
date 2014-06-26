package herokuSamples.util;

import java.io.Writer;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import com.samskivert.mustache.Mustache;

public class TemplateEngine {

	private static final String BASE = "herokuSamples/web/";

	private static final TemplateEngine instance = new TemplateEngine();
	private static final Template baseTemplate;

	static {
		try {
			baseTemplate = instance.create("base.html");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String getContentType(String filename) {
		if (filename.endsWith(".html")) return "text/html";
		if (filename.endsWith(".json")) return "application/json";

		return "text/plain";
	}

	public static void merge(HttpServletResponse res, String filename, Map<String, ?> params) {
		res.setCharacterEncoding("utf-8");
		res.setContentType(getContentType(filename) + ";charset=utf-8");
		try{
			Template t = instance.create(filename);
			StringWriter sw = new StringWriter();
			t.merge(sw, params);

			Map<String, Object> newParams = new HashMap<String, Object>(params);
			newParams.put("CONTENT", sw.toString());
			baseTemplate.merge(res.getWriter(), newParams);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}


	public Template create(String filename) throws IOException {
		com.samskivert.mustache.Template t = Mustache.compiler().compile(readTemplate(filename));
		return new MustacheTemplate(t);
	}

	private String readTemplate(String filename) throws IOException {
		InputStream is = TemplateEngine.class.getClassLoader().getResourceAsStream(BASE + filename);
		try {
			java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is, "utf-8"));
			try {
				StringBuilder buf = new StringBuilder();
				String line = reader.readLine();
				while (line != null) {
					buf.append(line).append("\n");
					line = reader.readLine();
				}
				return buf.toString();
			} finally {
				reader.close();
			}
		} finally {
			is.close();
		}
	}

	public static interface Template {
		public void merge(Writer writer, Map<String, ?> params)throws IOException;
	}

	private static class MustacheTemplate implements Template {
		private com.samskivert.mustache.Template m;

		public MustacheTemplate(com.samskivert.mustache.Template m) {
			this.m = m;
		}

		public void merge(Writer writer, Map<String, ?> params) throws IOException {
			String ret = m.execute(params);
			writer.write(ret);
		}
	}
}