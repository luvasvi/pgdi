import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-crud-tipos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; font-family: sans-serif;">
      <button (click)="voltar()" style="margin-bottom: 15px; cursor: pointer;">← Voltar</button>
      <h2>📄 Gestão de Tipos de Documento (Admin)</h2>

      <div style="background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd;">
        <h3>Novo Tipo</h3>
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <input [(ngModel)]="novoTipo.nome" placeholder="Nome (ex: RG, CNH)" style="padding: 8px; flex: 1;">
          <input [(ngModel)]="novoTipo.descricao" placeholder="Descrição" style="padding: 8px; flex: 2;">
          <button (click)="criar()" style="background: #28a745; color: white; border: none; padding: 8px 20px; cursor: pointer; border-radius: 4px;">
            Criar
          </button>
        </div>
        <p *ngIf="mensagem()" [style.color]="corMensagem" style="margin-top: 10px;">{{ mensagem() }}</p>
      </div>

      <table border="1" style="width: 100%; border-collapse: collapse;">
        <thead style="background: #eee;">
          <tr>
            <th style="padding: 10px;">ID</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let tipo of tipos()" style="text-align: center;">
            <td style="padding: 8px;">{{ tipo.id }}</td>
            <td>{{ tipo.nome }}</td>
            <td>{{ tipo.descricao }}</td>
            <td>
              <button (click)="deletar(tipo.id)"
                      style="background: #dc3545; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 3px;">
                Deletar
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class CrudTiposComponent implements OnInit {
  // ✅ Signals são reativos por natureza — atualizam o template automaticamente
  tipos = signal<any[]>([]);
  mensagem = signal('');
  novoTipo = { nome: '', descricao: '' };
  corMensagem = 'black';

  private apiUrl = 'http://localhost:8080/tipos';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.listar();
  }

  listar(): void {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (res) => this.tipos.set(res),
      error: (err) => console.error('Erro ao listar tipos:', err)
    });
  }

  criar(): void {
    if (!this.novoTipo.nome) {
      this.exibirMensagem('O nome é obrigatório!', 'red');
      return;
    }
    this.http.post<any>(this.apiUrl, this.novoTipo).subscribe({
      next: () => {
        this.exibirMensagem('Tipo criado com sucesso!', 'green');
        this.novoTipo = { nome: '', descricao: '' };
        this.listar();
      },
      error: (err) => this.exibirMensagem(err.error?.message || 'Erro ao criar tipo', 'red')
    });
  }

  deletar(id: number): void {
    if (!confirm('Deseja realmente deletar este tipo?')) return;
    this.http.delete(`${this.apiUrl}/${id}`).subscribe({
      next: () => this.listar(),
      error: (err) => alert('Erro ao deletar: ' + (err.error?.message || 'Ação negada'))
    });
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  private exibirMensagem(msg: string, cor: string): void {
    this.mensagem.set(msg);
    this.corMensagem = cor;
    setTimeout(() => this.mensagem.set(''), 3000);
  }
}