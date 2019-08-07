import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ConsentsComponent} from './consents/consents.component';
import {AuthGuard} from './common/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'consents',
    component: ConsentsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'account-information',
    loadChildren: './ais/ais.module#AisModule'
  },
  {
    path: 'payment-initiation',
    loadChildren: './pis/pis.module#PisModule'
  },
  {
    path: 'payment-cancellation',
    loadChildren: './payment-cancellation/payment-cancellation.module#PaymentCancellationModule'
  },
  {
    path: '**',
    redirectTo: '/account-information/login'
  }

];

@NgModule({
  declarations: [],
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})

export class AppRoutingModule {
}
