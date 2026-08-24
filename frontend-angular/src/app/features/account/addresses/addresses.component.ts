import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';

@Component({
    selector: 'app-addresses',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    template: `
    <div class="addresses-container">
      <h1>Meus Endereços</h1>
      
      <div class="address-list">
        <div *ngFor="let address of addresses" class="address-card">
          <div class="address-info">
            <h3>{{ address.street }}</h3>
            <p>{{ address.city }} - {{ address.state }}</p>
            <p>CEP: {{ address.zipCode }}</p>
          </div>
          <div class="address-actions">
            <button class="btn btn-secondary">Editar</button>
            <button class="btn btn-danger">Excluir</button>
          </div>
        </div>
      </div>
      
      <button class="btn btn-primary" (click)="showAddForm = !showAddForm">
        {{ showAddForm ? 'Cancelar' : 'Adicionar Novo Endereço' }}
      </button>
      
      <form *ngIf="showAddForm" (ngSubmit)="onSubmit()" [formGroup]="addressForm" class="address-form">
        <div class="form-group">
          <label for="street">Rua</label>
          <input id="street" type="text" formControlName="street" required>
        </div>
        <div class="form-group">
          <label for="number">Número</label>
          <input id="number" type="text" formControlName="number" required>
        </div>
        <div class="form-group">
          <label for="complement">Complemento</label>
          <input id="complement" type="text" formControlName="complement">
        </div>
        <div class="form-group">
          <label for="city">Cidade</label>
          <input id="city" type="text" formControlName="city" required>
        </div>
        <div class="form-group">
          <label for="state">Estado</label>
          <input id="state" type="text" formControlName="state" required>
        </div>
        <div class="form-group">
          <label for="zipCode">CEP</label>
          <input id="zipCode" type="text" formControlName="zipCode" required>
        </div>
        <button type="submit" class="btn btn-primary" [disabled]="addressForm.invalid || loading">
          {{ loading ? 'Salvando...' : 'Salvar Endereço' }}
        </button>
      </form>
    </div>
  `,
    styleUrls: ['./addresses.component.css']
})
export class AddressesComponent implements OnInit {
    loading = false;
    showAddForm = false;
    addresses: any[] = [];
    addressForm: FormGroup;

    constructor() {
        this.addressForm = new FormGroup({
            street: new FormControl(''),
            number: new FormControl(''),
            complement: new FormControl(''),
            city: new FormControl(''),
            state: new FormControl(''),
            zipCode: new FormControl('')
        });
    }

    ngOnInit(): void {
        this.loadAddresses();
    }

    loadAddresses(): void {
        // TODO: Implement addresses API call
        console.log('Loading addresses');
    }

    onSubmit(): void {
        if (this.addressForm.valid) {
            this.loading = true;
            // TODO: Implement address creation API call
            console.log('Address data:', this.addressForm.value);
            setTimeout(() => {
                this.loading = false;
                this.showAddForm = false;
            }, 1000);
        }
    }
}
