package sg.dm.pp2.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sg.dm.pp2.service.TokenService;

@Component
public class TokenAuthUtil {
    @Autowired
    TokenService tokenService;

    public Integer checkFullBearerUserTokenAndReturnUserUid(String token) {
        return Integer.getInteger(tokenService.tokenToUserUidStringService(token.substring(7)));
    }

    public Integer checkUserTokenWithoutSubstringAndReturnUserUid(String token) {
        return Integer.getInteger(tokenService.tokenToUserUidStringService(token));
    }
}
