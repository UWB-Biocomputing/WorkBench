package edu.uwb.braingrid.workbenchdashboard.nledit;

import org.jgrapht.*;

public class Vertex {
	
	enum Status{
		EXCITATORY,
		INHIBITORY
	}
	private int id;
	private Double x;
	private Double y;
	private Status neuronStatus;
	private boolean endogenouslyActive;
	private boolean probeStatus;
	public Vertex(Double x, Double y, Status neuronStatus ,Boolean endogenouslyActive ,Boolean probeStatus) {
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
		return this.x;
	}
	
	public Status getNeuronStatus() {
		return neuronStatus;
	}
	
	public boolean getEndogenouslyActive() {
		return endogenouslyActive;
	}
	
	public boolean getProbeStatus() {
		return probeStatus;
	}
	
}
