package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
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

}
