//  #**************************************************************************
//  #
//  #    $Id$  
//  #            - IntParams class - it holds integer  parameters 
//  #
//  #    Copyright (C) 2003  Wolfram Diestel
//  #
//  #    This program is free software; you can redistribute it and/or modify
//  #    it under the terms of the GNU General Public License as published by
//  #    the Free Software Foundation; either version 2 of the License, or
//  #    (at your option) any later version.
//  #
//  #    This program is distributed in the hope that it will be useful,
//  #    but WITHOUT ANY WARRANTY; without even the implied warranty of
//  #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  #    GNU General Public License for more details.
//  #
//  #    You should have received a copy of the GNU General Public License
//  #    along with this program; if not, write to the Free Software
//  #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//  #
//  #    Send comments and bug fixes to diestel@steloj.de
//  #
//  #**************************************************************************/

package params;

import params.Param;

class IntParam extends Param {
    private int min;
    private int max;
    private int deflt;
    private int value;

    IntParam(String nam,int mn, int mx, int def, String sh, String lng) {
	super(nam,sh,lng);
	min = mn;
	max = mx;
	deflt = def;
	value = Integer.MIN_VALUE;
    }

    public void setValue(String val) throws ErrorParam {
	Integer i = new Integer(val);
	
	if (i.intValue()<min) {
	    throw new ErrorParam("Value of "+name+" should be greater or equal to "+min);
	}

	if (i.intValue()>max) {
	    throw new ErrorParam("Value of "+name+" should be greater or equal to "+max);
	}
	
	value = i.intValue();
    }

    public int getValue() {
	if (value==Integer.MIN_VALUE) {
	    warn(name+" not given, using default value("+deflt+")");
	    // set value to default, t.e. don't warn again
	    value=deflt;
	}
	return value;
    }
}







