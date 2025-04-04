import { Component } from '@angular/core';
import { SignupRequest } from '../models/signupRequest.model';
import { AppFacade } from 'src/app/services/appFacade.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent {
  user!: SignupRequest | null;

  constructor(private appFacade: AppFacade) {}

  ngOnInit(): void {
    this.appFacade.getCurrentUserDetails().subscribe((data) => {
      this.user = data;
    });
  }
}
