package uz.mediasolutions.mdeliveryservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TgUser user;

    //USHBU TO'LOVDAN QANCHA TISHGANI
    @Column(nullable = false)
    private Double paySum;

    private Timestamp payDate = new Timestamp(System.currentTimeMillis());

    private Long orderTransactionId;

    private String transactionId;

    private Boolean cancelled = false;


    public Payment(TgUser user, Double paySum, Timestamp payDate, String transactionId) {
        this.user = user;
        this.paySum = paySum;
        this.payDate = payDate;
        this.transactionId = transactionId;
    }


}
