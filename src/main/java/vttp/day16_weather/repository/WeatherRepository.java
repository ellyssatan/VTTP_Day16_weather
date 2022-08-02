package vttp.day16_weather.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepository {

    @Value("${weather.cache.duration}")
    private long cacheTime;

    @Autowired
    @Qualifier("redislab")
    private RedisTemplate<String, String> redisTemplate;

    public void save(String city, String payload) {
        ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
        valueOp.set(city.toLowerCase(), payload, Duration.ofMinutes(cacheTime));
    }

    public Optional<String> get(String city) {
        ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
        String value = valueOp.get(city.toLowerCase());

        if (value == null) {
            return Optional.empty();    // empty box
        }
        return Optional.of(value);     // empty box

    }
}
