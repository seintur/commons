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
     * @param url   the url
     * @param file  the file
     */
    public static void get( String url, File file ) throws IOException, MalformedURLException {
        get( new URL(url), file );
    }

    /**
     * Get the content of an URL and save it into a file.
     *
     * @param url   the url
     * @param file  the file
     */
    public static void get( URL url, File file ) throws IOException, MalformedURLException {

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
     * @param url      the url
     * @param dirName  the directory name
     * @return         the file where the content has been saved
     */
    public static File get( String url, String dirName ) throws IOException, MalformedURLException {

        /** Get an input stream to load the content of the URL. */
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        InputStream is = conn.getInputStream();

        /** Guess a file name with an extension related to the content type. */
        String type = conn.getContentType();
        String fileExtension = (String) fileExtensions.get(type);
        // Unless createTempFile() is called, the returned file is not temporary.
        // The method is used to get an unique file name.
        File file = File.createTempFile("web",fileExtension,new File(dirName));
        FileOutputStream fos = new FileOutputStream(file);

        /** Write each byte read from the input stream to the file. */
        for ( int i=0 ; (i=is.read()) != -1 ; )
            fos.write(i);

        is.close();
        fos.close();

        return file;
    }

    /**
     * Store assocations between mime types and file extensions.
     */
    final public static Map<Object,Object> fileExtensions =
        MapExt.create(
            new Object[][]{
                {"text/html",".html"},
                {"image/gif",".gif"},
                {"image/jpeg",".jpg"}, {"image/jpg",".jpg"}, {"image/pjpeg",".jpg"}
            }
        );
    

    /**
     * Recursively get the content of an URL and save it into a directory.
     * All relative links (hrefs and imgs) referenced by the url are saved.
     *
     * @param initialUrl  the URL
     * @param dir         the directory
     * @return            a map of (url,file) loaded
     */
    public static Map<String,File> recursiveGet( String initialUrl, File dir )
        throws IOException, MalformedURLException {

        /**
         * urlsToGet contains mapping between urls to load and local files storing their contents.
         * Same thing for urlsLoaded except that the urls have already been loaded.
         *
         * At each iteration step, one url is extracted from urlsToGet.
         * Its content is analyzed and referenced links are added to urlsToGet.
         * At the end of each iteration step, the examined url is removed to urlsToGet
         * and added to urlsToLoad.
         *
         * urlsAbsolute stores mappings between absolute urls and their original form encountered
         * when they are referenced by a link.
         */
        Map<String,File> urlsToGet = new HashMap<String,File>();
        Map<String,File> urlsLoaded = new HashMap<String,File>();
        Map<String,String> urlsAbsolute = new HashMap<String,String>();
        urlsToGet.put( initialUrl, new File(dir,"index.html") );
        urlsAbsolute.put( initialUrl, initialUrl );

        Set<Map.Entry<String,File>> entrySetUrlsToGet = urlsToGet.entrySet();

        HTMLEditorKit.Parser parser = new URLExtHTMLEditorKit().getParser();
        HrefsExtractor hrefsExtractor = new HrefsExtractor(initialUrl,urlsToGet,urlsLoaded,urlsAbsolute);

        /**
         * Get an URL, parse it to extract its links.
         * Iterate the process with the links.
         */
        while ( urlsToGet.size() != 0 ) {

            /**
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

            /** Check whether the URL contains data. */
            if ( contentType != null ) {

                /** Guess the file extension from the content-type. */
                if ( currentFile == null ) {

                    // Content type strings are such as text/html;charset=ISO-8859-1 !!
                    // Hence the test with matchKeyWithString
                    // String linkFileExtension = (String) fileExtensions.get(contentType);
                    String linkFileExtension =
                        (String) MapExt.matchKeyWithString( fileExtensions, contentType );

                    // Unless createTempFile() is called, the returned file is not temporary.
                    // The method is used to get an unique file name.
                    currentFile = File.createTempFile("web",linkFileExtension,dir);
                }

                /** Extract HREFs. */
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

        /**
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
            urls[i] = (String) urlsAbsolute.get(fullUrl);
            File file = (File) urlsLoaded.get(fullUrl);
            filenames[i] = (file==null) ? "" : file.getName();
            System.out.println( urls[i] + " -> " + filenames[i] );
        }

        /**
         * Replace links with relative ones and save the file.
         */
        Set entrySetUrlsLoaded = urlsLoaded.entrySet();
        Iterator iteratorUrlsLoaded = entrySetUrlsLoaded.iterator();
        while ( iteratorUrlsLoaded.hasNext() ) {
            
            Map.Entry mapEntryUrlsLoaded = (Map.Entry) iteratorUrlsLoaded.next();
            String currentUrl = (String) mapEntryUrlsLoaded.getKey();
            File currentFile = (File) mapEntryUrlsLoaded.getValue();

            /** URLs that contain no data are associated with null File instances. */
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
    
    public void handleSimpleTag( HTML.Tag t, MutableAttributeSet a, int pos ) { handle(t,a,pos); }
    public void handleStartTag( HTML.Tag t, MutableAttributeSet a, int pos ) { handle(t,a,pos); }

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
             !urlsToGet.containsKey(fullUrlStr) && !urlsLoaded.containsKey(fullUrlStr) ) {

            urlsToGet.put( fullUrlStr, null );
            urlsAbsolute.put( fullUrlStr, urlStr );
        }
    }
}


//  /**
//   * Version 1.1 deprecated: used the Acme HTML parser.
//   *
//   * Recursively get the content of an URL and save it into a directory.
//   * All relative links (hrefs and imgs) referenced by the url are saved.
//   *
//   * @param initialUrl  the URL
//   * @param dirName     the directory name
//   */
//  public static void recursiveGet( String initialUrl, String dirName )
//      throws IOException, MalformedURLException {
//
//      /**
//       * urlsToGet contains mapping between urls to load and local files storing their contents.
//       * Same thing for urlsLoaded except that the urls have already been loaded.
//       *
//       * At each iteration step, one url is extracted from urlsToGet.
//       * Its content is analyzed and referenced links are added to urlsToGet.
//       * At the end of each iteration step, the examined url is removed to urlsToGet
//       * and added to urlsToLoad.
//       *
//       * urlsAbsolute stores mappings between absolute urls and their original form encountered
//       * when they are referenced by a link.
//       */
//      HashMap urlsToGet = new HashMap();
//      HashMap urlsLoaded = new HashMap();
//      HashMap urlsAbsolute = new HashMap();
//      urlsToGet.put( initialUrl, new File(dirName,"index.html") );
//      urlsAbsolute.put( initialUrl, initialUrl );
//
//      File dir = new File(dirName);
//      Set entrySetUrlsToGet = urlsToGet.entrySet();
//
//      HtmlObserver htmlObserver = new HrefsExtractor(initialUrl,urlsToGet,urlsLoaded,urlsAbsolute);
//
//      /**
//       * Get an URL, parse it to extract its links.
//       * Iterate the process with the links.
//       */
//      while ( urlsToGet.size() != 0 ) {
//
//          /**
//           * Elements may be added to urlsToGet after getting the iterator.
//           * If after that, we try to reuse iterator a ConcurrentModificationException
//           * would be thrown. To avoid this, we get a new iterator
//           * (even if we use it only to retrieve one element).
//           */
//          Iterator iteratorUrlsToGet = entrySetUrlsToGet.iterator();
//          Map.Entry mapEntryUrlsToGet = (Map.Entry) iteratorUrlsToGet.next();
//          String currentUrl = (String) mapEntryUrlsToGet.getKey();
//          File currentFile = (File) mapEntryUrlsToGet.getValue();
//
//          URL url = new URL(currentUrl);
//          URLConnection conn = url.openConnection();
//          String contentType = conn.getContentType();
//
//          /** Check whether the URL contains data. */
//          if ( contentType != null ) {
//
//              /** Guess the file extension from the content-type. */
//              if ( currentFile == null ) {
//
//                  // Content type strings are such as text/html;charset=ISO-8859-1 !!
//                  // Hence the test with matchKeyWithString
//                  // String linkFileExtension = (String) fileExtensions.get(contentType);
//                  String linkFileExtension =
//                      (String) fileExtensions.matchKeyWithString(contentType);
//
//                  // Unless createTempFile() is called, the returned file is not temporary.
//                  // The method is used to get an unique file name.
//                  currentFile = File.createTempFile("web",linkFileExtension,dir);
//              }
//
//              /** Extract HREFs. */
//              if ( contentType.startsWith("text/html") ) {                
//                  InputStream is = conn.getInputStream();
//                  HtmlScanner htmlScanner = new HtmlScanner(is,url,htmlObserver);
//                  int b;
//                  while ( (b=htmlScanner.read()) != -1 );
//                  is.close();
//              }
//          }
//
//          urlsToGet.remove(currentUrl);
//          urlsLoaded.put(currentUrl,currentFile);
//      }
//
//      /**
//       * Get all URLs extracted, change links to relative local ones and save the file.
//       */
//      Object[] fullUrls = urlsLoaded.keySet().toArray();
//      Object[] files = urlsLoaded.values().toArray();
//      
//      String[] urls = new String[ files.length ];
//      String[] filenames = new String[ files.length ];
//
//      for ( int i=0 ; i < files.length ; i++ ) {
//          urls[i] = (String) urlsAbsolute.get(fullUrls[i]);
//          File file = (File) files[i];
//          filenames[i] = (file==null) ? "" : file.getName();
//          System.out.println( urls[i] + " -> " + filenames[i] );
//      }
//
//      Set entrySetUrlsLoaded = urlsLoaded.entrySet();
//      Iterator iteratorUrlsLoaded = entrySetUrlsLoaded.iterator();
//      while ( iteratorUrlsLoaded.hasNext() ) {
//          
//          Map.Entry mapEntryUrlsLoaded = (Map.Entry) iteratorUrlsLoaded.next();
//          String currentUrl = (String) mapEntryUrlsLoaded.getKey();
//          File currentFile = (File) mapEntryUrlsLoaded.getValue();
//
//          /** URLs that contain no data are associated with null File instances. */
//          if ( currentFile != null ) {
//
//              URL url = new URL(currentUrl);
//              URLConnection conn = url.openConnection();
//              InputStream is = conn.getInputStream();
//              FileOutputStream fos = new FileOutputStream(currentFile);
//              FindAndReplaceOutputStream faros =
//                  FindAndReplaceOutputStream.create(fos,urls,filenames);
//
//              PipedStreams ps = new PipedStreams(is,faros);
//              ps.dump();
//              ps.close();
//          }
//      }
//  }
//
//  /**
//   * Version 1.0 deprecated: used an ad-hoc written parser for HTML.
//   *
//   * Load the content of a given URL,
//   * transform its relative links into local static links,
//   * and save the result into a file.
//   *
//   * @param url        the URL
//   * @param file       the file
//   * @param urlsToGet  the urls to get
//   * @param urlLoaded  the urls loaded so far
//   * @param dir        the directory where files are to be saved
//   */
//  public static void getAndUnlink(
//      String url, File file, HashMap urlsToGet, HashMap urlsLoaded, File dir ) 
//      throws IOException, MalformedURLException {
//
//      /**
//       * Get an OutputStreamWriter in order to write characters and strings
//       * extracted from the stream tokenizer.
//       * Associate the OutputStreamWriter with the given file.
//       */
//      FileOutputStream fos = new FileOutputStream(file);
//      OutputStreamWriter osw = new OutputStreamWriter(fos);
//
//      /**
//       * Get a stream tokenizer from the input stream.
//       */
//      URL u = new URL(url);
//      URLConnection conn = u.openConnection();
//      String type = conn.getContentType();
//      InputStream is = conn.getInputStream();
//      InputStreamReader isr = new InputStreamReader(is);
//      StreamTokenizer st = new StreamTokenizer(isr);
//      st.ordinaryChar('/');
//      st.eolIsSignificant(true);
//
//      /**
//       * Write back the input stream to the output stream and
//       * look for <a href="..."> and <img src="..."> in the input stream.
//       * If such occurrences are found and if the link is relative,
//       * transform it into a local static link.
//       */
//      for ( int state=0 ; st.ttype != StreamTokenizer.TT_EOF ;  ) {
//          int newState = 0;
//          st.nextToken();
//          switch(state) {
//              case 0 : if (st.ttype=='<') newState=1; break;
//              case 1 : if (st.ttype==StreamTokenizer.TT_WORD) {
//                          if (st.sval.equals("a")) newState=2;
//                          else if (st.sval.equals("img")) newState=5;
//                          else newState=0;
//                       }
//                       else newState=0;
//                       break;
//
//              case 2 : if (st.ttype==StreamTokenizer.TT_WORD && st.sval.equals("href")) newState=3;
//                       else if (st.ttype=='>') newState=0;
//                       break;
//              case 3 : if (st.ttype=='=') newState=4;
//                       else newState=2;
//                       break;
//              case 4 : if (st.ttype==StreamTokenizer.TT_WORD || st.ttype=='"') {
//                          newState=0;
//                       }
//                       else newState=2;
//                       break;
//
//              case 5 : if (st.ttype==StreamTokenizer.TT_WORD && st.sval.equals("src")) newState=6;
//                       else if (st.ttype=='>') newState=0;
//                       break;
//              case 6 : if (st.ttype=='=') newState=7;
//                       else newState=5;
//                       break;
//              case 7 : if (st.ttype==StreamTokenizer.TT_WORD || st.ttype=='"') {
//                          newState=0;
//                       }
//                       else newState=5;
//                       break;
//          }
//
//          if ( (state==4 || state==7) && newState==0 ) {
//              /**
//               * <a href="..."> or <img src="..."> have been found.
//               * st.sval contains the link url.
//               * We only consider http links.
//               */
//              URIExt linkURIExt = new URIExt(url);
//              linkURIExt.resolve(new URIExt(st.sval));
//              String linkURIExtString = linkURIExt.toString();
//
//              if ( linkURIExt.getScheme().equals("http") ) {
//
//                  File linkFile = (File) urlsToGet.get(linkURIExtString);
//                  if (linkFile==null)  linkFile = (File) urlsLoaded.get(linkURIExtString);
//
//                  if ( linkFile == null ) {
//                      /**
//                       * The link is neither is the list of urls to load, nor has been loaded.
//                       * Register it as an url to load if it is an html link, load it otherwise.
//                       * Tomcat returns content type strings such as text/html;charset=ISO-8859-1 !!
//                       * Hence the test with startsWith()
//                       */
//                      URL linkURL = new URL(linkURIExtString);
//                      URLConnection linkURLConn = linkURL.openConnection();
//                      String linkType = linkURLConn.getContentType();
//
//                      // String linkFileExtension = (String) fileExtensions.get(linkType);
//                      String linkFileExtension = null;
//                      Set entrySet = fileExtensions.entrySet();
//                      Iterator iterator = entrySet.iterator();
//                      while ( iterator.hasNext() && linkFileExtension==null ) {
//                          Map.Entry entry = (Map.Entry) iterator.next();
//                          String key = (String) entry.getKey();
//                          if ( linkType.startsWith(key) ) {
//                              linkFileExtension = (String) entry.getValue();
//                          }
//                      }
//                      
//                      // Unless createTempFile() is called, the returned file is not temporary.
//                      // The method is used to get an unique file name.
//                      linkFile = File.createTempFile("web",linkFileExtension,dir);
//                      System.out.println( file.getName() + " -> " + linkFile.getName() );
//
//                      if ( linkFileExtension.equals(".html") ) {
//                          urlsToGet.put( linkURIExtString, linkFile );
//                      }
//                      else {
//                          get( linkURL, linkFile );
//                          urlsLoaded.put( linkURIExtString, linkFile );
//                      }
//                  }
//
//                  String s = "\"" + linkFile.getName() + "\"";
//                  osw.write(s,0,s.length());
//
//              }
//              else {
//                  /** Non http links. Write them to the input stream unchanged. */
//                  osw.write( "\"" + st.sval + "\"" , 0 , st.sval.length()+2 );
//              }
//
//          }
//          else {
//              /**
//               * In all other cases,
//               * writes the content read from the input stream
//               * to the output stream.
//               */
//              switch(st.ttype) {
//                  case StreamTokenizer.TT_EOF :
//                      break;
//                  case StreamTokenizer.TT_EOL :
//                      osw.write('\r');
//                      osw.write('\n');
//                      break;
//                  case StreamTokenizer.TT_NUMBER :
//                      String d = Double.toString(st.nval);
//                      osw.write(d,0,d.length());
//                      break;
//                  case StreamTokenizer.TT_WORD :
//                      osw.write(st.sval,0,st.sval.length());
//                      if ( st.sval.charAt(0) != '&' ) {
//                          // If st.sval starts with &, we assume this is an HTML encoding
//                          // The ; is not in st.sval
//                          // Don't write space to keep &foo;
//                          osw.write(' ');
//                      }
//                      break;
//                  case '"' :
//                      String s = "\"" + st.sval + "\"";
//                      osw.write(s,0,s.length());
//                      break;
//                  default :
//                      osw.write(st.ttype);
//                      break;
//              }
//          }
//
//          state = newState;
//      }
//
//      osw.close();
//      fos.close();
//
//      is.close();
//  }

///**
// * Deprecated version 1.1: used the Acme HTML parser.
// *
// * Callback class for the Acme parser.
// * Notified whenever a HTML tag is encountered.
// */
//class HrefsExtractor implements HtmlObserver {
//
//  private String initialUrl;
//  private HashMap urlsToGet;
//  private HashMap urlsLoaded;
//  private HashMap urlsAbsolute;
//
//  public HrefsExtractor( String initialUrl, HashMap urlsToGet, HashMap urlsLoaded, HashMap urlsAbsolute ) {
//      this.initialUrl = initialUrl;
//      this.urlsToGet = urlsToGet;
//      this.urlsLoaded = urlsLoaded;
//      this.urlsAbsolute = urlsAbsolute;
//  }
//
//  private void treatHREF( String urlStr ) {
//      
//      URIExt url = new URIExt(initialUrl);
//      url.resolve( new URIExt(urlStr) );
//      String fullUrlStr = url.toString();
//
//      if ( url.getScheme().equals("http") &&
//           !urlsToGet.containsKey(fullUrlStr) && !urlsLoaded.containsKey(fullUrlStr) ) {
//          
//          urlsToGet.put( fullUrlStr, null );
//          urlsAbsolute.put( fullUrlStr, urlStr );
//      }
//  }
//
//  // HtmlObserver methods
//  public void gotAHREF( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotIMGSRC( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotFRAMESRC( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotBASEHREF( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotAREAHREF( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotLINKHREF( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//  public void gotBODYBACKGROUND( String urlStr, URL contextUrl, Object clientData ) { treatHREF(urlStr); }
//}