package com.southwind.service;

import com.southwind.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.form.SearchForm;
import com.southwind.form.StudentForm;
import com.southwind.vo.PageVO;
import com.southwind.vo.StudentVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2022-06-01
 */
public interface StudentService extends IService<Student> {
    public Boolean saveStudent(Student student);
    public PageVO list(Integer page, Integer size);
    public PageVO search(SearchForm searchForm);
    public Boolean update(StudentForm studentForm);
    public Boolean deleteById(Integer id);
}
