import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentoVisualizar } from './documento-visualizar';

describe('DocumentoVisualizar', () => {
  let component: DocumentoVisualizar;
  let fixture: ComponentFixture<DocumentoVisualizar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentoVisualizar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentoVisualizar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
