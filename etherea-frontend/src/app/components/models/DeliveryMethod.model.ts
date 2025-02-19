import { DeliveryAddress } from './DeliveryAddress.model';
import { DeliveryType } from './DeliveryType.enum';

export interface DeliveryMethod {
  id: number;
  deliveryType: DeliveryType;
  expectedDeliveryDate: string;
  cost: number;
  deliveryAddress?: DeliveryAddress;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
}
