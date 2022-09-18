package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Building;
import com.southwind.entity.Dormitory;
import com.southwind.entity.Student;
import com.southwind.form.SearchForm;
import com.southwind.mapper.BuildingMapper;
import com.southwind.mapper.DormitoryAdminMapper;
import com.southwind.mapper.DormitoryMapper;
import com.southwind.mapper.StudentMapper;
import com.southwind.service.BuildingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.vo.BuildingVO;
import com.southwind.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2022-06-01
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private DormitoryAdminMapper dormitoryAdminMapper;
    @Autowired
    private DormitoryMapper dormitoryMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public PageVO list(Integer page, Integer size) {
        Page<Building> buildingPage = new Page<>(page, size);
        Page<Building> resultPage = this.buildingMapper.selectPage(buildingPage, null);
        //building转为buildingVO
        List<BuildingVO> buildingVOList = new ArrayList<>();
        for (Building building : resultPage.getRecords()) {
            BuildingVO buildingVO = new BuildingVO();
            BeanUtils.copyProperties(building, buildingVO);
            buildingVO.setAdminName(this.dormitoryAdminMapper.selectById(building.getAdminId()).getName());
            buildingVOList.add(buildingVO);
        }
        PageVO pageVO = new PageVO();
        pageVO.setData(buildingVOList);
        pageVO.setTotal(resultPage.getTotal());
        return pageVO;
    }

    @Override
    public PageVO search(SearchForm searchForm) {
        Page<Building> buildingPage = new Page<>(searchForm.getPage(), searchForm.getSize());
        Page<Building> resultPage = null;
        if(searchForm.getValue().equals("")){
            resultPage = this.buildingMapper.selectPage(buildingPage, null);
        } else {
            QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(searchForm.getKey(), searchForm.getValue());
            resultPage = this.buildingMapper.selectPage(buildingPage, queryWrapper);
        }
        //building转为buildingVO
        List<BuildingVO> buildingVOList = new ArrayList<>();
        for (Building building : resultPage.getRecords()) {
            BuildingVO buildingVO = new BuildingVO();
            BeanUtils.copyProperties(building, buildingVO);
            buildingVO.setAdminName(this.dormitoryAdminMapper.selectById(building.getAdminId()).getName());
            buildingVOList.add(buildingVO);
        }
        PageVO pageVO = new PageVO();
        pageVO.setData(buildingVOList);
        pageVO.setTotal(resultPage.getTotal());
        return pageVO;
    }

    @Override
    public Boolean deleteById(Integer id) {
        //找到楼宇中的所有宿舍
        //找到宿舍中的所有学生
        //给学生换宿舍
        //删除宿舍
        //删除楼宇
        QueryWrapper<Dormitory> dormitoryQueryWrapper = new QueryWrapper<>();
        dormitoryQueryWrapper.eq("building_id", id);
        List<Dormitory> dormitoryList = this.dormitoryMapper.selectList(dormitoryQueryWrapper);
        for (Dormitory dormitory : dormitoryList) {
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("dormitory_id", dormitory.getId());
            List<Student> studentList = this.studentMapper.selectList(studentQueryWrapper);
            for (Student student : studentList) {
                Integer availableDormitoryId = this.dormitoryMapper.findAvailableDormitoryId();
                student.setDormitoryId(availableDormitoryId);
                try {
                    this.studentMapper.updateById(student);
                    this.dormitoryMapper.subAvailable(availableDormitoryId);
                } catch (Exception e) {
                    return false;
                }
            }
            int delete = this.dormitoryMapper.deleteById(dormitory.getId());
            if(delete != 1) return false;
        }
        int delete = this.buildingMapper.deleteById(id);
        if(delete != 1) return false;
        return true;
    }
}
