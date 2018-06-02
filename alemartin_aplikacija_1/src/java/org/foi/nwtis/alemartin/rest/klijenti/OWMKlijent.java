/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.rest.klijenti;

import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author nwtis_1
 */
public class OWMKlijent {

    String apiKey;
    OWMRESTHelper helper;
    Client client;
    private Object obj;

    public OWMKlijent(String apiKey) {
        this.apiKey = apiKey;
        helper = new OWMRESTHelper(apiKey);
        client = ClientBuilder.newClient();
    }

    public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Current_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);
        
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JsonReader reader = Json.createReader(new StringReader(odgovor));

            JsonObject jo = reader.readObject();

            MeteoPodaci mp = new MeteoPodaci();
            //mp.setSunRise(new Date(jo.getJsonObject("sys").getJsonNumber("sunrise").bigDecimalValue().longValue()*1000));
            //mp.setSunSet(new Date(jo.getJsonObject("sys").getJsonNumber("sunset").bigDecimalValue().longValue()*1000));
            
            mp.setTemperatureValue(((jo.getJsonObject("main").getJsonNumber("temp") != null)?
                    new Double(jo.getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue():null));
            mp.setTemperatureMin(((jo.getJsonObject("main").getJsonNumber("temp_min") != null)?
                    new Double(jo.getJsonObject("main").getJsonNumber("temp_min").doubleValue()).floatValue():null));
            mp.setTemperatureMax(((jo.getJsonObject("main").getJsonNumber("temp_max") != null)?
                    new Double(jo.getJsonObject("main").getJsonNumber("temp_max").doubleValue()).floatValue():null));
            mp.setTemperatureUnit("celsius");
            
            mp.setHumidityValue(((jo.getJsonObject("main").getJsonNumber("humidity") != null)?
                    new Double(jo.getJsonObject("main").getJsonNumber("humidity").doubleValue()).floatValue():null));
            mp.setHumidityUnit("%");
            
            mp.setPressureValue(((jo.getJsonObject("main").getJsonNumber("pressure") != null)?
                    new Double(jo.getJsonObject("main").getJsonNumber("pressure").doubleValue()).floatValue():null));
            mp.setPressureUnit("hPa");
            
            mp.setWindSpeedValue(((jo.getJsonObject("wind").getJsonNumber("speed") != null)?
                    new Double(jo.getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue():null));
            mp.setWindSpeedName("");
            
            mp.setWindDirectionValue(((jo.getJsonObject("wind").getJsonNumber("deg") != null)?
                    new Double(jo.getJsonObject("wind").getJsonNumber("deg").doubleValue()).floatValue():null));
            mp.setWindDirectionCode("");
            mp.setWindDirectionName("");
            
            mp.setCloudsValue((jo.getJsonObject("clouds")!=null)?jo.getJsonObject("clouds").getInt("all"):null);
            mp.setCloudsName((jo.getJsonArray("weather").getJsonObject(0).getString("description")!=null)
                    ?jo.getJsonArray("weather").getJsonObject(0).getString("description"):null);
            mp.setPrecipitationMode("");
            
            mp.setWeatherNumber(jo.getJsonArray("weather").getJsonObject(0).getInt("id"));
            mp.setWeatherValue(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            mp.setWeatherIcon(jo.getJsonArray("weather").getJsonObject(0).getString("icon"));
            
            mp.setLastUpdate(new Date(jo.getJsonNumber("dt").bigDecimalValue().longValue()*1000));
            return mp;
            
        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
