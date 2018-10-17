package edu.uwb.braingrid.workbenchdashboard.welcome;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import com.sun.javafx.iio.ImageLoader;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.jena.ext.com.google.common.io.Resources;

public class Welcome extends WorkbenchApp {
	private static final Logger LOG = Logger.getLogger(Welcome.class.getName());
	
	private HBox display_ = new HBox();
	
	
	public Welcome(Tab tab) {
		
		super(tab);
		LOG.info("new " + getClass().getName());

		System.out.println("No soup!");
		URL is = Resources.getResource("braingrid/color-logo.png");
		Image image = new Image(is.getFile());
		ImageView iv = new ImageView(image);
		
		display_.getChildren().addAll(iv);
		display_.setAlignment(Pos.CENTER);
		super.setTitle("Welcome!");
		
	}

	
	@Override
	public boolean close() {
		return true;
	}

	@Override
	public Node getDisplay() {
		// TODO Auto-generated method stub
		return display_;
	}
}
