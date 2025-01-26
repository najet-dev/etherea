import { PaymentOption } from './PaymentOption.enum';

export interface PaymentRequest {
  id: number;
  cardNumber: string;
  expiryDate: string;
  cvc: string;
  paymentOption: PaymentOption;
  cartId: number;
}
