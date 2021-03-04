package voiceCare.mapper;

import io.swagger.models.auth.In;
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

    int createFamily(User user);

    void exchangeToneId(@Param("post_id") int id, @Param("user_id") Integer userId);

    int findIdByPhone(@Param("phone") String phone);

    void uploadHeadImgUrl(@Param("id") int id, @Param("img_url") String headImgUrl);
}
