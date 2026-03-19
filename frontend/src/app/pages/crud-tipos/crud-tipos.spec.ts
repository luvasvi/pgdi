import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudTipos } from './crud-tipos';

describe('CrudTipos', () => {
  let component: CrudTipos;
  let fixture: ComponentFixture<CrudTipos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrudTipos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrudTipos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
