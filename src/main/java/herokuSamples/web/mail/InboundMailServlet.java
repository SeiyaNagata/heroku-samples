package herokuSamples.web.mail;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;
import herokuSamples.util.FileUtils;
import herokuSamples.util.Cache;


@WebServlet(name="InboundMailServlet", urlPatterns={"/inboundMail"})
public class InboundMailServlet extends HttpServlet {

	private static final String SESSION_KEY = "inboundMail.id";

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "メール受信 サンプル");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		String mailId = (String)req.getSession().getAttribute(SESSION_KEY);
		if (mailId == null) {
			mailId = UUID.randomUUID().toString();
			req.getSession().setAttribute(SESSION_KEY, mailId);
		}
		params.put("mailId", mailId);

		String mail = (String)Cache.get("mail");
		if (mail == null) {
			mail = "No mail";
		}
		params.put("mail", mail);
		TemplateEngine.merge(res, "mail/inboundMail.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String str = inputStreemToString(req.getInputStream(), "utf-8");
		Cache.set("mail", str, 7200);
		System.out.println(str);
	}

	private static String inputStreemToString(InputStream in, String enc) throws IOException{
		byte[] data = FileUtils.readStream(in);
		return new String(data, "utf-8");
	}
}
