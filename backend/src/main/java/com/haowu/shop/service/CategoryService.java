package com.haowu.shop.service;

import com.haowu.shop.entity.Category;
import com.haowu.shop.mapper.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByStatus(Integer status) {
        return categoryRepository.findByStatus(status);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    public Category addCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        existingCategory.setName(category.getName());
        if (category.getParentId() != null) {
            existingCategory.setParentId(category.getParentId());
        }
        if (category.getStatus() != null) {
            existingCategory.setStatus(category.getStatus());
        }
        if (category.getSort() != null) {
            existingCategory.setSort(category.getSort());
        }
        if (category.getIcon() != null) {
            existingCategory.setIcon(category.getIcon());
        }
        
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
