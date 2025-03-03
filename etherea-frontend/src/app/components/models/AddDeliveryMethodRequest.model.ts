export interface AddDeliveryMethodRequest {
  userId: number;
  deliveryTypeId: number;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
  orderAmount: number;
}
