import { Component, EventEmitter, Output } from '@angular/core';
import { PaymentStatus } from '../models/PaymentStatus.enum';
import { PaymentService } from 'src/app/services/payment.service';
import { PaymentRequest } from '../models/PaymentRequest.model';
import { PaymentResponse } from '../models/PaymentResponse.model';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
})
export class PaymentComponent {}
