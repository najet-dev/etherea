export interface AddDeliveryMethodRequestDTO {
  userId: number;
  deliveryOption: string;
  addressId?: number;
  pickupPointName?: string;
  pickupPointAddress?: string;
  pickupPointLatitude?: number;
  pickupPointLongitude?: number;
  orderAmount: number;
}
