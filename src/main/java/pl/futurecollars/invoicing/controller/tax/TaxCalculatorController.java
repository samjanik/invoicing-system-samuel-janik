package pl.futurecollars.invoicing.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.tax.TaxCalculatorResults;
import pl.futurecollars.invoicing.service.tax.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

    private final TaxCalculatorService taxCalculatorService;

    @Override
    public TaxCalculatorResults calculateTaxes(@RequestBody Company company) {
        return taxCalculatorService.calculateTaxes(company);
    }
}
