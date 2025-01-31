import { PaymentStatus } from './PaymentStatus.enum';

export interface PaymentResponse {
  paymentStatus: PaymentStatus;
  transactionId: string;
  clientSecret: string;
}
