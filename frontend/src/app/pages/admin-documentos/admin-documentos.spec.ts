import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDocumentos } from './admin-documentos';

describe('AdminDocumentos', () => {
  let component: AdminDocumentos;
  let fixture: ComponentFixture<AdminDocumentos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDocumentos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDocumentos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
