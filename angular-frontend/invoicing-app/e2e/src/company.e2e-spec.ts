import { CompanyPage } from './company.po';
import { browser, ExpectedConditions, logging } from 'protractor';

describe('Company page E2E test', () => {
  let page: CompanyPage;

  beforeEach(async() => {
    page = new CompanyPage();

    await page.navigateTo();

    await page.companyRows().each(async (row, index) => {
      await page.deleteButton(row).click()
    })

    browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(page.anyCompanyRow())))

    expect(await page.companyRows()).toEqual([])
  });

  it('should display correct values for table headers', async () => {
    await page.navigateTo();
    expect(await page.taxIdHeaderValue()).toEqual('Tax Identification Number');
    expect(await page.nameHeaderValue()).toEqual('Name');
    expect(await page.addressHeaderValue()).toEqual('Address');
    expect(await page.pensionInsuranceHeaderValue()).toEqual('Pension Insurance');
    expect(await page.healthInsuranceHeaderValue()).toEqual('Health Insurance');
  });

  it('can add company', async () => {
    await page.addNewCompany("123", "123 Inc.", "123 Wall Street", 1234, 123)
    page.companyRows().then(rows => expect(rows.length).toEqual(1))
  })


  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
