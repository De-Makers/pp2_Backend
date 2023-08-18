package sg.dm.pp2.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sg.dm.pp2.entity.SnsLogin;
import sg.dm.pp2.entity.impl.UserDetailsImpl;
import sg.dm.pp2.repository.SnsLoginRepository;

@Service
@RequiredArgsConstructor
// userDetailsImple에 account를 넣어주는 서비스입니다.
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SnsLoginRepository snsLoginRepository;

    public UserDetailsImpl loadUserByUserUid(Integer userUid) throws UsernameNotFoundException {
        SnsLogin snsLogin = snsLoginRepository.findByUserUid(userUid).orElseThrow(
                () -> new RuntimeException("Not Found Account")
        );

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUserUid(userUid);

        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
