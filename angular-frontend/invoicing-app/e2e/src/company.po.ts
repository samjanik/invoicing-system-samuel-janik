import { browser, by, element } from 'protractor';

export class CompanyPage {
  async navigateTo(): Promise<unknown> {
    return browser.get(browser.baseUrl);
  }

  async taxIdHeaderValue(): Promise<string> {
    return element(by.id('#taxIdHeader')).getText();
  }
}
