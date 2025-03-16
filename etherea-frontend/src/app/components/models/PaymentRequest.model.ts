import { PaymentOption } from './paymentOption.enum';

export interface PaymentRequest {
  paymentOption: PaymentOption;
  cartId: number;
}
