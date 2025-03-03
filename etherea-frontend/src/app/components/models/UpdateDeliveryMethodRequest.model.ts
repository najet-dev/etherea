export interface UpdateDeliveryMethodRequest {
  userId: number;
  deliveryMethodId: number;
  deliveryTypeId: number;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
}
