package pl.futurecollars.invoicing.service.tax;

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

    private BigDecimal pensionInsurance;
    private BigDecimal earningsLessPensionInsurance;
    private BigDecimal earningsLessPensionInsuranceRoundedTaxCalculationBase;

    private BigDecimal incomeTax;
    private BigDecimal healthInsuranceIncurredCost;
    private BigDecimal healthInsuranceDeductible;
    private BigDecimal incomeTaxLessHealthInsurance;
    private BigDecimal finalIncomeTax;


}
