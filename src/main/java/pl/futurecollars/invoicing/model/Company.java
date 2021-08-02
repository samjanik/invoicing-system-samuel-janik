package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    private int id;

    @ApiModelProperty(value = "Tax identification number", required = true, example = "884-158-84-22")
    private String taxIdentificationNumber;

    @ApiModelProperty(value = "Company address", required = true, example = "ul. Janickiego 123, 41-700 Ruda Śląska")
    private String address;

    @ApiModelProperty(value = "Company name", required = true, example = "Software Development LLC.")
    private String name;

    @Builder.Default
    @ApiModelProperty(value = "Pension insurance amount", required = true, example = "1328.75")
    private BigDecimal pensionInsurance = BigDecimal.ZERO;

    @Builder.Default
    @ApiModelProperty(value = "Health insurance amount", required = true, example = "458.34")
    private BigDecimal healthInsurance = BigDecimal.ZERO;

}
