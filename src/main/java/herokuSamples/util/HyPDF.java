package herokuSamples.util;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class HyPDF {
	
	private static final String URL_BASE = "https://www.hypdf.com/";
	private String username;
	private String password;

	static {
		System.setProperty ("jsse.enableSNIExtension", "false");
	}

	public HyPDF(String username, String password) {
		this.username = username;
		this.password = password;
	}

	// HyPDFで使用している証明書が証明書ストアに入っていないらしい
	// このため接続を確立するためには証明書の検証を無視する必要がある
	private static HttpClient createHttpClient() {
		try {
			TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;
				}
			};
			X509HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
			SSLSocketFactory sslSf = new SSLSocketFactory(trustStrategy, hostnameVerifier);
			
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("https", 8443, sslSf));
			schemeRegistry.register(new Scheme("https", 443, sslSf));
			
			ClientConnectionManager connection = new ThreadSafeClientConnManager(schemeRegistry);
			return new DefaultHttpClient(connection);
		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private HttpResponse request(String path, Map<String, Object> params) throws IOException {
		params.put("user", this.username);
		params.put("password", this.password);

		String body = new Gson().toJson(params);

		HttpClient client = createHttpClient();
		HttpPost method = new HttpPost(URL_BASE + path);
		StringEntity entity = new StringEntity(body);
		entity.setContentType("application/json");
		method.setEntity(entity);
		System.out.println("Content-Type: " + method.getEntity().getContentType());
		System.out.println(body);
		return client.execute(method);
	}

	public int htmltopdf(String content, File outputFile, Map<String, Object> options) throws IOException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("content", content);
		if (options != null) {
			params.putAll(options);
		}
		HttpResponse res = request("htmltopdf", params);
		FileUtils.writeFile(outputFile, res.getEntity().getContent());
		return res.getStatusLine().getStatusCode();
	}
}

