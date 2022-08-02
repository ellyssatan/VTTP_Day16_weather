package vttp.day16_weather.services;

import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.day16_weather.models.Weather;
import vttp.day16_weather.repository.WeatherRepository;

@Service
public class WeatherService {

    private static final String URL = "https://api.openweathermap.org/data/2.5/weather";

    @Value("${OWM_KEY}")
    private String key;

    @Autowired
    private WeatherRepository weatherRepo;
    
    public List<Weather> getWeather (String city) {

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        
        // Check if we have the weather cache
        Optional<String> opt = weatherRepo.get(encodedCity);
        String payload;

        if (opt.isEmpty()) {

            System.out.println("Getting weather report from OpenWeatherMap....");


            // Create url with query string (add parameters)
            String uri = UriComponentsBuilder.fromUriString(URL)
            .queryParam("q", encodedCity)
            .queryParam("appid", key)
            .toUriString();
        
            // Create the GET request, GET url
            RequestEntity<Void> req = RequestEntity.get(uri).build();

            // Make the call to OpenWeatherApp
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp;

            try {
                // Throws exception if status code is not between 200 - 399
                resp = template.exchange(req, String.class);
            } catch (Exception e) {
                System.err.printf("Error: " + e);
                return Collections.emptyList();

            }

            // Check status code
            if (resp.getStatusCodeValue() != 200) {
                System.err.println("Error status code is not 200");
                return Collections.emptyList();
            }

            // Get payload 
            payload = resp.getBody();
            System.out.println("Payload: " + payload);

            weatherRepo.save(encodedCity, payload);
            System.out.println("Saved payload of city: " + encodedCity);

        } else {
            // Retrieve the value of the box
            payload = opt.get();
            System.out.printf(">>> cache: %s\n", payload);

        }
        
        // Convert payload intoJsonObject
        // Convert string to a Reader
        Reader strReader = new StringReader(payload);

        // Create a JsonReader from reader
        JsonReader jsonReader = Json.createReader(strReader);

        // Read the payload as Json Object
        JsonObject weatherResult = jsonReader.readObject();

        JsonArray cities = weatherResult.getJsonArray("weather");

        List<Weather> list = new LinkedList<>();
        for (int i = 0; i < cities.size(); i++) {
            // weather[0]
            JsonObject jo = cities.getJsonObject(i);
            list.add(Weather.create(jo));
        }
        return list;
    }
}
