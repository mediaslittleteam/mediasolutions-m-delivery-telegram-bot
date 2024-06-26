package uz.mediasolutions.mdeliveryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.mdeliveryservice.entity.MeasureUnit;
import uz.mediasolutions.mdeliveryservice.entity.Product;
import uz.mediasolutions.mdeliveryservice.entity.Variation;
import uz.mediasolutions.mdeliveryservice.exceptions.RestException;
import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.mapper.VariationMapper;
import uz.mediasolutions.mdeliveryservice.payload.VariationDTO;
import uz.mediasolutions.mdeliveryservice.payload.VariationResDTO;
import uz.mediasolutions.mdeliveryservice.repository.MeasureUnitRepository;
import uz.mediasolutions.mdeliveryservice.repository.ProductRepository;
import uz.mediasolutions.mdeliveryservice.repository.VariationRepository;
import uz.mediasolutions.mdeliveryservice.service.abs.VariationService;

@Service
@RequiredArgsConstructor
public class VariationServiceImpl implements VariationService {

    private final VariationRepository variationRepository;
    private final VariationMapper variationMapper;
    private final MeasureUnitRepository measureUnitRepository;
    private final ProductRepository productRepository;

    @Override
    public ApiResult<Page<VariationResDTO>> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (!search.equals("null")) {
            Page<Variation> variations = variationRepository
                    .findAllByProductNameRuContainsIgnoreCaseOrProductNameUzContainsIgnoreCaseOrMeasureUnitNameRuContainsIgnoreCaseOrMeasureUnitNameUzContainsIgnoreCaseOrderByNumberAsc(
                            search, search, search, search, pageable);
            Page<VariationResDTO> map = variations.map(variationMapper::toDTO);
            return ApiResult.success(map);
        } else {
            Page<Variation> variations = variationRepository.findAllByOrderByNumberAsc(pageable);
            Page<VariationResDTO> dtos = variations.map(variationMapper::toDTO);
            return ApiResult.success(dtos);
        }
    }

    @Override
    public ApiResult<VariationResDTO> getById(Long id) {
        Variation variation = variationRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        VariationResDTO dto = variationMapper.toDTO(variation);
        return ApiResult.success(dto);
    }

    @Override
    public ApiResult<?> add(VariationDTO dto) {
        if (variationRepository.existsByNumber(dto.getNumber())) {
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        } else if (variationRepository.existsByNameUzOrNameRu(dto.getNameUz(), dto.getNameRu())) {
            throw RestException.restThrow("NAME ALREADY EXISTED", HttpStatus.BAD_REQUEST);
        } else {
            Variation entity = variationMapper.toEntity(dto);
            variationRepository.save(entity);
            return ApiResult.success("SAVED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> edit(Long id, VariationDTO dto) {
        if (variationRepository.existsByNumber(dto.getNumber()) &&
                !variationRepository.existsByNumberAndId(dto.getNumber(), id)) {
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        } else {
            Variation variation = variationRepository.findById(id).orElseThrow(
                    () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
            MeasureUnit measureUnit = measureUnitRepository.findById(dto.getMeasureUnitId()).orElseThrow(
                    () -> RestException.restThrow("MEASURE UNIT ID NOT FOUND", HttpStatus.BAD_REQUEST));
            Product product = productRepository.findById(dto.getProductId()).orElseThrow(
                    () -> RestException.restThrow("PRODUCT ID NOT FOUND", HttpStatus.BAD_REQUEST));

            variation.setNumber(dto.getNumber());
            variation.setPrice(dto.getPrice());
            variation.setMeasureUnit(measureUnit);
            variation.setMeasure(dto.getMeasure());
            variation.setProduct(product);
            variation.setNameUz(dto.getNameUz());
            variation.setNameRu(dto.getNameRu());
            variationRepository.save(variation);
            return ApiResult.success("EDITED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> delete(Long id) {
        try {
            variationRepository.deleteById(id);
            return ApiResult.success("DELETED SUCCESSFULLY");
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
    }
}
