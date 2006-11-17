//  #**************************************************************************
//  #
//  #    Copyright (C) 2003-2006  Wolfram Diestel
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

package net.sourceforge.arbaro.export;


import net.sourceforge.arbaro.meshfactory.*;
import net.sourceforge.arbaro.export.*;

/**
 * @author wolfram
 *
 */
abstract class MeshExporter extends AbstractExporter {
	protected MeshFactory meshFactory;
	/**
	 * 
	 */
	public MeshExporter(MeshFactory meshFactory) {
		this.meshFactory = meshFactory;
	}
	
	/*
	protected void incStemsProgressCount() {
		if (stemsProgressCount++ % 100 == 0) {
			progress.incProgress(100);
			progress.consoleProgress();
		}
	}
	
	protected void incFaceProgressCount() {
		if (faceProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			progress.consoleProgress();
		}
	}

	protected void incLeavesProgressCount() {
		if (leavesProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			progress.consoleProgress();
		}
	}
	
	protected void incVertexProgressCount() {
		if (leavesProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			progress.consoleProgress();
		}
	}
	*/
}
