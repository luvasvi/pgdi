import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-documento-visualizar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; font-family: sans-serif;">
      <button (click)="voltar()" style="margin-bottom: 15px; cursor: pointer;">← Voltar</button>
      <h2>📁 Filas Disponíveis</h2>

      <table border="1" style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
        <thead style="background: #f4f4f4;">
          <tr>
            <th style="padding: 10px;">Nome</th>
            <th>Descrição</th>
            <th>Ação</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let fila of filas()" style="text-align: center;">
            <td style="padding: 8px;">{{ fila.nome }}</td>
            <td>{{ fila.descricao }}</td>
            <td>
              <button (click)="selecionarFila(fila)" style="cursor: pointer; background: #007bff; color: white; border: none; padding: 5px 10px; border-radius: 3px;">
                Ver Documentos
              </button>
            </td>
          </tr>
          <tr *ngIf="filas().length === 0">
            <td colspan="3" style="padding: 15px; text-align: center; color: #888;">Nenhuma fila disponível</td>
          </tr>
        </tbody>
      </table>

      <div *ngIf="filaSelecionada()">
        <h3>📄 Documentos da fila: {{ filaSelecionada()?.nome }}</h3>

        <!-- Filtros -->
        <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 15px; align-items: flex-end;">
          <div style="display: flex; flex-direction: column; gap: 4px;">
            <label style="font-size: 12px; color: #666;">Buscar por nome</label>
            <input [(ngModel)]="filtros.busca"
                   placeholder="Ex: hamilton.jpg"
                   (keyup.enter)="buscar()"
                   style="padding: 7px 10px; border: 1px solid #ccc; border-radius: 4px; width: 180px;">
          </div>
          <div style="display: flex; flex-direction: column; gap: 4px;">
            <label style="font-size: 12px; color: #666;">Status</label>
            <select [(ngModel)]="filtros.status" style="padding: 7px 10px; border: 1px solid #ccc; border-radius: 4px;">
              <option value="">Todos</option>
              <option value="PENDENTE">Pendente</option>
              <option value="APROVADO">Aprovado</option>
              <option value="REPROVADO">Reprovado</option>
            </select>
          </div>
          <div style="display: flex; flex-direction: column; gap: 4px;">
            <label style="font-size: 12px; color: #666;">Itens por página</label>
            <select [(ngModel)]="paginacao.tamanho" (change)="buscar()" style="padding: 7px 10px; border: 1px solid #ccc; border-radius: 4px;">
              <option [value]="5">5</option>
              <option [value]="10">10</option>
              <option [value]="20">20</option>
            </select>
          </div>
          <button (click)="buscar()" style="padding: 7px 16px; background: #007bff; color: white; border: none; cursor: pointer; border-radius: 4px; height: 34px;">🔍 Buscar</button>
          <button (click)="limparFiltros()" style="padding: 7px 16px; background: #6c757d; color: white; border: none; cursor: pointer; border-radius: 4px; height: 34px;">Limpar</button>
        </div>

        <div style="margin-bottom: 10px; font-size: 13px; color: #666;">
          Exibindo {{ documentos().length }} de {{ paginacao.totalItens }} documentos
          — Página {{ paginacao.paginaAtual + 1 }} de {{ paginacao.totalPaginas }}
        </div>

        <table border="1" style="width: 100%; border-collapse: collapse; margin-bottom: 15px;">
          <thead style="background: #f4f4f4;">
            <tr>
              <th style="padding: 10px;">Arquivo</th>
              <th>Tipo</th>
              <th>Status</th>
              <th>Ação</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let doc of documentos()" style="text-align: center;">
              <td style="padding: 8px;">{{ doc.nomeArquivo }}</td>
              <td>{{ doc.tipoDocumentoNome }}</td>
              <td>
                <span [style.color]="doc.status === 'PENDENTE' ? 'orange' : (doc.status === 'APROVADO' ? 'green' : 'red')">
                  {{ doc.status }}
                </span>
              </td>
              <td>
                <button (click)="selecionarDocumento(doc)" style="cursor: pointer; margin-right: 5px;">Visualizar</button>
                <button (click)="baixarArquivo(doc)" style="cursor: pointer; background: #2196F3; color: white; border: none; padding: 3px 8px; border-radius: 3px;">⬇️ Baixar</button>
              </td>
            </tr>
            <tr *ngIf="documentos().length === 0">
              <td colspan="4" style="padding: 15px; text-align: center; color: #888;">Nenhum documento nesta fila</td>
            </tr>
          </tbody>
        </table>

        <!-- Paginação -->
        <div style="display: flex; gap: 6px; align-items: center; margin-bottom: 30px;">
          <button (click)="irParaPagina(0)" [disabled]="paginacao.primeira" style="padding: 5px 10px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">⏮</button>
          <button (click)="irParaPagina(paginacao.paginaAtual - 1)" [disabled]="paginacao.primeira" style="padding: 5px 10px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">◀</button>
          <span style="font-size: 13px; padding: 0 8px;">{{ paginacao.paginaAtual + 1 }} / {{ paginacao.totalPaginas }}</span>
          <button (click)="irParaPagina(paginacao.paginaAtual + 1)" [disabled]="paginacao.ultima" style="padding: 5px 10px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">▶</button>
          <button (click)="irParaPagina(paginacao.totalPaginas - 1)" [disabled]="paginacao.ultima" style="padding: 5px 10px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">⏭</button>
        </div>
      </div>

      <!-- Visualizador -->
      <div *ngIf="docSelecionado()" style="border: 2px solid #333; padding: 20px; border-radius: 8px; background: #eee; margin-top: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 8px;">
          <div>
            <h3 style="margin: 0;">Visualizando: {{ docSelecionado()?.nomeArquivo }}</h3>
            <small *ngIf="docSelecionado()?.status === 'REPROVADO'" style="color: red;">
              Motivo: {{ docSelecionado()?.motivoReprovacao }}
            </small>
          </div>
          <div style="display: flex; gap: 6px; flex-wrap: wrap;">
            <button (click)="zoomIn()" style="padding: 8px 12px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">➕ Zoom In</button>
            <button (click)="zoomOut()" style="padding: 8px 12px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">➖ Zoom Out</button>
            <button (click)="rotacionar()" style="padding: 8px 12px; cursor: pointer; border-radius: 4px; border: 1px solid #ccc;">🔄 Rotacionar</button>
            <button (click)="fecharDocumento()" style="padding: 8px 12px; background: #ff4444; color: white; border: none; cursor: pointer; border-radius: 4px;">Fechar</button>
          </div>
        </div>
        <div style="background: white; text-align: center; border: 1px solid #ccc; border-radius: 4px;
                    min-height: 400px; overflow: auto; display: flex; justify-content: center; align-items: center; padding: 20px;">
          <img [src]="urlImagem()"
               [style.transform]="'scale(' + zoom + ') rotate(' + rotacao + 'deg)'"
               style="transition: transform 0.2s ease-in-out; max-width: 100%; max-height: 600px; object-fit: contain; display: block;">
        </div>
      </div>
    </div>
  `
})
export class DocumentoVisualizarComponent implements OnInit {
  filas = signal<any[]>([]);
  documentos = signal<any[]>([]);
  filaSelecionada = signal<any>(null);
  docSelecionado = signal<any>(null);
  urlImagem = signal<string>('');
  zoom = 1;
  rotacao = 0;

  filtros = { busca: '', status: '' };
  paginacao = { paginaAtual: 0, totalPaginas: 1, totalItens: 0, tamanho: 10, primeira: true, ultima: true };

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void { this.listarFilas(); }

  listarFilas(): void {
    this.http.get<any[]>(`${this.baseUrl}/filas`).subscribe({
      next: (res) => this.filas.set(res),
      error: (err) => console.error('Erro ao listar filas:', err)
    });
  }

  selecionarFila(fila: any): void {
    this.filaSelecionada.set(fila);
    this.fecharDocumento();
    this.filtros = { busca: '', status: '' };
    this.paginacao.paginaAtual = 0;
    this.carregar();
  }

  buscar(): void {
    this.paginacao.paginaAtual = 0;
    this.carregar();
  }

  irParaPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.paginacao.totalPaginas) return;
    this.paginacao.paginaAtual = pagina;
    this.carregar();
  }

  limparFiltros(): void {
    this.filtros = { busca: '', status: '' };
    this.buscar();
  }

  carregar(): void {
    const fila = this.filaSelecionada();
    if (!fila) return;

    const params: any = {
      filaId: fila.id,
      pagina: this.paginacao.paginaAtual,
      tamanho: this.paginacao.tamanho
    };
    if (this.filtros.busca) params.busca = this.filtros.busca;
    if (this.filtros.status) params.status = this.filtros.status;

    this.http.get<any>(`${this.baseUrl}/documentos/buscar`, { params }).subscribe({
      next: (res) => {
        this.documentos.set(res.conteudo);
        this.paginacao.paginaAtual = res.paginaAtual;
        this.paginacao.totalPaginas = res.totalPaginas || 1;
        this.paginacao.totalItens = res.totalItens;
        this.paginacao.primeira = res.primeira;
        this.paginacao.ultima = res.ultima;
      },
      error: (err) => console.error('Erro ao carregar documentos:', err)
    });
  }

  selecionarDocumento(doc: any): void {
    this.zoom = 1;
    this.rotacao = 0;
    const urlAtual = this.urlImagem();
    if (urlAtual) URL.revokeObjectURL(urlAtual);
    this.docSelecionado.set(doc);
    this.http.get(`${this.baseUrl}/documentos/arquivo/${doc.id}`, { responseType: 'blob' }).subscribe({
      next: (blob) => this.urlImagem.set(URL.createObjectURL(blob)),
      error: (err) => console.error('Erro ao carregar imagem:', err)
    });
  }

  fecharDocumento(): void {
    const urlAtual = this.urlImagem();
    if (urlAtual) URL.revokeObjectURL(urlAtual);
    this.urlImagem.set('');
    this.docSelecionado.set(null);
  }

  baixarArquivo(doc: any): void {
    this.http.get(`${this.baseUrl}/documentos/arquivo/${doc.id}`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url; link.download = doc.nomeArquivo; link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Erro ao baixar arquivo:', err)
    });
  }

  voltar(): void { this.router.navigate(['/home']); }
  zoomIn(): void { this.zoom = Math.round((this.zoom + 0.2) * 10) / 10; }
  zoomOut(): void { if (this.zoom > 0.4) this.zoom = Math.round((this.zoom - 0.2) * 10) / 10; }
  rotacionar(): void { this.rotacao = (this.rotacao + 90) % 360; }
}