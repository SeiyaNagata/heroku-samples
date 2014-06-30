package herokuSamples.web.log;

import herokuSamples.util.TemplateEngine;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="LogServlet", urlPatterns={"/log"})
public class LogServlet extends HttpServlet {

	// ロガー
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "Log サンプル");
		params.put("message", "");
		TemplateEngine.merge(res, "log/log.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "Log サンプル");

		String kind = req.getParameter("kind");
		String message = "";
		if(kind == null){
			message = "Parameter is null";
		}else switch(kind){
			case "info":
				outInfolog();
				message = "情報ログを出力しました";
				break;
			case "error":
				outExceptionlog();
				message = "エラーログを出力しました";
				break;
			default:
				message = "Parameter is not define";
		}
		params.put("message", message);
		TemplateEngine.merge(res, "log/log.html", params);
	}

	public void outInfolog(){
		logger.log(Level.INFO,"これはログのサンプルです");
	}

	public void outExceptionlog(){
		try {
			throw new RuntimeException("");
		} catch (RuntimeException re) {
			logger.log(Level.SEVERE, "これはログのサンプルです", re);
		}
	}
}
