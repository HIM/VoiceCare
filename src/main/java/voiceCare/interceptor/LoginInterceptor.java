package voiceCare.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import voiceCare.utils.JWTUtils;
import voiceCare.utils.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 进入到controller之前的方法（AOP实现，前置通知）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {

            String accesToken = request.getHeader("token");
            if (accesToken == null) {
                accesToken = request.getParameter("token");
            }

            if (StringUtils.isNotBlank(accesToken)) {
                Claims claims = JWTUtils.checkJWT(accesToken);
                if (claims == null) {
                    //告诉登录过期，重新登录
                    sendJsonMessage(response, JsonData.buildError("登录过期，重新登录"));//响应json数据给前端
                    return false;
                }

                Integer id = (Integer) claims.get("id");
                String name = (String) claims.get("name");
                String familyId = (String) claims.get("family_id");
                String headImg = (String) claims.get("head_img");
                String audioUrl = (String) claims.get("audio_url");

                request.setAttribute("user_id", id);
                request.setAttribute("name", name);
                request.setAttribute("family_id",familyId);
                request.setAttribute("head_img",headImg);
                request.setAttribute("audio_url",audioUrl);

                return true;

            }

        }catch (Exception e){}

        sendJsonMessage(response, JsonData.buildError("登录过期，重新登录"));

        return false;
    }


    /**
     * 响应json数据给前端
     * @param response
     * @param obj
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj){

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json; charset=utf-8");//设置响应参数类型
            PrintWriter writer = response.getWriter();
            writer.print(objectMapper.writeValueAsString(obj));     //当做字符串通过writer流写出去
            writer.close();
            response.flushBuffer();//刷新，缓存刷新
        }catch (Exception e){
            e.printStackTrace();
        }


    }




    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
