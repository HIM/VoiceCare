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

    List<User> findListFamily(String familyId,int userId);


    void exchangeToneId(int id, Integer userId);

    int findIdByPhone(String phone);

    void uploadHeadImgUrl(int id, String headImgUrl);

    String createFamily(String familyName, Integer user_id);

    int getToneId(int id);

    void setStateOn(Integer id);

    int findFamilyIdExist(String familyId);

    void joinFamily(String familyId, int id);

    String getFamilyId(int id);

    List<User> simply(String familyId, int id);
}
