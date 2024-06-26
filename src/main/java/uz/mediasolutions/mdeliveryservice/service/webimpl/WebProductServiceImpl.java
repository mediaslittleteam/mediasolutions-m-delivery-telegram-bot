package uz.mediasolutions.mdeliveryservice.service.webimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.mdeliveryservice.entity.Product;
import uz.mediasolutions.mdeliveryservice.entity.TgUser;
import uz.mediasolutions.mdeliveryservice.entity.Variation;
import uz.mediasolutions.mdeliveryservice.enums.LanguageName;
import uz.mediasolutions.mdeliveryservice.exceptions.RestException;
import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.payload.ProductWebDTO;
import uz.mediasolutions.mdeliveryservice.repository.ProductRepository;
import uz.mediasolutions.mdeliveryservice.repository.TgUserRepository;
import uz.mediasolutions.mdeliveryservice.repository.VariationRepository;
import uz.mediasolutions.mdeliveryservice.service.webabs.WebProductService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebProductServiceImpl implements WebProductService {

    private final ProductRepository productRepository;
    private final TgUserRepository tgUserRepository;
    private final VariationRepository variationRepository;

    @Override
    public ApiResult<List<ProductWebDTO>> getAllByCategoryId(String chatId, Long categoryId) {
        if (tgUserRepository.existsByChatId(chatId)) {
            List<Product> products = productRepository
                    .findAllByCategoryIdAndVariationsIsNotEmptyAndActiveIsTrueAndCategoryActiveIsTrueOrderByNumberAsc(categoryId);
            List<ProductWebDTO> productWebDTOList = toProductWebDTOList(products, chatId);
            return ApiResult.success(productWebDTOList);
        } else {
            throw RestException.restThrow("USER ID NOT FOUND", HttpStatus.BAD_REQUEST);
        }
    }

    private float getLowestPrice(Product product) {
        List<Variation> variations = product.getVariations();
        float lowestPrice = variations.get(0).getPrice();
        for (Variation variation : variations) {
            if (lowestPrice > variation.getPrice())
                lowestPrice = variation.getPrice();
        }
        return lowestPrice;
    }

    private Long getVariationId(Product product) {
        List<Variation> variations = product.getVariations();
        float lowestPrice = variations.get(0).getPrice();
        Long id = variations.get(0).getId();
        for (Variation variation : variations) {
            if (lowestPrice > variation.getPrice()) {
                lowestPrice = variation.getPrice();
                id = variation.getId();
            }
        }
        return id;
    }

    private boolean oneVariation(Product product) {
        return product.getVariations().size() == 1;
    }

    private ProductWebDTO toProductWebDTO(Product product, String chatId) {
        if (product == null) {
            return null;
        }

        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        Variation variation = variationRepository.findById(getVariationId(product)).orElseThrow(
                () -> RestException.restThrow("VARIATION ID NOT FOUND", HttpStatus.BAD_REQUEST));

        ProductWebDTO.ProductWebDTOBuilder builder = ProductWebDTO.builder();
        builder.id(product.getId());
        builder.price(getLowestPrice(product));
        builder.variationId(getVariationId(product));
        builder.imageUrl(product.getImageUrl());
        builder.oneVariation(oneVariation(product));
        if (tgUser.getLanguage().getName().equals(LanguageName.UZ)) {
            builder.name(product.getNameUz());
            builder.variationName(variation.getNameUz());
        } else {
            builder.name(product.getNameRu());
            builder.variationName(variation.getNameRu());
        }
        return builder.build();
    }

    private List<ProductWebDTO> toProductWebDTOList(List<Product> products, String chatId) {
        if (products == null) {
            return null;
        }

        List<ProductWebDTO> productWebDTOS = new ArrayList<>();
        for (Product product : products) {
            productWebDTOS.add(toProductWebDTO(product, chatId));
        }
        return productWebDTOS;
    }

}
