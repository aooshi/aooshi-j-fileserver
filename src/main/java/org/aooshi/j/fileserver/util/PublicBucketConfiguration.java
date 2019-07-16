package org.aooshi.j.fileserver.util;

import org.aooshi.j.util.reader.LineCacheReader;

public class PublicBucketConfiguration extends LineCacheReader {

    public final static PublicBucketConfiguration instance = new PublicBucketConfiguration("public.txt");


    private PublicBucketConfiguration(String filepath)
    {
        super(buildPath(filepath));
    }

    private static String buildPath(String filename)
    {
        String filepath = AppConfiguration.singleton.getBasePath();
        filepath += filename;

        return  filepath;
    }
}
