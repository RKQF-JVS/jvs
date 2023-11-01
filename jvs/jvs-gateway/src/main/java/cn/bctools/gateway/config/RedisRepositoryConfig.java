package cn.bctools.gateway.config;

import cn.bctools.common.constant.SysConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis相关配置
 *
 * @author 
 */
@Configuration
public class RedisRepositoryConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory);
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("gateway:tmp");
        cacheNames.add("default");
        ConcurrentHashMap<String, RedisCacheConfiguration> configMap = new ConcurrentHashMap<>(2);
        //有效期10分钟自定义缓存时间
        configMap.put("tmp", config.entryTtl(Duration.ofMinutes(10L)));
        //永久 key1 的有效期是永久的
        configMap.put("default", config);
        return builder.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
    }

    @Bean
    TokenStore tokenStore(RedisConnectionFactory factory) {
        RedisTokenStore redisTokenStore = new RedisTokenStore(factory);
        redisTokenStore.setPrefix(SysConstant.JVS_AUTH);
        return redisTokenStore;
    }

}
