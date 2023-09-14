package sg.dm.pp2.configure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("{}====>addCorsMappings", registry);
        registry
                .addMapping("/**") // CORS를 적용할 URL 패턴
                .allowedOriginPatterns("*") // 자원 공유를 허락할 Origin 지정 // 바뀜
                .allowedMethods("*") // 허용할 HTTP Method
                .allowCredentials(true) // TODO: 바뀜
                .allowedHeaders("Accept", "Content-Type", "Origin", //TODO : 바뀜
                        "Authorization", "X-Auth-Token")
                .exposedHeaders("X-Auth-Token", "Authorization")
                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트 캐싱
    }
}
