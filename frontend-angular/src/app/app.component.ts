import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/components/header/header.component';
import { FooterComponent } from './shared/components/footer/footer.component';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [
        RouterOutlet,
        HeaderComponent,
        FooterComponent
    ],
    template: `
    <div class="app-shell">
      <app-header />

      <main class="main-content">
        <router-outlet />
      </main>

      <app-footer />
    </div>
  `,
    styleUrls: ['./app.component.css']
})
export class AppComponent {}