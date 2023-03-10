
package com.book.mall.service.impl;

import com.book.mall.api.mall.param.MallUserUpdateParam;
import com.book.mall.common.Constants;
import com.book.mall.common.BookMallException;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.dao.MallUserMapper;
import com.book.mall.dao.BookMallUserTokenMapper;
import com.book.mall.entity.MallUser;
import com.book.mall.entity.MallUserToken;
import com.book.mall.service.BookMallUserService;
import com.book.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.book.mall.api.mall.vo.BookMallUserVO;
import java.util.Date;
import java.util.List;

@Service
public class BookMallUserServiceImpl implements BookMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private BookMallUserTokenMapper bookMallUserTokenMapper;

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public BookMallUserVO login(String loginName, String passwordMD5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        BookMallUserVO loginUser=new BookMallUserVO();
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return loginUser;
            }
            // 登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            MallUserToken mallUserToken = bookMallUserTokenMapper.selectByPrimaryKey(user.getUserId());

            loginUser.setToken(token);
            loginUser.setUserId(user.getUserId());
            loginUser.setNickName(user.getNickName());
            loginUser.setLoginName(user.getLoginName());
            loginUser.setIntroduceSign(user.getIntroduceSign());
            // 当前时间
            Date now = new Date();
            // 过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);// 过期时间 48 小时
            if (mallUserToken == null) {
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                // 新增一条token数据
                if (bookMallUserTokenMapper.insertSelective(mallUserToken) > 0) {
                    // 新增成功后返回
                    return loginUser;
                }
            } else {
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);

                // 更新
                if (bookMallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken) > 0) {
                    // 修改成功后返回
                    return loginUser;
                }
            }

        }
        return loginUser;

    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            BookMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        //user.setPasswordMd5(mallUser.getPasswordMd5());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())){
            user.setPasswordMd5(mallUser.getPasswordMd5());
        }
        user.setIntroduceSign(mallUser.getIntroduceSign());
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        return bookMallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Override
    public PageResult getBookMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
