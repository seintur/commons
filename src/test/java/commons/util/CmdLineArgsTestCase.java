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

import org.junit.Before;
import org.junit.Test;

/**
 * Class for testing the functionalities of {@link CmdLineArgs}.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class CmdLineArgsTestCase {

	private static enum CmdLineFlags { DEBUG }
	private static enum CmdLineOptions { SOURCE }
	
    private CmdLineArgs<CmdLineFlags,CmdLineOptions> cla;
    
    @Before
    public void setUp() {        
        cla = new CmdLineArgs<CmdLineFlags,CmdLineOptions>();
        cla.registerOption(CmdLineOptions.SOURCE);
        cla.setOptionDomain(CmdLineOptions.SOURCE,new String[]{"1.5","1.6"});
    }
    
    @Test
    public void testRegisterOptionWithDomainLegalValue()
    throws IllegalArgumentException {
        cla.parse(new String[]{"--source","1.5"});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRegisterOptionWithDomainIllegalValue() {
        cla.parse(new String[]{"--source","0.75"});
    }
}
