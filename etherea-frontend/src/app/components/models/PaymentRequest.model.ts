import { PaymentOption } from './PaymentOption.enum';
import { PaymentStatus } from './PaymentStatus.enum.';

export interface PaymentRequest {
  paymentOption: PaymentOption;
  paymentStatus: PaymentStatus;
  cartId: number;
}
