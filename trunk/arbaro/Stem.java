//  #**************************************************************************
//  #
//  #    $Id$  - Stem class - here is most of the logic of 
//  #               the tree generating algorithm
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

#ifndef _STEM_H
#define _STEM_H 1

#include <iostream>
#include <strstream>
#include <exception>
#include <string>
#include "transformation.h"
//#include "mesh.h"
#include "segment.h"
#include "leaf.h"

class ArbaroError: public exception {
  string msg;
public:
  ArbaroError(string errmsg = "ArbaroError"): msg(errmsg) {}
  virtual const char* what () const {
    return msg.data();
  }
};

class ErrorNotYetImplemented: public ArbaroError{
  
};
class ErrorParam: public ArbaroError{
public:
  ErrorParam(string msg):ArbaroError(msg){};
};
//class ErrorTransformation: public exception{};


typedef vector<Segment> Segments;
class Stem;
typedef vector<Stem> Stems;
typedef vector<Leaf> Leaves;
typedef vector<int> Integers;

class Stem {
  // A helper class for making 3d trees, this class makes a stem
  // (trunk or branch)
  Tree &tree;   // the tree
  Stem *parent; // the parent stem
  unsigned level; // the branch level
  double offset; // how far from the parent's base
  
  Segments segments; // the segments forming the stem
  Stems clones;      // the stem clones (for splitting)
  Stems substems;    // the substems
  Leaves leaves;     // the leaves

  double leaves_per_segment;
  double split_corr;
  bool prunetest; // flag for pruning cycles
  unsigned index; // substem number
  Integers clone_index; // clone number

  // FIXME: maybe redesign so, that this isn't necessary
  friend class Tree;
  friend class Leaf;
  friend class Segment;

public:

  // FIXME: this empty constructor shouldn't be necesarry
  //Stem::Stem(const Tree &tr): tree(tr) {}

  Stem::Stem(const Tree &tr, const Stem *par, int lev,
	     Transformation &trf, int offs=0);

  void Stem::DBG(string dbgstr) const;

private:
  string Stem::tree_position() const;
	 
  void Stem::TRF(const string &where,const Transformation &transf) const {
    // print out the transformation to stderr nicely if debugging is enabled
    ostrstream os;
    os << where << ": " << transf << "\n";
    os.freeze();
    DBG(os.str());
  }

  Stem Stem::clone(const Transformation &transf,  int start_segm) const;

  double Stem::var(double variation) const;

  void Stem::make();
  void Stem::pruning();
   
  double Stem::stem_length() const;

  int Stem::make_segments(unsigned start_seg,unsigned end_seg);
	      
  bool Stem::inside_envelope(Vector vector) const;
  
  Transformation Stem::new_direction(Transformation trf, int nsegm) const;

  double Stem::stem_base_radius() const;

  double Stem::stem_radius(double h, double angle=0) const;

  void Stem::prepare_substem_params();
   
  int Stem::leaves_per_branch() const;

  void Stem::make_substems(const Segment &segment);
      
  Transformation Stem::substem_direction(const Transformation &trf, double offset) const;

  void Stem::make_leaves(const Segment &segment);
 
  int Stem::make_clones(Transformation &trf,int nseg);
      
  Transformation Stem::split(Transformation &trf,
			      double s_angle, int nseg, int nsplits);
    
  void Stem::povray(int lev) const;

};

#endif
	      














