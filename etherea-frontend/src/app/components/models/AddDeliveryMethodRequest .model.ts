export interface AddDeliveryMethodRequest {
  userId: number;
  deliveryOption: string;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
  orderAmount: number;
}
