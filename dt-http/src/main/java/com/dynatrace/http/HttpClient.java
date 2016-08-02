package com.dynatrace.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.dynatrace.authentication.Authenticator;
import com.dynatrace.http.config.Credentials;

/**
 * Objects implementing {@link HttpClient} are required to perform a few simple
 * task like sending HTTP requests using methods {@code GET} or {@code POST} 
 * and in addition to upload files.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface HttpClient {
	
	public static final String HEADER_CONTENT_TYPE =
			"Content-Type".intern();
	public static final String LINE_FEED =
			"\r\n".intern();
	public static final String HEADER_AUTHORIZATION =
			"Authorization".intern();
	public static final String BASIC =
			"Basic ".intern();
	public static final String HEADER_CONTENT_DISPOSITION =
			"Content-Disposition".intern();
	public static final String HEADER_TRANSFER_ENCODING =
			"Content-Transfer-Encoding".intern();

	/**
	 * Executes a request to the given {@link URL} using the given
	 * {@link Method} an optionally performs Basic Authentication with the
	 * given {@link Credentials}.
	 * 
	 * @param url the {@link URL} to send the request to
	 * @param m the {@link Method} to use for the request
	 * @param auth the user credentials to be used for
	 * 		Basic Authentication or {@code null} if no authentication is
	 * 		expected to be required
	 * @param out the {@link OutputStream} where the response delivered by the
	 * 		HTTP Server should be streamed into.
	 * 
	 * @return the HTTP Response code delivered by the Server
	 * 
	 * @throws IOException if opening the connection to the HTTP Server fails
	 * @throws NullPointerException if the given {@link URL} or the given
	 * 		{@link Method} are {@code null}.
	 */
	int request(URL url, Method m, Authenticator auth, OutputStream out)
		throws IOException;
	
	/**
	 * Sends out an HTTP request to the given {@link URL} using the given
	 * {@link Method} and {@link Credentials} and expects XML code within the
	 * response body.<br />
	 * <br />
	 * The given {@link Class} is required to implement the necessary JAXB
	 * annotation in order to deserialize that XML code into an object.
	 * 
	 * @param url the {@link URL} to send the request to
	 * @param m the HTTP method to use for the request
	 * @param auth the {@link Authenticator} for authentication or
	 * 		{@code null} if there is no authentication necessary
	 * @param responseClass the {@link Class} implementing the required JAXB
	 * 		annotation for umarshalling the HTTP response in form of XML code
	 * 
	 * @return an Object holding the deserialized HTTP response, HTTP response
	 * 		code and potentially an {@link Exception} that happened during
	 * 		the HTTP call. 
	 * 
	 * @throws IOException in case sending the HTTP request fails
	 */
	<T> HttpResponse<T> request(
		URL url, Method m, Authenticator auth, Class<T> responseClass
	) throws IOException;
	
	/**
	 * Uploads data to the given {@link URL} using {@code multipart/form-data}
	 * for transferring the bytes.
	 *  
	 * @param url the {@link URL} to upload the data to
	 * @param auth the {@link Authenticator} for authentication or
	 * 		{@code null} if no authentication is required
	 * @param fileName the file name to publish to the HTTP server
	 * @param is the {@link InputStream} offering the data to upload
	 * 
	 * @return the results of the upload procedure
	 * 
	 * @throws IOException in case sending the HTTP request fails
	 */
	UploadResult upload(
		URL url,
		Authenticator auth,
		String fileName,
		InputStream is
	) throws IOException;
	
	public void setResponseVerifier(ResponseVerifier verifier);

}
