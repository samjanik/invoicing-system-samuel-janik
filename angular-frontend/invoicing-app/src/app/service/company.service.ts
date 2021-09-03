import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Company } from "../model/company";

const PATH = 'companies';

@Injectable({
    providedIn: 'root'
})

export class CompanyService {

    private options = {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        withCredentials: true
    };

    constructor(private http: HttpClient) {
    }

    getCompanies(): Observable<Company[]> {
        return this.http.get<Company[]>(this.apiUrl(PATH));
    }

    addCompany(company: Company): Observable<any> {
        return this.http.post<any>(this.apiUrl(PATH), this.toCompanyRequest(company), this.options);
    }

    deleteCompany(id: number): Observable<any> {
        return this.http.delete<any>(this.apiUrl(PATH, id), this.options);
    }

    editCompany(company: Company): Observable<any> {
        return this.http.put<Company>(this.apiUrl(PATH, company.id), this.toCompanyRequest(company), this.options);
    }

    private apiUrl(service: string, id: number | null = null): string {
        const idInUrl = (id !== null ? '/' + id : '');

        return environment.apiUrl + '/' + service + idInUrl;
    }

    private toCompanyRequest(company: Company) {
        return {
            taxIdentificationNumber: company.taxIdentificationNumber,
            name: company.name,
            address: company.address,
            healthInsurance: company.healthInsurance,
            pensionInsurance: company.pensionInsurance,
        };
    }
}
