import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; font-family: sans-serif;">
      <button (click)="voltar()" style="margin-bottom: 15px; cursor: pointer;">← Voltar</button>
      <h2>👥 Gestão de Usuários (Admin)</h2>

      <div style="background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd;">
        <h3>Novo Usuário</h3>
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <input [(ngModel)]="novoUsuario.username" placeholder="Usuário" style="padding: 8px;">
          <input [(ngModel)]="novoUsuario.email" placeholder="E-mail" style="padding: 8px;">
          <input [(ngModel)]="novoUsuario.password" type="password" placeholder="Senha" style="padding: 8px;">
          <input value="USER (Comum)" disabled style="padding: 8px; background: #e9ecef; color: #666;">
          <button (click)="salvarUsuario()" style="background: #28a745; color: white; border: none; padding: 8px 20px; cursor: pointer; border-radius: 4px;">
            Cadastrar
          </button>
        </div>
        <p *ngIf="mensagem()" [style.color]="corMensagem" style="margin-top: 10px;">{{ mensagem() }}</p>
      </div>

      <table border="1" style="width: 100%; border-collapse: collapse;">
        <thead style="background: #eee;">
          <tr>
            <th style="padding: 10px;">ID</th>
            <th>Usuário</th>
            <th>E-mail</th>
            <th>Role</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of usuarios()" style="text-align: center;">
            <td style="padding: 8px;">{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.role }}</td>
            <td>
              <span [style.color]="user.ativo ? 'green' : 'red'">
                {{ user.ativo ? 'ATIVO' : 'INATIVO' }}
              </span>
            </td>
            <td>
              <button (click)="alternarStatus(user)"
                      [style.background]="user.ativo ? '#ffc107' : '#007bff'"
                      style="border: none; padding: 5px 10px; color: white; cursor: pointer; border-radius: 3px;">
                {{ user.ativo ? 'Desativar' : 'Ativar' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class CadastroComponent implements OnInit {
  usuarios = signal<any[]>([]);
  mensagem = signal('');
  novoUsuario = { username: '', email: '', password: '', role: 'USER' };
  corMensagem = 'black';

  private apiUrl = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.listarUsuarios();
  }

  listarUsuarios(): void {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (res) => this.usuarios.set(res),
      error: (err) => console.error('Erro ao listar:', err)
    });
  }

  salvarUsuario(): void {
    if (!this.novoUsuario.username || !this.novoUsuario.password || !this.novoUsuario.email) {
      this.exibirMensagem('Preencha todos os campos!', 'red');
      return;
    }
    this.http.post('http://localhost:8080/auth/register', this.novoUsuario).subscribe({
      next: () => {
        this.exibirMensagem('Usuário cadastrado com sucesso!', 'green');
        this.novoUsuario = { username: '', email: '', password: '', role: 'USER' };
        this.listarUsuarios();
      },
      error: (err: any) => this.exibirMensagem(err.error?.message || 'Erro ao cadastrar', 'red')
    });
  }

  alternarStatus(user: any): void {
    this.http.patch(`${this.apiUrl}/${user.id}/status`, {}).subscribe({
      next: () => this.listarUsuarios(),
      error: (err: any) => alert('Erro ao mudar status: ' + (err.error?.message || 'Ação negada'))
    });
  }

  voltar(): void { this.router.navigate(['/home']); }

  private exibirMensagem(msg: string, cor: string): void {
    this.mensagem.set(msg);
    this.corMensagem = cor;
    setTimeout(() => this.mensagem.set(''), 3000);
  }
}