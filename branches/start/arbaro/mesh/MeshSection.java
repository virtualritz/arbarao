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

//import java.util.Vector;
import transformation.Vector;

final class Vertex {
    Vector point;
    Vector normal;

    Vertex(Vector pt, Vector norm) {
	point = pt;
	normal = norm;
    }
}

public class MeshSection extends java.util.Vector {
    //A class holding a section of a mesh - 
    // this is a number of points in one layer - several layers build the mesh
      
    public MeshSection previos;
    public MeshSection next;
    /*Stem stem;*/
    
    public MeshSection(/*Stem st,*/ int ptcnt) {
	super(ptcnt);
	/*stem = st;*/
    }

    public void add_point(Vector pt) {
	addElement(new Vertex(pt,null));
    } 

    public Vector point_at(int i) {
	return ((Vertex)elementAt(i)).point;
    }

    public Vector normal_at(int i) {
	return ((Vertex)elementAt(i)).normal;
    }
   
    public Vector here(int i) {
	// returns point n-o i
	return ((Vertex)elementAt(i)).point;
    }
	
    public Vector left(int i) {
	// returns point to the left from point n-o i
	return ((Vertex)elementAt((i-1)%size())).point;
    }

    public Vector right(int i) {
	// returns point to the right from point n-o i
	return ((Vertex)elementAt((i+1)%size())).point;
    }

    public Vector up(int i) {
	// returns the point on top of the point n-o i (from next section)
	// next section has same number of points or only one point
	return ((Vertex)(next.elementAt(i%next.size()))).point;
    }

    public Vector down(int i) {
	// returns the point below the point n-o i (from previous section)
	// next section has same number of points or only one point
	return ((Vertex)(previos.elementAt(i%previos.size()))).point;
    }	  	  

    public Vector normal(Vector a, Vector b, Vector c) {
	// returns the normal of the plane buildt by the vectors a-b and c-b
	// n= (a1*b2 - a2*b1, a2*b0 - a0*b2, a0*b1 - a1*b0)
	Vector u = (a.sub(b)).normalize();
	Vector v = (c.sub(b)).normalize();
	return new Vector(u.getY()*v.getZ() - u.getZ()*v.getY(),
		      u.getZ()*v.getX() - u.getX()*v.getZ(),
		      u.getX()*v.getY() - u.getY()*v.getX()).normalize();
    }
	  	  
    public void set_normals_vector(Vector vec) {
	// set all normals to the vector vec
	for (int i=0; i<size(); i++) {
	    ((Vertex)elementAt(i)).normal=vec;
	}
    }

    public void set_normals_up() {
	// set all normals to the average 
	// of the two left and right upper triangles
	for (int i=0; i<size(); i++) {
	    ((Vertex)elementAt(i)).normal = 
		(normal(up(i),here(i),left(i)).add(
		 normal(right(i),here(i),up(i)))).normalize();
	}
    }

    public void set_normals_down() {
	// set all normals to the average 
	//of the two left and right lower triangles
	for (int i=0; i<size(); i++) {
	    ((Vertex)elementAt(i)).normal = 
		(normal(down(i),here(i),right(i)).add(
		 normal(left(i),here(i),down(i)))).normalize();
	}
    }

    public void set_normals_updown() {
	// set all normals to the average 
	// of the four left and right upper and lower triangles
	for (int i=0; i<size(); i++) {
	    ((Vertex)elementAt(i)).normal = 
		(normal(up(i),here(i),left(i)).add(
		 normal(right(i),here(i),up(i))).add(
		 normal(down(i),here(i),right(i))).add(
		 normal(left(i),here(i),down(i)))).normalize();
	}
    }
		
    public void povray_points(String indent) {
	for (int i=0; i<size(); i++) {
	    System.out.print(((Vertex)elementAt(i)).point.povray());
	    if (next != null || i<size()-1) {
		System.out.print(",");
	    }
	    if (i % 3 == 2) {
		// new line
		System.out.println();
		System.out.print(indent+"          ");
	    } 
	}
    }

    public void povray_normals(String indent) {
	for (int i=0; i<size(); i++) {
	    System.out.print(((Vertex)elementAt(i)).normal.povray());
	    if (next != null|| i<size()-1) {
		System.out.print(",");
	    }
	    if (i % 3 == 2) {
		// new line
		System.out.println();
		System.out.print(indent+"          ");
	    } 
	}
    }
};
	




