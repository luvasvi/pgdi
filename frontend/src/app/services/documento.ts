import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {
  private apiUrl = 'http://localhost:8080/documentos';

  constructor(private http: HttpClient) {}

  // Envia o arquivo para o Docker
  upload(file: File, tipoId: number, filaId: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('tipoId', tipoId.toString());
    formData.append('filaId', filaId.toString());

    return this.http.post(`${this.apiUrl}/upload`, formData);
  }

  // Lista os documentos da fila
  listarFila(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/fila`);
  }
}