package de.janschuri.lunaticFamily.commands;

import java.util.HexFormat;

public class ClickableMessage {
    private String text;
    private final String hoverText;
    private final String command;
    private String color;

    public ClickableMessage(String text, String hoverText, String command) {
        this.text = text;
        this.hoverText = hoverText;
        this.command = command;
    }

    public ClickableMessage(String text, String hoverText) {
        this.text = text;
        this.hoverText = hoverText;
        this.command = null;
    }

    public ClickableMessage(String text) {
        this.text = text;
        this.hoverText = null;
        this.command = null;
    }

    public ClickableMessage setColor(String color) {
        this.color = color;
        return this;
    }

    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public String getHoverText() {
        return hoverText;
    }

    public String getCommand() {
        return command;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHoverText(String hoverText) {
        this.text = hoverText;
    }

    public void setCommand(String command) {
        this.text = command;
    }

    public static ClickableMessage empty() {
        return new ClickableMessage("", "", "");
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }
}
