import { Component } from "@angular/core";

@Component({
    template: '<div> {{ balance | convertBalance }} </div>'
})
export class ConvertBalancePipeHostComponent {
    balance: number;
}