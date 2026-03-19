import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudFilas } from './crud-filas';

describe('CrudFilas', () => {
  let component: CrudFilas;
  let fixture: ComponentFixture<CrudFilas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrudFilas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrudFilas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
