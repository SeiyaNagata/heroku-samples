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

@WebServlet(name="IpFilterServlet", urlPatterns={"/ipfilter"})
public class IpFilterServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "IP制限 サンプル");
		return params;
	}

	private static String getClientAddressFromXForwardedFor(String s) {
		int n = s.indexOf(",");
		return n == -1 ? s : s.substring(0, n);
	}

	private static boolean isAllowedIp(String allowedIp, String remoteAddress) {
		for (String s : remoteAddress.split(",")) {
			if (allowedIp.equals(s.trim())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String allowedIp = req.getParameter("allowedIp");
		String remoteAddress = req.getRemoteAddr();
		String xForwardedFor = req.getHeader("x-forwarded-for");
		if (xForwardedFor == null) {
			xForwardedFor = "";
		}
		if (allowedIp == null) {
			if (xForwardedFor.length() == 0) {
				allowedIp = remoteAddress;
			} else {
				allowedIp = getClientAddressFromXForwardedFor(xForwardedFor);
			}
		} else if (!isAllowedIp(allowedIp, xForwardedFor.length() == 0 ? remoteAddress : xForwardedFor)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
			return;
		}

		Map<String, Object> params = initParams();
		params.put("allowedIp", allowedIp);
		params.put("remoteAddress", remoteAddress);
		params.put("xForwardedFor", xForwardedFor);
		TemplateEngine.merge(res, "ipfilter/ipfilter.html", params);
	}

}
