package voiceCare.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import voiceCare.model.entity.User;

import java.util.Date;

/**
 * Jwt工具类
 * 注意点:
 * 1、生成的token, 是可以通过base64进行解密出明文信息
 * 2、base64进行解密出明文信息，修改再进行编码，则会解密失败
 * 3、无法作废已颁布的token，除非改秘钥
 */
public class JWTUtils {


    /**
     * 过期时间，一周
     */
    private  static final long EXPIRE = 60000 * 60 * 24 * 7;
    //private  static final long EXPIRE = 1;


    /**
     * 加密秘钥
     */
    private  static final String SECRET = "voicecare396";


    /**
     * 令牌前缀
     */
    private  static final String TOKEN_PREFIX = "voicecare_";


    /**
     * subject主题，谁颁布的
     */
    private  static final String SUBJECT = "voicecare";


    /**
     * 根据用户信息，生成令牌
     * @param user
     * @return
     */
    public static String geneJsonWebToken(User user){         //生成token
        //builder用于构建令牌token
        String token = Jwts.builder().setSubject(SUBJECT)       //设置主题
                .claim("id",user.getId())               //claim自定义业务信息，key-value形式
                .claim("name",user.getName())
                .claim("family_id",user.getFamilyId())
                .claim("head_img",user.getHeadImg())
                .setIssuedAt(new Date())//设置下发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))//设置过期时间
                .signWith(SignatureAlgorithm.HS256,SECRET).compact();//设置签名，加密方式HS256，密钥SECRET

        token = TOKEN_PREFIX + token;       //拼令牌前缀


        return token;
    }


    /**
     * 校验token的方法，解密token
     * @param token
     * @return
     */
    public static Claims checkJWT(String token){        //通过Claims拿信息

        try{

            final  Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX,"")).getBody();
                                        //parser()解密钥，getBody()返回串
            return claims;

        }catch (Exception e){
            return null;
        }

    }



}
