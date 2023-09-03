package sg.dm.pp2.service.email;

import sg.dm.pp2.controller.dto.EmailAuthCodeCommandDTO;

public interface EmailAuthCodeService {
    void sendMail(EmailAuthCodeCommandDTO emailAuthCodeCommandDTO, int userUid);


    void checkAuthCode(String code, int userUid);
}
