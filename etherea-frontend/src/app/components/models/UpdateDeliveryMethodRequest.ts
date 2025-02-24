import { DeliveryType } from './DeliveryType.enum';

export interface UpdateDeliveryMethodRequest {
  userId: number;
  deliveryMethodId: number;
  deliveryType: DeliveryType;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
}
