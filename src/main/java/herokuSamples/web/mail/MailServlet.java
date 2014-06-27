package herokuSamples.web.mail;

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

@WebServlet(name="MailServlet", urlPatterns={"/mail"})
public class MailServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "メール サンプル");
		params.put("message", "");
		params.put("to", "");
		params.put("cc", "");
		params.put("subject", "");
		params.put("body", "");
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
		String to = req.getParameter("to");
		String cc = req.getParameter("cc");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		params.put("to", to);
		params.put("cc", cc);
		params.put("subject", subject);
		params.put("body", body);
		try {
			sendMail(to, cc, subject, body);
			params.put("message", "メールを送信しました。");
		} catch (Exception e) {
			params.put("message", "メールを送信できませんでした。: " + e.getMessage());
		}
		TemplateEngine.merge(res, "mail/mail.html", params);
	}

	private void sendMail(String to, String cc, String subject, String body) throws IOException, SendGridException {
		String username = System.getenv("SENDGRID_USERNAME");
		String password = System.getenv("SENDGRID_PASSWORD");
		SendGridClient client = new SendGridClient(username, password);
		WebMail mail = new WebMail();
		mail.setFrom("test@flect.co.jp");
		mail.setToList(Arrays.asList(to.split(",")));
		//mail.setCC(cc);
		mail.setSubject(subject);
		mail.setText(body);
		client.mail(mail);
	}
}
