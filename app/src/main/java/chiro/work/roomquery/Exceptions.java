package chiro.work.roomquery;

public class Exceptions extends Exception {

  public static final int LOGIN_FAILED = 405;
  public static final int CONNECT_ERROR = 511;
  public static final int DIALOG_MESSAGE = 510;
  public static final int FORMAT_ERROR = 535;

  int type;
  String dialogMessage;

  public static Exceptions getLoginFailedExpection() {
    Exceptions jew = new Exceptions();
    jew.setType(LOGIN_FAILED);
    return jew;
  }

  public static Exceptions getConnectErrorExpection() {
    Exceptions jew = new Exceptions();
    jew.setType(CONNECT_ERROR);
    return jew;
  }

  public static Exceptions getFormatErrorException() {
    Exceptions jew = new Exceptions();
    jew.setType(FORMAT_ERROR);
    return jew;
  }

  public static Exceptions newDialogMessageExpection(String s) {
    Exceptions jew = new Exceptions();
    jew.setType(DIALOG_MESSAGE);
    jew.setDialogMessage(s);
    return jew;
  }

  public String getDialogMessage() {
    return dialogMessage;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setDialogMessage(String dialogMessage) {
    this.dialogMessage = dialogMessage;
  }
}

