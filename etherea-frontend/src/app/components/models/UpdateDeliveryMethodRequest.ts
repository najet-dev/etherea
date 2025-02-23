import { DeliveryType } from './DeliveryType.enum';

export interface updateDeliveryMethodRequest {
  userId: number;
  deliveryMethodId: number;
  deliveryType: DeliveryType;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
}
