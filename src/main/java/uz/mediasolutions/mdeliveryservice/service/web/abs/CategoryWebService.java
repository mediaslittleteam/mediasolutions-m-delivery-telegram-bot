package uz.mediasolutions.mdeliveryservice.service.web.abs;

import uz.mediasolutions.mdeliveryservice.manual.ApiResult;
import uz.mediasolutions.mdeliveryservice.payload.CategoryWebDTO;

import java.util.List;

public interface CategoryWebService {
    ApiResult<List<CategoryWebDTO>> get(String chatId);
}
