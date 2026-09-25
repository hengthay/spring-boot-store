package com.hengthay.store.products;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public Iterable<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Byte id) {
        var category = categoryRepository.findById(id).orElseThrow();

        if(category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Byte id, @RequestBody Category categoryDetails) {
        var category = categoryRepository.findById(id);

        if(category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category existingCategory = category.get();

        existingCategory.setName(categoryDetails.getName());

        Category updatedCategory = categoryRepository.save(existingCategory);

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Byte id) {
        var category = categoryRepository.findById(id);

        if(category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        categoryRepository.deleteById(id);

        return ResponseEntity.ok("Category deleted successfully!");
    }
}
