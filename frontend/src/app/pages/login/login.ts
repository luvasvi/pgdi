import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; max-width: 300px; margin: 100px auto; border: 1px solid #ccc; border-radius: 8px;">
      <h2 style="text-align: center;">Login PGDI</h2>
      <input [(ngModel)]="username" placeholder="Usuário" style="display:block; margin-bottom:10px; width:100%; padding: 8px;">
      <input [(ngModel)]="password" type="password" placeholder="Senha" style="display:block; margin-bottom:10px; width:100%; padding: 8px;">
      <button (click)="fazerLogin()" style="width:100%; cursor:pointer; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px;">
        Entrar
      </button>
      <p *ngIf="erro" style="color: red; text-align: center; margin-top: 10px;">Usuário ou senha inválidos!</p>
    </div>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  erro = false;

  constructor(private authService: AuthService, private router: Router) {}

  fazerLogin() {
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (res: any) => {
        if (res.token) localStorage.setItem('token', res.token);
        if (res.role) localStorage.setItem('role', res.role);

        // ✅ Aguarda 100ms para garantir que o localStorage foi persistido
        // antes de navegar e disparar requisições autenticadas
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 100);
      },
      error: () => {
        this.erro = true;
      }
    });
  }
}