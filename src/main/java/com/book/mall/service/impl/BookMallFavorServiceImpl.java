
package com.book.mall.service.impl;

import com.book.mall.api.mall.vo.BookMallIndexFavorVO;
import com.book.mall.common.ServiceResultEnum;
import com.book.mall.dao.FavorMapper;
import com.book.mall.entity.Favor;
import com.book.mall.entity.BookMallFavors;
import com.book.mall.service.BookMallFavorService;
import com.book.mall.util.BeanUtil;
import com.book.mall.util.PageQueryUtil;
import com.book.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookMallFavorServiceImpl implements BookMallFavorService {

    @Autowired
    private FavorMapper favorMapper;


    @Override
    public PageResult getFavorPage(PageQueryUtil pageUtil) {
        List<Favor> Favors = favorMapper.findFavorList(pageUtil);
        int total = favorMapper.getTotalFavors(pageUtil);
        PageResult pageResult = new PageResult(Favors, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveFavor(Favor favor) {
        if (favorMapper.insertSelective(favor) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
    @Override
    public Boolean deleteByGoodsId(Long goodsId, Long userId){
        Favor favor=new Favor();
        favor.setGoodsId(goodsId);
        favor.setUserId(userId);
        //删除数据
        return favorMapper.deleteByGoodsId(favor) > 0;
    }

    @Override
    public String updateFavor(Favor favor) {
        Favor temp = favorMapper.selectByPrimaryKey(favor.getFavorId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setUserId(favor.getUserId());
        temp.setGoodsId(favor.getGoodsId());
        temp.setUpdateTime(new Date());
        if (favorMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Favor getFavorById(Integer id) {
        return favorMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return favorMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<BookMallIndexFavorVO> getFavorByUserId(Long userId) {
        List<BookMallIndexFavorVO> bookMallIndexFavorVOS = new ArrayList<>();
        List<BookMallFavors> Favors = favorMapper.findFavorsByUser(userId);
        if (!CollectionUtils.isEmpty(Favors)) {
            bookMallIndexFavorVOS = BeanUtil.copyList(Favors, BookMallIndexFavorVO.class);
        }
        return bookMallIndexFavorVOS;
    }
}
