package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceEntry {

    @ApiModelProperty(value = "Product/service description", required = true, example = "Apple iPhone 8")
    private String description;

    @ApiModelProperty(value = "Number of items", required = true, example = "3")
    private int quantity;

    @ApiModelProperty(value = "Product/service net price", required = true, example = "1857.15")
    private BigDecimal netPrice;

    @ApiModelProperty(value = "Car related expense, empty if expense is not related to car")
    private Car carExpense;

    @ApiModelProperty(value = "Product/service tax value", required = true, example = "187.45")
    @Builder.Default
    private BigDecimal vatValue = BigDecimal.ZERO;

    @ApiModelProperty(value = "Tax rate", required = true)
    private Vat vatRate;
}
