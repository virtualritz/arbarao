//  #**************************************************************************
//  #
//  #    {$file}  - a Mesh to hold the mesh points, triangles and normals
//  #               and output them to a povray mesh2 object
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
//  #**************************************************************************

package mesh;

import java.lang.Math;
import mesh.MeshSection;

class Face {
    int [] points;
    Face(int i, int j, int k) {
	points = new int[3];
	points[0]=i;
	points[1]=j;
	points[2]=k;
    }
}

public class Mesh extends java.util.Vector {
    final boolean debugmesh = false;


    // A class for creation, handling and output a mesh object
    public Mesh() { }

    public void add_section(MeshSection section) {
	//if (stem.tree.verbose) cerr << (".");
	
	if (size() > 0) {
	    // connect section with last of sections
	    ((MeshSection)lastElement()).next = section;
	    section.previos = (MeshSection)lastElement();
	}
	
	addElement(section);
    }
    
    void set_normals() {
	// normals for begin and end point (first and last section)
	// are set to a z-Vector by Segment.mesh()
	((MeshSection)elementAt(1)).set_normals_up();
	for (int i=2; i<size()-1; i++) {
	    ((MeshSection)elementAt(i)).set_normals_updown();
	}
	// sections.last().set_normals_down();
    }
    
    int vertex_cnt() {
	// count all meshpoints of all sections
	int cnt=0;
	
	for (int i = 0; i<size(); i++) {
	    cnt += ((MeshSection)elementAt(i)).size();
	}
	return cnt;
    }
    
    int face_cnt()  {
	// calcs how much faces have to be created, povray wants to know this
	// before the faces itself
	int cnt = 0;
	for (int i=0; i<size()-1; i++) {
	    int c_i = ((MeshSection)elementAt(i)).size();
	    int c_i1 = ((MeshSection)elementAt(i)).size();
	    
	    if (c_i != c_i1) {
		cnt += Math.max(c_i,c_i1);
	    } else if (c_i > 1) {
		cnt += 2 * c_i;
	    }
	}
	return cnt;
    }
    
    java.util.Vector faces(int inx, MeshSection section) throws Exception {
	//returns the triangles between section sec and next
	MeshSection next = section.next;
	java.util.Vector faces = new java.util.Vector();
	
	if (section.size() == 1) {
	    for (int i=0; i<next.size(); i++) {
		faces.addElement(new Face(inx,inx+1+i,inx+1+(i+1)%next.size()));
	    }
	} else if (next.size() == 1) {
	    int ninx = inx+section.size();
	    for (int i=0; i<section.size(); i++) {
		faces.addElement(new Face(inx+i,inx+(i+1)%section.size(),ninx));
	    }
	} else { // section and next must have same point_cnt>1!!!
	    int ninx = inx+section.size();
	    if (section.size() != next.size()) {
		throw new Exception("Error: vertice numbers of two sections "
				+ "differ ("+inx+","+ninx+")");
	    }
	    for (int i=0; i<section.size(); i++) {
		faces.addElement(new Face(inx+i,inx+(i+1)%section.size(),ninx+i));
		faces.addElement(new Face(inx+(i+1)%section.size(),ninx+i,
				      ninx+(i+1)%next.size()));
	    }
	}
      return faces;
    }
    
    public void povray(boolean output_normals, String indent) throws Exception {
	System.out.println(indent + "mesh2 {");
	// output section points
	System.out.println(indent+"  vertex_vectors { " + vertex_cnt());
	for (int i=0; i<size(); i++) {
	    System.out.print(indent + "  /*" + i + "*/ ");
	    ((MeshSection)elementAt(i)).povray_points(indent);
	    System.out.println();
	}
	System.out.println(indent + "  }");
	
	// output normals
	if (output_normals) {
	    set_normals();
	    System.out.println(indent + "  normal_vectors { " + vertex_cnt()); 
	    
	    for (int i=0; i<size(); i++) { 
		System.out.print(indent + "  /*" + i + "*/");
		((MeshSection)elementAt(i)).povray_normals(indent);
		System.out.println();
	    }
	}
	System.out.println(indent+"  }");
        
	// output mesh triangles
	System.out.println(indent + "  face_indices { " + face_cnt());
	int inx = 0;
	for (int i=0; i<size()-1; i++) { 
	    java.util.Vector faces = faces(inx,(MeshSection)elementAt(i));
	    inx += ((MeshSection)elementAt(i)).size();
	    System.out.print(indent + "  /*" + i + "*/ ");
	    for (int j=0; j<faces.size(); j++) {
		System.out.print("<" + ((Face)faces.elementAt(j)).points[0] + "," 
				 + ((Face)faces.elementAt(j)).points[1] + "," 
				 + ((Face)faces.elementAt(j)).points[2] + ">");
		if ((i<size()-2) || (j<faces.size()-1)) {
		    System.out.print(",");
		}
		if (j % 6 == 4) {
		    // new line
		    System.out.println();
		    System.out.print(indent + "          ");
		}
	    }
	    System.out.println();
	}
	System.out.println(indent + "  }");
	System.out.println(indent + "}");
	
	if (debugmesh) {
	    // draw normals as cones
	    System.out.println("union {");
	    for (int i=0; i<size(); i++) { 
		MeshSection section = ((MeshSection)elementAt(i));
		for (int j=0; i<section.size(); j++) {
		    System.out.println("  cone {" 
				       + section.point_at(j).povray()
				       + ",0.01," + (section.point_at(j).add( 
									     section.normal_at(j)).mul(0.2).povray()) 
				       + ",0}");
		}
	    }
	    System.out.println("}");
	}
    }
}












