package voiceCare.service;

import voiceCare.model.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 新增用户
     * @param userInfo
     * @return
     */
    int save(Map<String, String> userInfo);     //controller传进来的，先封装Map

    String findByPhoneAndPwd(String phone, String pwd);

    User findByUserId(Integer userId);

    List<User> findListFamily(String familyId);

    int create(Map<String, String> familyIdObj);

//    String findAudioUrlByUserId(Integer userId);
}
