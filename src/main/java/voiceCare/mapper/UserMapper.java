package voiceCare.mapper;

import io.swagger.models.auth.In;
import voiceCare.model.entity.Family;
import voiceCare.model.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int save(User user);

    //@Param("phone")  取别名
    User findByPhone(@Param("phone") String phone);

    User findByPhoneAndPwd(@Param("phone") String phone, @Param("pwd") String pwd);

    User findByUserId(@Param("user_id") Integer userId);

    List<User> findListFamily(@Param("family_id") String familyId, @Param("user_id") Integer userId);

    void exchangeToneId(@Param("post_id") int id, @Param("user_id") Integer userId);

    int findIdByPhone(@Param("phone") String phone);

    void uploadHeadImgUrl(@Param("id") int id, @Param("img_url") String headImgUrl);

    int createFamily(@Param("fid") String familyId, @Param("fname") String familyName);

    void updateFamily(@Param("fid") String familyId, @Param("uid") Integer user_id);

    int getToneId(@Param("id") int id);

    void setStateOn(@Param("id") Integer id);

    Family findFamilyExist(@Param("fid") String familyId);

    void joinFamily(@Param("fid") String familyId, @Param("uid") int id);

    String getFamilyId(@Param("uid") int id);

    List<User> simply(@Param("fid") String familyId, @Param("uid") int id);
}
