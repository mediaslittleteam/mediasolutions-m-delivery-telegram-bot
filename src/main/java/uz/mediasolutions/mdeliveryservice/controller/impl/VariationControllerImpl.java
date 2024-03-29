package uz.mediasolutions.mdeliveryservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.mdeliveryservice.controller.abs.VariationController;
import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.payload.VariationDTO;
import uz.mediasolutions.mdeliveryservice.payload.VariationResDTO;
import uz.mediasolutions.mdeliveryservice.service.abs.VariationService;

@RestController
@RequiredArgsConstructor
public class VariationControllerImpl implements VariationController {

    private final VariationService variationService;

    @Override
    public ApiResult<Page<VariationResDTO>> getAll(int page, int size, String search) {
        return variationService.getAll(page, size, search);
    }

    @Override
    public ApiResult<VariationResDTO> getById(Long id) {
        return variationService.getById(id);
    }

    @Override
    public ApiResult<?> add(VariationDTO dto) {
        return variationService.add(dto);
    }

    @Override
    public ApiResult<?> edit(Long id, VariationDTO dto) {
        return variationService.edit(id, dto);
    }

    @Override
    public ApiResult<?> delete(Long id) {
        return variationService.delete(id);
    }
}
