package de.codemakers.bot.supreme.util;

import java.io.InputStream;
import java.io.OutputStream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * XMLUtil
 *
 * @author Panzer1119
 */
public class XMLUtil {

    private static final SAXBuilder saxBuilder = new SAXBuilder();
    private static final XMLOutputter xmlOutput = new XMLOutputter();

    public static final synchronized Document fromInputStream(InputStream inputStream) {
        try {
            return saxBuilder.build(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final synchronized boolean toOutputStream(Element rootElement, Format format, OutputStream outputStream) {
        try {
            final Document document = new Document(rootElement);
            xmlOutput.setFormat(format);
            xmlOutput.output(document, outputStream);
            outputStream.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
