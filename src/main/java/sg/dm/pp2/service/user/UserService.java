package sg.dm.pp2.service.user;

public interface UserService {

    void doSignUp(
            String snsAccountUid,
            String kakaoToken,
            Integer platform
    );
}
