import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet],
    template: `
    <div class="app-container">
      <header class="header">
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
          <div class="container">
            <a class="navbar-brand" href="/">E-commerce</a>
          </div>
        </nav>
      </header>
      
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
      
      <footer class="footer">
        <div class="container">
          <p>&copy; 2024 E-commerce de Eletrodomésticos. Todos os direitos reservados.</p>
        </div>
      </footer>
    </div>
  `,
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    title = 'E-commerce';
}