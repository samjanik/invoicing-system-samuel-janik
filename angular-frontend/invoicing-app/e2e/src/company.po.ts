import { browser, by, element, ElementArrayFinder, ElementFinder, WebElement } from 'protractor';

export class CompanyPage {
  async navigateTo(): Promise<unknown> {
    return browser.get(browser.baseUrl);
  }

  async taxIdHeaderValue(): Promise<string> {
    return element(by.id('taxIdHeader')).getText();
  }

  async nameHeaderValue(): Promise<string> {
    return element(by.id('nameHeader')).getText();
  }

  async addressHeaderValue(): Promise<string> {
      return element(by.id('addressHeader')).getText();
  }

  async pensionInsuranceHeaderValue(): Promise<string> {
      return element(by.id('pensionInsuranceHeader')).getText();
  }

  async healthInsuranceHeaderValue(): Promise<string> {
      return element(by.id('healthInsuranceHeader')).getText();
  }

  companyRows(): ElementArrayFinder {
    return element.all(by.css('.companyRow'))
}

  anyCompanyRow(): ElementFinder {
    return element(by.css('.companyRow'))
}

  deleteButton(row: ElementFinder): WebElement {
    return row.element(by.css('.btn-danger'))
}

  async addNewCompany(taxId: string, name: string, address: string, pensionInsurance: number, healthInsurance: number) {
    await element(by.css('input[name=taxIdentificationNumber]')).sendKeys(taxId)
    await element(by.css('input[name=name]')).sendKeys(name)
    await element(by.css('input[name=address]')).sendKeys(address)
    await element(by.css('input[name=pensionInsurance]')).sendKeys(pensionInsurance)
    await element(by.css('input[name=healthInsurance]')).sendKeys(healthInsurance)
    await element(by.id("createCompanyButton")).click
  }


}
