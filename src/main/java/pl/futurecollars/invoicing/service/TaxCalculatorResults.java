package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaxCalculatorResults {

    private BigDecimal income;
    private BigDecimal costs;
    private BigDecimal earnings;

    private BigDecimal collectedVat;
    private BigDecimal paidVat;
    private BigDecimal dueVat;
}
