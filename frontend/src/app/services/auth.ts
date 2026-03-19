import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string, password: string }): Observable<any> {
    // ✅ Removido o tap — o LoginComponent salva token e role
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  registrar(usuario: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, usuario);
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  estaLogado(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}