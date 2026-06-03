package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interactionapi.dto.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;
    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    private BigDecimal productTotal;
    @Column(nullable = false)
    private BigDecimal deliveryTotal;
    @Column(nullable = false)
    private BigDecimal feeTotal;
    @Column(nullable = false)
    private BigDecimal totalPayment;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentState state;
}
