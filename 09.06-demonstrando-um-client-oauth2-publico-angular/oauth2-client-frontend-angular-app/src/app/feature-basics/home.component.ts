import { Component, OnInit, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from '../shared/api.service';

@Component({
  selector: 'app-home',
  template: `<p class="alert alert-primary">
    This is the <strong>🏠 HOME</strong> component.
    
  </p>`,
  standalone: false
})
export class HomeComponent implements OnInit {

  ngOnInit() {
    
  }
}
