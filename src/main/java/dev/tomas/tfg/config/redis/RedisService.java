package dev.tomas.tfg.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void guardarEnCache(String clave, String valor) {
        redisTemplate.opsForValue().set(clave, valor);
    }

    public String obtenerDeCache(String clave) {
        return redisTemplate.opsForValue().get(clave);
    }
}