package uz.mediasolutions.mdeliveryservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariationDTO {

    private Long id;

    private Long number;

    private String nameUz;

    private String nameRu;

    private Long measureUnitId;

    private float measure;

    private float price;

    private Long productId;

}
