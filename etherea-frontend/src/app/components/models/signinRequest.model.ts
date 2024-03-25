import { Role } from './role.model';

export interface SigninRequest {
  id: number;
  username: string;
  roles: Role[];
  accessToken: string;
}
