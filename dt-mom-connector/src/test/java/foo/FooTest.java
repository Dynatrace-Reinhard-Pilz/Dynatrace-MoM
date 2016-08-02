package foo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.junit.Test;

import com.dynatrace.utils.Base64;
import com.dynatrace.utils.Base64Output;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Strings;

public class FooTest {
	
	public static final String HEADER_AUTHORIZATION =
			"Authorization".intern();
	public static final String BASIC =
			"Basic ".intern();

	@Test
	public void testFoo() throws IOException {
		URL url = new URL("https://localhost:8021/mom/profiles/aaa");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		setCredentials(con);
		TrustAllCertsManager.handleSecurity(con);
		int responseCode = con.getResponseCode();
		System.out.println(responseCode);
		try (InputStream in = con.getErrorStream()) {
			Closeables.copy(in, System.out);
		}
	}
	
	private static void setCredentials(HttpURLConnection con)
		throws IOException
	{
		Objects.requireNonNull(con);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			baos.write(BASIC.getBytes());
			
			final String userPassword =	Strings.join(
				Strings.COLON,
				"reini",
				"reini"
			);
			try (
				Base64Output base64Out = new Base64Output(baos);
				InputStream in = new ByteArrayInputStream(
					userPassword.getBytes(Base64.UTF8)
				);
			) {
				base64Out.write(in, in.available());
			}
			con.setRequestProperty(
				HEADER_AUTHORIZATION,
				new String(baos.toByteArray())
			);
		}
	}
	
}
