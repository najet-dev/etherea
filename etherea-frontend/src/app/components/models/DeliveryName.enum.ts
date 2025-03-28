export enum DeliveryName {
  HOME_STANDARD = 'HOME_STANDARD',
  HOME_EXPRESS = 'HOME_EXPRESS',
  PICKUP_POINT = 'PICKUP_POINT',
}

export const DeliveryNameTranslations: Record<DeliveryName, string> = {
  [DeliveryName.HOME_STANDARD]: 'Domicile Standard',
  [DeliveryName.HOME_EXPRESS]: 'Domicile Express',
  [DeliveryName.PICKUP_POINT]: 'Point de retrait',
};
