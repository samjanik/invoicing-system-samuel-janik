package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @ApiModelProperty(value = "Invoice id (generated by application)", required = true, example = "1")
    private int id;

    @ApiModelProperty(value = "Date invoice was created", required = true)
    private LocalDate issueDate;

    @ApiModelProperty(value = "Invoice number (assigned by user)", required = true, example = "2020/03/08/0000001")
    private String number;

    @ApiModelProperty(value = "Company that bought the product/service", required = true)
    private Company buyer;

    @ApiModelProperty(value = "Company selling the product/service", required = true)
    private Company seller;

    @ApiModelProperty(value = "List of products/services", required = true)
    private List<InvoiceEntry> entries;

}
