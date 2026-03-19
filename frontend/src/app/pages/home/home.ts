import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px; font-family: sans-serif;">
      <h1>🏠 Home Page - Sistema PGDI</h1>
      <p>Bem-vindo! Escolha uma opção abaixo:</p>
      <hr>

      <div style="margin-bottom: 20px;">
        <h3>Área do Usuário</h3>
        <button (click)="navegar('/upload')" style="margin-right: 10px; cursor: pointer;">📤 Upload de Documentos</button>
        <button (click)="navegar('/filas')" style="cursor: pointer;">📁 Visualizar Filas</button>
      </div>

      <div *ngIf="isAdmin" style="padding: 15px; background: #fdf2f2; border: 2px solid #d9534f; border-radius: 8px;">
        <h3 style="color: #d9534f; margin-top: 0;">🛠️ Painel Administrativo</h3>
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <button (click)="navegar('/cadastro')" style="cursor: pointer;">👤 CRUD Usuários</button>
          <button (click)="navegar('/tipos')" style="cursor: pointer;">📄 CRUD Tipos Documento</button>
          <button (click)="navegar('/crud-filas')" style="cursor: pointer;">📊 CRUD Filas</button>
          <button (click)="navegar('/admin-documentos')" style="cursor: pointer;">📋 Gerenciar Documentos</button>
        </div>
      </div>

      <button (click)="sair()" style="margin-top: 30px; background: #f8f9fa; border: 1px solid #ccc; padding: 10px; cursor: pointer; border-radius: 4px;">
        Sair do Sistema
      </button>
    </div>
  `
})
export class HomeComponent implements OnInit {
  isAdmin = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.verificarAcesso();
  }

  verificarAcesso() {
    const role = localStorage.getItem('role');
    this.isAdmin = role === 'ADMIN' || role === 'ROLE_ADMIN';
  }

  navegar(rota: string) {
    this.router.navigate([rota]);
  }

  sair() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}