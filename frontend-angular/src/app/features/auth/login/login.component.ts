import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="login-container">
      <div class="login-card">
        <h1>Login</h1>
        
        <form (ngSubmit)="onSubmit()" [formGroup]="loginForm" class="login-form">
          <div class="form-group">
            <label for="email">E-mail</label>
            <input 
              id="email" 
              type="email" 
              formControlName="email" 
              placeholder="Digite seu e-mail"
              required>
            <div *ngIf="loginForm.get('email')?.touched && loginForm.get('email')?.invalid" class="error">
              E-mail inválido
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">Senha</label>
            <input 
              id="password" 
              type="password" 
              formControlName="password" 
              placeholder="Digite sua senha"
              required>
            <div *ngIf="loginForm.get('password')?.touched && loginForm.get('password')?.invalid" class="error">
              Senha é obrigatória
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loginForm.invalid || loading">
            {{ loading ? 'Entrando...' : 'Entrar' }}
          </button>
        </form>
        
        <div class="login-footer">
          <p>Não tem uma conta? <a routerLink="/auth/register">Cadastre-se</a></p>
        </div>
      </div>
    </div>
  `,
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    loading = false;
    loginForm: FormGroup;

    constructor(private router: Router) {
        this.loginForm = new FormGroup({
            email: new FormControl('', [Validators.required, Validators.email]),
            password: new FormControl('', Validators.required)
        });
    }

    onSubmit(): void {
        if (this.loginForm.valid) {
            this.loading = true;
            // TODO: Implement login API call
            console.log('Login data:', this.loginForm.value);
            setTimeout(() => {
                this.loading = false;
                this.router.navigate(['/home']);
            }, 1000);
        }
    }
}
