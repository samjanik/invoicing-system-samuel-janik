package pl.futurecollars.invoicing

import com.sun.tools.javac.Main
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest (classes = Main)
class InvoiceApplicationTest extends Specification {

    def "invoice application starts null since not fully setup with Spring Annotations"() {

        expect:
        InvoiceApplication

    }
}
