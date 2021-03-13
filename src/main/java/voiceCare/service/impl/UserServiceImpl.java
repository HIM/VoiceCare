package voiceCare.service.impl;

import org.springframework.transaction.annotation.Transactional;
import voiceCare.model.entity.User;
import voiceCare.mapper.UserMapper;
import voiceCare.service.UserService;
import voiceCare.utils.CommonUtils;
import voiceCare.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    //注入userMapper

    @Override
    public int save(Map<String, String> userInfo) {     //通过phone查重，同一手机号不能注册多次

        User user = parseToUser(userInfo);  //把Map转为User，方法实现在下面行
        if( user != null){
           return userMapper.save(user);
        }else {
            return -1;
        }

    }


    @Override
    public String findByPhoneAndPwd(String phone, String pwd) {

        User user = userMapper.findByPhoneAndPwd(phone, CommonUtils.MD5(pwd));

        if(user == null){
            return null;

        }else {
            String token = JWTUtils.geneJsonWebToken(user);
            return token;
        }

    }
    
    @Override
    public User findByUserId(Integer userId) {

        User user = userMapper.findByUserId(userId);
        return user;
    }

    @Override
    public List<User> findListFamily(String familyId, int userId) {

        return userMapper.findListFamily(familyId, userId);
    }

    @Override
    public void exchangeToneId(int id, Integer userId) {
        userMapper.exchangeToneId(id, userId);
    }

    @Override
    public int findIdByPhone(String phone) {
        return userMapper.findIdByPhone(phone);
    }

    @Override
    public void uploadHeadImgUrl(int id, String headImgUrl) {
        userMapper.uploadHeadImgUrl(id, headImgUrl);
    }

    @Override
    public String createFamily(String familyName, Integer user_id) {
        String familyId = testNum();
        userMapper.updateFamily(familyId, user_id);
        userMapper.createFamily(familyId, familyName, user_id);
        return familyId;
    }

    @Override
    public int getToneId(int id) {
        return userMapper.getToneId(id);
    }

    @Override
    public void setStateOn(Integer id) {
        userMapper.setStateOn(id);
    }

    public String testNum(){
        StringBuilder str=new StringBuilder();//定义变长字符串
        Random random=new Random();
        for (int i = 0; i < 5; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 新建的familyId给临时的User
     * @param familyIdObj
     * @return
     */
    private User createFamily(Map<String,String> familyIdObj){
        if(familyIdObj.containsKey("familyId") && familyIdObj.containsKey("phone")){
            User user = new User();
            user.setPhone(familyIdObj.get("phone"));
            user.setFamilyId(familyIdObj.get("familyId"));
            return user;
        }else {
            return null;
        }
    }

    /**
     * 解析 user 对象
     * @param userInfo
     * @return
     */
    private User parseToUser(Map<String,String> userInfo) {

        if(userInfo.containsKey("phone") && userInfo.containsKey("pwd") && userInfo.containsKey("name")){
            User user = new User();
            user.setName(userInfo.get("name"));
            user.setHeadImg(getRandomImg());//得随机头像
            user.setCreateTime(new Date());
            user.setPhone(userInfo.get("phone"));
//            user.setFamilyId(userInfo.get("familyId"));
            String pwd = userInfo.get("pwd");
            //MD5加密
            user.setPwd(CommonUtils.MD5(pwd));//对密码，调用utils包下的MD5加密工具类的方法加密

            return user;
        }else {
            return null;
        }

    }

    /**
     * 放在CDN上的随机头像
     */
    private static final String [] headImg = {
            "https://xd-video-pc-img.oss-cn-beijing.aliyuncs.com/xdclass_pro/default/head_img/12.jpeg",
            "https://xd-video-pc-img.oss-cn-beijing.aliyuncs.com/xdclass_pro/default/head_img/11.jpeg",
            "https://xd-video-pc-img.oss-cn-beijing.aliyuncs.com/xdclass_pro/default/head_img/13.jpeg",
            "https://xd-video-pc-img.oss-cn-beijing.aliyuncs.com/xdclass_pro/default/head_img/14.jpeg",
            "https://xd-video-pc-img.oss-cn-beijing.aliyuncs.com/xdclass_pro/default/head_img/15.jpeg"
    };

    private String getRandomImg(){
        int size =  headImg.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return headImg[index];
    }

}
