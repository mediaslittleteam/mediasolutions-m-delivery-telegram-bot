package uz.mediasolutions.mdeliveryservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClickInvoiceDTO {

    private Float amount;

    private String phoneNumber;

    private String merchantTransId;

}
