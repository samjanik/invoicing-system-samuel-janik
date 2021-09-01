import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { AppComponent } from './app.component';
import { Company } from './model/company';
import { CompanyService } from './service/company.service';

describe('AppComponent', () => {

  let fixture: ComponentFixture<AppComponent>;
  let app: AppComponent;

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

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.componentInstance;
        
    app.ngOnInit()
    fixture.detectChanges();

  });

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it(`should have as title 'Invoicing Application'`, () => {
    expect(app.title).toEqual('Invoicing Application');
  });

  it('should render title', () => {
    const mainPage = fixture.nativeElement;
    expect(mainPage.querySelector('div.toolbar').textContent).toContain('Invoicing Application app is running!');
  });

  it('should display list of companies', () => {

    const mainPage = fixture.nativeElement;

    expect(mainPage.textContent).toContain("111AgoraWarszawa111333")
    expect(mainPage.textContent).toContain("222DellKraków555666.08")

    expect(app.companies.length).toBe(2)
    expect(app.companies).toBe(MockCompanyService.companies)

  });

  it('newly added company is added to the list', () => {

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

    expect(mainPage.innerText).toContain("333-333-33-33	Third Ltd.	ul. Third 3	0	0")
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
      "Kraków",
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
