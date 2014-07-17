package herokuSamples.web.pdf;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import herokuSamples.util.TemplateEngine;
import jp.co.flect.hypdf.HyPDF;
import jp.co.flect.hypdf.model.HyPDFOption;
import jp.co.flect.hypdf.model.PdfResponse;
import jp.co.flect.hypdf.model.LengthUnit;

@WebServlet(name="PdfServlet", urlPatterns={"/pdf"})
public class PdfServlet extends HttpServlet {

	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "PDF サンプル");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = initParams();
		params.put("url", "http://" + req.getHeader("host") + "/pdfSample.html");
		TemplateEngine.merge(res, "pdf/pdf.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String content = req.getParameter("content");
		PdfResponse pdf = generatePdf(content);
		res.setContentType("application/pdf");
		InputStream is = pdf.getContent();
		try {
			OutputStream os = res.getOutputStream();
			try {
				byte[] buf = new byte[8192];
				int n = is.read(buf);
				while (n > 0) {
					os.write(buf, 0, n);
					n = is.read(buf);
				}
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}
	}

	private PdfResponse generatePdf(String content) throws IOException {
		String username = System.getenv("HYPDF_USER");
		String password = System.getenv("HYPDF_PASSWORD");
		HyPDF hypdf = new HyPDF(username, password);
		hypdf.getTransport().setIgnoreHostNameVerification(true);
		hypdf.setTestMode(true);
		HyPDFOption.HtmlToPdf option = new HyPDFOption.HtmlToPdf();
		option.margin_top = LengthUnit.inch(0.25);
		option.margin_bottom = LengthUnit.inch(0.25);
		option.footer = new HyPDFOption.Footer();
		option.footer.center = "FLECT CO.,LTD.";
		return hypdf.htmlToPdf(content, option);
	}

}
