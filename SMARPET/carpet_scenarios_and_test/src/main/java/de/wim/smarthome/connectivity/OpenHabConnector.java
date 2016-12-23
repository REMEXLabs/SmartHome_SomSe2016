package de.wim.smarthome.connectivity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class OpenHabConnector {

	public static final String BASE_URL = "http://localhost:8080/";
	public static final String URL_LIGHT_GF_KITCHEN_TABLE = BASE_URL + "CMD?Light_GF_Kitchen_Table=";
	public static final String URL_LIGHT_GF_KITCHEN_CEILING = BASE_URL + "CMD?Light_GF_Kitchen_Ceiling=";

	public void setLight(String baseUrl, LightState lightState) {
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(baseUrl + lightState);
		Response response = target.request().get();
		response.close();
	}
}
