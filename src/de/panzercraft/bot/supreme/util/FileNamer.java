package de.panzercraft.bot.supreme.util;

import java.io.File;

/**
 * FileNamer
 *
 * @author Panzer1119
 */
public class FileNamer {
    
    private String prefix;
    private String suffix;

    public FileNamer() {
        this("", "");
    }
    
    public FileNamer(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final FileNamer setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public final String getSuffix() {
        return suffix;
    }

    public final FileNamer setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }
    
    public final File createFile(String extra) {
        if (extra == null) {
            return null;
        }
        return new File(createFileName(extra));
    }
    
    public final String createFileName(String extra) {
        if (extra == null) {
            return null;
        }
        return String.format("%s%s%s", prefix, extra, suffix);
    }
    
    public final boolean isFilePathOfThis(File file) {
        if (file == null) {
            return false;
        }
        return isFileNameOfThis(file.getPath());
    }
    
    public final boolean isFileNameOfThis(File file) {
        if (file == null) {
            return false;
        }
        return isFileNameOfThis(file.getName());
    }
    
    public final boolean isFileNameOfThis(String name) {
        if (name == null) {
            return false;
        }
        return name.startsWith(prefix) && name.endsWith(suffix);
    }
    
    public final String getExtraOfFilePath(File file) {
        return getExtraOfName(file.getPath());
    }
    
    public final String getExtraOfFileName(File file) {
        return getExtraOfName(file.getName());
    }
    
    public final String getExtraOfName(String name) {
        if (!isFileNameOfThis(name)) {
            return null;
        }
        if (name.isEmpty()) {
            return "";
        }
        return name.substring(prefix.length(), name.length() - suffix.length());
    }

}
