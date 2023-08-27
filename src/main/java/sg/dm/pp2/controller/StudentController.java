package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.dm.pp2.service.student.StudentService;
import sg.dm.pp2.service.vo.MyProfileVO;
import sg.dm.pp2.service.vo.ProfileListVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentController {
    @Autowired
    StudentService studentService;

    @Autowired
    TokenAuthUtil tokenAuthUtil;

    @PostMapping("/pp/profile")
    public void postUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("student_id") String studentId,
            @RequestParam("name") String name,
            @RequestParam("message") String message
    ) {
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        studentService.saveFirstProfileForStudentInfo(
                userUid = userUid,
                studentId = studentId,
                name = name,
                message = message
        );
    }
//    @GetMapping("/pp/profile") // TODO: WIP
//    public StudentInfo getUserProfileFromStudentUid(
//            @RequestHeader("Authorization") String token,
//            @PathVariable("student_uid") Integer studentUid
//    ) {
//        return tokenService.tokenToUserUidTestService(token.substring(7));
//    }

    @GetMapping("/pp/profile")
    public MyProfileVO getMyProfile(@RequestHeader ("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return studentService.getMyProfile(userUid);
    }

    @GetMapping("/pp/profiles")
    public List<ProfileListVO> getFamilyProfileList(@RequestHeader ("Authorization") String token){
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        return studentService.getFamilyProfileList(userUid);
    }
}
