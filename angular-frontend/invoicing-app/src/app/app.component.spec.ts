import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { AppComponent } from './app.component';
import { Company } from './model/company';
import { CompanyService } from './service/company.service';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: CompanyService, useClass: MockCompanyService }
      ],
      declarations: [
        AppComponent
      ],
      imports: [
        FormsModule
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'Invoicing Application'`, () => {
    const fixture = TestBed.createComponent(AppComponent);

    const app = fixture.componentInstance;
    expect(app.title).toEqual('Invoicing Application');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('div.toolbar').textContent).toContain('Invoicing Application app is running!');
  });

  it('should display list of companies', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit();
    fixture.detectChanges();

    const mainPage = fixture.nativeElement;

    expect(mainPage.textContent).toContain("111WarszawaAgora111333")
    expect(mainPage.textContent).toContain("222krakowDell555666.08")

    expect(app.companies.length).toBe(2)
    expect(app.companies).toBe(MockCompanyService.companies)


  });

  it('newly added company is added to the list', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit();
    fixture.detectChanges();

    const mainPage = fixture.nativeElement;
    
    const createCompanyButton = mainPage.querySelector("#createCompanyButton")
    createCompanyButton.click();
    fixture.detectChanges()
    expect(app.companies.length).toBe(3);

    const taxIdInput: HTMLInputElement = mainPage.querySelector("input[name=taxIdentificationNumber]")
    taxIdInput.value = "333-333-33-33"
    taxIdInput.dispatchEvent(new Event('input'));

    const nameInput: HTMLInputElement = mainPage.querySelector("input[name=name]")
    nameInput.value = "Third Ltd."
    nameInput.dispatchEvent(new Event('input'));

    const addressInput: HTMLInputElement = mainPage.querySelector("input[name=address]")
    addressInput.value = "ul. Third 3"
    addressInput.dispatchEvent(new Event('input'));

    const addInvoiceButton: HTMLElement = mainPage.querySelector("#createCompanyButton")
    addInvoiceButton.click()

    fixture.detectChanges();

    expect(mainPage.innerText).toContain("333-333-33-33	ul. Third 3	Third Ltd.	0	0")
    expect(app.companies.length).toBe(4);

  });

class MockCompanyService {

  static companies: Company[] = [
    new Company(
      1,
      "111",
      "Agora",
      "Warszawa",
      111,
      333
    ),
    new Company(
      2,
      "222",
      "Dell",
      "krakow",
      555,
      666.08
    )
  ];

  getCompanies() {
    return of(MockCompanyService.companies);
  }

  addCompany(company: Company): Observable<any> {
    MockCompanyService.companies.push(company);
    return of();
  }
}
});
