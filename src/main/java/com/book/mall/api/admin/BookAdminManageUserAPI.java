
package com.book.mall.api.admin;

import io.swagger.annotations.Api;
import com.book.mall.api.admin.param.AdminLoginParam;
import com.book.mall.api.admin.param.UpdateAdminNameParam;
import com.book.mall.api.admin.param.UpdateAdminPasswordParam;
import com.book.mall.common.Constants;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.config.annotation.TokenToAdminUser;
import com.book.mall.entity.AdminUser;
import com.book.mall.entity.AdminUserToken;
import com.book.mall.service.AdminUserService;
import com.book.mall.util.Result;
import com.book.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@Api(value = "v1", tags = "8-0.后台管理系统管理员模块接口")
@RequestMapping("/manage-api/v1")
public class BookAdminManageUserAPI {

    @Resource
    private AdminUserService adminUserService;

    private static final Logger logger = LoggerFactory.getLogger(BookAdminManageUserAPI.class);

    @RequestMapping(value = "/adminUser/login", method = RequestMethod.POST)
    public Result<String> login(@RequestBody @Valid AdminLoginParam adminLoginParam) {
        String loginResult = adminUserService.login(adminLoginParam.getUserName(), adminLoginParam.getPasswordMd5());
        logger.info("manage login api,adminName={},loginResult={}", adminLoginParam.getUserName(), loginResult);

        //登录成功
        if (StringUtils.hasText(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @RequestMapping(value = "/adminUser/profile", method = RequestMethod.GET)
    public Result profile(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        AdminUser adminUserEntity = adminUserService.getUserDetailById(adminUser.getAdminUserId());
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserEntity);
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    @RequestMapping(value = "/adminUser/password", method = RequestMethod.PUT)
    public Result passwordUpdate(@RequestBody @Valid UpdateAdminPasswordParam adminPasswordParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (adminUserService.updatePassword(adminUser.getAdminUserId(), adminPasswordParam.getOriginalPassword(), adminPasswordParam.getNewPassword())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    @RequestMapping(value = "/adminUser/name", method = RequestMethod.PUT)
    public Result nameUpdate(@RequestBody @Valid UpdateAdminNameParam adminNameParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (adminUserService.updateName(adminUser.getAdminUserId(), adminNameParam.getLoginUserName(), adminNameParam.getNickName())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public Result logout(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        adminUserService.logout(adminUser.getAdminUserId());
        return ResultGenerator.genSuccessResult();
    }

}