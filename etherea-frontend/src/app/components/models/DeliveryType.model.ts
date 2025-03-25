import { DeliveryName } from './deliveryName.enum';

export interface DeliveryType {
  id: number;
  deliveryName: DeliveryName;
  deliveryDays: number;
  cost: number;
  estimatedDeliveryDate: Date;
}
