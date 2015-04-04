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

package commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a way for retrieving options, flags and free values
 * transmitted as command line arguments.
 * 
 * Options and flags are supposed to be string starting with two minus
 * characters. Options are followed by a value. 
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class CmdLineArgs< F extends Enum<F>, O extends Enum<O> > {

    private Set<String> flags = new HashSet<>();
    private Set<String> options = new HashSet<>();
    private Map<String,String> values = new HashMap<>();
    private List<String> files = new ArrayList<>();
    
    /**
     * The domains of values for options. Option-indexed map. Not all option
     * necessarily define a domain.
     * 
     * @see #registerOption(Enum, String[])
     */
    private Map<String,Set<String>> domains = new HashMap<>();
    
    /**
     * Register a new flag.
     * 
     * @param flag  the flag to register
     * @throws IllegalArgumentException
     *      if flag is already registered as an option
     */
    public void registerFlag( F flag ) throws IllegalArgumentException {
        String key = "--"+flag.toString().toLowerCase();
        if( options.contains(key) ) {
            final String msg = key+" already registered as an option";
            throw new IllegalArgumentException(msg);
        }
        flags.add(key);
    }

    /**
     * Register new flags.
     * 
     * @param flags  the flags to register
     * @throws IllegalArgumentException
     *      if one of the flags is already registered as an option
     */
    public void registerFlags( F[] flags ) throws IllegalArgumentException {
        for (F flag : flags) {
            registerFlag(flag);
        }
    }

    /**
     * Register a new option.
     * 
     * @param option  the option to register
     * @throws IllegalArgumentException
     *      if option is already registered as a flag
     */
    public void registerOption( O option ) throws IllegalArgumentException {
        String key = "--"+option.toString().toLowerCase();
        if( flags.contains(key) ) {
            final String msg = key+" already registered as a flag";
            throw new IllegalArgumentException(msg);
        }
        options.add(key);
    }
    
    /**
     * Register new options.
     * 
     * @param options  the options to register
     * @throws IllegalArgumentException
     *      if one of the options is already registered as an option
     */
    public void registerOptions( O[] options ) throws IllegalArgumentException {
        for (O option : options) {
            registerOption(option);
        }
    }

    /**
     * Register a domain of values for the specified option.
     * {@link #parse(String[])} will throw {@link IllegalArgumentException} if
     * the specified value is not included in the domain.
     * 
     * @param option  the option
     * @param domain  the domain of values
     * @throws IllegalArgumentException
     *      if option is not registered as an option
     */
    public void setOptionDomain( O option, String[] domain )
    throws IllegalArgumentException {
        String key = "--"+option.toString().toLowerCase();
        if( ! options.contains(key) ) {
            final String msg = option+" is not an registered option";
            throw new IllegalArgumentException(msg);
        }
        List<String> list = Arrays.asList(domain);
        Set<String> set = new HashSet<>(list);
        domains.put(key,set);
    }
    
    /**
     * Return <code>true</code> if the specified flag is set in the command line
     * parsed by {@link #parse(String)}.
     */
    public boolean isFlagSet( F flag ) {
        String key = "--"+flag.toString().toLowerCase();
        return values.containsKey(key);
    }
    
    /**
     * Return the value of the specified option in the command line parsed by
     * {@link #parse(String)}.
     */
    public String getOptionValue( O option ) {
        String key = "--"+option.toString().toLowerCase();
        return values.get(key);
    }
    
    /**
     * Return the list of files in in the command line parsed by
     * {@link #parse(String[])}. Files are the strings in the command line
     * which are neither options, nor flags.
     */
    public List<String> getFiles() {
        return files;
    }
    
    /**
     * Parse the specified command line.
     * 
     * @param args  the command line to parse
     * @throws IllegalArgumentException  if an option is missing a value
     */
    public void parse( String[] args ) throws IllegalArgumentException {
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String key = arg.toLowerCase();
            if( flags.contains(key) ) {
                values.put(key,null);
            }
            else if( options.contains(key) ) {
                if( i == args.length-1 ) {
                    final String msg = "Missing value for option: "+arg;
                    throw new IllegalArgumentException(msg);
                }
                i++;
                if( domains.containsKey(key) ) {
                    // Option with a domain
                    Set<String> domain = domains.get(key);
                    if( ! domain.contains(args[i]) ) {
                        // Not a legal value
                        final String msg =
                            "Illegal value "+args[i]+" for option "+key;
                        throw new IllegalArgumentException(msg);
                    }
                }
                values.put(key,args[i]);
            }
            else if( key.charAt(0) == '-' ) {
                final String msg = "Illegal option: "+arg;
                throw new IllegalArgumentException(msg);
            }
            else {
                files.add(arg);
            }
        }
    }
}
