package com.gps;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PhotoGeolocation {
	
	private static final String GOOGLE_GPS_API_URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private static final String GOOGLE_MAPS_CITY = "administrative_area_level_1";
	private static final String GOOGLE_MAPS_COUNTRY = "country";

	private static final String SHOW_GOOGLE_MAPS_URL = "http://maps.google.com/maps?q=";
	  
	  public static void main(String [] args)	{
		  PhotoGeolocation service = new PhotoGeolocation();
		  File file = service.getFile();
		  if (file == null) {
			  System.out.println("No photo selected");
			  return;
		  }
		  try {
			  String city = service.getCity(file);
			  System.out.println("The photo "+file.getAbsolutePath()+" was taken in "+city);
			  service.showGoogleMap(file);
		  } catch (Exception e) {
			  System.out.println(e);
		  }
		  
	  }
	  
	  private File getFile() {
		  JPanel panel = new JPanel();
		  final JFileChooser fc = new JFileChooser();
		  FileFilter imageFilter = new FileNameExtensionFilter("Image Files", "jpg");
		  fc.addChoosableFileFilter(imageFilter);
		  fc.setFileFilter(imageFilter);
		  int file = fc.showOpenDialog(panel);
		  if (file == JFileChooser.APPROVE_OPTION) {
			  return fc.getSelectedFile();
		  }
		  return null;
	  }
	  
	  private String getCity(File file) throws Exception {
		  double[] gps = getGPSCoordinate(file);
		  JSONObject object = getJSONObject(getAPIUrl(gps));
		  return extractCity(object);
	  }
	  
	  private double[] getGPSCoordinate(File file) throws Exception{
		  javaxt.io.Image image = new javaxt.io.Image(file);
		  double[] gps = image.getGPSCoordinate();
		  if (gps == null) {
			  throw new Exception("Cannot retrieve the GPS coordinate from the file "+file.getName());
		  }
		  return gps;
	  }
	  
	  private String extractCity(JSONObject object) {
		  String country=null, city=null;
		  try {
			JSONArray results = object.getJSONArray("results");
			JSONObject r = results.getJSONObject(0);
			JSONArray addressComponentsArray = r.getJSONArray("address_components");
			for (int i=0;i<addressComponentsArray.length();i++) {
				JSONObject addressComponents = addressComponentsArray.getJSONObject(i);
				 JSONArray typesArray = addressComponents.getJSONArray("types");
				 for (int j=0;j<typesArray.length();j++) {
					 String type = typesArray.getString(j);
					 if(GOOGLE_MAPS_COUNTRY.equals(type)){
						 country = addressComponents.getString("long_name");
					 }else if(GOOGLE_MAPS_CITY.equals(type)){
						 city = addressComponents.getString("long_name");
					 }
				 }
			}
                
               
            if (city != null && country != null) {
            	return city+","+country;
            }
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
		  return null;
	  }
		  
	  private final URL getAPIUrl(double[] gps) {
		  URL url = null;
		  try {
			url = new URL(GOOGLE_GPS_API_URL+gps[1]+","+gps[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		  return url;
	  }
	  
	  private final URL getGoogleMapsUrl(double[] gps) {
		  URL url = null;
		  try {
			url = new URL(SHOW_GOOGLE_MAPS_URL+gps[1]+","+gps[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		  return url;
	  }
	  
	  private void showGoogleMap(File file) {
		try {
			double[] gps = getGPSCoordinate(file);
			String url = getGoogleMapsUrl(gps).toString();
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("xdg-open " + url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	  }
	  
	  private JSONObject getJSONObject(URL url) {
		  JSONObject jsonObject = null;
		  BufferedReader reader = null;
		  String source = null;
		  try {
			  reader = new BufferedReader(new InputStreamReader(url.openStream()));
			  StringBuffer buffer = new StringBuffer();
			  int read;
			  char[] chars = new char[1024];
			  while ((read = reader.read(chars)) != -1)
				  buffer.append(chars, 0, read); 
			  source = buffer.toString();
			  jsonObject = new JSONObject(source);
			  reader.close();
		  } catch (Exception e) {
		  }
		  
		  return jsonObject;
	  }
}
