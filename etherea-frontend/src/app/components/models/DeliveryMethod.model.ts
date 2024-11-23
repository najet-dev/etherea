import { DeliveryOption } from './DeliveryOption.enum';

export interface DeliveryMethod {
  id: number;
  deliveryOption: DeliveryOption;
  expectedDeliveryDate: string;
  cost: number;
}
