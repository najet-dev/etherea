export interface CommandResponse {
  id: number;
  commandDate: string;
  referenceCode: string;
  status: string;
  firstName: string;
  lastName: string;
  deliveryAddress: string;
  paymentMethod: string;
  deliveryMethod: string;
  total: number;
}
