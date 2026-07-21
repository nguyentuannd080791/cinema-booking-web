package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Category;
import com.springboot.cinema.repository.CategoryRepository;
import com.springboot.cinema.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategoryList() {
        return (List<Category>) categoryRepository.findAll();
    }
}
