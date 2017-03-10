package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(1000, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	System.out.println(f.getLocation());
	    	// PointFeatures also have a getLocation method
	    }

	    for(PointFeature pf : earthquakes){
	    	markers.add(createMarker(pf));
	    }
	    map.addMarkers(markers);
	}

	private SimplePointMarker createMarker(PointFeature feature){
		SimplePointMarker spmarker = new SimplePointMarker(feature.getLocation(), feature.getProperties());
    	float magnitude = (float)feature.getProperty("magnitude");
		if(magnitude < 4.0f){
    		spmarker.setColor(color(0, 61, 245));
    		spmarker.setRadius(10);
    	} else if(magnitude >= 4.0f && magnitude < 4.9f){
    		spmarker.setColor(color(255, 255, 0));
    		spmarker.setRadius(20);
    	}else if(magnitude >= 5.0f){
    		spmarker.setColor(color(255, 0, 0));
    		spmarker.setRadius(30);
    	}

		return spmarker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		fill(255,255,255);
		rect(30, 50, 160, 250);
		
		textSize(12);
		fill(0,0,0);
		text("Earthquake Key", 60,70);
		text("5.0+ Magnitude", 85, 120); 
		fill(255, 0, 0);
		ellipse(60, 120, 30, 30);
		
		fill(0,0,0);
		text("4.0+ Magnitude", 85, 160); 
		fill(255, 255, 0);
		ellipse(60, 160, 20, 20);
		
		fill(0,0,0);
		text("Below 4.0", 85, 200); 
		fill(0, 61, 245);
		ellipse(60, 200, 10, 10);
	
	}
}
