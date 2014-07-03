package herokuSamples.web.upload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import herokuSamples.util.TemplateEngine;
import org.joda.time.DateTime;

@WebServlet(name="UploadServlet", urlPatterns={"/upload"})
public class UploadServlet extends HttpServlet {

	private static final String BUCKET_NAME = System.getenv("AWS_S3_BUCKETNAME");
	private static final String ACCESSKEY = System.getenv("AWS_S3_UPLOAD_ACCESSKEY");
	private static final String SECRETKEY = System.getenv("AWS_S3_UPLOAD_SECRETKEY");

	private static final String PREFIX = "upload-sample/";
	private static final String DELIMITER = "/";
	private static final long EXPIRATION_MILLS = 3000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "Upload サンプル");

		AmazonS3Client s3 = makeS3Client();

		List<UploadFile> files = getUploadedFiles(s3);
		params.put("files", files);
		params.put("message", "");
		TemplateEngine.merge(res, "upload/upload.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String name = req.getParameter("name");
		String type = req.getParameter("type");
		String url = generatePresignedRequestUrl(PREFIX + name, type, "PUT");
		res.getWriter().print(url);
	}

	private List<UploadFile> getUploadedFiles(AmazonS3Client s3) {
		ObjectListing objects = s3.listObjects(new ListObjectsRequest()
				.withBucketName(BUCKET_NAME)
				.withPrefix(PREFIX)
				.withMarker(PREFIX)
				.withDelimiter(DELIMITER));
		final List<S3ObjectSummary> summaries = objects.getObjectSummaries();
		List<UploadFile> files = new ArrayList<>();
		for (S3ObjectSummary summary : summaries) {
			files.add(new UploadFile(summary.getKey(), s3.getResourceUrl(BUCKET_NAME, summary.getKey())));
		}
		return files;
	}

	public String generatePresignedRequestUrl(String path, String type,  String method) throws UnsupportedEncodingException {
		Date expiration = DateTime.now().plus(EXPIRATION_MILLS).toDate();
		String url = generatePresignedRequestUrl(BUCKET_NAME, expiration, path, type, method);
		return URLEncoder.encode(url, "UTF-8");
	}

	public String generatePresignedRequestUrl(String bucketName, Date expired, String path, String type, String method) {
		AmazonS3Client client = makeS3Client();
		GeneratePresignedUrlRequest gpur = new GeneratePresignedUrlRequest(bucketName, path);
		gpur.setMethod(HttpMethod.valueOf(method));
		gpur.setExpiration(expired);
		gpur.setContentType(type);

		gpur.addRequestParameter("x-amz-acl", "public-read");
		return client.generatePresignedUrl(gpur).toString();
	}

	public AmazonS3Client makeS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(ACCESSKEY, SECRETKEY);
		return new AmazonS3Client(credentials);
	}

	public static class UploadFile {
		public final String key;
		public final String url;
		public UploadFile(String key, String url) {
			this.key = key.replaceAll(PREFIX, "");
			this.url = url;
		}
	}

}
