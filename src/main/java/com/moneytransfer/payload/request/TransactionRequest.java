package com.moneytransfer.payload.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @ApiModelProperty(value = "transactionId", name = "transactionId", dataType = "Long", example = "12324")
    private Long transactionId;
    @ApiModelProperty(value = "playerId", name = "playerId", dataType = "Long", example = "1")
    private Long playerId;
    @ApiModelProperty(value = "amount", name = "amount", dataType = "BigDecimal", example = "10")
    private BigDecimal amount;
}
