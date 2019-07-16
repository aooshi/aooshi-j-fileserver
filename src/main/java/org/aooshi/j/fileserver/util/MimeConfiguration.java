package org.aooshi.j.fileserver.util;

import org.aooshi.j.util.reader.MapCacheReader;


public class MimeConfiguration extends MapCacheReader {

    public final static MimeConfiguration instance = new MimeConfiguration("mime.txt"," ");

    private MimeConfiguration(String filename,String separator)
    {
        super(buildPath(filename),separator);
    }

    private static String buildPath(String filename)
    {
        String filepath = AppConfiguration.singleton.getBasePath();
        filepath += filename;

        return  filepath;
    }

}
