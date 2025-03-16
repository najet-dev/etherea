import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {}
  logout() {
    this.authService.logout();
  }
  isInformationsActive(): boolean {
    return (
      this.router.url.includes('/informations') ||
      this.router.url.includes('/updateEmail') ||
      this.router.url.includes('/updatePassword')
    );
  }
}
