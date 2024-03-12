import { Role } from './role.model';

export interface SigninRequest {
  username: string;
  password: string;
  roles?: Role[];
}
