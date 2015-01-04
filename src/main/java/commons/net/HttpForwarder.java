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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.StringTokenizer;

import commons.io.InputStreamLiner;

/**
 * This class listens on a socket for a HTTP request of the form<br>
 * <code>GET URL version</code><br>
 * e.g. <code>GET http://some.host.com/some/file.html HTTP/1.0</code><br>
 * and forwards the request to the URL.
 * 
 * Most of the job is delegated to {@link HttpForwarderRequest}.
 * This class only accept connections on a source port.
 *  
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class HttpForwarder {

    /** The port number where data is to be listen. */
    private int srcport;
    
    public HttpForwarder( int srcport ) {
        this.srcport = srcport;
    }
    
    public void run() {
        try {
			_run();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    private void _run() throws IOException {
        ServerSocket ss = new ServerSocket(srcport);
        System.out.println("HttpForwarder is ready on port "+srcport);
        while (true) {
            Socket client = ss.accept();
            new HttpForwarderRequest(client).start();
        }
    }
    
}


/**
 * This class listens on a socket for a HTTP request of the form<br>
 * <code>GET URL version</code><br>
 * e.g. <code>GET http://some.host.com/some/file.html HTTP/1.0</code><br>
 * and forwards the request to the URL.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
class HttpForwarderRequest extends Thread {
        
    /** The listener socket. */
    private Socket s;
    
    public HttpForwarderRequest( Socket s ) {
        this.s = s;
    }
    
    @Override
    public void run() {
        try {
            _run();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    private void _run() throws IOException, InterruptedException {
        
        /*
         * The streams to read and send back the data.
         */
        InputStream is = s.getInputStream();
        InputStreamLiner isl = new InputStreamLiner(is);
        OutputStream os = s.getOutputStream();
        
        /*
         * Analyze the request.
         */
        String request = isl.readLineWin();
        System.out.println(request);
        StringTokenizer st = new StringTokenizer(request);
        
        /* Skip the command. */
        st.nextToken();
        
        /*
         * Analyze the URL to get the hosts and ports.
         */
        String urlStr = st.nextToken();
        URL url = null;
        try {
            url = new URL(urlStr);
        }
        catch( MalformedURLException mue ) {
        	/*
        	 * The URL may be malformed if a protocol is missing. This happens
        	 * with HTTPS CONNECT request which are of the form:
        	 * 		CONNECT adminlip6.lip6.fr:443 HTTP/1.0
        	 * Add a mock https:// prefix just to make URL.init happy.
        	 */
        	url = new URL("https://"+urlStr);
        }
        String host = url.getHost();
        int port = url.getPort();
        if (port==-1) port=80;
        
        /*
         * Create the socket to forward the data.
         */
        Socket d = new Socket(host,port);
        InputStream dis = d.getInputStream();
        OutputStream dos = d.getOutputStream();
        dos.write( request.getBytes() );
        dos.write( 0x0D );
        dos.write( 0x0A );
        
        /*
         * Create two threads to send and receive data.
         */
        HttpForwarderPipe sender = new HttpForwarderPipe(is,dos);
        HttpForwarderPipe receiver = new HttpForwarderPipe(dis,os);
        receiver.start();
        sender.start();
        
        /*
         * Wait for the end of the receiver thread.
         * This thread ends when the remote server closes
         * its output stream and/or its socket.
         */
        receiver.join();
        
        dis.close();
        dos.close();
        os.close();
        isl.close();
        d.close();
        s.close();
    }
}

/**
 * This class reads data from an input stream and sends them to
 * an output stream.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
class HttpForwarderPipe extends Thread {
    
    private InputStream from;
    private OutputStream to;
    
    public HttpForwarderPipe( InputStream from, OutputStream to ) {
        this.from = from;
        this.to = to;
    }
    
    @Override
    public void run() {
        try {
			_run();
        } catch( SocketException se ) {
            /*
             * The socket may be closed by HttpForwarderRequest._run().
             * Explanations follow.
             *
             * Browser <-> Forwarder <-> Server
             *
             * Streams in used:
             * Forwarder -> Server : sender
             * Forwarder <- Server : receiver
             *
             * When receiver is closed by the server,
             * HttpForwarderRequest._run() closes sender, which is
             * blocked reading on the stream (b=from.read() below),
             * and SocketException is thrown.
             * There is nothing else to do, as we just want to stop reading.
             * Hence this is an excepted exception, and we just want
             * to go on the program.
             */
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    private void _run() throws IOException {
        int b;
        while ( (b=from.read()) != -1 )
            to.write(b);
    }
}
