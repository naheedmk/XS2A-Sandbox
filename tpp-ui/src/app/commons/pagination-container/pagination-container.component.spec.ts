import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginationContainerComponent } from './pagination-container.component';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

describe('PaginationContainerComponent', () => {
  let component: PaginationContainerComponent;
  let fixture: ComponentFixture<PaginationContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaginationContainerComponent],
      imports: [NgbPaginationModule, FormsModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaginationContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change the page', () => {
    component.paginationConfig = {
      itemsPerPage: 0,
      currentPageNumber: 0,
      totalItems: 0,
    };
    const expectedPage = 15;

    component.pageChange(expectedPage);
    expect(component.paginationConfig.currentPageNumber).toEqual(expectedPage);
  });

  it('should change the page size', () => {
    component.paginationConfig = {
      itemsPerPage: 0,
      currentPageNumber: 0,
      totalItems: 0,
    };
    const expectedPageSize = 10;
    component.pageSizeChange(expectedPageSize);
    expect(component.paginationConfig.itemsPerPage).toEqual(expectedPageSize);
  });

  it('should call emit page, when page size is not set', () => {
    component.paginationConfig = {
      itemsPerPage: 8,
      currentPageNumber: 0,
      totalItems: 0,
    };
    const expectedPageSize = 8;
    component.pageSizeChange(null);
    expect(component.paginationConfig.itemsPerPage).toEqual(expectedPageSize);
  });
  it('should call the emitCurrentPageConfig', () => {
    const pageDataConfigSpy = spyOn(component.pageDataConfig, 'emit');
    const expected = {
      pageNumber: 6,
      pageSize: 10,
    };
    component.paginationConfig = {
      itemsPerPage: 10,
      currentPageNumber: 6,
      totalItems: 100,
    };
    component.emitCurrentPageConfig();
    expect(pageDataConfigSpy).toHaveBeenCalledWith(expected);
  });
});
