import { Component } from '@angular/core';
import { Company } from './model/company';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  title = 'Invoicing Application';

  companies: Company[] = [
      new Company(
          "123-3",
          "krakow",
          "Google",
          123,
          344
      ),
      new Company(
          "444-3",
          "Warsaw",
          "Gogs",
          333,
          444
      )
  ];

  newCompany: Company = new Company("", "", "", 0, 0);

  addCompany() {
      this.companies.push(this.newCompany);
      this.newCompany = new Company("", "", "", 0, 0);
  }

  deleteCompany(companyToDelete: Company) {
      this.companies = this.companies.filter(company => company !== companyToDelete);
  }

  triggerUpdate(company: Company) {
      company.editedCompany = new Company(
          company.taxIdentificationNumber,
          company.address,
          company.name,
          company.healthInsurance,
          company.pensionInsurance
      )
      company.editMode = true
  }

  cancelCompanyUpdate(company: Company) {
      company.editMode = false;
  }

  updateCompany(updatedCompany: Company) {
      updatedCompany.taxIdentificationNumber = updatedCompany.editedCompany.taxIdentificationNumber
      updatedCompany.address = updatedCompany.editedCompany.address
      updatedCompany.name = updatedCompany.editedCompany.name
      updatedCompany.healthInsurance = updatedCompany.editedCompany.healthInsurance
      updatedCompany.pensionInsurance = updatedCompany.editedCompany.pensionInsurance

      updatedCompany.editMode = false;
  }

}
