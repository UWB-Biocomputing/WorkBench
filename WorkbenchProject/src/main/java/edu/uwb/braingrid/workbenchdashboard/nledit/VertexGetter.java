package edu.uwb.braingrid.workbenchdashboard.nledit;


import com.google.common.base.Supplier;

public class VertexGetter implements Supplier<Vertex>{
	Vertex vertex;
	public VertexGetter(){}
	public VertexGetter(Vertex vertex) {
		this.vertex=vertex;
	}
	public void set(Vertex vertex) {
		this.vertex=vertex;
	}
	public Vertex get() {
		return vertex;
	}
}
