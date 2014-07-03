package herokuSamples.batch;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

/**
 *
 * Created by shimizu on 2014/07/02.
 */
public class CleanupBatch {

	private static final String BUCKET_NAME = System.getenv("AWS_S3_BUCKETNAME");
	private static final String ACCESSKEY = System.getenv("AWS_S3_UPLOAD_ACCESSKEY");
	private static final String SECRETKEY = System.getenv("AWS_S3_UPLOAD_SECRETKEY");

	private static final String PREFIX = "upload-sample/";
	private static final String DELIMITER = "/";

	// ロガー
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) {
		CleanupBatch batch = new CleanupBatch();

		try {
			batch.cleanUpS3();
		} catch (Exception e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Failed to run clean up S3", e);
		}

		try {
			batch.cleanUpCloudinary();
		} catch (Exception e) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Failed to run clean up Cloudinary", e);
		}
	}

	/**
	 * Cloudinaryのimage-sampleタグが付いたファイルを全て削除する
	 * @throws Exception
	 */
	public void cleanUpCloudinary() throws Exception {
		logger.info("Start clean up Cloudinary");
		DateTime start = DateTime.now();

		Cloudinary cloudinary = Singleton.getCloudinary();
		final Api.ApiResponse apiResponse = cloudinary.api().deleteResourcesByTag("image-sample", Cloudinary.emptyMap());
		final Map deleted = (Map) apiResponse.get("deleted");

		logger.info(String.format("%s file deleted ", deleted.size()));
		logger.info(String.format("End clean up Cloudinary Time : %sms", DateTime.now().minus(start.getMillis()).getMillis()) );
	}

	/**
	 * S3上のupload-sampleフォルダ内のファイルを全て削除する
	 * @throws Exception
	 */
	public void cleanUpS3() throws Exception {
		logger.info("Start clean up S3");
		DateTime start = DateTime.now();
		AmazonS3Client s3 = makeS3Client();
		final ObjectListing objects = s3.listObjects(new ListObjectsRequest()
				.withBucketName(BUCKET_NAME)
				.withPrefix(PREFIX)
				.withMarker(PREFIX)
				.withDelimiter(DELIMITER));
		List<KeyVersion> keys = new ArrayList<>();
		for (S3ObjectSummary summary : objects.getObjectSummaries()) {
			final String key = summary.getKey();
			final boolean add = keys.add(new KeyVersion(key));
		}
		if (!keys.isEmpty()){
			s3.deleteObjects(new DeleteObjectsRequest(BUCKET_NAME).withKeys(keys));
		}
		logger.info(String.format("%s file deleted ",keys.size()));
		logger.info(String.format("End clean up S3 Time : %sms", DateTime.now().minus(start.getMillis()).getMillis()) );
	}

	public AmazonS3Client makeS3Client() {
			AWSCredentials credentials = new BasicAWSCredentials(ACCESSKEY, SECRETKEY);
			return new AmazonS3Client(credentials);
	}

}
