package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.db.WithId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Company implements WithId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @ApiModelProperty(value = "Company id (generated by application)", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "Company name", required = true, example = "Software Development LLC.")
    private String name;

    @ApiModelProperty(value = "Company address", required = true, example = "ul. Janickiego 123, 41-700 Ruda Śląska")
    private String address;

    @ApiModelProperty(value = "Tax identification number", required = true, example = "884-158-84-22")
    private String taxIdentificationNumber;

    @Builder.Default
    @ApiModelProperty(value = "Pension insurance amount", required = true, example = "1328.75")
    private BigDecimal pensionInsurance = BigDecimal.ZERO;

    @Builder.Default
    @ApiModelProperty(value = "Health insurance amount", required = true, example = "458.34")
    private BigDecimal healthInsurance = BigDecimal.ZERO;

}
