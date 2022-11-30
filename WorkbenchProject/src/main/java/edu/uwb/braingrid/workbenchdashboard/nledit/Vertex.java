package edu.uwb.braingrid.workbenchdashboard.nledit;

import org.jgrapht.*;

public class Vertex {
	private int id;
	private Double x;
	private Double y;
	private String neuronStatus;
	private boolean endogenouslyActive;
	private boolean probeStatus;
	public Vertex(int id,double x, double y, String neuronStatus ,boolean endogenouslyActive ,boolean probeStatus) {
		this.id=id;
		this.x=x;
		this.y=y;
		this.neuronStatus=neuronStatus;
		this.endogenouslyActive=endogenouslyActive;
		this.probeStatus=probeStatus;
	}
	
	public int getID() {
		return this.id;
	}
	
	public Double getX() {
		return this.x;
	}
	
	public Double getY() {
		return this.y;
	}
	
	public String getNeuronStatus() {
		return neuronStatus;
	}
	
	public boolean getEndogenouslyActive() {
		return endogenouslyActive;
	}
	
	public boolean getProbeStatus() {
		return probeStatus;
	}
	
}
