import { DeliveryType } from './DeliveryType.enum';

export interface DeliveryMethodDTO {
  deliveryType: DeliveryType;
  expectedDeliveryDate: string;
  cost: number;
  deliveryAddress?: string;
  pickupPointName?: string;
}
