import { Component, OnInit } from '@angular/core';
import {  NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'app-modal-content',
  templateUrl: './modal-content.component.html',
  styleUrls: ['./modal-content.component.less']
})
export class ModalContentComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }
  type:string;
  data:string;

  ngOnInit() {
  }

}
