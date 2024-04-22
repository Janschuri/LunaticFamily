package de.janschuri.lunaticFamily.utils;

public class ClickableDecisionMessage {

    private String text;
    private String confirmHoverText;
    private String confirmCommand;
    private String cancelHoverText;
    private String cancelCommand;

    public ClickableDecisionMessage(String message, String confirmHoverText, String confirmCommand, String cancelHoverText, String cancelCommand) {
        this.text = message;
        this.confirmHoverText = confirmHoverText;
        this.confirmCommand = confirmCommand;
        this.cancelHoverText = cancelHoverText;
        this.cancelCommand = cancelCommand;
    }

    public String getText() {
        return text;
    }

    public String getConfirmHoverText() {
        return confirmHoverText;
    }

    public String getConfirmCommand() {
        return confirmCommand;
    }

    public String getCancelHoverText() {
        return cancelHoverText;
    }

    public String getCancelCommand() {
        return cancelCommand;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setConfirmHoverText(String confirmHoverText) {
        this.confirmHoverText = confirmHoverText;
    }

    public void setConfirmCommand(String confirmCommand) {
        this.confirmCommand = confirmCommand;
    }

    public void setCancelHoverText(String cancelHoverText) {
        this.cancelHoverText = cancelHoverText;
    }

    public void setCancelCommand(String cancelCommand) {
        this.cancelCommand = cancelCommand;
    }



    public static ClickableDecisionMessage empty() {
        return new ClickableDecisionMessage("", "", "", "", "");
    }
}
