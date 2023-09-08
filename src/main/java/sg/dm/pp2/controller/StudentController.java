package sg.dm.pp2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.dm.pp2.controller.dto.PostStudentProfileDTO;
import sg.dm.pp2.service.S3Upload;
import sg.dm.pp2.service.student.StudentService;
import sg.dm.pp2.service.vo.MyProfileVO;
import sg.dm.pp2.service.vo.ProfileListVO;
import sg.dm.pp2.util.TokenAuthUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private TokenAuthUtil tokenAuthUtil;

    @Autowired
    private S3Upload s3Upload;

    @RequestMapping(value = "/pp/profile", method = RequestMethod.POST
            ,consumes = {MediaType.APPLICATION_JSON_VALUE ,MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public void postUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestPart("data") PostStudentProfileDTO postStudentProfileDTO
            ) throws IllegalStateException, IOException{
        Integer userUid = tokenAuthUtil.checkFullBearerUserTokenAndReturnUserUid(token);
        String url = s3Upload.upload(multipartFile);
        studentService.saveFirstProfileForStudentInfo(
                userUid,
                postStudentProfileDTO.getStudentId(),
                postStudentProfileDTO.getName(),
                postStudentProfileDTO.getMessage(),
                url
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

    @GetMapping("/pp/profile/{userUid}")
    public ProfileListVO getSomeoneProfile(@PathVariable(value = "userUid") int userUid){
        return studentService.getSomeoneProfile(userUid);
    }
}
