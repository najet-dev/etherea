export interface Role {
  id: number;
  name: ERole;
}

export enum ERole {
  ROLE_USER = 'ROLE_USER',
  ROLE_ADMIN = 'ROLE_ADMIN',
}
