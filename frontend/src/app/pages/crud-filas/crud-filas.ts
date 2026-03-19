import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-crud-filas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; font-family: sans-serif;">
      <button (click)="voltar()" style="margin-bottom: 15px; cursor: pointer;">← Voltar</button>
      <h2>📊 Gestão de Filas (Admin)</h2>

      <div style="background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd;">
        <h3>Nova Fila</h3>
        <div style="display: flex; gap: 10px; flex-wrap: wrap; align-items: center;">
          <input [(ngModel)]="novaFila.nome" placeholder="Nome da fila" style="padding: 8px; flex: 1;">
          <input [(ngModel)]="novaFila.descricao" placeholder="Descrição" style="padding: 8px; flex: 2;">
          <label style="display: flex; align-items: center; gap: 5px; cursor: pointer;">
            <input type="checkbox" [(ngModel)]="novaFila.restrita"> Restrita
          </label>
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
            <th>Restrita</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let fila of filas()" style="text-align: center;">
            <td style="padding: 8px;">{{ fila.id }}</td>
            <td>{{ fila.nome }}</td>
            <td>{{ fila.descricao }}</td>
            <td>
              <span [style.color]="fila.restrita ? 'red' : 'green'">
                {{ fila.restrita ? 'Sim' : 'Não' }}
              </span>
            </td>
            <td>
              <button (click)="deletar(fila.id)"
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
export class CrudFilasComponent implements OnInit {
  filas = signal<any[]>([]);
  mensagem = signal('');
  novaFila = { nome: '', descricao: '', restrita: false };
  corMensagem = 'black';

  private apiUrl = 'http://localhost:8080/filas';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.listar();
  }

  listar(): void {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (res) => this.filas.set(res),
      error: (err) => console.error('Erro ao listar filas:', err)
    });
  }

  criar(): void {
    if (!this.novaFila.nome) {
      this.exibirMensagem('O nome é obrigatório!', 'red');
      return;
    }
    this.http.post<any>(this.apiUrl, this.novaFila).subscribe({
      next: () => {
        this.exibirMensagem('Fila criada com sucesso!', 'green');
        this.novaFila = { nome: '', descricao: '', restrita: false };
        this.listar();
      },
      error: (err) => this.exibirMensagem(err.error?.message || 'Erro ao criar fila', 'red')
    });
  }

  deletar(id: number): void {
    if (!confirm('Deseja realmente deletar esta fila?')) return;
    this.http.delete(`${this.apiUrl}/${id}`).subscribe({
      next: () => this.listar(),
      error: (err) => alert('Erro ao deletar: ' + (err.error?.message || 'Ação negada'))
    });
  }

  voltar(): void { this.router.navigate(['/home']); }

  private exibirMensagem(msg: string, cor: string): void {
    this.mensagem.set(msg);
    this.corMensagem = cor;
    setTimeout(() => this.mensagem.set(''), 3000);
  }
}