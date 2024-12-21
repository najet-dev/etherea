import { DeliveryAddress } from './DeliveryAddress.model';
import { DeliveryOption } from './DeliveryOption.enum';

export interface DeliveryMethod {
  id: number;
  deliveryOption: DeliveryOption;
  expectedDeliveryDate: string;
  cost: number;
  deliveryAddress?: DeliveryAddress;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
}
