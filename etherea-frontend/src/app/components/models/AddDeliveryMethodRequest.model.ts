import { DeliveryType } from './DeliveryType.enum';

export interface AddDeliveryMethodRequest {
  userId: number;
  deliveryType: DeliveryType;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
  orderAmount: number;
}
