package uz.mediasolutions.mdeliveryservice.service.web.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.mediasolutions.mdeliveryservice.entity.Category;
import uz.mediasolutions.mdeliveryservice.entity.TgUser;
import uz.mediasolutions.mdeliveryservice.enums.LanguageName;
import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.mapper.CategoryMapper;
import uz.mediasolutions.mdeliveryservice.payload.CategoryWebDTO;
import uz.mediasolutions.mdeliveryservice.repository.CategoryRepository;
import uz.mediasolutions.mdeliveryservice.repository.TgUserRepository;
import uz.mediasolutions.mdeliveryservice.service.web.abs.CategoryWebService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryWebServiceImpl implements CategoryWebService {

    private final CategoryRepository categoryRepository;
    private final TgUserRepository tgUserRepository;

    @Override
    public ApiResult<List<CategoryWebDTO>> get(String chatId) {
        List<Category> categories = categoryRepository.findAllByActiveIsTrueOrderByNumberAsc();
        List<CategoryWebDTO> dtoList = toDTOList(categories, chatId);
        return ApiResult.success(dtoList);
    }

    private CategoryWebDTO toWebDTO(Category category, String chatId) {
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        CategoryWebDTO.CategoryWebDTOBuilder builder = CategoryWebDTO.builder();
        builder.id(category.getId());
        builder.imageUrl(category.getImageUrl());
        if (tgUser.getLanguage().getName().equals(LanguageName.UZ)) {
            builder.name(category.getNameUz());
        } else {
            builder.name(category.getNameRu());
        }
        return builder.build();
    }

    private List<CategoryWebDTO> toDTOList(List<Category> categories, String chatId) {
        List<CategoryWebDTO> categoryWebDTOList = new ArrayList<>(categories.size());
        for (Category category : categories) {
            categoryWebDTOList.add(toWebDTO(category, chatId));
        }
        return categoryWebDTOList;
    }
}
