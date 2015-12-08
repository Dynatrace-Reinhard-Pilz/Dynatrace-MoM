package com.dynatrace.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Classes implementing {@link XmlSerializableBackup} are required to produce a valid
 * representation of their state in XML Code and the other way around.
 * </p>
 * <p>
 * The reason for the generic definition of this interface is to enforce the
 * signature of {@link #fromXML(InputStream)} to have as the type of its return
 * value the same class as the implementing class. Therefore callers of
 * {@link #fromXML(InputStream)} are not required to cast the result.
 * </p>
 * <p>
 * The utility class {@link XMLUtil} offers functionality to produce XML code
 * for classes implementing {@link XmlSerializableBackup} if they and their methods
 * are properly annotated with JAXB Annotations.
 * </p>
 * <p>
 * <strong>Sample class implementing {@link XmlSerializableBackup}</strong>
 * <pre>
 * {@link XmlRootElement}(name = "point")
 * {@link XmlAccessorType}(name = {@link XmlAccessType#FIELD})
 * public class Point implements {@link XmlSerializableBackup}&lt;Point&gt; {
 * 
 *   {@link XmlAttribute}(name = "x")
 *   private int x;
 *   {@link XmlAttribute}(name = "y")
 *   private int</strong> y;
 * 
 *   public Point() {
 *     // a public default constructor is required for JAXB deserialization
 *   }
 *   
 *   public int getX() {
 *     return x;
 *   }
 *   
 *   public void setX(int x) {
 *     this.x = x;
 *   }
 *   
 *   public int getY() {
 *     return y;
 *   }
 *   
 *   public void setY(int y) {
 *     this.y = y;
 *   }
 *   
 *   public void toXML(final OutputStream out) throws {@link IOException} {
 *     {@link XMLUtil}.serialize(
 *       this,
 *       out,
 *       Charset.defaultCharset(),
 *       Point.class
 *     );
 *   }
 *   
 *   public T fromXML(final InputStream in) throws {@link IOException} {
 *     return {@link XMLUtil}.deserialize(
 *       in,
 *       Point.class
 *     );
 *   }
 *   
 * }
 * </pre>
 * </p> 
 * 
 * @see XMLUtil#serialize(Object, OutputStream, java.nio.charset.Charset, Class...)
 * @see XMLUtil#serialize(Object, OutputStream, Class...)
 * 
 * @see XmlRootElement
 * @see XmlElement
 * @see XmlAttribute
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> as by definition the only possible type here is the class which
 * 				is implementing {@link XmlSerializableBackup}.
 */
public interface XmlSerializableBackup<T extends XmlSerializableBackup<T>> {

	/**
	 * Writes an XML representation of this object to the given
	 * {@link OutputStream}.<br />
	 * <br />
	 * Although not required it is recommended to ensure that the produced
	 * XML Code, when resolved by {@link #fromXML(InputStream)}, creates an
	 * exact copy of this object.
	 * 
	 * @param out the {@link OutputStream} to send the XML representation of
	 * 		this object to.
	 * 
	 * @throws IOException if either the writing to the given
	 * 		{@link OutputStream} fails or if an error happens when producing
	 * 		the XML code.
	 */
	void toXML(OutputStream out) throws IOException;
	
	/**
	 * Resolves a new instance of the class this object is an instance of out
	 * of XML code available from the given {@link InputStream}.<br />
	 * <br />
	 * Although not required it is recommended to ensure that the XML code
	 * produced by {@link #toXML(OutputStream)} produces, when resolved by
	 * {@link #fromXML(InputStream)}, produces an exact copy of the object.
	 * 
	 * @param in the {@link InputStream} offering the XML representation of the
	 * 		object to resolve.
	 * 
	 * @return a new instance of the class this object is an instance of.
	 * 
	 * @throws IOException if either reading from the given {@link InputStream}
	 * 		fails or if an error hapens during resolving the XML code.
	 */
	T fromXML(InputStream in) throws IOException;
}
