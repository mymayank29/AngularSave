import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConvertToCSVService {

  private window: Window = window;

  public convertJsonToCSV(json: any): any {
    const replacer = (key, value) => value === null ? '' : value;
    const header = Object.keys(json[0]);
    const csv = json.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
    csv.unshift(header.join(','));
    const csvArray = csv.join('\r\n');
    return csvArray;
  }

  public saveFile(fileName: string, blob: Blob) {
    if (this.window.navigator && this.window.navigator.msSaveOrOpenBlob) {
      this.window.navigator.msSaveOrOpenBlob(blob, fileName);
    } else {
      const url = this.window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      this.window.URL.revokeObjectURL(url);
      a.remove();
    }
  }

  public sum(a, b) {
    return a + b;
  }

}
