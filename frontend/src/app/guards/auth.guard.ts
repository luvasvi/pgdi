import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaLogado()) {
    
    // Verificação de Admin (Requisito 4.3 do PDF)
    const expectedRole = route.data['role'];
    const userRole = authService.getRole();

    if (expectedRole && userRole !== expectedRole) {
      console.warn('Acesso negado: Usuário não possui a role necessária.');
      router.navigate(['/home']);
      return false;
    }
    
    return true;
  }

  // Se não estiver logado, manda para o login (Requisito 4.1)
  router.navigate(['/login']);
  return false;
};