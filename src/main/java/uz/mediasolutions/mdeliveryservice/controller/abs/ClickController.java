package uz.mediasolutions.mdeliveryservice.controller.abs;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.mdeliveryservice.payload.ClickInvoiceDTO;
import uz.mediasolutions.mdeliveryservice.payload.ClickOrderDTO;
import uz.mediasolutions.mdeliveryservice.utills.constants.Rest;

@RequestMapping(ClickController.CLICK)
public interface ClickController {

    String CLICK = Rest.BASE_PATH + "click/merchant/";
    String CREATE_INVOICE = "create/invoice";
    String CREATE = "create";
    String PREPARE = "prepare";
    String COMPLETE = "complete";
    String GET_INFO = "get-info";

    @PostMapping(CREATE_INVOICE)
    HttpEntity<?> createInvoice(@RequestBody ClickInvoiceDTO dto,
                                @RequestParam("chatId") String chatId);

    @PostMapping(CREATE)
    HttpEntity<?> create(@RequestParam("amount") Double amount,
                         @RequestParam("chatId") String chatId);

    @RequestMapping(method = RequestMethod.POST, value = PREPARE)
    @ResponseBody
    ClickOrderDTO prepareMethod(@ModelAttribute ClickOrderDTO clickDTO) throws TelegramApiException;

    @RequestMapping(method = RequestMethod.POST, value = COMPLETE)
    @ResponseBody
    ClickOrderDTO completeMethod(@ModelAttribute ClickOrderDTO clickDTO);

    @RequestMapping(method = RequestMethod.POST, value = GET_INFO)
    @ResponseBody
    ClickOrderDTO getInfo(@ModelAttribute ClickOrderDTO clickDTO);
}
