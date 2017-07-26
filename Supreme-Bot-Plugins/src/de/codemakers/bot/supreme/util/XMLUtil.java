package de.codemakers.bot.supreme.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public static final synchronized Document load(File file) {
        try {
            return load(new FileInputStream(file));
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

    public static final synchronized boolean save(Element rootElement, Format format, File file) {
        return save(new Document(rootElement), format, file);
    }

    public static final synchronized boolean save(Document document, Format format, File file) {
        try {
            return save(document, format, new FileOutputStream(file, false));
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
