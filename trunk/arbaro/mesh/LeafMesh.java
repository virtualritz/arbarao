//  #**************************************************************************
//  #
//  #    $Id$  - LeafMesh class - creates a meshobject for the leaves
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

package net.sourceforge.arbaro.mesh;

import net.sourceforge.arbaro.transformation.*;

import java.util.regex.*;


abstract class LeafShape {
    Vertex[] vertices;
    Face[] faces;

    double length=1;
    double width=1;
    double stemLen=0.5;

    LeafShape(double len, double wid, double stem_len) {
	length = len;
	width = wid;
	stemLen = stem_len;
    }

    void setPoint(int i, double x, double y, double z) {
	Vector point = new Vector(x*width,y*width,(stemLen+z)*length);
	if (vertices[i] == null) {
	    vertices[i] = new Vertex(point,null);
	} else {
	    vertices[i].point = point;
	}
    }


    int getVertexCount() {
	return vertices.length;
    }

    int getFaceCount() {
	return faces.length;
    }
};


class DiscShape extends LeafShape {

    public DiscShape(int facecount, double len, double wid, double stem_len) {
	super(len,wid,stem_len);

	vertices = new Vertex[facecount+2];
	faces = new Face[facecount];

	setCirclePoints();
	setFaces();
    }

    void setCirclePoints() {
	double angle;
	double x;
	double z;
	int cnt = vertices.length;
	// set vertices along a circular curve
	for (int i=0; i<cnt; i++) {
	    angle = i * 2.0 * Math.PI / cnt;
	    x = Math.sin(angle);
	    z = Math.cos(angle);
	    
	    // add a peak to the leaf
	    if (angle < Math.PI) {
		x -= leaffunc(angle);
	    } else if (angle > Math.PI) {
		x += leaffunc(2*Math.PI-angle);
	    }
	    setPoint(i, 0.5*x, 0, 0.5*z + 0.5);
	}
    }

    double leaffunc(double angle) {
	return leaffuncaux(angle)
	    - angle*leaffuncaux(Math.PI)/Math.PI;
    }

    double leaffuncaux(double x) {
	return 0.8 * Math.log(x+1)/Math.log(1.2) - 1.0*Math.sin(x);
    }

    void setFaces() {
	int left = 0;
	int right = vertices.length-1;
	boolean alternate = false;
	// add triangles with an edge on alternating sides
	// of the leaf
	for (int i=0; i<faces.length; i++) {
	    if (i % 2 == 0) {
		faces[i] = new Face(left,left+1,right);
		left++;
	    } else {
		faces[i] = new Face(left,right,right-1);
		right--;
	    }
	}
    }
};

class SphereShape extends LeafShape {
    // use ikosaeder as a "sphere"

    public SphereShape(double len, double wid, double stem_len) {
	super(len,wid,stem_len);

	vertices = new Vertex[12];
	faces = new Face[20];

	double s = (Math.sqrt(5)-1)/2*Math.sqrt(2/(5-Math.sqrt(5))) / 2; 
	// half edge length so, that the vertices are at distance of 0.5 from the center
	double t = Math.sqrt(2/(5-Math.sqrt(5))) / 2;

	setPoint(0, 0,s,-t+0.5);	setPoint(6, 0,-s,-t+0.5);
	setPoint(1, t,0,-s+0.5);	setPoint(7, t,0,s+0.5);
	setPoint(2, -s,t,0+0.5);	setPoint(8, s,t,0+0.5);
	setPoint(3, 0,s,t+0.5);	setPoint(9, 0,-s,t+0.5);
	setPoint(4, -t,0,-s+0.5);	setPoint(10,-t,0,s+0.5);
	setPoint(5,-s,-t,0+0.5);	setPoint(11, s,-t,0+0.5);
	
	faces[0] = new Face(0,1,6); faces[1] = new Face(0,6,4);
	faces[2] = new Face(1,8,7); faces[3] = new Face(1,7,11);
	faces[4] = new Face(2,3,0); faces[5] = new Face(2,3,8);

	faces[6] = new Face(3,9,7);  faces[7] = new Face(3,10,9);
	faces[8] = new Face(4,10,2); faces[9] = new Face(4,5,10);
	faces[10] = new Face(5,6,11);faces[11] = new Face(5,11,9);
	
	faces[12] = new Face(0,8,1); faces[13] = new Face(6,1,11);
	faces[14] = new Face(6,5,4); faces[15] = new Face(0,4,2);

	faces[16] = new Face(7,8,3); faces[17] = new Face(10,3,2);
	faces[18] = new Face(10,5,9);faces[19] = new Face(9,11,7);
    }
};

public class LeafMesh {
    
    LeafShape shape;
    long faceOffset;

    public LeafMesh(String leafShape, double length, double width, double stemLen) {
	Pattern pattern = Pattern.compile("disc(\\d*)");
	Matcher m = pattern.matcher(leafShape);
	// disc shape
	if (m.matches()) {
	    // FIXME: given "disc" without a number, the face count could 
	    // be dependent from the smooth value
	    int facecnt = 6;
	    if (! m.group(1).equals("")) {
		facecnt = Integer.parseInt(m.group(1));
	    }
	    shape = new DiscShape(facecnt,length,width,stemLen);
	} else if (leafShape.equals("sphere")) {
	    shape = new SphereShape(length,width,stemLen);
	} else
// test other shapes like  palm here
	    {
		// any other leaf shape, like "0" a.s.o. - use normal disc

		// FIXME: given "disc" without a number, the face count could 
		// be dependent from the smooth value
		int facecnt = 6;
		shape = new DiscShape(facecnt,length,width,stemLen);
	    }
    }

    public Vertex shapeVertexAt(int i) {
    	return shape.vertices[i];
    }
    
    public Face shapeFaceAt(int i) {
    	return shape.faces[i];
    }
    
//    public void povray_points(PrintWriter w, String indent, Transformation transf) {
//	shape.povray_points(w,indent,transf);
//	faceOffset = 0;
//    }
//
//    public void povray_faces(PrintWriter w, String indent) {
//	shape.povray_faces(w,indent,faceOffset);
//	faceOffset += shape.getVertexCount();
//    }
//
//    public void povray_normals(PrintWriter w, String indent, Transformation transf) {
//	shape.povray_normals(w,indent,transf);
//    }

    public int getShapeVertexCount() {
	return shape.getVertexCount();
    }

    public int getShapeFaceCount() {
	return shape.getFaceCount();
    }

};





