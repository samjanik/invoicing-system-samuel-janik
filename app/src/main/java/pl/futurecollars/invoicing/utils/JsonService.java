package pl.futurecollars.invoicing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.futurecollars.invoicing.model.Invoice;

public class JsonService {

    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String objectToString(Invoice invoice) {
        try {
            return objectMapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            System.out.println("Serialization from object to Json string failed");
            e.printStackTrace();
        }
        String emptyString = "";
        return emptyString;
    }

    public Invoice stringToObject(String objectAsString) {
        Invoice invoice = new Invoice();
        try {
            return objectMapper.readValue(objectAsString, Invoice.class);
        } catch (JsonProcessingException e) {
            System.out.println("Serialization from Json string to object failed");
            e.printStackTrace();
            return invoice;
        }
    }
}
