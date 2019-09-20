import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class UserInfoService {

    private url = `${environment.tppBackend}`;

    constructor(private http: HttpClient) {}

    public loadUserInfo() {
      return this.http.get(`${this.url}/users/me`);
    }
}
