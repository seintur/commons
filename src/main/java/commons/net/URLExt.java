/***
 * Commons
 * Copyright (C) 2015 University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Lionel.Seinturier@univ-lille1.fr
 *
 * Author: Lionel Seinturier
 */

package commons.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import commons.io.FindAndReplaceOutputStream;
import commons.io.PipedStreams;
import commons.lang.StringExt;
import commons.util.MapExt;

/**
 * This class holds web related functionalities not found in
 * java.net.URL (hence the suffix Ext).
 *
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class URLExt {
    
    /**
     * Get the content of an URL and save it into a file.
     *
     * @param url   the URL
     * @param file  the file
     */
    public static void get( String url, File file )
    throws IOException, MalformedURLException {
        get( new URL(url), file );
    }

    /**
     * Get the content of an URL and save it into a file.
     *
     * @param url   the URL
     * @param file  the file
     */
    public static void get( URL url, File file )
    throws IOException, MalformedURLException {

        InputStream is = url.openStream();
        FileOutputStream fos = new FileOutputStream(file);

        /** Write each byte read from the input stream to the file. */
        for ( int i=0 ; (i=is.read()) != -1 ; )
            fos.write(i);

        is.close();
        fos.close();
    }

    /**
     * Get the content of an URL,
     * guess a file name (related to the content type),
     * and save it into a directory.
     *
     * @param url      the URL
     * @param dirName  the directory name
     * @return         the file where the content has been saved
     */
    public static File get( String url, String dirName )
    throws IOException, MalformedURLException {

        // Get an input stream to load the content of the URL
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        InputStream is = conn.getInputStream();

        // Guess a file name with an extension related to the content type
        String type = conn.getContentType();
        String fileExtension = fileExtensions.get(type);
        
        // Unless createTempFile() is called, the returned file is not temporary.
        // The method is used to get an unique file name.
        File file = File.createTempFile("web",fileExtension,new File(dirName));
        FileOutputStream fos = new FileOutputStream(file);

        // Write each byte read from the input stream to the file
        for ( int i=0 ; (i=is.read()) != -1 ; )
            fos.write(i);

        is.close();
        fos.close();

        return file;
    }

    /**
     * Store associations between mime types and file extensions.
     */
    final public static Map<String,String> fileExtensions =
        new HashMap<String,String>() {
            private static final long serialVersionUID = 3474986436687996921L;
        {
            put( "text/html",".html" );
            put( "image/gif",".gif" );
            put( "image/jpeg",".jpg" );
            put( "image/jpg",".jpg" );
            put( "image/pjpeg",".jpg" );
        }};    

    /**
     * Recursively get the content of an URL and save it into a directory.
     * All relative links (hrefs and imgs) referenced by the URL are saved.
     *
     * @param initialUrl  the URL
     * @param dir         the directory
     * @return            a map of (URL,file) loaded
     */
    public static Map<String,File> recursiveGet( String initialUrl, File dir )
        throws IOException, MalformedURLException {

        /*
         * urlsToGet contains mapping between URLs to load and local files
         * storing their contents. Same thing for urlsLoaded except that the
         * URLs have already been loaded.
         *
         * At each iteration step, one URL is extracted from urlsToGet.
         * Its content is analyzed and referenced links are added to urlsToGet.
         * At the end of each iteration step, the examined URL is removed from
         * urlsToGet and added to urlsToLoad.
         *
         * urlsAbsolute stores mappings between absolute URLs and their original
         * form encountered when they are referenced by a link.
         */
        Map<String,File> urlsToGet = new HashMap<String,File>();
        Map<String,File> urlsLoaded = new HashMap<String,File>();
        Map<String,String> urlsAbsolute = new HashMap<String,String>();
        urlsToGet.put( initialUrl, new File(dir,"index.html") );
        urlsAbsolute.put( initialUrl, initialUrl );

        Set<Map.Entry<String,File>> entrySetUrlsToGet = urlsToGet.entrySet();

        HTMLEditorKit.Parser parser = new URLExtHTMLEditorKit().getParser();
        HrefsExtractor hrefsExtractor =
            new HrefsExtractor(initialUrl,urlsToGet,urlsLoaded,urlsAbsolute);

        /*
         * Get an URL, parse it to extract its links.
         * Iterate the process with the links.
         */
        while ( urlsToGet.size() != 0 ) {

            /*
             * Elements may be added to urlsToGet after getting the iterator.
             * If after that, we try to reuse iterator a ConcurrentModificationException
             * would be thrown. To avoid this, we get a new iterator
             * (even if we use it only to retrieve one element).
             */
            Iterator<Map.Entry<String,File>> iteratorUrlsToGet = entrySetUrlsToGet.iterator();
            Map.Entry<String,File> mapEntryUrlsToGet = iteratorUrlsToGet.next();
            String currentUrl = mapEntryUrlsToGet.getKey();
            File currentFile = mapEntryUrlsToGet.getValue();

            URL url = new URL(currentUrl);
            URLConnection conn = url.openConnection();
            String contentType = conn.getContentType();

            // Check whether the URL contains data
            if ( contentType != null ) {

                // Guess the file extension from the content-type
                if ( currentFile == null ) {

                    // Content type strings are such as text/html;charset=ISO-8859-1 !!
                    // Hence the test with matchKeyWithString
                    // String linkFileExtension = (String) fileExtensions.get(contentType);
                    String linkFileExtension =
                        MapExt.matchKeyWithString( fileExtensions, contentType );

                    // Unless createTempFile() is called, the returned file is not temporary.
                    // The method is used to get an unique file name.
                    currentFile = File.createTempFile("web",linkFileExtension,dir);
                }

                // Extract HREFs
                if ( contentType.startsWith("text/html") ) {                
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    parser.parse( isr, hrefsExtractor, true );
                    isr.close();
                    is.close();
                }
            }

            urlsToGet.remove(currentUrl);
            urlsLoaded.put(currentUrl,currentFile);
        }

        /*
         * Get all URLs extracted URLs. Sort them.
         * Then, take the array in reverse order to look for most significant URLs first
         * e.g. if URLs http://f.com/a and http://f.com/aa are be looked for,
         * http://f.com/aa is to be looked for first.
         */
        Object[] fullUrls = urlsLoaded.keySet().toArray();
        Arrays.sort(fullUrls);
        
        String[] urls = new String[ fullUrls.length ];
        String[] filenames = new String[ fullUrls.length ];

        for ( int i=0 ; i < urls.length ; i++ ) {
            Object fullUrl = fullUrls[urls.length-1-i];
            urls[i] = urlsAbsolute.get(fullUrl);
            File file = urlsLoaded.get(fullUrl);
            filenames[i] = (file==null) ? "" : file.getName();
            System.out.println( urls[i] + " -> " + filenames[i] );
        }

        /*
         * Replace links with relative ones and save the file.
         */
        Set<Map.Entry<String,File>> entrySetUrlsLoaded = urlsLoaded.entrySet();
        for (Map.Entry<String,File> mapEntryUrlsLoaded : entrySetUrlsLoaded) {

            String currentUrl = mapEntryUrlsLoaded.getKey();
            File currentFile = mapEntryUrlsLoaded.getValue();

            // URLs that contain no data are associated with null File instances
            if ( currentFile != null ) {

                URL url = new URL(currentUrl);
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(currentFile);
                FindAndReplaceOutputStream faros = FindAndReplaceOutputStream.create(fos,urls,filenames);
                PipedStreams.dump(is,faros);
                is.close();
                faros.close();
            }
        }

        return urlsLoaded;
    }


    /**
     * Return an URL from an array of directory names.
     *
     * @param pathNames  an array of directory names
     * @return           the corresponding URL
     */
    public static String toURL( String[] pathNames ) {
       return StringExt.insert(pathNames,"/");
    }

}

class URLExtHTMLEditorKit extends HTMLEditorKit {
    static final long serialVersionUID = 8178444825951807423L;
    @Override
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}

class HrefsExtractor extends HTMLEditorKit.ParserCallback {

    private String initialUrl;
    private Map<String,File> urlsToGet;
    private Map<String,File> urlsLoaded;
    private Map<String,String> urlsAbsolute;

    public HrefsExtractor(
            String initialUrl, Map<String,File> urlsToGet,
            Map<String,File> urlsLoaded, Map<String,String> urlsAbsolute ) {
        this.initialUrl = initialUrl;
        this.urlsToGet = urlsToGet;
        this.urlsLoaded = urlsLoaded;
        this.urlsAbsolute = urlsAbsolute;
    }
    
    @Override
    public void handleSimpleTag( HTML.Tag t, MutableAttributeSet a, int pos ) {
        handle(t,a,pos);
    }

    @Override
    public void handleStartTag( HTML.Tag t, MutableAttributeSet a, int pos ) {
        handle(t,a,pos);
    }

    private void handle( HTML.Tag t, MutableAttributeSet a, int pos ) {

        String urlStr = (String) a.getAttribute(HTML.Attribute.HREF);
        if (urlStr==null) urlStr = (String) a.getAttribute(HTML.Attribute.SRC);
        if (urlStr==null) return;

        URI url = null;
        try {
            url = new URI(initialUrl);
        }
        catch( URISyntaxException use ) {
            return;
        }

        URI fullUrl = url.resolve(urlStr);
        String fullUrlStr = fullUrl.toString();

        if ( fullUrl.getScheme().equals("http") &&
             !urlsToGet.containsKey(fullUrlStr) &&
             !urlsLoaded.containsKey(fullUrlStr) ) {

            urlsToGet.put( fullUrlStr, null );
            urlsAbsolute.put( fullUrlStr, urlStr );
        }
    }
}
