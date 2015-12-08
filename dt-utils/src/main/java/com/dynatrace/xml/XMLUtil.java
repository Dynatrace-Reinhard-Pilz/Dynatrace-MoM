package com.dynatrace.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Throwables;
import com.dynatrace.utils.Unchecked;

public class XMLUtil {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(XMLUtil.class.getName());
	
	@SuppressWarnings("unused")
	private final JAXBContext ctx;
	
	public XMLUtil(Class<?>... classes) throws IOException {
		ctx = createContext(classes);
	}
	
	public static String toString(Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			serialize(o, out);
			Closeables.closeQuietly(out);
			return new String(out.toByteArray());
		} catch (IOException e) {
			Closeables.closeQuietly(out);
			return Throwables.toString(e);
		}
	}
	
	public static void serialize(Object o, OutputStream out)
		throws IOException
	{
		serialize(o, out, o.getClass());
	}
	
	public static void serialize(
		Object o, OutputStream out, Class<?>... classes
	)
		throws IOException
	{
		serialize(o, out, Charset.defaultCharset(), classes);
	}
	
	/**
	 * Creates a {@link Marshaller} based on the given {@link JAXBContext}
	 * and configures it to use the default {@link Charset}, and allows to
	 * output the XML code to be generated formatted and as an XML fragment.
	 * 
	 * @param ctx the {@link JAXBContext} to create a {@link Marshaller} for
	 * @param formatted {@code true} if the XML code should be formatted,
	 * 		{@code false} otherwise
	 * @param fragment {@code false} if the XML code should start with
	 * 		{@code <?xml }, or {@code true} if just fragment XML code should
	 * 		get generated
	 * 
	 * @return a preconfigured {@link Marshaller}
	 * 
	 * @throws IOException in case no {@link Marshaller} could get created
	 */
	public static Marshaller createMarshaller(
		JAXBContext ctx,
		boolean formatted,
		boolean fragment
	)
		throws IOException
	{
		return createMarshaller(
			ctx, Charset.defaultCharset(), formatted, fragment
		);
	}
	
	/**
	 * Creates a {@link Marshaller} based on the given {@link JAXBContext}
	 * and configures it to use the given {@link Charset}, and allows to
	 * output the XML code to be generated formatted and as an XML fragment.
	 * 
	 * @param ctx the {@link JAXBContext} to create a {@link Marshaller} for
	 * @param charset the {@link Charset} the XML code should be formatted
	 * @param formatted {@code true} if the XML code should be formatted,
	 * 		{@code false} otherwise
	 * @param fragment {@code false} if the XML code should start with
	 * 		{@code <?xml }, or {@code true} if just fragment XML code should
	 * 		get generated
	 * 
	 * @return a preconfigured {@link Marshaller}
	 * 
	 * @throws IOException in case no {@link Marshaller} could get created
	 */
	public static Marshaller createMarshaller(
		JAXBContext ctx,
		Charset charset,
		boolean formatted,
		boolean fragment
	)
		throws IOException
	{
		if (charset == null) {
			return createMarshaller(
				ctx, Charset.defaultCharset(), formatted, fragment
			);
		}
		Objects.requireNonNull(ctx);
		try {
			Marshaller marshaller =	ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, fragment);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
			return marshaller;
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	/**
	 * <p>
	 * Produces an XML representation of the given object and sends it to the
	 * given {@link OutputStream}.
	 * </p>
	 * <p>
	 * The class the object to serialize is expected to be properly annotated
	 * with JAXB annotations.
	 * </p>
	 *
	 * @param o the object to produce an XML representation for
	 * @param out the {@link OutputStream} to send the XML code to
	 * @param charset the {@link Charset} used for encoding the XML code
	 * @param classes the classes required for JAXB for proper marshaling.
	 * 		At a minimum the class the given object is an instance of is
	 * 		required.
	 * 
	 * @throws IOException if either writing to the {@link OutputStream} fails
	 * 		or producing the XML code is not possible for this object.
	 */
	public static void serialize(
		Object o,
		OutputStream out,
		Charset charset,
		Class<?>... classes
	)
		throws IOException
	{
		Objects.requireNonNull(o);
		Marshaller marshaller =
				createMarshaller(createContext(classes), charset, true, false);
		serialize(o, out, marshaller);
	}
	
	/**
	 * Creates a {@link JAXBContext} using the given {@link Class}es, but does
	 * not throw a {@link JAXBException} and wraps it instead into an
	 * {@link IOException}.
	 * 
	 * @param classes the {@link Class}es relevant for serialization and
	 * 		deserialization
	 * 
	 * @return the {@link JAXBContext} to be aware of the given {@link Class}es
	 * 
	 * @throws IOException in case creating the {@link JAXBContext} fails
	 * @throws IllegalArgumentException if the given {@link Class}es are either
	 * 		empty, {@code null} or one of them is {@code null}.
	 */
	public static JAXBContext createContext(Class<?>... classes)
		throws IOException
	{
		if ((classes == null) || (classes.length == 0)) {
			throw new IllegalArgumentException();
		}
		try {
			return JAXBContext.newInstance(classes);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Sends an XML representation of the given {@link Object} into the given
	 * {@link OutputStream} encoded with the given {@link Charset} utilizing
	 * the given {@link JAXBContext} for figuring out how to serialize this
	 * {@link Object}.
	 * 
	 * @param o the {@link Object} to create an XML representation for
	 * @param out the {@link OutputStream} to send the XML data into
	 * @param charset the {@link Charset} for which to encode the data with
	 * @param ctx the {@link JAXBContext} which is aware of how to serialize
	 * 		the {@link Object}
	 * 
	 * @throws IOException in case serialization fails
	 */
	public static void serialize(
		Object o,
		OutputStream out,
		Charset charset,
		JAXBContext ctx
	)
		throws IOException
	{
		serialize(o, out, charset, ctx, true, false);
	}
	
	/**
	 * Stream an XML representation of the given {@link Object} into the given
	 * {@link OutputStream} using the given {@link Charset} by utilizing the
	 * given {@link JAXBContext}.
	 * 
	 * @param o the {@link Object} to create XML code for
	 * @param out the {@link OutputStream} to send the XML code to
	 * @param charset the {@link Charset} to use or {@code null} if you wish to
	 * 		use {@link Charset#defaultCharset()}.
	 * @param ctx the {@link JAXBContext} which is aware of the {@link Class}es
	 * 		to use for serialization
	 * @param formatted {@code true} if the XML code should be formatted,
	 * 		{@code false} otherwise
	 * @param fragment {@code true} if the XML code should start with a
	 * 		{@code <?xml>} header, {@code false} otherwise
	 * 
	 * @throws IOException in case serialization fails
	 */
	public static void serialize(
		Object o,
		OutputStream out,
		Charset charset,
		JAXBContext ctx,
		boolean formatted,
		boolean fragment
	)
		throws IOException
	{
		Objects.requireNonNull(o);
		Objects.requireNonNull(out);
		Objects.requireNonNull(charset);
		Objects.requireNonNull(ctx);
		Marshaller marshaller =	createMarshaller(ctx, formatted, fragment);
		serialize(o, out, marshaller);
	}
	
	/**
	 * Sends an XML representation of the given {@link Object} into the given
	 * {@link OutputStream} using the given {@link Marshaller}.
	 * 
	 * @param o the {@link Object} to create XML code for
	 * @param out the {@link OutputStream} to send the XML code into
	 * @param marshaller the {@link Marshaller} to use for creating the XML code
	 * 
	 * @throws IOException in case serialization fails
	 */
	public static void serialize(
		Object o,
		OutputStream out,
		Marshaller marshaller
	)
		throws IOException
	{
		Objects.requireNonNull(o);
		Objects.requireNonNull(marshaller);
		Objects.requireNonNull(out);
		try {
			marshaller.marshal(o, out);
		} catch (final JAXBException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Deserializes an object using the given {@link Class}es for
	 * deserialization pulling the data from the given {@link InputStream}.
	 * 
	 * @param in the {@link InputStream} holding the serialized XML data
	 * @param classes the {@link Class}es necessary to deserialize the object
	 * 
	 * @return the deserialized object
	 * 
	 * @throws IOException in case deserialization fails
	 */
	public static <T> T deserialize(InputStream in,	Class<?>... classes)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(classes);
		if (Thread.currentThread().isInterrupted()) {
			return null;
		}
		try {
			final Unmarshaller unmarshaller = 
					createContext(classes).createUnmarshaller();
			XMLInputFactory xif = XMLInputFactory.newFactory();
	        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
	        XMLStreamReader xsr = xif.createXMLStreamReader(in);
	        return Unchecked.cast(unmarshaller.unmarshal(xsr));	
		} catch (final JAXBException | XMLStreamException e) {
			throw new IOException(e);
		}
	}
	
}
