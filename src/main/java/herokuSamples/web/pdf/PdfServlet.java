package herokuSamples.web.pdf;

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
import jp.co.flect.sendgrid.SendGridClient;
import jp.co.flect.sendgrid.SendGridException;
import jp.co.flect.sendgrid.model.WebMail;

@WebServlet(name="PdfServlet", urlPatterns={"/pdf"})
public class PdfServlet extends HttpServlet {

	private static final String DEFAULT_MAIL_FROM = "test@ht.flectdev.com";

	private static Map<String, Object> initParams() {
		String from = System.getenv("MAIL_FROM");
		if (from == null) {
			from = DEFAULT_MAIL_FROM;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "メール サンプル");
		params.put("message", "");
		params.put("to", "");
		params.put("subject", "");
		params.put("body", "");
		params.put("from", from);
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		TemplateEngine.merge(res, "mail/mail.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		req.setCharacterEncoding("utf-8");

		String from = (String)params.get("from");
		String to = req.getParameter("to");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		params.put("to", to);
		params.put("subject", subject);
		params.put("body", body);
		try {
			sendMail(from, to, subject, body);
			params.put("message", "メールを送信しました。");
		} catch (Exception e) {
			params.put("message", "メールを送信できませんでした。: " + e.getMessage());
		}
		TemplateEngine.merge(res, "mail/mail.html", params);
	}

	private void sendMail(String from, String to, String subject, String body) throws IOException, SendGridException {
		String username = System.getenv("SENDGRID_USERNAME");
		String password = System.getenv("SENDGRID_PASSWORD");
		SendGridClient client = new SendGridClient(username, password);
		WebMail mail = new WebMail();
		mail.setFrom(from);
		mail.setToList(Arrays.asList(to.split(",")));
		mail.setSubject(subject);
		mail.setText(body);
		client.mail(mail);
	}
}
