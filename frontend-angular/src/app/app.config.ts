import {
    ApplicationConfig,
    LOCALE_ID
} from '@angular/core';

import {
    registerLocaleData
} from '@angular/common';

import localePt from '@angular/common/locales/pt';

import {
    provideRouter
} from '@angular/router';

import {
    provideHttpClient
} from '@angular/common/http';

import {
    provideAnimations
} from '@angular/platform-browser/animations';

import {
    appRoutes
} from './app.routes';

registerLocaleData(localePt);

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(appRoutes),
        provideHttpClient(),
        provideAnimations(),

        {
            provide: LOCALE_ID,
            useValue: 'pt-BR'
        }
    ]
};