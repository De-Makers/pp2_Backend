package sg.dm.pp2.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sg.dm.pp2.service.TokenService;

import java.time.Duration;

@Component
public class TokenAuthUtil {
    @Autowired
    TokenService tokenService;

    public static final long ACCESS_JWT_TOKEN_VALIDITY = Duration.ofHours(5).toMillis();
    public static final long REFRESH_JWT_TOKEN_VALIDITY =  Duration.ofDays(14).toMillis();

    public Integer checkFullBearerUserTokenAndReturnUserUid(String token) {
        return Integer.parseInt(tokenService.tokenToUserUidStringService(token.substring(7)));
    }

    public Integer checkUserTokenWithoutSubstringAndReturnUserUid(String token) {
        return Integer.parseInt(tokenService.tokenToUserUidStringService(token));
    }

}
