/**
 * 
 */
package net.sourceforge.arbaro.export;

import java.io.PrintWriter;
import java.text.NumberFormat;

import net.sourceforge.arbaro.mesh.LeafMesh;
import net.sourceforge.arbaro.params.FloatFormat;
import net.sourceforge.arbaro.tree.DefaultTreeTraversal;
import net.sourceforge.arbaro.tree.Tree;

/**
 * @author wolfram
 *
 */
public class OBJLeafExporter extends DefaultTreeTraversal {
	Progress progress;
	LeafMesh leafMesh;
	public int leafVertexOffset;
	PrintWriter w;
	long leavesProgressCount=0;
	Tree tree;

	static final NumberFormat fmt = FloatFormat.getInstance();

	/**
	 * 
	 */
	public OBJLeafExporter(PrintWriter pw, LeafMesh leafMesh,
			int leafVertexOffset) {
		super();
		this.w = pw;
		this.leafMesh = leafMesh;
		this.leafVertexOffset = leafVertexOffset;
	}

	public boolean enterTree(Tree tree) {
		progress = tree.getProgress();
		this.tree = tree;
		return true;
	}
	
	/*
	void incLeavesProgressCount() {
		if (leavesProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			if (tree.params.verbose) System.err.print(".");
		}
	}
	
	void writeVector(Vector v) {
		// FIXME: why I cannot get a FloatFormat instance
		// when creating the class?
		// NumberFormat fmt = FloatFormat.getInstance();
		w.print("<"+fmt.format(v.getX())+","
		+fmt.format(v.getZ())+","
		+fmt.format(v.getY())+">");
	}
*/
}
