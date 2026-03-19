import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DocumentoService } from '../../services/documento';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-documento-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="padding: 20px; border: 1px solid #ccc; border-radius: 8px; max-width: 450px; background: #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin: 20px auto;">
      <h3 style="margin-top: 0; color: #333;">📤 Upload de Documento</h3>

      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px; font-weight: bold;">Escolha o arquivo:</label>
        <input type="file" (change)="selecionarArquivo($event)" accept=".jpg,.jpeg,.png">
      </div>

      <div style="margin-bottom: 15px;">
        <label style="display: block; margin-bottom: 5px; font-weight: bold;">Tipo de Documento:</label>
        <select [(ngModel)]="tipoIdSelecionado" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #ccc;">
          <option [value]="null" disabled selected>Selecione o tipo...</option>
          <option *ngFor="let t of tipos()" [value]="t.id">{{ t.nome }} - {{ t.descricao }}</option>
        </select>
      </div>

      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px; font-weight: bold;">Fila de Destino:</label>
        <select [(ngModel)]="filaIdSelecionado" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #ccc;">
          <option [value]="null" disabled selected>Selecione a fila...</option>
          <option *ngFor="let f of filas()" [value]="f.id">{{ f.nome }}</option>
        </select>
      </div>

      <button (click)="enviar()" [disabled]="!arquivoSelecionado || !tipoIdSelecionado || !filaIdSelecionado"
              style="padding: 12px 20px; background: #28a745; color: white; border: none; cursor: pointer; border-radius: 4px; width: 100%; font-weight: bold;">
        Enviar para o Servidor
      </button>

      <p *ngIf="mensagem()" [style.color]="corMensagem" style="margin-top: 15px; text-align: center; font-weight: bold;">
        {{ mensagem() }}
      </p>
    </div>
  `
})
export class DocumentoUploadComponent implements OnInit {
  tipos = signal<any[]>([]);
  filas = signal<any[]>([]);
  mensagem = signal('');
  arquivoSelecionado: File | null = null;
  corMensagem = 'black';
  tipoIdSelecionado: number | null = null;
  filaIdSelecionado: number | null = null;

  private baseUrl = 'http://localhost:8080';

  constructor(private docService: DocumentoService, private http: HttpClient) {}

  ngOnInit(): void {
    this.carregarDadosIniciais();
  }

  carregarDadosIniciais(): void {
    this.http.get<any[]>(`${this.baseUrl}/tipos`).subscribe({
      next: (res) => this.tipos.set(res),
      error: (err) => console.error('Erro ao buscar tipos:', err)
    });

    this.http.get<any[]>(`${this.baseUrl}/filas`).subscribe({
      next: (res) => {
        this.filas.set(res);
        if (res.length === 1) this.filaIdSelecionado = res[0].id;
      },
      error: (err) => console.error('Erro ao buscar filas:', err)
    });
  }

  selecionarArquivo(event: any): void {
    const fileList: FileList | null = (event.currentTarget as HTMLInputElement).files;
    if (fileList) this.arquivoSelecionado = fileList[0];
  }

  enviar(): void {
    if (!this.arquivoSelecionado || !this.tipoIdSelecionado || !this.filaIdSelecionado) return;

    this.docService.upload(this.arquivoSelecionado, this.tipoIdSelecionado, this.filaIdSelecionado).subscribe({
      next: () => {
        this.mensagem.set('✅ Arquivo enviado com sucesso!');
        this.corMensagem = 'green';
        this.resetForm();
      },
      error: (err: any) => {
        const msg = err?.error?.message || err?.error || 'Verifique a conexão ou extensão do arquivo';
        this.mensagem.set('❌ Erro: ' + msg);
        this.corMensagem = 'red';
      }
    });
  }

  private resetForm(): void {
    this.arquivoSelecionado = null;
    this.tipoIdSelecionado = null;
    this.filaIdSelecionado = this.filas().length === 1 ? this.filas()[0].id : null;
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }
}