package com.doubeye.core.authorization;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.token.TokenUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * 权限过滤器，尚未启用
 */
@SuppressWarnings("unused")
public class AuthorizationFilter implements Filter {

    private static Logger logger = LogManager.getLogger(AuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, String[]> formDataParameter = RequestHelper.processFromData(servletRequest);
        String expiredDateString = RequestHelper.getString(formDataParameter, "expiredDate");
        String userId = RequestHelper.getString(formDataParameter, "userId");
        String token = RequestHelper.getString(formDataParameter, "token");
        logger.debug("userId=" + userId + " expiredDate= " + expiredDateString + " token=" + token);
        if (StringUtils.isEmpty(expiredDateString)) { //无过期时间
            logger.debug("empty expired");
            //redirect to login
        } else if (DateTimeUtils.getDateFromDefaultFormat(expiredDateString).compareTo(new Date()) > 0) { //已经过期
            //redirect to login
            logger.debug("expired");
        } else {
            String secretKey = "com.doubeye";
            try {
                String generatedToken = TokenUtils.generateToken(userId, expiredDateString, secretKey);
                if (!generatedToken.equalsIgnoreCase(token)) {
                    JSONObject result = ResponseHelper.getSuccessObject();
                    result.put("NOT_LOGIN", true);
                    logger.debug("expired");
                    ResponseHelper.success((HttpServletResponse) servletResponse, result);
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                //redirect to login
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {

    }

}
