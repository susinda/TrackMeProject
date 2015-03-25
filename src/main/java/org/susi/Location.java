package org.susi;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

@WebService(serviceName = "Location")
public class Location{

	String lat;
	String lon;

	@WebMethod(operationName = "update")
	public String update(@WebParam(name = "id") String id, @WebParam(name = "lat") String lat, @WebParam(name = "lon") String lon) {
		this.lat = lat;
		this.lon = lon;
		return "Updated lon " +  lon + " lat " + lat ;
	}
	
	@WebMethod(operationName = "view")
	public String view(@WebParam(name = "id") String id) {
		return "("+ lat + "," + lon +")";
	}
}