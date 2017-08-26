package de.codemakers.bot.supreme.commands.impl.moderation.xml;

import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.XMLUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;

/**
 * XMLEditor
 *
 * @author Panzer1119
 */
public class XMLEditor {

    private AdvancedFile file = null;
    private Document document;
    private Element rootElement;
    private final ArrayList<Element> path = new ArrayList<>();

    public XMLEditor(AdvancedFile file) {
        load(file);
    }

    public XMLEditor(String jar_path) {
        load(jar_path);
    }

    public XMLEditor(InputStream inputStream) {
        load(inputStream);
    }

    public XMLEditor(Document document) {
        load(document);
    }

    public XMLEditor(Element rootElement) {
        load(rootElement);
    }

    public XMLEditor(Document document, Element rootElement) {
        load(document, rootElement);
    }

    public XMLEditor() {
        this.document = null;
        this.rootElement = null;
    }

    public final XMLEditor load(AdvancedFile file) {
        setFile(file);
        return load(XMLUtil.load(file));
    }

    public final XMLEditor load(String jar_path) {
        return load(XMLUtil.load(jar_path));
    }

    public final XMLEditor load(InputStream inputStream) {
        return load(XMLUtil.load(inputStream));
    }

    public final XMLEditor load(Document document) {
        path.clear();
        if (document == null) {
            this.rootElement = null;
        } else {
            this.rootElement = document.detachRootElement();
            addFirst(this.rootElement);
        }
        this.document = document;
        return this;
    }

    public final XMLEditor load(Element rootElement) {
        this.rootElement = rootElement;
        path.clear();
        addFirst(this.rootElement);
        return this;
    }

    public final XMLEditor load(Document document, Element rootElement) {
        this.document = document;
        this.rootElement = rootElement;
        path.clear();
        addFirst(this.rootElement);
        return this;
    }

    public final boolean save() {
        if (file == null) {
            return false;
        }
        return save(file);
    }

    public final boolean save(AdvancedFile file) {
        rootElement.detach();
        return XMLUtil.save(rootElement, Format.getPrettyFormat(), file);
    }

    public final boolean save(OutputStream outputStream) {
        rootElement.detach();
        return XMLUtil.save(rootElement, Format.getPrettyFormat(), outputStream);
    }

    public final AdvancedFile getFile() {
        return file;
    }

    public final XMLEditor setFile(AdvancedFile file) {
        this.file = file;
        return this;
    }

    public final Document getDocument() {
        return document;
    }

    public final XMLEditor setDocument(Document document) {
        this.document = document;
        return this;
    }

    public final Element getRootElement() {
        return rootElement;
    }

    public final XMLEditor setRootElement(Element rootElement) {
        this.rootElement = rootElement;
        path.clear();
        return this;
    }

    public final ArrayList<Element> getPath() {
        return path;
    }

    public final Element goDown(String childName) {
        final Element element = getLast();
        if (element == null) {
            return null;
        }
        final Element child = element.getChild(childName);
        if (child == null) {
            return null;
        }
        addLast(child);
        return child;
    }

    public final Element goDown(String childName, int index) {
        final Element element = getLast();
        if (element == null) {
            return null;
        }
        final List<Element> children = getChildren(childName);
        if (index < 0 || index >= children.size()) {
            return null;
        }
        final Element child = children.get(index);
        if (child == null) {
            return null;
        }
        addLast(child);
        return child;
    }

    public final List<Element> getChildren(String childName) {
        final Element element = getLast();
        if (element == null) {
            return new ArrayList<>();
        }
        return element.getChildren(childName);
    }

    public final List<Element> getAllChildren() {
        final Element element = getLast();
        if (element == null) {
            return new ArrayList<>();
        }
        return element.getChildren();
    }

    public final Element goUp() {
        if (getLast() == null || path.size() < 1) { //FIXME good?
            return null;
        }
        removeLast();
        return getLast();
    }

    public final Element getLast() {
        return get(path.size() - 1);
    }

    public final Element getFirst() {
        return get(0);
    }

    public final Element get(int index) {
        if (index < 0 || index >= path.size()) {
            return null;
        }
        return path.get(index);
    }

    public final XMLEditor addLast(Element element) {
        return add(element, path.size());
    }

    public final XMLEditor addFirst(Element element) {
        return add(element, 0);
    }

    public final XMLEditor add(Element element, int index) {
        if (index < 0 || index > path.size() + 1) {
            return this;
        }
        path.add(index, element);
        return this;
    }

    public final XMLEditor removeLast() {
        return remove(path.size() - 1);
    }

    public final XMLEditor removeFirst() {
        return remove(0);
    }

    public final XMLEditor remove(int index) {
        if (index < 0 || index >= path.size()) {
            return this;
        }
        path.remove(index);
        return this;
    }

}
