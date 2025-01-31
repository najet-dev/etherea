import { PaymentOption } from './PaymentOption.enum';

export interface PaymentRequest {
  paymentOption: PaymentOption;
  cartId: number;
}
