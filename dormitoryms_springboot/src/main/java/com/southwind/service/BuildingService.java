package com.southwind.service;

import com.southwind.entity.Building;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.form.SearchForm;
import com.southwind.vo.PageVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2022-06-01
 */
public interface BuildingService extends IService<Building> {
    public PageVO list(Integer page,Integer size);
    public PageVO search(SearchForm searchForm);
    public Boolean deleteById(Integer id);
}
