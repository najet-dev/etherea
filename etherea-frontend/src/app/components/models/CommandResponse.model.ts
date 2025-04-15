export interface CommandResponse {
  id: number;
  commandDate: string;
  referenceCode: string;
  status: string;
  firstName: string;
  lastName: string;
  address: string;
  zipCode: number;
  city: string;
  country: string;
  phoneNumber: string;
  paymentMethod: string;
  deliveryMethod: string;
  total: number;
}
