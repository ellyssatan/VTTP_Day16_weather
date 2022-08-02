package vttp.day16_weather.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp.day16_weather.models.Weather;
import vttp.day16_weather.services.WeatherService;

@Controller
@RequestMapping(path = {"/weather"})
public class WeatherController {

    @Autowired
    private WeatherService weatherSvc;

    @GetMapping
    public String getWeather(@RequestParam String city, Model model) {
        List<Weather> weather = weatherSvc.getWeather(city);
        model.addAttribute("city", city.toUpperCase());
        model.addAttribute("weather", weather);
        return "weather";
    }

    
}
