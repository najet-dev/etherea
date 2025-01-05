import { DeliveryOption } from './DeliveryOption.enum';

export interface DeliveryMethodDTO {
  deliveryOption: DeliveryOption;
  expectedDeliveryDate: string;
  cost: number;
  deliveryAddress?: string;
  pickupPointName?: string;
}
