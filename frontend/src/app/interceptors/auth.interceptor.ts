import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  if (!token) {
    return next(req);
  }

  const authReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`)
  });

  return next(authReq);
};