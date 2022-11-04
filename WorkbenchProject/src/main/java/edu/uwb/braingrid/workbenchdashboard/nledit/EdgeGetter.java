package edu.uwb.braingrid.workbenchdashboard.nledit;

import com.google.common.base.Supplier;

public class EdgeGetter implements Supplier<Edge>{
	Edge edge = new Edge();
	public Edge get() {
		return edge;
	}
}
