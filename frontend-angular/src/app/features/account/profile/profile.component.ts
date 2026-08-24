import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="profile-container">
      <h1>Meu Perfil</h1>
      
      <form (ngSubmit)="onSubmit()" [formGroup]="profileForm" class="profile-form">
        <div class="form-group">
          <label for="fullName">Nome Completo</label>
          <input 
            id="fullName" 
            type="text" 
            formControlName="fullName" 
            required>
        </div>
        <div class="form-group">
          <label for="email">E-mail</label>
          <input 
            id="email" 
            type="email" 
            formControlName="email" 
            required>
        </div>
        <div class="form-group">
          <label for="phone">Telefone</label>
          <input 
            id="phone" 
            type="text" 
            formControlName="phone">
        </div>
        <button type="submit" class="btn btn-primary" [disabled]="profileForm.invalid || loading">
          {{ loading ? 'Salvando...' : 'Salvar Alterações' }}
        </button>
      </form>
    </div>
  `,
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    loading = false;
    profileForm: FormGroup;

    constructor() {
        this.profileForm = new FormGroup({
            fullName: new FormControl(''),
            email: new FormControl(''),
            phone: new FormControl('')
        });
    }

    ngOnInit(): void {
        this.loadProfile();
    }

    loadProfile(): void {
        // TODO: Implement profile API call
        console.log('Loading profile data');
    }

    onSubmit(): void {
        if (this.profileForm.valid) {
            this.loading = true;
            // TODO: Implement profile update API call
            console.log('Profile data:', this.profileForm.value);
            setTimeout(() => {
                this.loading = false;
            }, 1000);
        }
    }
}
