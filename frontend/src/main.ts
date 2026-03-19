import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component'; // Verifique se o arquivo se chama app.component.ts ou app.ts

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));