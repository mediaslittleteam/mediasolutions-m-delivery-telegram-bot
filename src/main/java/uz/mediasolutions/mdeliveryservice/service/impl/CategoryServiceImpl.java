package uz.mediasolutions.mdeliveryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.mdeliveryservice.entity.Category;
import uz.mediasolutions.mdeliveryservice.exceptions.RestException;
import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.mapper.CategoryMapper;
import uz.mediasolutions.mdeliveryservice.payload.CategoryDTO;
import uz.mediasolutions.mdeliveryservice.repository.CategoryRepository;
import uz.mediasolutions.mdeliveryservice.service.abs.CategoryService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public ApiResult<Page<CategoryDTO>> getAll(int page, int size, String name, boolean active) {
        Pageable pageable = PageRequest.of(page, size);
        if (active) {
            if (!name.equals("null")) {
                Page<Category> categories = categoryRepository
                        .findAllByActiveIsTrueAndDescriptionRuContainsIgnoreCaseOrDescriptionUzContainsIgnoreCaseOrNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseOrderByNumberAsc(
                                name, name, name, name, pageable);
                Page<CategoryDTO> dtos = categories.map(categoryMapper::toDTO);
                return ApiResult.success(dtos);
            }
            Page<Category> categories = categoryRepository.findAllByActiveIsTrueOrderByNumberAsc(pageable);
            Page<CategoryDTO> dtos = categories.map(categoryMapper::toDTO);
            return ApiResult.success(dtos);
        }
        if (!name.equals("null")) {
            Page<Category> categories = categoryRepository
                    .findAllByDescriptionRuContainsIgnoreCaseOrDescriptionUzContainsIgnoreCaseOrNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseOrderByNumberAsc(
                    name, name, name, name, pageable);
            Page<CategoryDTO> dtos = categories.map(categoryMapper::toDTO);
            return ApiResult.success(dtos);
        }
        Page<Category> categories = categoryRepository.findAllByOrderByNumberAsc(pageable);
        Page<CategoryDTO> dtos = categories.map(categoryMapper::toDTO);
        return ApiResult.success(dtos);
    }

    @Override
    public ApiResult<CategoryDTO> getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        CategoryDTO dto = categoryMapper.toDTO(category);
        return ApiResult.success(dto);
    }

    @Override
    public ApiResult<?> add(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNumber(categoryDTO.getNumber())) {
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        } else if (categoryRepository.existsByNameUzOrNameRu(
                categoryDTO.getNameUz(), categoryDTO.getNameRu())) {
            throw RestException.restThrow("NAME ALREADY EXISTED", HttpStatus.BAD_REQUEST);
        } else {
            Category entity = categoryMapper.toEntity(categoryDTO);
            categoryRepository.save(entity);
            return ApiResult.success("SAVED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> edit(Long id, CategoryDTO categoryDTO) throws IOException {
        if (categoryRepository.existsByNumber(categoryDTO.getNumber()) &&
        !categoryRepository.existsByNumberAndId(categoryDTO.getNumber(), id)) {
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        } else {
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));

            String imageUrl = category.getImageUrl();
            if (imageUrl != null) {
                if (!Objects.equals(imageUrl, categoryDTO.getImageUrl())) {
                    String imagePath = "delivery-files/" + imageUrl.substring(imageUrl.lastIndexOf('/'));
                    Path path = Paths.get(imagePath);
                    Files.deleteIfExists(path);
                }
            }

            category.setNumber(categoryDTO.getNumber());
            category.setNameUz(categoryDTO.getNameUz());
            category.setNameRu(categoryDTO.getNameRu());
            category.setDescriptionUz(categoryDTO.getDescriptionUz());
            category.setDescriptionRu(categoryDTO.getDescriptionRu());
            category.setActive(categoryDTO.isActive());
            category.setImageUrl(categoryDTO.getImageUrl());
            categoryRepository.save(category);
            return ApiResult.success("EDITED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> delete(Long id) throws IOException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        String imageUrl = category.getImageUrl();
        if (imageUrl != null) {
            String imagePath = "delivery-files/" + imageUrl.substring(imageUrl.lastIndexOf('/'));
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        }
        try {
            categoryRepository.deleteById(id);
            return ApiResult.success("DELETED SUCCESSFULLY");
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
        }
}
