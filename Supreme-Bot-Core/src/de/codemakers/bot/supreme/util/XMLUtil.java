package de.codemakers.bot.supreme.util;

import de.codemakers.io.file.AdvancedFile;
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

    public static final synchronized Document load(AdvancedFile file) {
        try {
            return load(file.createInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final synchronized Document load(String jar_path) {
        try {
            return load(XMLUtil.class.getResourceAsStream(jar_path));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final synchronized Document load(InputStream inputStream) {
        try {
            return saxBuilder.build(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final synchronized boolean save(Element rootElement, Format format, AdvancedFile file) {
        return save(new Document(rootElement), format, file);
    }

    public static final synchronized boolean save(Document document, Format format, AdvancedFile file) {
        try {
            return save(document, format, file.createOutputstream(false));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final synchronized boolean save(Element rootElement, Format format, OutputStream outputStream) {
        return save(new Document(rootElement), format, outputStream);
    }

    public static final synchronized boolean save(Document document, Format format, OutputStream outputStream) {
        try {
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
