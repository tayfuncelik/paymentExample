package com.moneytransfer.payload.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

  @ApiModelProperty(value = "transactionId", name = "transactionId", dataType = "Long", example = "12324")
  private Long transactionId;
  @ApiModelProperty(value = "message", name = "message", dataType = "String", example = "Your message")
  private String message;
}
