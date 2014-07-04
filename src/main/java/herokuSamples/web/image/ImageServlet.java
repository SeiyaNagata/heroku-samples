package herokuSamples.web.image;

import herokuSamples.util.TemplateEngine;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.cloudinary.*;

@WebServlet(name="ImageServlet", urlPatterns={"/image"})
public class ImageServlet extends HttpServlet {


	static {
	}
	private static Map<String, Object> initParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "画像編集 サンプル");
		return params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Map<String, Object> params = getCloudinaryInfo();
		params.put("title", "画像編集 サンプル");
		params.put("message", "");
		TemplateEngine.merge(res, "image/image.html", params);
	}

	/**
	 * Cloudinaryへの接続情報を取得する
	 * @return
	 */
	private Map<String, Object> getCloudinaryInfo() {
		Map<String, Object> params = new HashMap<String, Object>();

		Map options = Cloudinary.asMap("resource_type", "auto");
		Cloudinary cloudinary = Singleton.getCloudinary();

		boolean returnError = Cloudinary.asBoolean(options.get("return_error"), false);
		if (returnError){
			throw new IllegalStateException("Failed to get cloudinary infomation.");
		}
		String timestamp=(new Long(System.currentTimeMillis() / 1000L)).toString();
		params.put("timestamp", timestamp);
		params.put("tags", "image-sample");
		String apiSecret = Cloudinary.asString(options.get("api_secret"), cloudinary.getStringConfig("api_secret"));
		if (apiSecret == null)
			throw new IllegalArgumentException("Must supply api_secret");
		// Cloudinaryのシグネチャを取得する
		String expected_signature = cloudinary.apiSignRequest(params, apiSecret);
		params.put("signature", expected_signature);

		String apiKey = Cloudinary.asString(options.get("api_key"), cloudinary.getStringConfig("api_key"));
		if (apiKey == null)
			throw new IllegalArgumentException("Must supply api_key");
		params.put("api_key", apiKey);
		String cloudName = Cloudinary.asString(options.get("cloud_name"), cloudinary.getStringConfig("cloud_name"));
		if (cloudName == null)
			throw new IllegalArgumentException("Must supply cloud_name");
		params.put("cloud_name", cloudName);

		return params;
	}
}
