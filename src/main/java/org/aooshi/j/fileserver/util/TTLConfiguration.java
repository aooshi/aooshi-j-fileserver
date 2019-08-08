package org.aooshi.j.fileserver.util;

import org.aooshi.j.util.reader.MapCacheReader;


public class TTLConfiguration extends MapCacheReader {

    public final static TTLConfiguration instance = new TTLConfiguration("ttl.txt",":");

    private TTLConfiguration(String filename, String separator)
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
