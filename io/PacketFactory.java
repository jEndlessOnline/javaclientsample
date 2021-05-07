package com.endlessonline.client.io;

import com.endlessonline.client.world.Direction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class PacketFactory {

    private PacketFactory() {}

    public static int getTimeStamp()  {
        Date time = Calendar.getInstance().getTime();
        int milliseconds = (int)(time.getTime() % 1000);
        return time.getHours() * 3600
                + time.getMinutes() * 60
                + time.getSeconds() * 100
                + ((milliseconds < 0 ? milliseconds + 1000 : milliseconds) / 10);
    }

    public static String getPCName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static EOWriter init(int version) {
        return new EOWriter(EOAction.INIT, EOFamily.INIT, 11)
                .writeThree(0)
                .writeChar(0)
                .writeChar(0)
                .writeChar(version)
                .writeFixedString(String.valueOf(12345));
    }

    public static EOWriter acceptConnection() {
        return new EOWriter(EOAction.ACCEPT, EOFamily.CONNECTION, 0);
    }

    public static EOWriter keepAlive() {
        return new EOWriter(EOAction.PING, EOFamily.CONNECTION, 1)
                .writeFixedString("k");
    }

    public static EOWriter validateAccount(String username) {
        return new EOWriter(EOAction.REQUEST, EOFamily.ACCOUNT, username.length())
                .writeFixedString(username);
    }

    public static EOWriter createAccount(String username, String password, String fullname, String location, String email) {
        return new EOWriter(EOAction.CREATE, EOFamily.ACCOUNT, 0)
                .writeShort(0)
                .writeByte(0)
                .writeBreakString(username)
                .writeBreakString(password)
                .writeBreakString(fullname)
                .writeBreakString(location)
                .writeBreakString(email)
                .writeBreakString(getPCName())
                .writeBreakString(String.valueOf(12345));
    }

    public static EOWriter login(String username, String password) {
        return new EOWriter(EOAction.REQUEST, EOFamily.LOGIN, 2 + username.length() + password.length())
                .writeBreakString(username)
                .writeBreakString(password);
    }

    public static EOWriter selectCharacter(int id) {
        return new EOWriter(EOAction.REQUEST, EOFamily.WELCOME, 4)
                .writeInt(id);
    }

    public static EOWriter requestFile(EOFile file) {
        return new EOWriter(EOAction.AGREE, EOFamily.WELCOME, 1)
                .writeChar(file.getID());
    }

    public static EOWriter enterGame(int selectedUID) {
        return new EOWriter(EOAction.MESSAGE, EOFamily.WELCOME, 7)
                .writeThree(0) // ?
                .writeInt(selectedUID);
    }

    public static EOWriter acceptWarp() {
        return new EOWriter(EOAction.ACCEPT, EOFamily.WARP, 0);
    }

    public static EOWriter requestMap() {
        return new EOWriter(EOAction.TAKE, EOFamily.WARP, 0);
    }

    public static EOWriter attack(Direction direction) {
        return new EOWriter(EOAction.USE, EOFamily.ATTACK, 4)
                .writeChar(direction.ordinal())
                .writeThree(getTimeStamp());
    }

    public static EOWriter walk(Direction direction, int x, int y) {
        return new EOWriter(EOAction.PLAYER, EOFamily.WALK, 6)
                .writeChar(direction.ordinal())
                .writeThree(getTimeStamp())
                .writeChar(x)
                .writeChar(y);
    }

    public static EOWriter openDoor(int x, int y) {
        return new EOWriter(EOAction.OPEN, EOFamily.DOOR, 2)
                .writeChar(x)
                .writeChar(y);
    }

    public static EOWriter face(Direction direction) {
        return new EOWriter(EOAction.PLAYER, EOFamily.FACE, 1)
                .writeChar(direction.ordinal());
    }

    public static EOWriter sitFloor() {
        return new EOWriter(EOAction.REQUEST, EOFamily.SIT, 1)
                .writeChar(1);
    }

    public static EOWriter standFloor() {
        return new EOWriter(EOAction.REQUEST, EOFamily.SIT, 1)
                .writeChar(2);
    }

    public static EOWriter sitChair(int x, int y) {
        return new EOWriter(EOAction.REQUEST, EOFamily.CHAIR, 3)
                .writeChar(1)
                .writeChar(x)
                .writeChar(y);
    }

    public static EOWriter standChair(int x, int y) {
        return new EOWriter(EOAction.REQUEST, EOFamily.CHAIR, 3)
                .writeChar(2)
                .writeChar(x)
                .writeChar(y);
    }

    public static EOWriter chantSpell(int id) {
        return new EOWriter(EOAction.REQUEST, EOFamily.SPELL, 5)
                .writeShort(id)
                .writeThree(getTimeStamp());
    }

    public static EOWriter castSelfSpell() {
        return new EOWriter(EOAction.TARGET_SELF, EOFamily.SPELL, 6)
                .writeChar(0)
                .writeShort(0)
                .writeThree(getTimeStamp());
    }

    public static EOWriter talkLocal(String message) {
        return new EOWriter(EOAction.REPORT, EOFamily.TALK, message.length())
                .writeFixedString(message);
    }

    public static EOWriter dropItem(int id, int amount, int x, int y) {
        return new EOWriter(EOAction.DROP, EOFamily.ITEM, 8)
                .writeShort(id)
                .writeInt(amount)
                .writeChar(x)
                .writeChar(y);
    }

    public static EOWriter junkItem(int id, int amount) {
        return new EOWriter(EOAction.JUNK, EOFamily.ITEM, 6)
                .writeShort(id)
                .writeInt(amount);
    }

    public static EOWriter useItem(int id) {
        return new EOWriter(EOAction.USE, EOFamily.ITEM, 2)
                .writeShort(2);
    }

    public static EOWriter requestRefresh() {
        return new EOWriter(EOAction.REQUEST, EOFamily.REFRESH, 1)
                .writeByte(0);
    }


    /*public static EOWriter walkAdmin(Direction direction, float x, float y) {

        return new EOWriter(EOAction.ADMIN, EOFamily.WALK)
                .writeChar(direction.value())
                .writeThree((int)Tools.generateTimeStamp())
                .writeChar((int)x)
                .writeChar((int)y);
    }*/

    /*public static EOWriter message(Channel.Type channel, String message) {

        EOFamily family = EOFamily.TALK;
        EOAction action;
        switch (channel) {

            default:
            case LOCAL:
                action = EOAction.REPORT;
                break;

            case GLOBAL:
                action = EOAction.MESSAGE;
                break;

            case GUILD:
                action = EOAction.REQUEST;
                break;
        }

        return new EOWriter(action, family)
                .writeFixedString(message);
    }*/
}
