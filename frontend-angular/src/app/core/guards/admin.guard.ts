import { Router, CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn() &&
        (authService.hasRole('ROLE_ADMIN') || authService.hasRole('ROLE_MANAGER'))) {
        return true;
    }

    router.navigate(['/']);
    return false;
};