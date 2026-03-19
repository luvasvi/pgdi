import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { DocumentoUploadComponent } from './pages/documento-upload/documento-upload';
import { HomeComponent } from './pages/home/home';
import { DocumentoVisualizarComponent } from './pages/documento-visualizar/documento-visualizar';
import { CrudFilasComponent } from './pages/crud-filas/crud-filas';
import { CrudTiposComponent } from './pages/crud-tipos/crud-tipos';
import { AdminDocumentosComponent } from './pages/admin-documentos/admin-documentos';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'upload', component: DocumentoUploadComponent, canActivate: [authGuard] },
  { path: 'filas', component: DocumentoVisualizarComponent, canActivate: [authGuard] },
  {
    path: 'cadastro',
    component: CadastroComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'crud-filas',
    component: CrudFilasComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'admin-documentos',
    component: AdminDocumentosComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'tipos',
    component: CrudTiposComponent,
    canActivate: [authGuard],
    data: { role: 'ADMIN' }
  },
  { path: '**', redirectTo: 'login' }
];