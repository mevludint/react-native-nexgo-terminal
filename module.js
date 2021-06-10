import { NativeModules } from "react-native";

const { NexgoPrinterModule } = NativeModules;

class NexgoPrinter {
  async init() {
    return await NexgoPrinterModule.init();
  }

  async appendStr(str = "", itemAlign = "LEFT", fontEntity = fontEnum.NORMAL) {
    return await NexgoPrinterModule.appendPrnStr(str, itemAlign, fontEntity);
  }

  async appendImg(
    imgBase64,
    imgAlign = "LEFT",
    imgWidth = 150,
    imgHeight = 150
  ) {
    return await NexgoPrinterModule.appendImg(
      imgBase64,
      imgAlign,
      imgWidth,
      imgHeight
    );
  }

  async startPrint(cutOnFinish = false) {
    return await NexgoPrinterModule.startPrint(cutOnFinish);
  }

  async cutPaper() {
    return await NexgoPrinterModule.cutPaper();
  }

  async getPrinterStatus() {
    return await NexgoPrinterModule.getPrinterStatus();
  }

  async setGrayLevel(greyLevel = 0) {
    return await NexgoPrinterModule.setGrayLevel(greyLevel);
  }

  async print(printArray) {
    try {
      for (const item of printArray) {
        if (item.type === printArrayTypeEnum.DASH) {
          await this.appendDash();
        }

        if (item.type === printArrayTypeEnum.TEXT) {
          await this.appendStr(item.string, item.align, item.fontntity);
        }

        if (item.type === printArrayTypeEnum.IMG) {
          await this.appendImg(
            item.string,
            item.align,
            item.imgWidth,
            item.imgHeight
          );
        }
        if (item.type === printArrayTypeEnum.EMPTY) {
          await this.appendEmptyLine();
        }
      }

      await this.startPrint(true);
    } catch (e) {
      return "ERROR: " + e;
    }
  }

  async setLetterSpacing(spacing) {
    return await NexgoPrinterModule.setLetterSpacing(spacing);
  }

  async appendDash() {
    return await NexgoPrinterModule.appendPrnStr(
      "-------------------------",
      alignEnum.CENTER,
      fontEnum.NORMAL
    );
  }

  async appendEmptyLine() {
    return await NexgoPrinterModule.appendPrnStr(
      " ",
      alignEnum.CENTER,
      fontEnum.NORMAL
    );
  }
}

const printArrayTypeEnum = {
  DASH: "DASH",
  TEXT: "TEXT",
  IMG: "IMG",
  EMPTY: "EMPTY",
};

const alignEnum = {
  CENTER: "CENTER",
  LEFT: "LEFT",
  RIGHT: "RIGHT",
};

const fontEnum = {
  BOLD: "BOLD",
  NORMAL: "NORMAL",
  SMALL: "SMALL",
};
const RNNexgoPrinter = new NexgoPrinter();

export { alignEnum, printArrayTypeEnum, fontEnum };
export default RNNexgoPrinter;
