//  #**************************************************************************
//  #
//  #    $Id$  
//  #            - Params class - it holds the tree parameters and 
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

import java.io.PrintWriter;
import params.LevelParams;
import params.IntParam;
import params.FloatParam;
import params.StringParam;
import params.Random;


public class Params {
    
    // Outputformats
    public final static int MESH = 0;
    public final static int CONES = 1;
    public final static int BLOBS = 2;

    // Tree Shapes 
    public final static int CONICAL = 0;
    public final static int SPHERICAL = 1;
    public final static int HEMISPHERICAL = 2;
    public final static int CYLINDRICAL = 3;
    public final static int TAPERED_CYLINDRICAL = 4;
    public final static int FLAME = 5;
    public final static int INVERSE_CONICAL = 6;
    public final static int TEND_FLAME = 7;
    public final static int ENVELOPE = 8;

    public LevelParams [] levelParams;
    java.util.Hashtable paramDB;

    // general Params
    // debugging
    public boolean debug;
    public boolean verbose;
    public boolean ignoreVParams;

    // default param values
    public String species;
      
    public double LeafQuality;
      
    // this mesh parameters are influenced by Smooth, 
    // this are only defaults here
    public double Smooth;
    public double mesh_quality;  // 0..1 - factor for mesh point number 
                          // (1+mesh_quality)
    public int smooth_mesh_level; // -1..Levels - add average normals 
                          // to mesh points of all levels below

      
    public int output; // mesh/cones/blobs

    // the seed
    public int Seed;

    // defauls values for tree params
    public int Levels;

    // trunk&radius parameters
    public double Ratio;
    public double RatioPower;
    public int Shape;
    public double BaseSize;
    public double Flare;
    
    public double Lobes;
    public double LobeDepth;
    
    // leave parameters
    public int Leaves;
    public String LeafShape;
    public double LeafScale;
    public double LeafScaleX;

    // new introduced - not in the paper
    public double LeafStemLen;
    public double LeafBend;
       
    // tree scale
    public double Scale;
    public double ScaleV;
    
    // additional trunk scaling
    public double _0Scale; // only 0SCale used
    public double _0ScaleV; // only 0ScaleV used

    // attraction and pruning/envelope
    public double AttractionUp;
    public double PruneRatio;
    public double PrunePowerLow;
    public double PrunePowerHigh;
    public double PruneWidth;
    public double PruneWidthPeak;
    
    // base splits
    public int _0BaseSplits;
      
    // variables need for stem creation
    public double scale_tree;

    public Params() {

	debug = false;
	verbose = true;
	ignoreVParams = false;
	
	species = "default";
    
	LeafQuality = 1;
      
	Smooth = 0.5;
	output = MESH; // mesh/cones/blobs

	// the default seed
	Seed = 13;

	paramDB = new java.util.Hashtable();

	levelParams = new LevelParams[4];
	for (int l=0; l<4; l++) {
	    levelParams[l] = new LevelParams(l,paramDB);
	}

	register_params();
    };

    // help methods for output of params
    private void xml_param(PrintWriter w, String name, int value) {
	w.println("    <param name='" + name + "'  value='"+value+"'/>");
    }
    
    private void xml_param(PrintWriter w, String name, double value) {
	w.println("    <param name='" + name + "'  value='"+value+"'/>");
    }

    private void xml_param(PrintWriter w, String name, String value) {
	w.println("    <param name='" + name + "'  value='"+value+"'/>");
    }

    void toXML(PrintWriter w) {
	w.println("<?xml version='1.0' ?>");
	w.println();
	w.println("<arbaro>");
	w.println("  <species name='" + species + ">");
	w.println("    <!-- general params -->");
	// FIXME: maybe use paramDB to print out params
	// thus no one yould be forgotten
	xml_param(w,"LeafQuality",LeafQuality);
	xml_param(w,"Smooth",Smooth);
	xml_param(w,"Levels",Levels);
	xml_param(w,"Ratio",Ratio);
	xml_param(w,"RatioPower",RatioPower);
	xml_param(w,"Shape",Shape);
	xml_param(w,"BaseSize",BaseSize);
	xml_param(w,"Flare",Flare);
	xml_param(w,"Lobes",Lobes);
	xml_param(w,"LobeDepth",LobeDepth);
	xml_param(w,"Leaves",Leaves);
	xml_param(w,"LeafShape",LeafShape);
	xml_param(w,"LeafScale",LeafScale);
	xml_param(w,"LeafScaleX",LeafScaleX);
	xml_param(w,"LeafStemLen",LeafStemLen);
	xml_param(w,"LeafBend",LeafBend);
	xml_param(w,"Scale",Scale);
	xml_param(w,"ScaleV",ScaleV);
	xml_param(w,"0Scale",_0Scale); 
	xml_param(w,"0ScaleV",_0ScaleV);
	xml_param(w,"AttractionUp",AttractionUp);
	xml_param(w,"PruneRatio",PruneRatio);
	xml_param(w,"PrunePowerLow",PrunePowerLow);
	xml_param(w,"PrunePowerHigh",PrunePowerHigh);
	xml_param(w,"PruneWidth",PruneWidth);
	xml_param(w,"PruneWidthPeak",PruneWidthPeak);
	xml_param(w,"0BaseSplits",_0BaseSplits);

	for (int i=0; i<Levels; i++) {
	    levelParams[i].toXML(w);
	}
	w.println("  </species>");
	w.println("</arbaro>");
    }


    // help method for loading params
    private int int_param(String name) throws ErrorParam {
	IntParam par = (IntParam)paramDB.get(name);
	if (par != null) {
	    return par.getValue();
	} else {
	    throw new ErrorParam("bug: param "+name+" not found!");
	}
    }

    private double dbl_param(String name) throws ErrorParam {
	FloatParam par = (FloatParam)paramDB.get(name);
	if (par != null) {
	    return par.getValue();
	} else {
	    throw new ErrorParam("bug: param "+name+" not found!");
	}   
    }

    private String str_param(String name) throws ErrorParam {
	StringParam par = (StringParam)paramDB.get(name);
	if (par != null) {
	    return par.getValue();
	} else {
	    throw new ErrorParam("bug: param "+name+" not found!");
	}    
    }
 
    void fromDB() throws ErrorParam {
	LeafQuality = dbl_param("LeafQuality");
	Smooth = dbl_param("Smooth");
	Levels = int_param("Levels");
	Ratio = dbl_param("Ratio");
	RatioPower = dbl_param("RatioPower");
	Shape = int_param("Shape");
	BaseSize = dbl_param("BaseSize");
	Flare = dbl_param("Flare");
	Lobes = int_param("Lobes");
	LobeDepth = dbl_param("LobeDepth");
	Leaves = int_param("Leaves");
	LeafShape = str_param("LeafShape");
	LeafScale = dbl_param("LeafScale");
	LeafScaleX = dbl_param("LeafScaleX");
	LeafStemLen = dbl_param("LeafStemLen");
	LeafBend = dbl_param("LeafBend");
	Scale = dbl_param("Scale");
	ScaleV = dbl_param("ScaleV");
	_0Scale = dbl_param("0Scale"); 
	_0ScaleV = dbl_param("0ScaleV");
	AttractionUp = dbl_param("AttractionUp");
	PruneRatio = dbl_param("PruneRatio");
	PrunePowerLow = dbl_param("PrunePowerLow");
	PrunePowerHigh = dbl_param("PrunePowerHigh");
	PruneWidth = dbl_param("PruneWidth");
	PruneWidthPeak = dbl_param("PruneWidthPeak");
	_0BaseSplits = int_param("0BaseSplits");

	for (int i=0; i<Levels; i++) {
	    levelParams[i].fromDB();
	}
    }

    public void prepare() throws ErrorParam {
	if (debug) { verbose=false; }

	// read in parameter values from ParamDB
	fromDB();
      
	if (ignoreVParams) {
	    ScaleV=0;
	    for (int i=1; i<4; i++) {
		LevelParams lp = levelParams[i];
		lp.nCurveV = 0;
		lp.nLengthV = 0;
		lp.nSplitAngleV = 0;
		lp.nRotateV = 0;
		lp.nBranchDistV = 0;
		if (lp.nDownAngle>0) { lp.nDownAngle=0; }
	    }
	}
	
	// check params
	if (Shape>ENVELOPE || Shape<CONICAL) {
	    throw new ErrorParam("Shape must be in 0..8. It's "+Shape+ " now.");
	}
    
	// FIXME: do this in LevleParams.prepare() ?
	for (int l=0; l < Math.min(Levels,4); l++) {
	    LevelParams lp = levelParams[l];
	    if (lp.nSegSplits>0 && lp.nSplitAngle==0) {
		throw new ErrorParam("nSplitAngle may not be 0.");
	    }
	    if (lp.nCurveV<=-90) {
		throw new ErrorParam("nCurveV must be greater then -90");
	    }
	    if (lp.nCurveRes<=0) {
		throw new ErrorParam("nCurveRes must be greater then 0");
	    }
	    if (lp.nTaper<0) {
		throw new ErrorParam("nTaper must be greater then 0");
	    }
	    if (lp.nTaper>=3) {
		throw new ErrorParam("nTaper must be less then 3");
	    }
	}
	         
	// create one random generator for every level
	// so you can develop a tree level by level without
	// influences between the levels
	levelParams[0].random = new params.Random(Seed);
	for (int i=1; i<4; i++) {
	    levelParams[i].random = new params.Random(levelParams[i-1].random.nextLong());
	}
    
	// mesh settings
	if (Smooth <= 0.2) {
	    smooth_mesh_level = -1;
	} else {
	    smooth_mesh_level = (int)(Levels*Smooth);
	}
	mesh_quality = Smooth;
    
	// mesh points per cross-section for the levels
	// minima
	levelParams[0].mesh_points = 4;
	levelParams[1].mesh_points = 3;
	levelParams[2].mesh_points = 2;
	levelParams[3].mesh_points = 1;
	// set meshpoints with respect to mesh_quality and Lobes
	if (Lobes>0) {
	    levelParams[0].mesh_points = (int)(Lobes*(Math.pow(2,(int)(1+2.5*mesh_quality))));
	    levelParams[0].mesh_points = 
		Math.max(levelParams[0].mesh_points,(int)(4*(1+2*mesh_quality)));
	}
	for (int i=1; i<4; i++) {
	    levelParams[i].mesh_points = 
		Math.max(3,(int)(levelParams[i].mesh_points*(1+1.5*mesh_quality)));
	}

	scale_tree = Scale + levelParams[0].random.uniform(-ScaleV,ScaleV);
    }
    
    public double shape_ratio(double ratio) {
	return shape_ratio(ratio,Shape);
    }

    public double shape_ratio(double ratio, int shape) {

	switch (shape) { 
	case CONICAL: return 0.2+0.8*ratio;
	case SPHERICAL: return 0.2+0.8*Math.sin(Math.PI*ratio);
	case HEMISPHERICAL: return 0.2+0.8*Math.sin(0.5*Math.PI*ratio);
	case CYLINDRICAL: return 1.0;
	case TAPERED_CYLINDRICAL: return 0.5+0.5*ratio;
	case FLAME: 
	    return ratio<=0.7? 
		ratio/0.7 : 
		    (1-ratio)/0.3;
	case INVERSE_CONICAL: return 1-0.8*ratio;
	case TEND_FLAME: 
	    return ratio<=0.7? 
		0.5+0.5*ratio/0.7 :
		    0.5+0.5*(1-ratio)/0.3;
	case ENVELOPE:
	    if (ratio<0 || ratio>1) {
		return 0;
	    } else if (ratio<(1-PruneWidthPeak)) {
		return Math.pow(ratio/(1-PruneWidthPeak),PrunePowerHigh);
	    } else {
		return Math.pow((1-ratio)/(1-PruneWidthPeak),PrunePowerLow);
	    }
	    // tested in prepare() default: throw new ErrorParam("Shape must be between 0 and 8");
	}
	return 0; // shouldn't reach here
    }

    public void setParam(String name, String value) throws ErrorParam {
	Param p = (Param)paramDB.get(name);
	if (p!=null) {
	    p.setValue(value);
	    if (debug) {
		System.err.println("Params.setParam(): set "+name+" to "+value);
	    }

	} else {
	    throw new ErrorParam("Unknown parameter "+name+"!");
	}
    }

    /*
void Tree::setParams(Paramset &paramset) {
    for (Params::const_iterator pi = params.begin();
	 pi != params.end(); pi++) {

      Param *param = pi->second;

      // FIXME: warn and set default value if parameter is not in paramset
      // don't warn for additional parameters not in the original model
      if (! param->has_levels) {
	param->set_value(paramset[param->name]);
      } else {
	for (int l=0; l<4; l++) {
	  param.set_value(paramset[toString(l)+param.name.copy(0,param.name.length()-1)]);
	}
      }
    }
}
    */
    // help methods for createing param-db
    private void int_par(String name, int min, int max, int deflt,
			 String short_desc, String long_desc) {
	paramDB.put(name,new IntParam(name,min,max,deflt,short_desc,long_desc));
    }

    private void int4_par(String name, int min, int max, 
			  int deflt0,int deflt1, int deflt2, int deflt3,
			  String short_desc, String long_desc) {
	int [] deflt = {deflt0,deflt1,deflt2,deflt3};
	for (int i=0; i<4; i++) {
	    name = "" + i + name.substring(1);
	    paramDB.put(name,new IntParam(name,min,max,deflt[i],short_desc,long_desc));
	}
    }

    private void flt_par(String name, double min, double max, double deflt,
			 String short_desc, String long_desc) {
	paramDB.put(name,new FloatParam(name,min,max,deflt,short_desc,long_desc));
    }

    private void flt4_par(String name, double min, double max, 
			  double deflt0, double deflt1, double deflt2, double deflt3,
			  String short_desc, String long_desc) {
	double [] deflt = {deflt0,deflt1,deflt2,deflt3};
	for (int i=0; i<4; i++) {
	    name = "" + i + name.substring(1);
	    paramDB.put(name,new FloatParam(name,min,max,deflt[i],short_desc,long_desc));
	}
    }

    private void str_par(String name, String deflt,
			 String short_desc, String long_desc) {
	paramDB.put(name,new StringParam(name,deflt,short_desc,long_desc));
    }


    private void register_params() {
	int_par ("Shape",0,8,0,"general tree shape id",
		 "The Shape can be one of: \n"+
		 "0 - conical\n"+
		 "1 - spherical\n"+
		 "2 - hemispherical\n"+
		 "3 - cylindrical\n"+
		 "4 - tapered cylindrical\n"+
		 "5 - flame\n"+
		 "6 - inverse conical\n"+
		 "7 - tend flame\n"+
		 "8 - envelope - use pruning envelope\n"+
		 "(see PruneWidth, PruneWidtPeak, PRunePowerLow, PrunePowerHigh)\n");

	flt_par ("BaseSize",0.0,1.0,0.25,"fractional branchless area at tree base",
		 "BaseSize is the fractional branchless part of the trunk. E.g.\n"+
		 "BaseSize=0   means branches begin on the bottom of the tree,\n"+
		 "BaseSize=0.5 means half of the trunk is branchless,\n"+
		 "BaseSize=1.0 branches grow only from the peak of the trunk.\n");

	flt_par("Scale",0.000001,Double.POSITIVE_INFINITY,10.0,"average tree size in meters",
		"Scale gives the average tree size in meters.\n"+
		"Scale = 10.0, ScaleV = 2.0 means, trees of this species\n"+
		"reach from 8.0 to 12.0 meters.\n"+
		"Note, that the trunk length can be different from the tree size.\n"+
		"(See 0Length and 0LengthV)\n");

	flt_par("ScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"variation of tree size in meters",
		"ScaleV gives the variation range of the tree size in meters.\n"+
		"Scale = 10.0, ScaleV = 2.0 means, trees of this species\n"+
		"reach from 8.0 to 12.0 meters.\n"+
		"(See Scale)\n");

	flt_par("ZScale",0.000001,Double.POSITIVE_INFINITY,1.0,"additional Z-scaling (not used)",
		"ZScale and ZScaleV are not described in the Weber/Penn paper.\n"+
		"so there meaning is unclear and they aren't used at the moment\n");

	flt_par("ZScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"additional Z-scaling variation (not used)",
		"ZScale and ZScaleV are not described in the Weber/Penn paper.\n"+
		"so there meaning is unclear and they aren't used at the moment\n");

	int_par("Levels",0,9,3,"levels of recursion",
		"Levels are the levels of recursion when creating the\n"+
		"stems of the tree.\n" +
		"Levels=1 means the tree consist only of the (may be splitting) trunk\n"+
		"Levels=2 the tree consist of the trunk an one level of branches\n"+
		"Levels>4 seldom necesarry, the parameters of the forth level are used\n"+
		"Leaves are considered to be one level over the last stem level.\n");

	flt_par("Ratio",0.000001,Double.POSITIVE_INFINITY,0.05,"trunk radius/length ratio",
		"Ratio gives the radius/length ratio of the trunk.\n"+
		"Ratio=0.05 means the trunk is 1/20 as thick as it is long,\n"+
		"t.e. a 10m long trunk has a base radius of 50cm.\n"+
		"Note, that the real base radius could be greater, when Flare\n"+
		"and/or Lobes are used. (See Flare, Lobes, LobesDepth, RatioPower)\n");

	flt_par("RatioPower",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1.0,"radius reduction",
		"RatioPower gives a reduction value for the radius of the\n"+
		"substems.\n"+
		"RatioPower=1.0  means the radius decreases linearly with\n"+
		"decreasing stem length\n"+
		"RatioPower=2.0  means it decreases with the second power\n"+
		"RatioPower=0.0  means radius is the same as parent radius\n"+
		"(t.e. it doesn't depend of the length)\n"+
		"RatioPower=-1.0 means the shorter the stem the thicker it is\n"+
		"(radius = parent radius * 1 / length)\n"+
		"Note, that the radius of a stem cannot be greater then\n"+
		"the parent radius at the stem offset. So with negative RatioPower\n"+
		"you cannot create stems thicker than it's parent. Instead you\n"+
		"can use it to make stems thinner, which are longer than it's parent.\n"+
		"(See Ratio)\n");
	
	flt_par("Flare",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0.5,"exponential expansion at base of tree",
		"Flare makes the trunk base thicker.\n"+
		"Flare = 0.0 means base radius is used at trunk base\n"+
		"Flare = 1.0 means trunk base is twice as thick as it's base radius\n"+
		"(See Ratio)\n"+
		"Note, that using Lobes make the trunk base thicker too.\n"+
		"(See Lobes, LobeDepth)\n");

	int_par("Lobes",0,Integer.MAX_VALUE,0,"sinusoidal cross-section variation",
		"With Lobes you define how much lobes (this are variations in it's\n"+
		"cross-section) the trunk will have. This isn't supported for \n"+
		"cones output, but for mesh only.\n"+
		"(See LobeDepth too)\n");
	
	flt_par("LobeDepth",0,Double.POSITIVE_INFINITY,0,"amplitude of cross-section variation",
		"LobeDepth defines, how deep the lobes of the trunk will be.\n"+
		"This is the amplitude of the sinusoidal cross-section variations.\n"+
		"(See Lobes)\n");
		
	int_par("Leaves",Integer.MIN_VALUE,Integer.MAX_VALUE,0,"number of leaves per stem",
		"Leaves gives the maximal number of leaves per stem.\n"+
		"Leaves grow only from stems of the last level. The\n"+
		"actual number of leaves on a stem depending on the\n"+
		"stem offset and length, can be smaller than Leaves.\n"+
		"When Leaves is negativ, the leaves grow in a fan at\n"+
		"the end of the stem.\n");
	
	str_par("LeafShape","0","leaf shape id",
		"LeafShape gives the shape of the leaf. \"0\"\n" +
		"means oval shape. The length and width of the \n"+
		"leaf are given by LeafScale and LeafScaleX.\n"+
		"Other possible values of LeafShape references\n"+
		"to the declarations in arbaro.inc. At the moment\n"+
		"there are:\n"+
		"\"disc\" the standard oval form of a leaf, defined\n"+
		"as a unit circle of radius 0.5m. The real\n"+
		"length and with ar given by the LeafScale \n"+
		"parameters.\n"+
		"\"sphere\" a spherical form, you can use this to \n"+
		"simulate seed on herbs or knots on branches\n"+
		"like in the desert bush. You can use the\n"+
		"sphere shape for needles to, because they\n"+
		"are visible from all sides\n"+
		"\"palm\" a palm leaf, this are two disc halfs put together\n"+
		"with an angle between them. So they are visible\n"+
		"also from the side and the light effects are\n"+
		"more typically, especialy for fan palms seen\n"+
		"from small distances.\n");
	
	flt_par("LeafScale",0.000001,Double.POSITIVE_INFINITY,0.2,"leaf length",
		"LeafScale gives the length of the leaf in meters. \n"+
		"The unit leaf is scaled in z-direction (y-direction in Povray)\n"+
		"by this factor. (See LeafShape, LeafScaleX)\n");
	
	flt_par("LeafScaleX",0.000001,Double.POSITIVE_INFINITY,0.5,"fractional leaf width",
		"LeafScaleX gives the fractional width of the leaf\n"+
		"relativly to it's length. So \n"+
		"LeafScaleX=0.5 means the leaf is half as wide as long\n"+
		"LeafScaleX=1.0 means the leaf is a circle\n"+
		"The unit leaf is scaled by LeafScale*LeafScaleX in x- and\n"+
		"y-direction (x- and z-direction in Povray). So the spherical\n"+
		"leaf is transformed to a needle 5cm long and 1mm wide \n"+
		"by LeafScale=0.05 and LeafScaleX=0.02.\n");

	flt_par("LeafBend",0,1,0.3,"leaf orientation toward light",
		"With LeafBend you can influence, how much leaves are oriented\n"+
		"outside and up. Values near 0.5 are good. With low values the leaves\n"+
		"are oriented to the stem with high value to the light.\n"+
		"For trees with bi long leaves like palms you should use lower values.\n");
	
	flt_par("LeafStemLen",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0.5,"fractional leaf stem length",
		"The length of the leaf stem. For normal trees with many nearly circular\n"+
		"leaves the default value of 0.5 (meaning the stem has half of the length\n"+
		"of the leaf) is quite good. For other trees like palms with long leaves\n"+
		"or some herbs you need a LeafStemLen near 0. Negative stem length is "+
		"allowed for special cases.");
		
	flt_par("LeafQuality",0.000001,1.0,1.0,"leaf quality/leaf count reduction",
		"With a LeafQuality less then 1.0 you can reduce the number of leaves\n"+
		"to improve rendering speed and memory usage. The leaves are scaled\n"+
		"with the same amount to get the same coverage.\n"+
		"For trees in the background of the scene you will use a reduces\n"+
		"LeafQuality around 0.9. Very small values would cause strange results.\n"+
		"(See LeafScale)" );

	flt_par("Smooth",0.0,1.0,0.5,"smooth value for mesh creation",
		"Higher Smooth values creates meshes with more vertices and\n"+
		"adds normal vectors to them for some or all branching levels.\n"+
		"normally you would specify this value on the command line, but for\n"+
		"some species a special default smooth value could be best\n"+
		"E.g. for shave-grass a low smooth value is best, because this herb\n"+
		"has angular stems.");
		
	flt_par("AttractionUp",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0.0,"upward/downward growth tendency",
		"AttractionUp gives the tendency of stems with level>=2 to grow\n"+
		"upwards (downwards for negative values). A value of 1.0 means\n"+
		"the last segment should point upward. Greater values means \n"+
		"earlier reaching of upward direction. Values of 10 and greater\n"+
		"could cause overcorrection resulting in a snaking oscillation.\n"+
		"The weeping willow has an AttractionUp value of -3.\n");
	
	flt_par("PruneRatio",0.0,1.0,0.0,"fractional effect of pruning",
		"...\n");

	flt_par("PruneWidth",0.0,1.0,0.5,"width of envelope peak",
		"...\n");

	flt_par("PruneWidthPeak",0.0,1.0,0.5,"position of envelope peak",
		"...\n");

	flt_par("PrunePowerLow",0.0,Double.POSITIVE_INFINITY,0.5,"curvature of envelope",
		"...\n");
	
	flt_par("PrunePowerHigh",0.0,Double.POSITIVE_INFINITY,0.5,"curvature of envelope",
		"...\n");

	flt_par("0Scale",0.000001,Double.POSITIVE_INFINITY,1.0,"extra trunk scaling",
		"0Scale and 0ScaleV makes the trunk thicker. This parameters\n"+
		"exists for the level 0 only. From the Weber/Penn paper it is\n"+
		"not clear, why there are two trunk scaling parameters \n"+
		"0Scale and Ratio. See Ratio, 0ScaleV, Scale, ScaleV.\n"+
		"(Maybe 0Scale should not influence the trunk base radius\n"+
		"but aplied finally to the stem_radius formular. Thus the\n"+
		"trunk radius could be influenced independently from the\n"+
		"Ratio/RatioPower parameters and the periodic tapering\n"+
		"could be scaled, that the sections are elongated spheres)\n");
	
	flt_par("0ScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"variation for extra trunk scaling",
		"0Scale and 0ScaleV makes the trunk thicker. This parameters\n"+
		"exists for the level 0 only. From the Weber/Penn paper it is\n"+
		"not clear, why there are two trunk scaling parameters\n"+
		"0Scale and Ratio. See Ratio, 0ScaleV, Scale, ScaleV.\n");
	
	int_par("0BaseSplits",0,Integer.MAX_VALUE,0,"stem splits at base of trunk",
		"BaseSplit determines how many clones are created at\n"+
		"the top of the first trunk segment. So with BaseSplits=2\n"+
		"you get a trunk splitting into three parts. Other then\n"+
		"with 0SegSplits the clones are evenly distributed over\n"+
		"the 360�. So, if you want to use splitting, you should\n"+
		"use BaseSplits for the first splitting to get a circular\n"+
		"stem distribution (seen from top).\n");
	
	flt4_par("nLength",0.0000001,1.0,1.0,0.5,0.5,0.5,"fractional trunk scaling",
		 "0Length and 0LengthV give the fractional length of the\n"+
		 "trunk. So with Scale=10 and 0Length=0.8 the length of the\n"+
		 "trunk will be 8m. Dont' confuse the height of the tree with\n"+
		 "the length of the trunk here.\n"+
		 "nLength and nLengthV give the fractional length of a stem\n"+
		 "relating to the length of it's parent length\n");
	
	flt4_par("nLengthV",0.0,Double.POSITIVE_INFINITY,0.0,0.0,0.0,0.0,"variation of fractional trunk scaling",
		 "nLengthV gives the variation for nLength.\n");
	
	flt4_par("nTaper",0.0,2.99999999,1.0,1.0,1.0,1.0,"cross-section scaling",
		 "nTaper gives the tapering of the stem along its length.\n"+
		 "0 non-tapering cylinder\n"+
		 "1 taper to a point (cone)\n"+
		 "2 taper to a spherical end\n"+
		 "3 periodic tapering (concatenated spheres)\n"+
		 "You can use also fractional values\n");
	
	flt4_par("nSegSplits",0,Double.POSITIVE_INFINITY,0,0,0,0,"stem splits per segment",
		 "comes later");
	
	flt4_par("nSplitAngle",0,180,0,0,0,0,"splitting angle",
		 "comes later");

	flt4_par("nSplitAngleV",0,180,0,0,0,0,"splitting angle variation",
		 "comes later");
	
	int4_par("nCurveRes",1,Integer.MAX_VALUE,3,3,1,1,"curvature resolution",
		 "comes later");
	
	flt4_par("nCurve",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0,0,0,0,"curving angle","");
	
	flt4_par("nCurveV",0,Double.POSITIVE_INFINITY,0,0,0,0,"curving angle variation","");
	
	flt4_par("nCurveBack",0,Double.POSITIVE_INFINITY,0,0,0,0,"curving angle upper stem half","");

	flt4_par("nDownAngle",0.000001,179.999999,30,30,30,30,"angle from parent","");
	
	flt4_par("nDownAngleV",-179.9999999,179.9999999,0,0,0,0,"down angle variation","");

	flt4_par("nRotate",-360,360,120,120,120,120,"spirangling angle","");

	flt4_par("nRotateV",-360,360,0,0,0,0,"spiraling angle variation","");

	int4_par("nBranches",0,Integer.MAX_VALUE,1,10,5,5,"number of branches","");

	flt4_par("nBranchDist",0,1,1,1,1,1,"branch distribution over the segment","");

	flt4_par("nBranchDistV",0,1,0.5,0.5,0.5,0.5,"branch distribution variation","");

	//	if (debug) {
	//	System.err.println("REGISTERPARAMS\n");
	//  System.err.println("Branchdist? "+((Param)paramDB.get("0BranchDist")).name);
	//}
    }
    /*
    public long trunc(double d) {
	return new Double(d).longValue();
    }
    */
};
























