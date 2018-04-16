package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by pj on 2018/4/16.
 */

public class KeyboardSwitch {
    public static byte[] firstData = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    public final static byte[] UP = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};


    public static void changeByteAndSend(byte b0,byte b2){
        firstData[0] = b0;
        firstData[2] = b2;
        SendPackage.sendToKeyboard(firstData);
        SendPackage.sendToKeyboard(UP);
    }
    public static void sendEnterKey(){
        firstData[0] = 0x00;
        firstData[2] = 0x28;
        SendPackage.sendToKeyboard(firstData);
        SendPackage.sendToKeyboard(UP);
    }

    public static void sendToKeyboard(String str){
        char[] dataChar = str.toCharArray();
        for (int i = 0; i <dataChar.length ; i++) {
            char oneData = dataChar[i];

            switch (oneData){
                case '0':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x27);
                    break;
                case ')':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x27);
                    break;
                case '-':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x2D);
                    break;
                case '_':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x2D);
                    break;
                case '=':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x2E);
                    break;
                case '+':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x2E);
                    break;
                case '[':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x2F);
                    break;
                case '{':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x2F);
                    break;
                case ']':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x30);
                    break;
                case '}':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x30);
                    break;
                case 0x5C:
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x31);
                    break;
                case '|':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x31);
                    break;
                case ';':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x33);
                    break;
                case ':':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x33);
                    break;
                case '"':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x34);
                    break;
                case '\'':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x34);
                    break;
                case ',':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x36);
                    break;
                case '<':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x36);
                    break;
                case '.':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x37);
                    break;
                case '>':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x37);
                    break;
                case '/':
                    KeyboardSwitch.changeByteAndSend((byte)0x00,(byte)0x38);
                    break;
                case '?':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x38);
                    break;
                case '!':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x1E);
                    break;
                case '@':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x1F);
                    break;
                case '#':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x20);
                    break;
                case '$':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x21);
                    break;
                case '%':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x22);
                    break;
                case '^':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x23);
                    break;
                case '&':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x24);
                    break;
                case '*':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x25);
                    break;
                case '(':
                    KeyboardSwitch.changeByteAndSend((byte)0x02,(byte)0x26);
                    break;

                default:
                    if(oneData >= 'a' && oneData <= 'z'){
                        byte b2= (byte)(oneData - 'a' + 0x04);
                        KeyboardSwitch.changeByteAndSend((byte)0x00,b2);
                    }else if(oneData >= '1' && oneData <= '9'){
                        byte b2 =(byte)( oneData - '1' + 0x1E);
                        KeyboardSwitch.changeByteAndSend((byte)0x00,b2);
                    }else if(oneData >= 'A' && oneData <= 'Z'){
                        byte b2 = (byte)(oneData - 'A' + 0x04);
                        KeyboardSwitch.changeByteAndSend((byte)0x02,b2);
                    }
                    break;
            }

        }
        KeyboardSwitch.sendEnterKey();
    }
}
