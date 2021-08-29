package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

public class InvoiceSqlDatabase extends AbstractSqlDatabase implements Database<Invoice> {

    private static final String SELECT_QUERY = "select i.id, i.date, i.number, "
        + "c1.id as seller_id, "
        + "c1.name as seller_name, "
        + "c1.tax_identification_number as seller_tax_id, "
        + "c1.address as seller_address, "
        + "c1.pension_insurance as seller_pension_insurance, "
        + "c1.health_insurance as seller_health_insurance, "
        + "c2.id as buyer_id, "
        + "c2.name as buyer_name, "
        + "c2.tax_identification_number as buyer_tax_id, "
        + "c2.address as buyer_address, "
        + "c2.pension_insurance as buyer_pension_insurance, "
        + "c2.health_insurance as buyer_health_insurance "
        + "from invoice i "
        + "inner join company c1 on i.seller = c1.id "
        + "inner join company c2 on i.buyer = c2.id ";

    public InvoiceSqlDatabase(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private Integer insertCarAndGetId(Car car) {

        if (car == null) {
            return null;
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(
                    "insert into car ("
                        + "registration_number, "
                        + "private_expense) "
                        + "values (?, ?);",
                    new String[] {"id"});
            ps.setString(1,
                car.getRegistrationNumber());
            ps.setBoolean(2,
                car.isPrivateExpense());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    private int insertInvoice(Invoice invoice, long buyerId, long sellerId) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement("insert into invoice ("
                        + "date, "
                        + "number, "
                        + "buyer, "
                        + "seller) "
                        + "values (?, ?, ?, ?);",
                    new String[] {"id"});
            ps.setDate(1, Date.valueOf(invoice.getDate()));
            ps.setString(2, invoice.getNumber());
            ps.setLong(3, buyerId);
            ps.setLong(4, sellerId);
            return ps;
        }, keyHolder);

        int invoiceId = keyHolder.getKey().intValue();
        return invoiceId;
    }

    @Override
    @Transactional
    public long save(Invoice invoice) {

        long buyerId = insertCompany(invoice.getBuyer());
        long sellerId = insertCompany(invoice.getSeller());

        long invoiceId = insertInvoice(invoice, buyerId, sellerId);
        addEntriesRelatedToInvoice(invoiceId, invoice);

        return invoiceId;
    }

    @Override
    public Optional<Invoice> getById(long id) {

        List<Invoice> invoices = jdbcTemplate.query(SELECT_QUERY
                + "where i.id = " + id,
            invoiceRowMapper());

        return invoices.isEmpty() ? Optional.empty() : Optional.of(invoices.get(0));
    }

    @Override
    public List<Invoice> getAll() {

        return jdbcTemplate.query(SELECT_QUERY, invoiceRowMapper());
    }

    private RowMapper<Invoice> invoiceRowMapper() {

        return (rs, rowNr) -> {
            long invoiceId = rs.getLong("id");

            List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
                "select * from invoice_invoice_entry iie "
                    + "inner join invoice_entry e on iie.invoice_entry_id = e.id "
                    + "left outer join car c on e.car_expense = c.id "
                    + "where invoice_id = " + invoiceId,
                (response, ignored) -> InvoiceEntry.builder()
                    .description(response.getString("description"))
                    .quantity(response.getBigDecimal("quantity"))
                    .netPrice(response.getBigDecimal("net_price"))
                    .vatValue(response.getBigDecimal("vat_value"))
                    .vatRate(Vat.valueOf(response.getString("vat_rate")))
                    .carExpense(response.getObject("registration_number") != null
                        ? Car.builder()
                        .registrationNumber(response.getString("registration_number"))
                        .privateExpense(response.getBoolean("private_expense"))
                        .build()
                        : null)
                    .build());

            return Invoice.builder()
                .id(rs.getLong("id"))
                .date(rs.getDate("date").toLocalDate())
                .number(rs.getString("number"))
                .buyer(Company.builder()
                    .id(rs.getLong("buyer_id"))
                    .taxIdentificationNumber(rs.getString("buyer_tax_id"))
                    .name(rs.getString("buyer_name"))
                    .address(rs.getString("buyer_address"))
                    .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
                    .build()
                )
                .seller(Company.builder()
                    .id(rs.getLong("seller_id"))
                    .taxIdentificationNumber(rs.getString("seller_tax_id"))
                    .name(rs.getString("seller_name"))
                    .address(rs.getString("seller_address"))
                    .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
                    .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
                    .build()
                )
                .entries(invoiceEntries)
                .build();
        };
    }

    @Override
    @Transactional
    public Optional<Invoice> update(long id, Invoice updatedInvoice) {
        Optional<Invoice> originalInvoice = getById(id);

        if (originalInvoice.isEmpty()) {
            return originalInvoice;
        }

        updatedInvoice.getBuyer().setId(originalInvoice.get().getBuyer().getId());
        updateCompany(updatedInvoice.getBuyer());

        updatedInvoice.getSeller().setId(originalInvoice.get().getSeller().getId());
        updateCompany(updatedInvoice.getSeller());

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement(
                    "update invoice "
                        + "set date=?, "
                        + "number=? "
                        + "where id=?"
                );
            ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
            ps.setString(2, updatedInvoice.getNumber());
            ps.setLong(3, id);
            return ps;
        });

        deleteEntriesAndCarsRelatedToInvoice(id);
        addEntriesRelatedToInvoice(id, updatedInvoice);

        return Optional.of(updatedInvoice);
    }

    private void addEntriesRelatedToInvoice(long invoiceId, Invoice invoice) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        invoice.getEntries().forEach(entry -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    .prepareStatement(
                        "insert into invoice_entry ("
                            + "description, "
                            + "quantity, "
                            + "net_price, "
                            + "vat_value, "
                            + "vat_rate, "
                            + "car_expense) "
                            + "values (?, ?, ?, ?, ?, ?);",
                        new String[] {"id"});
                ps.setString(1, entry.getDescription());
                ps.setBigDecimal(2, entry.getQuantity());
                ps.setBigDecimal(3, entry.getNetPrice());
                ps.setBigDecimal(4, entry.getVatValue());
                ps.setString(5, entry.getVatRate().name());
                ps.setObject(6, insertCarAndGetId(entry.getCarExpense()));
                return ps;
            }, keyHolder);

            int invoiceEntryId = keyHolder.getKey().intValue();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "insert into invoice_invoice_entry ("
                        + "invoice_id, "
                        + "invoice_entry_id) "
                        + "values (?, ?);");
                ps.setLong(1, invoiceId);
                ps.setLong(2, invoiceEntryId);
                return ps;
            });
        });
    }

    @Override
    @Transactional
    public Optional<Invoice> delete(long id) {

        Optional<Invoice> invoiceOptional = getById(id);
        if (invoiceOptional.isEmpty()) {
            return invoiceOptional;
        }

        Invoice invoice = invoiceOptional.get();

        deleteEntriesAndCarsRelatedToInvoice(id);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from invoice where id = ?;");
            ps.setLong(1, id);
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from company where id in (?, ?);");
            ps.setLong(1, invoice.getBuyer().getId());
            ps.setLong(2, invoice.getSeller().getId());
            return ps;
        });

        return invoiceOptional;
    }

    private void deleteEntriesAndCarsRelatedToInvoice(long id) {

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("delete from car where id in ("
                + "select car_expense from invoice_entry where id in ("
                + "select invoice_entry_id from invoice_invoice_entry where invoice_id=?));");
            ps.setLong(1, id);
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "delete from invoice_entry where id in (select invoice_entry_id from invoice_invoice_entry where invoice_id=?);");
            ps.setLong(1, id);
            return ps;
        });
    }
}
