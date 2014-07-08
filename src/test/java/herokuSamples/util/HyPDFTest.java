package herokuSamples.util;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.io.File;

public class HyPDFTest {

	private static HyPDF hypdf;

	@BeforeClass
	public static void setup() {
		String username = System.getenv("HYPDF_USER");
		String password = System.getenv("HYPDF_PASSWORD");
		hypdf = new HyPDF(username, password);

		new File("target/testoutput").mkdirs();
	}

	//@Test
	public void test1() {
		String content = "<html><h1>Hello world!</h1></html>";
		File file = new File("target/testoutput/test1.pdf");
		try {
			hypdf.htmltopdf(content, file, null);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}