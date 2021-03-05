import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

// used to create fake backend
//import { fakeBackendProvider } from './_helpers';

import { AppComponent } from './app.component';
import { appRoutingModule } from './app.routing';

import { JwtInterceptor, ErrorInterceptor } from './_helpers';
import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { ModalContentComponent } from './modal-content/modal-content.component';
import { CreateTopicComponent } from './create-topic/create-topic.component';
@NgModule({
    imports: [
        BrowserModule,
        ReactiveFormsModule,
        HttpClientModule,
        appRoutingModule,
        NgbModule,
        FontAwesomeModule,
        FormsModule
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        LoginComponent,
        ModalContentComponent ,
        CreateTopicComponent   ],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },

        // provider used to create fake backend
        //fakeBackendProvider
    ],
    bootstrap: [AppComponent],
    entryComponents: [ ModalContentComponent, CreateTopicComponent ]
})
export class AppModule { }