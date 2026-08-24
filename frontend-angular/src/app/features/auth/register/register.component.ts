import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="register-container">
      <div class="register-card">
        <h1>Cadastro</h1>
        
        <form (ngSubmit)="onSubmit()" [formGroup]="registerForm" class="register-form">
          <div class="form-group">
            <label for="fullName">Nome Completo</label>
            <input 
              id="fullName" 
              type="text" 
              formControlName="fullName" 
              placeholder="Digite seu nome completo"
              required>
            <div *ngIf="registerForm.get('fullName')?.touched && registerForm.get('fullName')?.invalid" class="error">
              Nome é obrigatório
            </div>
          </div>
          
          <div class="form-group">
            <label for="email">E-mail</label>
            <input 
              id="email" 
              type="email" 
              formControlName="email" 
              placeholder="Digite seu e-mail"
              required>
            <div *ngIf="registerForm.get('email')?.touched && registerForm.get('email')?.invalid" class="error">
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
            <div *ngIf="registerForm.get('password')?.touched && registerForm.get('password')?.invalid" class="error">
              Senha é obrigatória
            </div>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Confirmar Senha</label>
            <input 
              id="confirmPassword" 
              type="password" 
              formControlName="confirmPassword" 
              placeholder="Confirme sua senha"
              required>
            <div *ngIf="registerForm.get('confirmPassword')?.touched && registerForm.get('confirmPassword')?.invalid" class="error">
              As senhas não coincidem
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary btn-block" [disabled]="registerForm.invalid || loading">
            {{ loading ? 'Cadastrando...' : 'Cadastrar' }}
          </button>
        </form>
        
        <div class="register-footer">
          <p>Já tem uma conta? <a routerLink="/auth/login">Faça login</a></p>
        </div>
      </div>
    </div>
  `,
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    loading = false;
    registerForm: FormGroup;

    constructor(private router: Router) {
        this.registerForm = new FormGroup({
            fullName: new FormControl('', Validators.required),
            email: new FormControl('', [Validators.required, Validators.email]),
            password: new FormControl('', [Validators.required, Validators.minLength(6)]),
            confirmPassword: new FormControl('', [Validators.required, this.passwordMatchValidator])
        });
    }

    passwordMatchValidator = (control: FormControl): { [key: string]: boolean } | null => {
        if (!control.parent) {
            return null;
        }
        const password = control.parent.get('password')?.value;
        const confirmPassword = control.value;
        return password === confirmPassword ? null : { mismatch: true };
    };

    onSubmit(): void {
        if (this.registerForm.valid) {
            this.loading = true;
            // TODO: Implement registration API call
            console.log('Registration data:', this.registerForm.value);
            setTimeout(() => {
                this.loading = false;
                this.router.navigate(['/auth/login']);
            }, 1000);
        }
    }
}
