package pl.futurecollars.invoicing.controller.invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping(value = "invoices", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"invoice-controller"})
public interface InvoiceApi {

    @GetMapping
    @ApiOperation(value = "Get list of all invoices")
    List<Invoice> getAll();

    @PostMapping
    @ApiOperation(value = "Add new invoice to the system")
    int save(@RequestBody Invoice invoice);

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get invoice by id")
    ResponseEntity<Invoice> getById(@PathVariable int id);

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update invoice with given id")
    ResponseEntity<Invoice> updateById(@PathVariable int id, @RequestBody Invoice invoice);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete invoice with given id")
    ResponseEntity<Invoice> deleteById(@PathVariable int id);
}
