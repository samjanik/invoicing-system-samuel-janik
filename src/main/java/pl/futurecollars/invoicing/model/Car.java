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
public class Car {

    @ApiModelProperty(value = "Car id (generated by application)", required = true, example = "1")
    private int id;

    @ApiModelProperty(value = "Car registration number", required = true, example = "SL 846259")
    private String registrationNumber;

    @ApiModelProperty(value = "Specifies whether company car is used for personal reasons", required = true, example = "true")
    private boolean privateExpense;
}
