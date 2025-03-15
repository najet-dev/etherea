import { PaymentStatus } from './paymentStatus.enum';

export interface PaymentResponse {
  paymentStatus: PaymentStatus;
  transactionId: string;
  clientSecret: string;
}
